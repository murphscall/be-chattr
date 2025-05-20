package com.kimje.chat.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/api/admin/users")
  public String getUsers() {
    return "getUsers";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/api/admin/users/{userId}")
  public String blockUser( @PathVariable String userId) {
    return "postUsers";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/api/admin")
  public String createAdmin() {
    return "createAdmin";
  }

}
