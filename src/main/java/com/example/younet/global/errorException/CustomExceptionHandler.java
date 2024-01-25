package com.example.younet.global.errorException;

import com.example.younet.global.dto.ApplicationErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@CrossOrigin(origins = "*")
public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ApplicationErrorResponse> handleCustomException(CustomException e){

        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(new ApplicationErrorResponse(e.getErrorCode()));
    }
}
