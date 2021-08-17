package com.littleyes.manager.util;

import com.littleyes.common.util.web.ApiException;
import com.littleyes.common.util.web.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <p> <b> 全局异常处理 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-24
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public ApiResponse handleApiException(Exception e) {
        return ApiResponse.failure(e.getMessage());
    }

}
