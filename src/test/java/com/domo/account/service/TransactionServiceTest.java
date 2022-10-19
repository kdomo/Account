package com.domo.account.service;

import com.domo.account.domain.Account;
import com.domo.account.domain.AccountUser;
import com.domo.account.domain.Transaction;
import com.domo.account.dto.TransactionDto;
import com.domo.account.exception.AccountException;
import com.domo.account.repository.AccountRepository;
import com.domo.account.repository.AccountUserRepository;
import com.domo.account.repository.TransactionRepository;
import com.domo.account.type.AccountStatus;
import com.domo.account.type.ErrorCode;
import com.domo.account.type.TransactionResultType;
import com.domo.account.type.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.domo.account.type.AccountStatus.*;
import static com.domo.account.type.AccountStatus.IN_USE;
import static com.domo.account.type.TransactionResultType.*;
import static com.domo.account.type.TransactionType.USE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("잔액 사용 성공")
    void successUseBalance() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("domo")
                .build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        Account account = Account.builder()
                .balance(10000L)
                .accountUser(user)
                .accountNumber("1000000012").build();
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionResultType(S)
                        .transactionType(USE)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnapshot(9000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        //when
        TransactionDto transactionDto = transactionService.useBalance(
                1L, "1000000000", 200L
        );
        //then
        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(200L, captor.getValue().getAmount());
        assertEquals(9800L, captor.getValue().getBalanceSnapshot());

        assertEquals(S, transactionDto.getTransactionResultType());
        assertEquals(USE, transactionDto.getTransactionType());
        assertEquals(9000L, transactionDto.getBalanceSnapshot());
        assertEquals(1000L, transactionDto.getAmount());
    }

    @Test
    @DisplayName("잔액 사용 실패 - 해당 유저 없음")
    void failUseBalance_UserNotFound() {
        //given

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1000000000", 10000L));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, accountException.getErrorCode());
    }

    @Test
    @DisplayName("잔액 사용 실패 - 해당 계좌 없음")
    void failUseBalance_AccountNotFound() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("domo")
                .build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1000000000", 10000L));


        //then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, accountException.getErrorCode());
    }

    @Test
    @DisplayName("잔액 사용 실패 - 계좌 소유주 다름")
    void failUseBalance_UserUnMatch() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("domo")
                .build();
        AccountUser otherUser = AccountUser.builder()
                .id(13L)
                .name("Pobi")
                .build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.ofNullable(Account.builder()
                        .accountUser(otherUser)
                        .build()));

        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1000000000", 10000L));

        //then
        assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH, accountException.getErrorCode());
    }

    @Test
    @DisplayName("잔액 사용 실패 - 이미 해지된 계좌")
    void failUseBalance_AlreadyUnregistered() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("domo")
                .build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.ofNullable(Account.builder()
                        .balance(0L)
                        .accountStatus(UNREGISTERED)
                        .accountUser(user)
                        .build()));

        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1000000000", 10000L));

        //then
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, accountException.getErrorCode());
    }

    @Test
    @DisplayName("잔액 사용 실패 - 계좌 잔액 부족")
    void failUseBalance_AmountExceedBalance() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("domo")
                .build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.ofNullable(Account.builder()
                        .balance(1000L)
                        .accountStatus(IN_USE)
                        .accountUser(user)
                        .build()));

        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1000000000", 10000L));

        //then
        verify(transactionRepository,times(0)).save(any());
        assertEquals(ErrorCode.AMOUNT_EXCEED_BALANCE, accountException.getErrorCode());
    }

    @Test
    @DisplayName("잔액 사용 실패 - 실패 트랜잭션 저장 성공")
    void saveFailedUseTransaction() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("domo")
                .build();

        // UnnecessaryStubbingException 발생
        // mockito-core 2.x 버전부터 Strictness(테스트코드의 엄격성)을 규정
        // 사용하지 않는 stub을 정의할 필요 없음
//        given(accountUserRepository.findById(anyLong()))
//                .willReturn(Optional.of(user));

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(USE)
                        .transactionResultType(S)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .amount(200L)
                        .balanceSnapshot(9000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        //when
        transactionService.saveFailedUseTransaction(
                "1000000000", 200L
        );
        //then
        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(200L, captor.getValue().getAmount());
        assertEquals(10000L, captor.getValue().getBalanceSnapshot());
        assertEquals(F, captor.getValue().getTransactionResultType());
    }



}