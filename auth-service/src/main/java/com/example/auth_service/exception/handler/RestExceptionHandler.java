package com.example.auth_service.exception.handler;

import com.example.auth_service.dto.ErrorDto;
import com.example.auth_service.exception.UserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(value = {UserException.class})
    @ResponseBody
    public ResponseEntity<ErrorDto> handleException(UserException ex){
        return ResponseEntity.status(ex.getCode())
                .body(ErrorDto.builder().message(ex.getMessage()).build());
    }
}
