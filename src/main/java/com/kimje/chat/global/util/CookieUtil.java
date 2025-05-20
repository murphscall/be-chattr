package com.kimje.chat.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import org.springframework.http.ResponseCookie;

public class CookieUtil {

  public static String getCookie(HttpServletRequest request, String name) {
    if (request.getCookies() == null) return null;

    return Arrays.stream(request.getCookies())
        .filter(c -> name.equals(c.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);
  }

  public static ResponseCookie deleteCookie(String name) {
    return ResponseCookie.from(name, "")
        .path("/")
        .httpOnly(true)
        .secure(false)
        .maxAge(0)
        .build();
  }

}
