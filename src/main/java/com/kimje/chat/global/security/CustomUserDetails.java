package com.kimje.chat.global.security;

import com.kimje.chat.user.entity.Users;
import com.kimje.chat.user.enums.UserRole;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails,AuthUser {

  private final Users user;

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

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
