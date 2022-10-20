package com.domo.account.dto;

import lombok.*;


/**
 * [
 *     {
 *         "balance": 100000,
 *         "accountNumber": "1000000000"
 *     },
 *     {
 *         "balance": 100000,
 *         "accountNumber": "1000000001"
 *     }
 * ]
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfo {
    private String AccountNumber;
    private Long balance;
}
