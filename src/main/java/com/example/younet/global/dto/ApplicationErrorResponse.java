package com.example.younet.global.dto;

import com.example.younet.global.errorException.ErrorCode;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationErrorResponse {
    private int status;
    private String message;
    private int code;

    public ApplicationErrorResponse(ErrorCode e){
        this.status = e.getHttpStatus().value();
        this.message = e.getMessage();
        this.code = e.getCode();
    }
}
