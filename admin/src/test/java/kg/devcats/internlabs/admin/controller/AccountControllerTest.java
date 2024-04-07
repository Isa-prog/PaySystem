package kg.devcats.internlabs.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.devcats.internlabs.admin.dto.response.AccountDTO;
import kg.devcats.internlabs.admin.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {
    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    AccountDTO accountDTO;
    String workerJson;
    @BeforeEach
    public void setup() throws Exception {
        accountDTO = new AccountDTO(1L,"996111222333", new BigDecimal(555), "Akmal", false, LocalDateTime.now());
        workerJson = objectMapper.writeValueAsString(accountDTO);
    }

    @Test
    @WithMockUser(authorities = "account.read")
    public void testGetAccountByRequisite() throws Exception {
        when(accountService.getAccountByRequisite("996111222333")).thenReturn(accountDTO);
        mockMvc.perform(get("/api/admin/accounts/{requisite}", "996111222333"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requisite").value("996111222333"))
                .andExpect(jsonPath("$.balance").value(555))
                .andExpect(jsonPath("$.fullName").value("Akmal"))
                .andExpect(jsonPath("$.isBlocked").value(false));
        verify(accountService, times(1)).getAccountByRequisite("996111222333");
    }

    @Test
    @WithMockUser(authorities = "account.create")
    public void testCreatingAccount() throws Exception  {
        when(accountService.createAccount(accountDTO)).thenReturn(accountDTO);
        mockMvc.perform(post("/api/admin/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(workerJson))
                .andExpect(status().isCreated());
        verify(accountService, times(1)).createAccount(accountDTO);
    }

    @Test
    @WithMockUser(authorities = "account.create")
    public void testCreatingBadAccount() throws Exception {
        when(accountService.createAccount(accountDTO)).thenReturn(null);
        mockMvc.perform(post("/api/admin/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(workerJson))
                .andExpect(status().isBadRequest());
        verify(accountService, times(1)).createAccount(accountDTO);
    }

    @Test
    @WithMockUser(authorities = "account.create")
    public void testUpdateAccount() throws Exception {
        when(accountService.updateAccount(accountDTO.requisite(), accountDTO)).thenReturn(accountDTO);
        mockMvc.perform(put("/api/admin/accounts/{requisite}", accountDTO.requisite())
                .contentType(MediaType.APPLICATION_JSON)
                .content(workerJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "account.create")
    public void testUpdateNotFoundAccount() throws Exception {
        when(accountService.updateAccount(accountDTO.requisite(), accountDTO)).thenReturn(null);
        mockMvc.perform(put("/api/admin/accounts/{requisite}", accountDTO.requisite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(workerJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "account.delete")
    public void testDeleteAccount() throws Exception {
        doNothing().when(accountService).delete(anyString());
        mockMvc.perform(delete("/api/admin/accounts/{requisite}", accountDTO.requisite()))
                .andExpect(status().isNoContent());
        verify(accountService, times(1)).delete(anyString());
    }

    private Page<AccountDTO> createAccountsPage() {
        return new PageImpl<>(Collections.singletonList(accountDTO));
    }
}
