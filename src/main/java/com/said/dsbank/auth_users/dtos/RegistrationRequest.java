package com.said.dsbank.auth_users.dtos;

import java.util.List;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data


public class RegistrationRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    @NotBlank(message ="Email is required")
    @Email
    private String email;
    private List<String> roles;
    @NotBlank(message ="password is required")

    private String password;
}
