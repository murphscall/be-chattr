package com.kimje.chat.global.security;

import com.kimje.chat.global.security.OAuth2.AuthUser;
import com.kimje.chat.user.entity.Users;
import com.kimje.chat.user.enums.UserRole;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails, AuthUser {

  private final Users user;
  private final String loginType = "NORMAL";

  public CustomUserDetails(Users user) {
    this.user = user;
  }

  @Override
  public Long getUserId() {
    return user.getUserId();
  }

  @Override
  public String getEmail() {
    return user.getEmail();
  }

  @Override
  public String getLoginType() {
    return loginType;
  }

  @Override
  public UserRole getRole() {
    return user.getRole();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

}
