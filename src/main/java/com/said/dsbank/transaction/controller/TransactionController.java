package com.said.dsbank.transaction.controller;

import com.said.dsbank.res.Response;
import com.said.dsbank.transaction.dtos.TransactionRequest;
import com.said.dsbank.transaction.entity.Transaction;
import com.said.dsbank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class TransactionController {

   private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Response<?>> createTransaction(@RequestBody @Valid TransactionRequest transactionRequest) {

        return ResponseEntity.ok(transactionService.createTransaction(transactionRequest));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Response<?>> getTransactionsForAnAccount(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {

        return ResponseEntity.ok(transactionService.getTransactionsForAnAccount(accountNumber,page,size));
    }
}
