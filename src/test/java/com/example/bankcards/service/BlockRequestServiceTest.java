package com.example.bankcards.service;

import com.example.bankcards.dto.card.BlockResponse;
import com.example.bankcards.entity.BlockRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BlockRequestNotFoundException;
import com.example.bankcards.exception.ForbiddenOperationException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.BlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlockRequestServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private BlockRequestRepository blockRequestRepository;

    @InjectMocks
    private BlockRequestService blockRequestService;

    private User user;
    private Card card;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.ACTIVE);
        card.setUser(user);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Создание запроса на блокировку — успешно")
    void createRequest_ShouldSaveRequest() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(blockRequestRepository.existsByUserAndCard(user, card)).thenReturn(false);

        blockRequestService.createRequest(1L, "user@example.com");

        verify(blockRequestRepository, times(1)).save(any(BlockRequest.class));
    }

    @Test
    @DisplayName("Создание запроса — карта не найдена")
    void createRequest_ShouldThrowIfCardNotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> blockRequestService.createRequest(1L, "user@example.com"));
    }

    @Test
    @DisplayName("Создание запроса — чужая карта")
    void createRequest_ShouldThrowIfNotCardOwner() {
        User anotherUser = new User();
        anotherUser.setEmail("someone@example.com");
        card.setUser(anotherUser);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(ForbiddenOperationException.class,
                () -> blockRequestService.createRequest(1L, "user@example.com"));
    }

    @Test
    @DisplayName("Создание запроса — карта уже заблокирована")
    void createRequest_ShouldThrowIfAlreadyBlocked() {
        card.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(ForbiddenOperationException.class,
                () -> blockRequestService.createRequest(1L, "user@example.com"));
    }

    @Test
    @DisplayName("Создание запроса — карта просрочена")
    void createRequest_ShouldThrowIfExpired() {
        card.setStatus(CardStatus.EXPIRED);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(ForbiddenOperationException.class,
                () -> blockRequestService.createRequest(1L, "user@example.com"));
    }

    @Test
    @DisplayName("Создание запроса — уже существует")
    void createRequest_ShouldThrowIfRequestExists() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(blockRequestRepository.existsByUserAndCard(user, card)).thenReturn(true);

        assertThrows(ForbiddenOperationException.class,
                () -> blockRequestService.createRequest(1L, "user@example.com"));
    }

    @Test
    @DisplayName("Получение всех необработанных запросов")
    void getAllPendingRequests_ShouldReturnList() {
        BlockRequest request = new BlockRequest();
        request.setId(1L);
        request.setUser(user);
        request.setCard(card);
        request.setRequestedAt(LocalDateTime.now());

        when(blockRequestRepository.findByProcessedFalse()).thenReturn(List.of(request));

        List<BlockResponse> responses = blockRequestService.getAllPendingRequests();

        assertEquals(1, responses.size());
        assertEquals(request.getId(), responses.get(0).getId());
    }

    @Test
    @DisplayName("Обработка запроса — успешно")
    void processBlockRequest_ShouldProcessAndBlockCard() {
        BlockRequest request = new BlockRequest();
        request.setId(1L);
        request.setCard(card);

        when(blockRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        blockRequestService.processBlockRequest(1L);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
        verify(blockRequestRepository).save(request);
    }

    @Test
    @DisplayName("Обработка запроса — не найден")
    void processBlockRequest_ShouldThrowIfNotFound() {
        when(blockRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BlockRequestNotFoundException.class,
                () -> blockRequestService.processBlockRequest(1L));
    }
}