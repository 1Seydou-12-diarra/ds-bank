package com.said.dsbank.acount.service;


import com.said.dsbank.acount.dtos.AccountDTO;
import com.said.dsbank.acount.entity.Account;
import com.said.dsbank.auth_users.dtos.UserDTO;
import com.said.dsbank.auth_users.entity.User;
import com.said.dsbank.enums.AccountType;
import com.said.dsbank.res.Response;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AccountService {

    Account createAccount(AccountType accountType, User user);

    Response<List<AccountDTO>> getMyAccounts();

    Response<?> closeAccount(String accountNumber);
}
