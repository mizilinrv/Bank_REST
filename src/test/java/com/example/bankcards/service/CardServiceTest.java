package com.example.bankcards.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.TransferHistoryResponse;
import com.example.bankcards.dto.card.TransferRequest;
import com.example.bankcards.entity.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferHistoryRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardEncryptor;
import com.example.bankcards.util.CardNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardEncryptor cardEncryptor;

    @Mock
    private CardNumberGenerator cardNumberGenerator;

    @Mock
    private TransferHistoryRepository transferHistoryRepository;

    @InjectMocks
    private CardService cardService;

    private User user;
    private Card card;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setFullName("Test User");
        user.setRole(RoleType.ROLE_USER);

        card = new Card();
        card.setId(1L);
        card.setUser(user);
        card.setEncryptedNumber("encrypted");
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.valueOf(100));
    }

    @Test
    void createCard_success() {
        CreateCardRequest request = new CreateCardRequest(1L, BigDecimal.TEN,LocalDate.now().plusYears(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardNumberGenerator.generate()).thenReturn("1234567890123456");
        when(cardEncryptor.encrypt(anyString())).thenReturn("encrypted");
        when(cardRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        CardResponse response = cardService.createCard(request);

        assertEquals("Test User", response.getOwnerFullName());
        assertTrue(response.getMaskedCardNumber().endsWith("3456"));
    }

    @Test
    void getCardBalance_success() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        BigDecimal balance = cardService.getCardBalance(1L, "user@example.com");

        assertEquals(BigDecimal.valueOf(100), balance);
    }

    @Test
    void transferBetweenCards_success() {
        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setUser(user);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setBalance(BigDecimal.valueOf(50));

        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.valueOf(30));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        cardService.transferBetweenCards(request, "user@example.com");

        verify(cardRepository, times(2)).save(any(Card.class));
        verify(transferHistoryRepository).save(any(TransferHistory.class));

        assertEquals(BigDecimal.valueOf(70), card.getBalance());
        assertEquals(BigDecimal.valueOf(80), toCard.getBalance());
    }

    @Test
    void getCardById_success() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        CardResponse response = cardService.getCardById(1L, "user@example.com");

        assertEquals("Test User", response.getOwnerFullName());
    }

    @Test
    void getAllTransferUser_empty() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(cardRepository.findByUser(eq(user), any())).thenReturn(Page.empty());

        ResponseEntity<List<TransferHistoryResponse>> response = cardService.getAllTransferUser("user@example.com", PageRequest.of(0, 10));

        assertNotNull(response);
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getUserCards_withStatus_success() {
        CardStatus status = CardStatus.ACTIVE;
        Page<Card> cardPage = new PageImpl<>(List.of(card));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(cardRepository.findByUserAndStatus(user, status, PageRequest.of(0, 10))).thenReturn(cardPage);

        Page<CardResponse> result = cardService.getUserCards("user@example.com", Optional.of("active"), PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }
}