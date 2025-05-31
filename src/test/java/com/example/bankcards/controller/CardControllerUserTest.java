package com.example.bankcards.controller;

import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.dto.card.TransferHistoryResponse;
import com.example.bankcards.dto.card.TransferRequest;
import com.example.bankcards.security.CustomUserServiceImpl;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.service.BlockRequestService;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardControllerUser.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerUserTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CardService cardService;

    @Autowired
    private BlockRequestService blockRequestService;


    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CardService cardService() {
            return Mockito.mock(CardService.class);
        }

        @Bean
        public BlockRequestService blockRequestService() {
            return Mockito.mock(BlockRequestService.class);
        }

        @Bean
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }

        @Bean
        public CustomUserServiceImpl customUserService() {
            return Mockito.mock(CustomUserServiceImpl.class);
        }
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMyCards_ShouldReturnCardsPage() throws Exception {
        Page<CardResponse> response = new PageImpl<>(List.of(new CardResponse(1L, "**** **** **** 1234", "Ivan Ivanov", LocalDate.now().plusYears(1), "ACTIVE", new BigDecimal("500"))));
        Mockito.when(cardService.getUserCards(any(), any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/cards/user"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCardById_ShouldReturnCard() throws Exception {
        CardResponse response = new CardResponse(1L, "**** **** **** 1234", "Ivan Ivanov", LocalDate.now().plusYears(1), "ACTIVE", new BigDecimal("500"));
        Mockito.when(cardService.getCardById(eq(1L), anyString())).thenReturn(response);

        mockMvc.perform(get("/api/cards/user/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getBalance_ShouldReturnCardBalance() throws Exception {
        Mockito.when(cardService.getCardBalance(eq(1L), anyString())).thenReturn(BigDecimal.valueOf(1000));

        mockMvc.perform(get("/api/cards/user/1/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void requestBlock_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/cards/user/1/block-request"))
                .andExpect(status().isOk());

        Mockito.verify(blockRequestService).createRequest(eq(1L), anyString());
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_ShouldReturnOk() throws Exception {
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("100"));

        mockMvc.perform(post("/api/cards/user/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(cardService).transferBetweenCards(any(TransferRequest.class), anyString());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllTransfer_ShouldReturnList() throws Exception {
        List<TransferHistoryResponse> transfers = List.of(new TransferHistoryResponse(1L, 1L, 2L, new BigDecimal(500), LocalDateTime.now()));
        Mockito.when(cardService.getAllTransferUser(anyString(), any())).thenReturn(ResponseEntity.ok(transfers));

        mockMvc.perform(get("/api/cards/user/transfer/all"))
                .andExpect(status().isOk());
    }
}
