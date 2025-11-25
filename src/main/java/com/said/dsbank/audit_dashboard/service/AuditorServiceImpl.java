package com.said.dsbank.audit_dashboard.service;


import com.said.dsbank.acount.dtos.AccountDTO;
import com.said.dsbank.acount.repo.AccountRepo;
import com.said.dsbank.auth_users.dtos.UserDTO;
import com.said.dsbank.auth_users.repo.UserRepo;
import com.said.dsbank.res.Response;
import com.said.dsbank.transaction.dtos.TransactionDTO;
import com.said.dsbank.transaction.dtos.TransactionRequest;
import com.said.dsbank.transaction.repo.TransactionRepo;
import com.said.dsbank.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuditorServiceImpl implements AuditorService {

    private final UserRepo userRepo;
    private final AccountRepo accountRepo;
    private  final TransactionRepo transactionRepo;
    private final ModelMapper modelMapper;



    @Override
    public Map<String, Long> getSytemTotale() {
        long totalUsers = userRepo.count();
        long totalAccounts = accountRepo.count();
        long totalTransactions = transactionRepo.count();
        return Map.of(
                "totalUsers",totalUsers,
                "totalAccounts",totalAccounts,
                "totalTransactions",totalTransactions
        );
    }

    @Override
    public Optional<UserDTO> findUserByEmail(String email) {

        return userRepo.findByEmail(email)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    public Optional<AccountDTO> findAccountDetailsByAccountNumber(String accountNumber) {

        return accountRepo.findByAccountNumber(accountNumber)
                .map(account -> modelMapper.map(account, AccountDTO.class));
    }

    @Override
    public List<TransactionDTO> findTransactionsByAccountNumber(String accountNumber) {
        return transactionRepo.findByAccount_AccountNumber(accountNumber).stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                 .collect(Collectors.toList());

    }

    @Override
    public Optional<TransactionDTO> findTransactionById(Long transactionId) {
        return transactionRepo.findById(transactionId)
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class));    }
}
