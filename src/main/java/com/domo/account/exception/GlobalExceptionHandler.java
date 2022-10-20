package com.domo.account.exception;

import com.domo.account.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.domo.account.type.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ErrorResponse handleAccountException(AccountException e){
        log.error("{} : {}",e.getErrorCode(), e.getErrorMessage());

        return ErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .errorMessage(e.getErrorMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("MethodArgumentNotValidException", e);

        return ErrorResponse.builder()
                .errorCode(INVALID_REUQEST)
                .errorMessage(INVALID_REUQEST.getDescription())
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e){
        log.error("DataIntegrityViolationException", e);

        return ErrorResponse.builder()
                .errorCode(INVALID_REUQEST)
                .errorMessage(INVALID_REUQEST.getDescription())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e){
        log.error("{}", e.getMessage());

        return ErrorResponse.builder()
                .errorCode(INTERNAL_SERVER_ERROR)
                .errorMessage(INTERNAL_SERVER_ERROR.getDescription())
                .build();
    }
}
