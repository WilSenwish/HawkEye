package com.littleyes.common.util.web;

import lombok.Getter;

/**
 * <p> <b> API 响应 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-17
 */
@Getter
public class ApiResponse<T> {

    /**
     * 接口-成功码：{@value}
     */
    public static final String CODE_SUCCESS = "success";

    /**
     * 接口-失败码：{@value}
     */
    public static final String CODE_FAILURE = "failure";

    /**
     * 接口-警告码：{@value}
     */
    public static final String CODE_WARN = "warn";

    /**
     * 接口-成功的默认消息：{@value}
     */
    public static final String DEFAULT_SUCCESS_MESSAGE = "操作成功！";

    /**
     * 接口-失败的默认消息：{@value}
     */
    public static final String DEFAULT_FAILURE_MESSAGE = "操作失败！";


    /**
     * 接口成功与否-码
     */
    private String code;

    /**
     * 接口-结果数据体
     */
    private T data;

    /**
     * 接口成功与否-消息
     */
    private String message;


    public boolean isScucess() {
        return CODE_SUCCESS.equals(code);
    }


    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, DEFAULT_SUCCESS_MESSAGE);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(CODE_SUCCESS, data, message);
    }

    public static <T> ApiResponse<T> failure() {
        return failure(DEFAULT_FAILURE_MESSAGE);
    }

    public static <T> ApiResponse<T> failure(String message) {
        return failure(null, message);
    }

    public static <T> ApiResponse<T> failure(T data, String message) {
        return new ApiResponse<>(CODE_FAILURE, data, message);
    }

    public static <T> ApiResponse<T> warn(T data, String message) {
        return new ApiResponse<>(CODE_WARN, data, message);
    }


    private ApiResponse() {
    }

    private ApiResponse(String code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

}
