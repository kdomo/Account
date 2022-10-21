package com.domo.account.dto;

import com.domo.account.aop.AccountLockIdInterface;
import com.domo.account.type.TransactionResultType;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class UseBalance {

    /**
     * {
     *     "userId" : 1,
     *     "accountNumber" : "1000000000",
     *     "amount" : 1000
     * }
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request implements AccountLockIdInterface {
        @NotNull
        @Min(1)
        private Long userId;


        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotNull
        @Min(10)
        @Max(1000_000_000)
        private Long amount;
    }

    /**
     * {
     *     "accountNumber": "1000000000",
     *     "transactionResultType": "S",
     *     "transactionId": "72f032218a24448da84a7eb7a127a977",
     *     "amount": 1000,
     *     "transactedAt": "2022-10-20T16:34:59.726587"
     * }
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String accountNumber;
        private TransactionResultType transactionResultType;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;

        public static UseBalance.Response from(TransactionDto transactionDto) {
            return Response.builder()
                    .accountNumber(transactionDto.getAccountNumber())
                    .transactionResultType(transactionDto.getTransactionResultType())
                    .transactionId(transactionDto.getTransactionId())
                    .amount(transactionDto.getAmount())
                    .transactedAt(transactionDto.getTransactedAt())
                    .build();
        }
    }
}
