package com.said.dsbank.acount.entity;

import com.said.dsbank.auth_users.entity.User;
import com.said.dsbank.enums.AccountStatus;
import com.said.dsbank.enums.AccountType;
import com.said.dsbank.enums.Currency;
import jakarta.persistence.*;
import jakarta.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity

@Table(name ="account")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 15)
    private String accountNumber;

    @Column(nullable = false, precision = 19, length = 15)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable =false)
    private User users;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus;

    @OneToOne(mappedBy ="account",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY )
    private List<Transaction> transactions = new ArrayList<>();

    private LocalDateTime closedAt;
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;






}


