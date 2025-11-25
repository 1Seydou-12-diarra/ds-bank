package com.said.dsbank.audit_dashboard.controller;

import com.said.dsbank.acount.dtos.AccountDTO;
import com.said.dsbank.acount.repo.AccountRepo;
import com.said.dsbank.audit_dashboard.service.AuditorService;
import com.said.dsbank.auth_users.dtos.UserDTO;
import com.said.dsbank.transaction.dtos.TransactionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/audit")
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AUDITOR')")
public class AuditorController {
    private final AuditorService auditorService;

    @GetMapping("/totals")
    public ResponseEntity<Map<String, Long>> getSytemTotale(){
        return ResponseEntity.ok().body(auditorService.getSytemTotale());
    }

    @GetMapping("/users")
    public ResponseEntity<UserDTO> findUserByEmail(@RequestParam String email){
        Optional<UserDTO> userDTO =auditorService.findUserByEmail(email);
        return userDTO.map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/accounts")
    public ResponseEntity<AccountDTO> findAccountDetailsByAccountNumber(@RequestParam String accountNumber){
        Optional<AccountDTO> accountDTO=auditorService.findAccountDetailsByAccountNumber(accountNumber);
        return accountDTO.map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @GetMapping("/transactions/by-account")
    public ResponseEntity<List<TransactionDTO>>getTransactionByAccountNumber(@RequestParam String accountNumber){
        List<TransactionDTO> transactionDTOList=auditorService.findTransactionsByAccountNumber(accountNumber);

        if(transactionDTOList.isEmpty()){
            return ResponseEntity.notFound().build();
        }else {
            return ResponseEntity.ok(transactionDTOList);
        }
    }


    @GetMapping("/transactions/by-id")
    public ResponseEntity<TransactionDTO> getTransactionById(@RequestParam Long id){
        Optional<TransactionDTO> transactionDTO=auditorService.findTransactionById(id);
        return transactionDTO.map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }






}
