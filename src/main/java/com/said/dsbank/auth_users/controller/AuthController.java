package com.said.dsbank.auth_users.controller;

import com.said.dsbank.auth_users.dtos.LoginReponse;
import com.said.dsbank.auth_users.dtos.LoginRequest;
import com.said.dsbank.auth_users.dtos.RegistrationRequest;
import com.said.dsbank.auth_users.dtos.ResetPaaswordRequest;
import com.said.dsbank.auth_users.service.AuthService;
import com.said.dsbank.res.Response;
import com.said.dsbank.role.entity.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<String>> register(@RequestBody  @Valid  RegistrationRequest  registrationRequest){

        return ResponseEntity.ok(authService.register(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Response<LoginReponse>> login(@RequestBody  @Valid LoginRequest loginRequest){

        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Response<?>> forgetPassword(@RequestBody ResetPaaswordRequest resetPaaswordRequest){

        return ResponseEntity.ok(authService.forgetPassword(resetPaaswordRequest.getEmail()));
    }
    @PostMapping("/reset-password")
    public ResponseEntity<Response<?>> resetPassword(@RequestBody ResetPaaswordRequest resetPaaswordRequest){

        return ResponseEntity.ok(authService.updatePasswordViaResetCode(resetPaaswordRequest));
    }

}
