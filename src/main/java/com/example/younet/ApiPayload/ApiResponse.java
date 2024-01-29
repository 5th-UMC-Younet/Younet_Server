package com.example.younet.ApiPayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"httpStatus","message","data"})
public class ApiResponse<T> {

    @JsonProperty("httpStatus")
    private final HttpStatus status;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ApiResponse<T> onSuccess(HttpStatus status,T data){
        return new ApiResponse<>(status,"Success",data);
    }
}
