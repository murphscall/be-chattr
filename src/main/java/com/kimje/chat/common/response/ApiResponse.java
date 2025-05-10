package com.kimje.chat.common.response;

import org.springframework.http.HttpStatus;



public class ApiResponse<T> {

    private String status;
    private int statusCode;
    private T data;

    public ApiResponse(String status,int statusCode , T data) {
        this.status = status;
        this.statusCode = statusCode;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success",HttpStatus.OK.value(), data);
    }

}
