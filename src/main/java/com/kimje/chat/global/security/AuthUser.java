package com.kimje.chat.global.security;

import com.kimje.chat.user.enums.UserRole;

public interface AuthUser {
  Long getUserId();
  String getEmail();
  UserRole getRole();
}
