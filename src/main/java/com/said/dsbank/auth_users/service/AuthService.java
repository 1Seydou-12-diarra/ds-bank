package com.said.dsbank.auth_users.service;

import com.said.dsbank.auth_users.dtos.LoginReponse;
import com.said.dsbank.auth_users.dtos.LoginRequest;
import com.said.dsbank.auth_users.dtos.RegistrationRequest;
import com.said.dsbank.auth_users.dtos.ResetPaaswordRequest;
import com.said.dsbank.res.Response;

public interface AuthService {

    Response<String> register(RegistrationRequest registrationRequest);
    Response<LoginReponse> login(LoginRequest loginRequest);
    Response<?> forgetPassword(String email );
    Response<?> updatePasswordViaResetCode(ResetPaaswordRequest resetPaaswordRequest);
}
