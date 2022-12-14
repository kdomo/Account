package com.domo.account.domain;

import com.domo.account.type.TransactionResultType;
import com.domo.account.type.TransactionType;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Transaction extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 6507099094905766398L;

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // USE, CANCEL

    @Enumerated(EnumType.STRING)
    private TransactionResultType transactionResultType; // S(SUCCESS), F(FAIL)

    @ManyToOne
    private Account account;

    private Long amount;

    private Long balanceSnapshot;

    private String transactionId;

    private LocalDateTime transactedAt;

}
