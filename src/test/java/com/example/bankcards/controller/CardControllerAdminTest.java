package com.example.bankcards.controller;

import com.example.bankcards.dto.card.*;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.security.CustomUserServiceImpl;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.service.BlockRequestService;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardControllerAdmin.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerAdminTest {

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
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Создание карты - 200 OK")
    void createCard_ShouldReturnCreatedCard() throws Exception {
        CreateCardRequest request = new CreateCardRequest(1L, new BigDecimal("500"), LocalDate.now().plusYears(1));
        CardResponse response = new CardResponse(1L, "**** **** **** 1234", "Ivan Ivanov", LocalDate.now().plusYears(1), "ACTIVE", new BigDecimal("500"));

        Mockito.when(cardService.createCard(any(CreateCardRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/cards/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.card_number").value("**** **** **** 1234"))
                .andExpect(jsonPath("$.owner").value("Ivan Ivanov"))
                .andExpect(jsonPath("$.balance").value(500))
                .andExpect(jsonPath("$.expirationDate").value(LocalDate.now().plusYears(1).toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Изменение статуса карты - 200 OK")
    void updateStatus_ShouldReturnOk() throws Exception {
        ChangeStatusRequest request = new ChangeStatusRequest(CardStatus.BLOCKED);

        mockMvc.perform(put("/api/cards/admin/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(cardService).changeStatus(eq(1L), any(ChangeStatusRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Удаление карты - 204 No Content")
    void deleteCard_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/cards/admin/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(cardService).delete(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Получение всех карт - 200 OK")
    void getAllCards_ShouldReturnPage() throws Exception {
        CardResponse card = new CardResponse(1L, "**** **** **** 1234", "Ivan Ivanov", LocalDate.now().plusYears(1),"ACTIVE" , new BigDecimal(500));
        Page<CardResponse> page = new PageImpl<>(List.of(card));

        Mockito.when(cardService.getAllCards(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/cards/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(card.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Получение заявок на блокировку - 200 OK")
    void getAllPending_ShouldReturnList() throws Exception {
        BlockResponse blockResponse = new BlockResponse(1L, 1L, 1L, LocalDateTime.now());
        List<BlockResponse> list = List.of(blockResponse);

        Mockito.when(blockRequestService.getAllPendingRequests()).thenReturn(list);

        mockMvc.perform(get("/api/cards/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(blockResponse.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Получение всей истории переводов - 200 OK")
    void getAllTransfer_ShouldReturnList() throws Exception {
        TransferHistoryResponse transfer = new TransferHistoryResponse(1L,1L,1L, new BigDecimal(500), LocalDateTime.now());
        List<TransferHistoryResponse> list = List.of(transfer);

        Mockito.when(cardService.getAllTransfer()).thenReturn(list);

        mockMvc.perform(get("/api/cards/admin/transfer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(transfer.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Получение истории переводов по ID пользователя - 200 OK")
    void getTransferByUserId_ShouldReturnList() throws Exception {
        TransferHistoryResponse transfer = new TransferHistoryResponse(1L,1L,1L, new BigDecimal(500), LocalDateTime.now());
        List<TransferHistoryResponse> list = List.of(transfer);

        Mockito.when(cardService.getTransferByUserId(1L)).thenReturn(list);

        mockMvc.perform(get("/api/cards/admin/1/transfer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(transfer.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Обработка запроса на блокировку - 200 OK")
    void processBlockRequest_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/cards/admin/1/process"))
                .andExpect(status().isOk());

        Mockito.verify(blockRequestService).processBlockRequest(1L);
    }
}