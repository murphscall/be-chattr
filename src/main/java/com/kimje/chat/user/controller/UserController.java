package com.kimje.chat.user.controller;

import com.kimje.chat.global.exception.FieldErrorException;
import com.kimje.chat.user.dto.UserRequestDTO;
import com.kimje.chat.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "USER API" ,description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/users")
    public ResponseEntity<?> create(@Valid @RequestBody UserRequestDTO.Create dto , BindingResult result) {
        if(result.hasErrors()) {
            fieldErrorsHandler(result);
        }
        userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/api/users")
    public ResponseEntity<?> update(@RequestBody UserRequestDTO.Update dto){
        userService.updateUser(dto);
        return null;
    }

    @DeleteMapping("/api/users")
    public ResponseEntity<?> delete(@RequestBody UserRequestDTO.Delete dto){
        userService.deleteUser(dto);
        return null;
    }

    @GetMapping("/api/users/me")
    public ResponseEntity<?> me(HttpSession session){
        userService.getUserInfo();
        return null;
    }

    public void fieldErrorsHandler(BindingResult result){
        Map<String, String> errors = new HashMap<>();

        for(FieldError error : result.getFieldErrors()){
            errors.put(error.getField(), error.getDefaultMessage());
        }

        throw new FieldErrorException(errors);
    }


}
