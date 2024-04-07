package kg.devcats.internlabs.admin.service;

import kg.devcats.internlabs.admin.dto.mapper.PaymentDTOMapper;
import kg.devcats.internlabs.core.dto.response.PutRollbackResult;
import kg.devcats.internlabs.core.entity.Payment;
import kg.devcats.internlabs.core.repository.PaymentRepository;
import kg.devcats.internlabs.admin.dto.response.PaymentDTO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentDTOMapper paymentDTOMapper;

    @Mock
    private kg.devcats.internlabs.core.service.PaymentService paymentServiceCore;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    public void testGetPayments() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "paymentDate";
        String sortOrder = "asc";
        List<Payment> payments = new ArrayList<>();
        payments.add(new Payment(BigDecimal.ONE, LocalDateTime.now(), null, null, null, null, 10L));
        payments.add(new Payment(BigDecimal.TEN, LocalDateTime.now(), null, null, null, null, 10L));
        Page<Payment> paymentsPage = new PageImpl<>(payments);
        when(paymentRepository.findAll(any(PageRequest.class))).thenReturn(paymentsPage);
        PaymentDTO paymentDTO1 = new PaymentDTO(1L,BigDecimal.ONE, LocalDateTime.now(), null, null, null, null, null);
        PaymentDTO paymentDTO2 = new PaymentDTO(2L,BigDecimal.TEN, LocalDateTime.now(), null, null, null, null, null);
        List<PaymentDTO> expectedPaymentDTOList = Arrays.asList(paymentDTO1, paymentDTO2);
        when(paymentDTOMapper.apply(payments.get(0))).thenReturn(paymentDTO1);
        when(paymentDTOMapper.apply(payments.get(1))).thenReturn(paymentDTO2);
        Page<PaymentDTO> resultPage = paymentService.getPayments(pageNumber, pageSize, sortField, sortOrder);
        verify(paymentRepository, times(1)).findAll(PageRequest.of(pageNumber, pageSize, Sort.by(sortField).ascending()));
        assertEquals(expectedPaymentDTOList.size(), resultPage.getContent().size());
    }

    @Test
    public void testGetPaymentById_WhenPaymentExists() {
        Long paymentId = 1L;
        Payment payment = new Payment(BigDecimal.ONE, LocalDateTime.now(), null, null, null, null, 10L);
        PaymentDTO expectedPaymentDTO = new PaymentDTO(1L,BigDecimal.ONE, LocalDateTime.now(), null, null, null, null, null);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentDTOMapper.apply(payment)).thenReturn(expectedPaymentDTO);
        Optional<PaymentDTO> result = paymentService.getPaymentById(paymentId);
        assertTrue(result.isPresent());
        assertEquals(expectedPaymentDTO, result.get());
    }

    @Test
    public void testGetPaymentById_WhenPaymentDoesNotExist() {
        Long paymentId = 1L;
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());
        Optional<PaymentDTO> result = paymentService.getPaymentById(paymentId);
        assertFalse(result.isPresent());
    }

    @Test
    public void testPutRollbackById() {
        Long paymentId = 1L;
        boolean isLimited = false;
        PutRollbackResult expectedResult = new PutRollbackResult(null);
        when(paymentServiceCore.validateRollback(paymentId, false, null, null)).thenReturn(expectedResult);
        PutRollbackResult result = paymentService.putRollbackById(paymentId, isLimited);
        assertEquals(expectedResult, result);
        verify(paymentServiceCore, times(1)).validateRollback(paymentId, false, null, null);
    }
}