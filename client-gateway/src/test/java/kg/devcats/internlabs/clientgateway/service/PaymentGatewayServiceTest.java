package kg.devcats.internlabs.clientgateway.service;

import kg.devcats.internlabs.clientgateway.dto.Status;
import kg.devcats.internlabs.clientgateway.dto.response.PaymentFromToResponse;
import kg.devcats.internlabs.clientgateway.dto.response.RequisiteResponse;
import kg.devcats.internlabs.clientgateway.service.PaymentGatewayService;
import kg.devcats.internlabs.core.dto.response.GetFromToResult;
import kg.devcats.internlabs.core.dto.response.GetRequisiteResult;
import kg.devcats.internlabs.core.repository.PaymentLogRepository;
import kg.devcats.internlabs.core.repository.PaymentRepository;
import kg.devcats.internlabs.core.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;

import static kg.devcats.internlabs.clientgateway.dto.Status.PAYMENTS_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PaymentGatewayServiceTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentLogRepository paymentLogRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private PaymentGatewayService paymentGatewayService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRequisite_Success() {
        GetRequisiteResult mockResult = new GetRequisiteResult(GetRequisiteResult.Status.SUCCESS, "Name", null);
        when(paymentService.validateRequisite(anyString(), anyString(), anyLong(), anyString()))
                .thenReturn(mockResult);
        RequisiteResponse response = paymentGatewayService.getRequisite("Requisite", "ClientLogin", 1L, "IP");
        assertNotNull(response);
        assertEquals(Status.REQUISITE_FOUND, response.getStatus());
        assertTrue(response.isRequisiteFound());
        assertEquals("Name", response.getName());
    }


    @Test
    void getPaymentsFromTo_Success() {
        LocalDateTime now = LocalDateTime.now();
        GetFromToResult mockResult = new GetFromToResult(GetFromToResult.Status.SUCCESS, "Test Name", true); // Убедитесь, что статус правильно устанавливается здесь
        when(paymentService.validateFromTo(any(LocalDateTime.class), any(LocalDateTime.class), anyString(), anyString()))
                .thenReturn(mockResult);

        PaymentFromToResponse response = paymentGatewayService.getPaymentsFromTo("2024-01-01", "2024-12-31", "client", "127.0.0.1");

        assertEquals(PAYMENTS_FOUND, response.getStatus());
        assertEquals("Success", response.getMessage());
        assertEquals("Test Name", response.getPayments());
        assertEquals(true, response.isPaymentsFound());
    }




    @Test
    void getRequisite_AccessDeniedException() {
        when(paymentService.validateRequisite(anyString(), anyString(), anyLong(), anyString()))
                .thenReturn(new GetRequisiteResult(GetRequisiteResult.Status.ERROR, GetRequisiteResult.DetailError.IP_ADDRESS_FORBIDDEN, "Error", false));

        assertThrows(AccessDeniedException.class, () -> paymentGatewayService.getRequisite("Requisite", "ClientLogin", 1L, "IP"));
    }
}

