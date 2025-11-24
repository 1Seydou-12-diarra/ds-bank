package com.said.dsbank.acount.controller;

import com.said.dsbank.acount.service.AccountService;
import com.said.dsbank.auth_users.dtos.LoginReponse;
import com.said.dsbank.auth_users.dtos.LoginRequest;
import com.said.dsbank.res.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/accounts")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor

public class AccountController {
    private final AccountService accountService;


    @GetMapping("/me")
    public ResponseEntity<Response<?>> getMyAccounts(){

        return ResponseEntity.ok(accountService.getMyAccounts());
    }

    @DeleteMapping("/close/{accountNumber}")
    public ResponseEntity<Response<?>> closeAccount(@PathVariable("accountNumber") String accountNumber){

        return ResponseEntity.ok(accountService.closeAccount(accountNumber));
    }



}
