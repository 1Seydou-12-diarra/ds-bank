package com.said.dsbank.auth_users.service;


import com.said.dsbank.auth_users.repo.PasswordResetCodeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CodeGenerator {

  private  final PasswordEncoder passwordEncoder;

  private static  final String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static  final int CODE_LENGTH = 5;
    private final PasswordResetCodeRepo passwordResetCodeRepo;

    public String generateCode() {
      String code;
      do {
          code = generateRandomCode();

      }while (passwordResetCodeRepo.findByCode(code).isPresent());
      return code;
  }

  private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt( ALPHA_NUMERIC.length());
            sb.append(ALPHA_NUMERIC.charAt(index));
        }
        return sb.toString();
  }

    public String generateUniqueCode() {
        return generateRandomCode();
    }
}
