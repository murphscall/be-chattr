package com.kimje.chat.global.exception.exhandler.global;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		// 1. 발생한 예외 로그로 남기기
		log.error("🔴 [비동기 처리 에러 발생] Exception message - {}", ex.getMessage());

		// 2. 어떤 메서드에서 에러가 발생했는지 명시
		log.error("Method name - {}", method.getName());

		// 3. 메서드에 전달된 파라미터들은 무엇이었는지 기록
		Arrays.stream(params).forEach(param -> log.error("Parameter value - {}", param));
	}
}
