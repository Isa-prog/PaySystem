package kg.devcats.internlabs.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import kg.devcats.internlabs.admin.dto.response.ClientDTO;
import kg.devcats.internlabs.admin.dto.response.PaymentDTO;
import kg.devcats.internlabs.admin.excel.ExcelHandler;
import kg.devcats.internlabs.admin.service.ClientService;
import kg.devcats.internlabs.admin.service.PaymentService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ExcelHandler excelHandler;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PaymentService paymentService;

    PaymentDTO paymentDTO;

    @Test
    @WithMockUser(authorities = "payment.read")
    public void testGetEmptyPaymentsPage() throws Exception {
        Page<PaymentDTO> mockedPage = Mockito.mock(Page.class);
        Mockito.when(mockedPage.isEmpty()).thenReturn(true);
        when(paymentService.getPayments(anyInt(), anyInt(), anyString(), anyString())).thenReturn(mockedPage);
        mockMvc.perform(get("/api/admin/payments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "payment.read")
    public void testGetNoExistPayment() throws Exception {
        when(paymentService.getPaymentById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/admin/payments/{id}", 1L))
                .andExpect(status().isNotFound());
        verify(paymentService, times(1)).getPaymentById(1L);
    }
}
