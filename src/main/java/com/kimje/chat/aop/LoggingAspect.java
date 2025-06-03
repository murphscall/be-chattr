package com.kimje.chat.aop;



import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.kimje.chat.global.exception.customexception.LoginFailException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

	private final HttpServletRequest request;
	private final HttpServletResponse response;

	@Around("execution(* com.kimje..*Controller.*(..)) &&" +
		"!within(com.kimje.chat.chats.controller.StompController)")
	public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
		boolean success = true;
		long start = System.currentTimeMillis();
		long end ;
		String method = request.getMethod();
		String uri = request.getRequestURI();

		String userEmail = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
			.map(auth -> auth.getName())
			.orElse("익명");

		// 요청 파라미터
		Object[] args = joinPoint.getArgs();
		String params = Arrays.stream(args)
			.filter(arg -> !(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse))
			.map(this::formatArg)
			.collect(Collectors.joining(", ", "{", "}"));

		log.info("🟢[API 요청] {} {} | 회원={}", method, uri, userEmail);

		// 실제 메서드 실행
		Object result = null;
		try {
			result = joinPoint.proceed();
			return result;
		} catch (Exception e) {
			success = false;
			throw e;
		}finally {
			end = System.currentTimeMillis();
			long duration = end - start;
			int status = response.getStatus();

			if(success){
				log.info("🟢[API 요청 완료] 응답 = {} 응답 시간 = {}ms", status, duration);
			}else{
				log.info("🟢[API 요청] 응답 시간 = {}ms ", duration);
			}

		}


	}

	private String formatArg(Object arg) {
		if (arg == null) return "null";
		return arg.toString();
	}
}
