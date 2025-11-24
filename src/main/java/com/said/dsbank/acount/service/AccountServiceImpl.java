package com.said.dsbank.acount.service;

import com.said.dsbank.acount.dtos.AccountDTO;
import com.said.dsbank.acount.entity.Account;
import com.said.dsbank.acount.repo.AccountRepo;
import com.said.dsbank.auth_users.entity.User;
import com.said.dsbank.auth_users.service.UserService;
import com.said.dsbank.enums.AccountStatus;
import com.said.dsbank.enums.AccountType;
import com.said.dsbank.enums.Currency;
import com.said.dsbank.exceptions.BadRequestException;
import com.said.dsbank.exceptions.NotFoundException;
import com.said.dsbank.res.Response;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountServiceImpl  implements AccountService {

    private final AccountRepo accountRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;

    private final Random random = new Random();
    private final RestClient.Builder builder;


    @Override
    public Account createAccount(AccountType accountType, User user) {

        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountNumber(UUID.randomUUID().toString().substring(0, 15))
                .balance(BigDecimal.ZERO)
                .currency(Currency.USD)
                .accountStatus(AccountStatus.ACTIVE)
                .user(user)
                .build();

        return accountRepo.save(account); // ✅ obligatoire


    }

    @Override
    public Response<List<AccountDTO>> getMyAccounts() {
        User user =userService.getCurrentUser();

        List<AccountDTO> accounts =accountRepo.findByUserId(user.getId())
                .stream()
                .map(account -> modelMapper.map(account,AccountDTO.class))
                .toList();

        return Response.<List<AccountDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("user account fectched successfully")
                .data(accounts)
                .build();
    }

    @Override
    public Response<?> closeAccount(String accountNumber) {
        User user =userService.getCurrentUser();

        Account account= accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(()->new NotFoundException("account not found"));



        if (!user.getAccounts().contains(account)){

            throw new NotFoundException("account not found");
        }

        if(account.getBalance().compareTo(BigDecimal.ZERO)>0){

            throw new BadRequestException("account balance is greater than 0");

        }
       account.setAccountStatus(AccountStatus.CLOSED);
       account.setClosedAt(LocalDateTime.now());
       accountRepo.save(account);


        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message(" account closed successfully")
                .build();

    }
















    private String generateAccountNumber() {
        String accountNumber = "";
        do {
            accountNumber ="66" +(random.nextInt(90000000)+ 1000000000);

        } while (accountRepo.findByAccountNumber(accountNumber).isEmpty());

        log.info("account number generated{}",accountNumber);
         return  accountNumber;
    }


}
