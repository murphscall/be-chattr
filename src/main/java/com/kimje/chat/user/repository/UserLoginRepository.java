package com.kimje.chat.user.repository;

import com.kimje.chat.user.entity.UserLogin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {

	boolean existsByUserIdAndLoginType(long userId, String loginType);

	UserLogin findByLoginTypeAndProviderId(String normal, String email);
}
