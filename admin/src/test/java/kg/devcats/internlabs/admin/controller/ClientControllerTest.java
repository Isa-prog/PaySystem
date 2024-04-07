package kg.devcats.internlabs.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.devcats.internlabs.admin.dto.response.AccountDTO;
import kg.devcats.internlabs.admin.dto.response.ClientDTO;
import kg.devcats.internlabs.admin.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ClientService clientService;
    ClientDTO clientDTO;

    @BeforeEach
    public void setup() {
        clientDTO = new ClientDTO(1L,"Akmal","Akmal","qwert123", new BigDecimal(333), "Day", 1000L, "127.0.0.0.1");
    }

    @Test
    @WithMockUser(authorities = "client.read")
    public void testGetClientsPage() throws Exception {
        when(clientService.getClients(anyInt(), anyInt(), anyString(), anyString())).thenReturn(createClientsPage());
        mockMvc.perform(get("/api/admin/clients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    @WithMockUser(authorities = "client.read")
    public void testGetEmptyClientsPage() throws Exception {
        when(clientService.getClients(anyInt(), anyInt(), anyString(), anyString())).thenReturn(Page.empty());
        mockMvc.perform(get("/api/admin/clients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "client.read")
    public void testGetClient() throws Exception {
        when(clientService.getClientById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/admin/clients/{id}", 1L))
                .andExpect(status().isNotFound());
        verify(clientService, times(1)).getClientById(1L);
    }

    @Test
    @WithMockUser(authorities = "client.create")
    public void testCreateClient() throws Exception {
        String workerJson = objectMapper.writeValueAsString(clientDTO);
        mockMvc.perform(post("/api/admin/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(workerJson))
                .andExpect(status().isCreated());
        verify(clientService, times(1)).createClient(clientDTO);
    }

    @Test
    @WithMockUser(authorities = "client.create")
    public void testUpdateClient() throws Exception {
        String workerJson = objectMapper.writeValueAsString(clientDTO);
        when(clientService.updateClient(1L, clientDTO)).thenReturn(null);
        mockMvc.perform(put("/api/admin/clients/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(workerJson))
                .andExpect(status().is(not(200)));
        verify(clientService, times(1)).updateClient(1L, clientDTO);
    }
    @Test
    @WithMockUser(authorities = "client.delete")
    public void testDeleteClient() throws Exception {
        doNothing().when(clientService).delete(anyLong());
        mockMvc.perform(delete("/api/admin/clients/{id}", 1L))
                .andExpect(status().isNoContent());
        verify(clientService, times(1)).delete(1L);
    }

    private Page<ClientDTO> createClientsPage() {
        return new PageImpl<>(Collections.singletonList(clientDTO));
    }
}

