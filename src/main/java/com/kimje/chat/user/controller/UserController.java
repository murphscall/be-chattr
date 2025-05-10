package com.kimje.chat.user.controller;

import com.kimje.chat.common.response.ApiResponse;
import com.kimje.chat.user.dto.UserRequestDTO;
import com.kimje.chat.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/users")
    public ResponseEntity<?> create(@RequestBody UserRequestDTO.Create dto){
        userService.createUser(dto);
        return null;
    }

    @PutMapping("/api/users")
    public String update(@RequestBody UserRequestDTO.Update dto){
        userService.updateUser(dto);
        return null;
    }

    @DeleteMapping("/api/users")
    public String delete(@RequestBody UserRequestDTO.Delete dto){
        userService.deleteUser(dto);
        return null;
    }


}
