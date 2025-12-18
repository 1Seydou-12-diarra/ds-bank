package com.said.dsbank.card;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long accountId;

    private String cardNumber;

    private String cvv;

    private LocalDate expiryDate;

    private BigDecimal balanceLimit;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        ACTIVE, BLOCKED, EXPIRED
    }
}
