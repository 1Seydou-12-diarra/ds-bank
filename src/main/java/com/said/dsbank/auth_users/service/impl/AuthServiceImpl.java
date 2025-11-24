package com.said.dsbank.auth_users.service.impl;

import com.said.dsbank.acount.entity.Account;
import com.said.dsbank.acount.repo.AccountRepo;
import com.said.dsbank.acount.service.AccountService;
import com.said.dsbank.auth_users.dtos.LoginReponse;
import com.said.dsbank.auth_users.dtos.LoginRequest;
import com.said.dsbank.auth_users.dtos.RegistrationRequest;
import com.said.dsbank.auth_users.dtos.ResetPaaswordRequest;
import com.said.dsbank.auth_users.entity.PasswordResetCode;
import com.said.dsbank.auth_users.entity.User;
import com.said.dsbank.auth_users.repo.PasswordResetCodeRepo;
import com.said.dsbank.auth_users.repo.UserRepo;
import com.said.dsbank.auth_users.service.AuthService;
import com.said.dsbank.auth_users.service.CodeGenerator;
import com.said.dsbank.enums.AccountType;
import com.said.dsbank.enums.Currency;
import com.said.dsbank.exceptions.BadRequestException;
import com.said.dsbank.exceptions.NotFoundException;
import com.said.dsbank.notification.dtos.NotificationDTO;
import com.said.dsbank.notification.service.NotificationService;
import com.said.dsbank.res.Response;
import com.said.dsbank.role.entity.Role;
import com.said.dsbank.role.repo.RoleRepo;
import com.said.dsbank.security.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;



import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final NotificationService notificationService;
    private final AccountService accountService;

     private  final CodeGenerator codeGenerator;
     private final PasswordResetCodeRepo passwordResetCodeRepo;

     @Value("${password.reset.link")
     private String resetLink;



    @Override
    @Transactional
    public Response<String> register(RegistrationRequest request) {

        // Vérifier le rôle
        List<Role> roles;
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            Role defaultRole = roleRepo.findByName("CUSTOMER")
                    .orElseThrow(() -> new NotFoundException("CUSTOMER ROLE IS NOT FOUND"));
            roles = Collections.singletonList(defaultRole);
        } else {
            roles = request.getRoles().stream()
                    .map(roleName -> roleRepo.findByName(roleName)
                            .orElseThrow(() -> new NotFoundException("ROLE NOT FOUND : " + roleName)))
                    .toList();
        }

        // Vérifier email existant
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("EMAIL ALREADY EXISTS");
        }

        // Construction de l'utilisateur
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())   // 👉 ajoute ce champ dans l'entité si nécessaire
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .active(true)
                .build();

        User savedUser = userRepo.save(user);


        Account saveAccount = accountService.createAccount(AccountType.SAVINGS, savedUser);


        //SEND WELCOME EMAIL
        Map<String,Object> vars  = new HashMap<>();
        vars.put("name",savedUser.getFirstName());

        NotificationDTO notificationDTO =  NotificationDTO.builder()
                .recipient(savedUser.getEmail())
                .subject("Welcome to said bank")
                .templateName("welcome")
                .templateVariables(vars)
                .build();

        notificationService.sendEmail(notificationDTO ,savedUser);

        //SEND ACCOUNT CREATION /DETAIL EMAIL

        Map<String,Object> accountVars = new HashMap<>();
        accountVars.put("name", savedUser.getFirstName());
        accountVars.put("accountNumber", saveAccount.getAccountNumber());
        accountVars.put("accountType", AccountType.SAVINGS.name());
        accountVars.put("currency", Currency.USD.name());

        NotificationDTO accountCreatedEmail = NotificationDTO.builder()
                .recipient(savedUser.getEmail())
                .subject("Your new bank account has been created")
                .templateName("account_created")
                .templateVariables(accountVars)
                .build();

        notificationService.sendEmail(accountCreatedEmail, savedUser);


        return Response.<String>builder()
        .statusCode(201)
        .message("User registered successfully")
        .data("Email of your account details has bean sent to you. your account number is " + saveAccount.getAccountNumber())
        .build();

    }

    @Override
    public Response<LoginReponse> login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        User user = userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("EMAIL NOT FOUND"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("INVALID PASSWORD");
        }
        String token = tokenService.generateToken(user.getEmail());
        LoginReponse loginResponse = LoginReponse.builder()
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .token(token)
                .build();

        return  Response.<LoginReponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("login succeful")
                .data(loginResponse)
                .build();
    }

    @Override
    @Transactional
    public Response<?> forgetPassword(String email) {

        User user = userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("email NOT FOUND"));
        passwordResetCodeRepo.deleteByUserId(user.getId());

        String  code = codeGenerator.generateUniqueCode();

        PasswordResetCode resetCode = PasswordResetCode.builder()
                .user(user)
                .code(code)
                .expiryDate(calculateExpiryDate())
                .used(false)
                .build();


        passwordResetCodeRepo.save(resetCode);

        //send email reset out
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getFirstName());
        templateVariables.put("resetLink", resetLink + code);
        templateVariables.put("code", code); // ✅ AJOUT IMPORTANT


        NotificationDTO notificationDTO =  NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Welcome to said bank")
                .templateName("password-reset")
                .templateVariables(templateVariables)
                .build();
         notificationService.sendEmail(notificationDTO ,user);


         return Response.builder()
                 .statusCode(HttpStatus.OK.value())
                 .message("password reset code sent to your email")
                 .build();


    }

    @Override
    @Transactional
    public Response<?> updatePasswordViaResetCode(ResetPaaswordRequest resetPaaswordRequest) {

        String code = resetPaaswordRequest.getCode();
        String newPassword = resetPaaswordRequest.getNewPassword();

        //FIND AND VALIDATE CODE

        PasswordResetCode resetCode = passwordResetCodeRepo.findByCode(code)
                .orElseThrow(() -> new BadRequestException("Invalid reset code "));

        //CHECK EXPIRATED USER PASSWORD
        if(resetCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetCodeRepo.delete(resetCode);//clean up expired code
            throw new BadRequestException("Reset code has expired ");

        }
        //update user pasword
        User user =resetCode.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        //Delete the code immedialitely aafter succeful use
        passwordResetCodeRepo.delete(resetCode);


        //send email reset out
        Map<String,Object> templateVariables = new HashMap<>();
        templateVariables.put("name",user.getFirstName());

        NotificationDTO confirmationEmail=  NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("password updated succeful ")
                .templateName("password update confirmation")
                .templateVariables(templateVariables)
                .build();
        notificationService.sendEmail(confirmationEmail ,user);


        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("password updated succefully")
                .build();


    }




    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusHours(5);
    }
}
