package com.said.dsbank.transaction.dtos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.said.dsbank.acount.dtos.AccountDTO;
import com.said.dsbank.acount.entity.Account;
import com.said.dsbank.enums.TransactionStatus;
import com.said.dsbank.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

    private Long id;

    private BigDecimal amount;


    private TransactionType transactionType;

    private LocalDateTime transactionDate ;

    private String description;


    private TransactionStatus status;

    @JsonBackReference
    private AccountDTO account;

    //for transfer
    private String sourceAccount;
    private String destinationAccount;


}
