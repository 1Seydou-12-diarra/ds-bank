package com.said.dsbank.auth_users.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginReponse {

    private String token;
    public List<String> roles;

}
