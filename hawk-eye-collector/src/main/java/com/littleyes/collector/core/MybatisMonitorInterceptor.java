package com.littleyes.collector.core;

import com.littleyes.common.util.PerformanceContext;
import com.littleyes.common.config.HawkEyeConfig;
import com.littleyes.common.enums.PerformanceTypeEnum;
import com.littleyes.common.trace.TraceContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p> <b> MyBatis Executor 拦截器 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-22
 */
@Intercepts({
        @Signature(method = "query", type = Executor.class, args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(method = "update", type = Executor.class, args = {MappedStatement.class, Object.class})
})
public class MybatisMonitorInterceptor implements Interceptor {

    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\?");
    private static final String MYSQL_DEFAULT_URL = "jdbc:mysql://unknown:3306/%s?useUnicode=true";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (HawkEyeConfig.isPerformanceDisabled()) {
            return invocation.proceed();
        }

        Object returnObj;
        long start = System.currentTimeMillis();
        boolean success = false;

        MappedStatement mappedStatement = getStatement(invocation);

        String commandType = mappedStatement.getSqlCommandType().name();
        String sql = extractSql(invocation, mappedStatement);

        try {
            returnObj = invocation.proceed();
            success = true;
        } finally {
            PerformanceContext.init(
                    mappedStatement.getId(),
                    commandType,
                    PerformanceTypeEnum.MYSQL.getType(),
                    success,
                    start,
                    System.currentTimeMillis()
            );
            // TODO sql
            TraceContext.append(PerformanceTypeEnum.MYSQL.getType());
        }

        return returnObj;
    }

    private MappedStatement getStatement(Invocation invocation) {
        return (MappedStatement) invocation.getArgs()[0];
    }

    private String extractSql(Invocation invocation, MappedStatement mappedStatement) {
        Object parameter = null;

        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }

        try {
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();

            return resolveSql(configuration, boundSql);
        } catch (Exception e) {
            return mappedStatement.getBoundSql(parameter).getSql();
        }
    }

    private String resolveSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");

        if (!parameterMappings.isEmpty() && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(resolveParameterValue(parameterObject)));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                Matcher matcher = PARAMETER_PATTERN.matcher(sql);
                StringBuffer sqlBuffer = new StringBuffer();

                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    Object obj = null;

                    if (metaObject.hasGetter(propertyName)) {
                        obj = metaObject.getValue(propertyName);
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        obj = boundSql.getAdditionalParameter(propertyName);
                    }

                    if (matcher.find()) {
                        matcher.appendReplacement(sqlBuffer, Matcher.quoteReplacement(resolveParameterValue(obj)));
                    }
                }

                matcher.appendTail(sqlBuffer);
                sql = sqlBuffer.toString();
            }
        }

        return sql;
    }

    private String resolveParameterValue(Object obj) {
        String value;

        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format((Date) obj) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }

        return value;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }

        return target;
    }

    @Override
    public void setProperties(Properties properties) {
    }

}
