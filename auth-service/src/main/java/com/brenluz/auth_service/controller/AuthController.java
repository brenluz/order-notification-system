package com.brenluz.auth_service.controller;

import com.brenluz.auth_service.dto.UserRequest;
import com.brenluz.auth_service.dto.UserResponse;
import com.brenluz.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRequest request){
        String message = authService.register(request);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody @Valid UserRequest request){
        UserResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
