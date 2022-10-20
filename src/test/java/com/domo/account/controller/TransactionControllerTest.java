package com.domo.account.controller;

import com.domo.account.dto.CancelBalance;
import com.domo.account.dto.TransactionDto;
import com.domo.account.dto.UseBalance;
import com.domo.account.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.domo.account.type.TransactionResultType.S;
import static com.domo.account.type.TransactionType.USE;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("잔액 사용")
    void successUseBalance() throws Exception {
        //given
        given(transactionService.useBalance(anyLong(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                        .accountNumber("1000000000")
                        .transactedAt(LocalDateTime.now())
                        .amount(12345L)
                        .transactionId("transactionId")
                        .transactionResultType(S)
                        .build());

        //when
        //then
        mockMvc.perform(post("/transaction/use")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UseBalance.Request(1L, "2000000000", 10000L)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1000000000"))
                .andExpect(jsonPath("$.transactionResultType").value("S"))
                .andExpect(jsonPath("$.transactionId").value("transactionId"))
                .andExpect(jsonPath("$.amount").value(12345L))
                .andDo(print());

    }

    @Test
    @DisplayName("잔액 사용 취소")
    void successCancelBalance() throws Exception {
        //given
        given(transactionService.cancelBalance(anyString(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                        .accountNumber("1000000000")
                        .transactedAt(LocalDateTime.now())
                        .amount(54321L)
                        .transactionId("transactionIdForCancel")
                        .transactionResultType(S)
                        .build());

        //when
        //then
        mockMvc.perform(post("/transaction/cancel")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CancelBalance.Request("transactionId", "2000000000", 10000L)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1000000000"))
                .andExpect(jsonPath("$.transactionResultType").value("S"))
                .andExpect(jsonPath("$.transactionId").value("transactionIdForCancel"))
                .andExpect(jsonPath("$.amount").value(54321L))
                .andDo(print());

    }

    @Test
    @DisplayName("거래 조회 성공")
    void successGetQueryTransaction() throws Exception {
        //given
        given(transactionService.queryTransaction(anyString()))
                .willReturn(
                        TransactionDto.builder()
                                .accountNumber("1000000000")
                                .transactionType(USE)
                                .transactedAt(LocalDateTime.now())
                                .amount(54321L)
                                .transactionId("transactionId")
                                .transactionResultType(S)
                                .build()
                );

        //when
        //then
        mockMvc.perform(get("/transaction/1232145"))
                .andExpect(jsonPath("$.accountNumber").value("1000000000"))
                .andExpect(jsonPath("$.transactionType").value("USE"))
                .andExpect(jsonPath("$.transactionResultType").value("S"))
                .andExpect(jsonPath("$.transactionId").value("transactionId"))
                .andExpect(jsonPath("$.amount").value(54321L))
                .andDo(print());

    }
}