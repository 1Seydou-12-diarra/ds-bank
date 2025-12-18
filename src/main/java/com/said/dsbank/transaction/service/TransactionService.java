package com.said.dsbank.transaction.service;

import com.said.dsbank.res.Response;
import com.said.dsbank.transaction.dtos.TransactionDTO;
import com.said.dsbank.transaction.dtos.TransactionRequest;
import com.said.dsbank.transaction.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

Response<?> createTransaction(TransactionRequest transactionRequest);

Response<List<TransactionDTO>>getTransactionsForAnAccount(String accountNumber , int page, int size);

Transaction payWithVirtualCard(Long id, BigDecimal amount, String description);




}
