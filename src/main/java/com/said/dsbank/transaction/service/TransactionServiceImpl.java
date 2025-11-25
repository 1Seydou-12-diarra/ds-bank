package com.said.dsbank.transaction.service;

import com.said.dsbank.acount.entity.Account;
import com.said.dsbank.acount.repo.AccountRepo;
import com.said.dsbank.auth_users.entity.User;
import com.said.dsbank.auth_users.service.UserService;
import com.said.dsbank.enums.TransactionStatus;
import com.said.dsbank.enums.TransactionType;
import com.said.dsbank.exceptions.BadRequestException;
import com.said.dsbank.exceptions.InsufficientBalanceException;
import com.said.dsbank.exceptions.NotFoundException;
import com.said.dsbank.notification.dtos.NotificationDTO;
import com.said.dsbank.notification.service.NotificationService;
import com.said.dsbank.res.Response;
import com.said.dsbank.transaction.dtos.TransactionDTO;
import com.said.dsbank.transaction.dtos.TransactionRequest;
import com.said.dsbank.transaction.entity.Transaction;
import com.said.dsbank.transaction.repo.TransactionRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2

public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;
    private final NotificationService notificationService;
    private final UserService userService;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public Response<?> createTransaction(TransactionRequest transactionRequest) {

        Transaction transaction = new Transaction();

        transaction.setTransactionType(transactionRequest.getTransactionType());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setDescription(transactionRequest.getDescription());


        switch (transactionRequest.getTransactionType()) {
            case DEPOSIT-> handleDeposit(transactionRequest,transaction);
            case WITHDRAWAL -> handleWithdrawal(transactionRequest,transaction);
            case TRANSFER -> handleTransfer(transactionRequest,transaction);
            default -> throw new IllegalArgumentException("Invalid transaction type");
        }

        transaction.setStatus(TransactionStatus.SUCCESS);
        Transaction savedTxn = transactionRepo.save(transaction);

        //send notification out
        sendTransactionNotification(savedTxn);

        return  Response.builder()
                .statusCode(200)
                .message("Transaction successful")
                .build();



    }

    @Override
    @Transactional
    public Response<List<TransactionDTO>> getTransactionsForAnAccount(String accountNumber, int page, int size) {
        User user = userService.getCurrentUser();
        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        if(!account.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Account does not belong to this account");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> txns = transactionRepo.findByAccount_AccountNumber(accountNumber,pageable);
        List<TransactionDTO> transactionDTOS = txns.getContent().stream()
                .map(transaction -> modelMapper.map(transaction,TransactionDTO.class))
                .toList();

        return Response.<List<TransactionDTO>>builder()
                .statusCode(200)
                .message("Transactions found")
                .data(transactionDTOS)
                .meta(Map.of("currentPage", txns.getNumber(),
                                "totalItems",txns.getTotalElements(),
                                 "totalPage", txns.getTotalPages(),
                                 "pageSize", txns.getSize()))

                .build();
    }







    private void handleDeposit(TransactionRequest request, Transaction transaction) {

        Account account = accountRepo.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("account not found"));

        account.setBalance(account.getBalance().add(request.getAmount()));
        transaction.setAccount(account);
        accountRepo.save(account);
    }


    private void handleWithdrawal(TransactionRequest request, Transaction transaction) {

        Account account = accountRepo.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("account not found"));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {

            throw new InsufficientBalanceException("Insufficient balance");
        }
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        transaction.setAccount(account);
        accountRepo.save(account);
    }

    private void handleTransfer(TransactionRequest request, Transaction transaction) {

        Account sourceAccount = accountRepo.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("account not found"));

        Account destination = accountRepo.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() -> new NotFoundException("Destination account not found"));

        // Vérification du solde
        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in source account");
        }

        // Retirer du compte source
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        accountRepo.save(sourceAccount);

        // Ajouter au compte destination
        destination.setBalance(destination.getBalance().add(request.getAmount())); // CORRECTION
        accountRepo.save(destination);

        transaction.setAccount(sourceAccount);
        transaction.setSourceAccount(sourceAccount.getAccountNumber());
        transaction.setDestinationAccount(destination.getAccountNumber());
    }

    private void sendTransactionNotification(Transaction tnx) {

        User user = tnx.getAccount().getUser();
        String subject;
        String templateName ="";

        Map<String,Object> templateVariables = new HashMap<>();
    templateVariables.put("name",user.getFirstName());
    templateVariables.put("amount",tnx.getAmount());
    templateVariables.put("accountNumber",tnx.getAccount().getAccountNumber());
    templateVariables.put("date",tnx.getTransactionDate());
    templateVariables.put("balance",tnx.getAccount().getBalance());
    if (tnx.getTransactionType() == TransactionType.DEPOSIT) {

        subject =" Credit alert";
        subject= "credit-alert";

        NotificationDTO notificationEmailToSendOut = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject(subject)
                .templateName(templateName)
                .templateVariables(templateVariables) // Map<String, Object>
                .build();

        notificationService.sendEmail(notificationEmailToSendOut,user);
    }else if (tnx.getTransactionType() == TransactionType.WITHDRAWAL) {

        subject =" Credit alert";
        subject= "debit-alert";

        NotificationDTO notificationEmailToSendOut = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject(subject)
                .templateName(templateName)
                .templateVariables(templateVariables) // Map<String, Object>
                .build();

        notificationService.sendEmail(notificationEmailToSendOut,user);

    }else if (tnx.getTransactionType() == TransactionType.TRANSFER) {
        subject =" Credit alert";
        subject= "transfer-alert";


        NotificationDTO notificationEmailToSendOut = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject(subject)
                .templateName(templateName)
                .templateVariables(templateVariables) // Map<String, Object>
                .build();

        notificationService.sendEmail(notificationEmailToSendOut,user);

        //receiver CREDIT ALERT

        Account destination = accountRepo.findByAccountNumber(tnx.getDestinationAccount())
                .orElseThrow(() -> new NotFoundException("Destination account not found"));

        User receiver = destination.getUser();

        Map<String,Object> receiverVars = new HashMap<>();
        receiverVars.put("name",user.getFirstName());
        receiverVars.put("amount",tnx.getAmount());
        receiverVars.put("accountNumber",tnx.getAccount().getAccountNumber());
        receiverVars.put("date",tnx.getTransactionDate());
        receiverVars.put("balance",tnx.getAccount().getBalance());


        NotificationDTO notificationEmailToSendOutToReceiver = NotificationDTO.builder()
                .recipient(receiver.getEmail())
                .subject("Credit-Alert")
                .templateName("credit-alert")
                .templateVariables(receiverVars) // Map<String, Object>
                .build();

        notificationService.sendEmail(notificationEmailToSendOut,user);

    }

}

}
