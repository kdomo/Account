package com.domo.account.dto;

import com.domo.account.domain.Account;
import com.domo.account.type.TransactionResultType;
import com.domo.account.type.TransactionType;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private String accountNumber;
    private TransactionType transactionType; // USE, CANCEL
    private TransactionResultType transactionResultType; // S(SUCCESS), F(FAIL)
    private Long amount;
    private Long balanceSnapshot;
    private String transactionId;
    private LocalDateTime transactedAt;
}
