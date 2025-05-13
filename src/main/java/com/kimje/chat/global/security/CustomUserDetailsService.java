package com.kimje.chat.global.security;

import com.kimje.chat.user.entity.Users;
import com.kimje.chat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Users user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 이메일 입니다." + email));

    return new CustomUserDetails(user);
  }
}
