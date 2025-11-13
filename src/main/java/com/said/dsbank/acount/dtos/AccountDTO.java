package com.said.dsbank.acount.dtos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.said.dsbank.auth_users.dtos.UserDTO;
import com.said.dsbank.auth_users.entity.User;
import com.said.dsbank.enums.AccountStatus;
import com.said.dsbank.enums.AccountType;
import com.said.dsbank.enums.Currency;
import com.said.dsbank.transaction.dtos.TransactionDTO;
import jakarta.persistence.*;
import jakarta.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    private Long id;
    private String accountNumber;
    private BigDecimal balance ;

    private AccountType accountType;

    @JsonBackReference
    private UserDTO users;

    private Currency currency;

    private AccountStatus accountStatus;

    @JsonBackReference
    private List<TransactionDTO> transactions ;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

}
