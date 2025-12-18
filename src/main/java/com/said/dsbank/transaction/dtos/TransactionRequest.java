package com.said.dsbank.transaction.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.said.dsbank.card.VirtualCard;
import com.said.dsbank.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionRequest {

    private TransactionType transactionType;
    private BigDecimal amount;
    private String  accountNumber;
    private String  description;
    private  String  destinationAccountNumber;
    private VirtualCard virtualCard;






}
