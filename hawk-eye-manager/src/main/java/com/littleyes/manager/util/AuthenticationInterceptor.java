package com.littleyes.manager.util;

import com.littleyes.common.util.ApiResponse;
import com.littleyes.common.util.JsonUtils;
import com.littleyes.manager.service.AuthService;
import com.littleyes.storage.entity.AccountModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * <p> <b>认证拦截器</b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-17
 */
@Slf4j
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    private final AuthService authService;

    AuthenticationInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (Objects.isNull(token)) {
            token = request.getParameter("token");
            if (Objects.isNull(token)) {
                outputAuthorizationMessage(response, "登录信息不合法！");
                return false;
            }
        }

        AccountModel account = authService.getAccountByToken(token);
        if (Objects.isNull(account)) {
            outputAuthorizationMessage(response, "登录已失效！");
            return false;
        }

        AccountContext.set(account);

        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        AccountContext.remove();
    }

    private static void outputAuthorizationMessage(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(JsonUtils.toString(ApiResponse.failure(message)));
    }

}
