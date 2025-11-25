package com.said.dsbank.audit_dashboard.service;

import com.said.dsbank.acount.dtos.AccountDTO;
import com.said.dsbank.auth_users.dtos.UserDTO;
import com.said.dsbank.transaction.dtos.TransactionDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AuditorService {

    Map<String, Long> getSytemTotale();
    Optional<UserDTO> findUserByEmail(String email);
    Optional<AccountDTO> findAccountDetailsByAccountNumber(String accountNumber);
    List<TransactionDTO> findTransactionsByAccountNumber(String accountNumber);
    Optional<TransactionDTO> findTransactionById(Long transactionId);

}
