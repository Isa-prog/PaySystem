package kg.devcats.internlabs.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.devcats.internlabs.admin.dto.response.ServicesDTO;
import kg.devcats.internlabs.admin.service.ServicesService;
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
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ServiceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ServicesService servicesService;

    ServicesDTO servicesDTO;
    @BeforeEach
    public void setup() {
        servicesDTO = new ServicesDTO(1L, "O!", BigDecimal.ONE, BigDecimal.TEN, true, "Logo.png");
    }

    @Test
    @WithMockUser(authorities = "services.read")
    public void testGetServicesPage() throws Exception {
        when(servicesService.getServices(anyInt(), anyInt(), anyString(), anyString())).thenReturn(createServicesPage());
        mockMvc.perform(get("/api/admin/services")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    @WithMockUser(authorities = "services.read")
    public void testGetEmptyServicesPage() throws Exception {
        when(servicesService.getServices(anyInt(), anyInt(), anyString(), anyString())).thenReturn(Page.empty());
        mockMvc.perform(get("/api/admin/services")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "services.read")
    public void testGetNotFoundService() throws Exception {
        when(servicesService.getServiceById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/admin/services/{id}", 1L))
                .andExpect(status().isNotFound());
        verify(servicesService, times(1)).getServiceById(1L);
    }

    @Test
    @WithMockUser(authorities = "services.read")
    public void testGetService() throws Exception {
        when(servicesService.getServiceById(anyLong())).thenReturn(Optional.of(servicesDTO));
        mockMvc.perform(get("/api/admin/services/{id}", 1L))
                .andExpect(status().isOk());
        verify(servicesService, times(1)).getServiceById(1L);
    }

    @Test
    @WithMockUser(authorities = "services.delete")
    public void testDeleteClient() throws Exception {
        doNothing().when(servicesService).deleteService(anyLong());
        mockMvc.perform(delete("/api/admin/services/{id}", 1L))
                .andExpect(status().isNoContent());
        verify(servicesService, times(1)).deleteService(1L);
    }

    private Page<ServicesDTO> createServicesPage() {
        return new PageImpl<>(Collections.singletonList(servicesDTO));
    }
}
