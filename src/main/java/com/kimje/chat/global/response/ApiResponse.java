package com.kimje.chat.global.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {

	private static final String SUCCESS_STATUS = "success";
	private static final String FAIL_STATUS = "fail";
	private static final String ERROR_STATUS = "error";

	private String status;
	private int statusCode;
	private T data;
	private String message;

	private ApiResponse(String status, int statusCode, T data , String message) {
		this.status = status;
		this.statusCode = statusCode;
		this.data = data;
		this.message = message;
	}

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(SUCCESS_STATUS, HttpStatus.OK.value(), data , null);
	}
	public static <T> ApiResponse<T> success(T data , String message) {
		return new ApiResponse<>(SUCCESS_STATUS, HttpStatus.OK.value(), data , message);
	}

	public static <T> ApiResponse<T> error(T data) {
		return new ApiResponse<>(FAIL_STATUS, HttpStatus.BAD_REQUEST.value(), data , null);
	}
	public static <T> ApiResponse<T> error(T data, String message) {
		return new ApiResponse<>(FAIL_STATUS, HttpStatus.BAD_REQUEST.value(), data , message);
	}

}
