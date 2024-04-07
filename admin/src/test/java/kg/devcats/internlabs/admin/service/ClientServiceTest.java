package kg.devcats.internlabs.admin.service;

import kg.devcats.internlabs.admin.dto.mapper.ClientDTOMapper;
import kg.devcats.internlabs.admin.dto.response.ClientDTO;
import kg.devcats.internlabs.core.entity.Client;
import kg.devcats.internlabs.core.entity.User;
import kg.devcats.internlabs.core.repository.ClientRepository;
import kg.devcats.internlabs.core.repository.LimitPeriodRepository;
import kg.devcats.internlabs.core.repository.LimitRepository;
import kg.devcats.internlabs.core.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private LimitPeriodRepository limitPeriodRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private LimitRepository limitRepository;

    @InjectMocks
    private ClientService clientService;
    @Mock
    private ClientDTOMapper clientDTOMapper;

    @Test
    public void testGetClients() {
        int pageNumber = 0;
        int pageSize = 5;
        String sortField = "fullName";
        String sortOrder = "asc";
        List<Client> clients = new ArrayList<>();
        clients.add(new Client("Akmal", "Akmal", "Akmal", BigDecimal.valueOf(1000), "127.0.0.0.1", null, null));
        clients.add(new Client("Erkin", "Erkin", "Erkin", BigDecimal.valueOf(2000), "127.0.0.0.1", null, null));
        Page<Client> clientsPage = new PageImpl<>(clients);
        when(clientRepository.findAll(any(PageRequest.class))).thenReturn(clientsPage);
        Page<ClientDTO> resultPage = clientService.getClients(pageNumber, pageSize, sortField, sortOrder);
        verify(clientRepository, times(1)).findAll(PageRequest.of(pageNumber, pageSize, Sort.by(sortField).ascending()));
        assertEquals(clients.size(), resultPage.getContent().size());
    }

    @Test
    public void testFindByFilters() {
        String fullNamePrefix = "Ak";
        String loginPrefix = "akm";
        BigDecimal minBalance = BigDecimal.valueOf(1000);
        BigDecimal maxBalance = BigDecimal.valueOf(2000);
        String period = "2024-03";
        Long amount = 100L;
        PageRequest pageable = PageRequest.of(0, 10);
        List<Client> clients = new ArrayList<>();
        clients.add(new Client("Akmal", "Akmal", "Akmal", BigDecimal.valueOf(1500), "127.0.0.0.1", null, null));
        clients.add(new Client("Akmal", "Akmal", "Akmal", BigDecimal.valueOf(1800), "127.0.0.0.2", null, null));
        Page<Client> clientsPage = new PageImpl<>(clients);
        when(clientRepository.findByFilters(fullNamePrefix, loginPrefix, minBalance, maxBalance, period, amount, pageable)).thenReturn(clientsPage);
        Page<ClientDTO> resultPage = clientService.findByFilters(fullNamePrefix, loginPrefix, minBalance, maxBalance, period, amount, pageable);
        verify(clientRepository, times(1)).findByFilters(fullNamePrefix, loginPrefix, minBalance, maxBalance, period, amount, pageable);
        assertEquals(clients.size(), resultPage.getContent().size());
    }

    @Test
    public void testUpdateNotFoundClient() {
        ClientDTO clientDTO = new ClientDTO(1L,"Akmal","Akmal","qwert123", new BigDecimal(333), "Day", 1000L, "127.0.0.0.1");
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
        Client result = clientService.updateClient(1L, clientDTO);
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteClient() {
        Client client = new Client();
        client.setId(1L);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        clientService.delete(1L);
        verify(clientRepository).delete(client);
    }

    @Test
    public void testDelete() {
        Long clientId = 1L;
        Client client= new Client("Akmal","Akmal","qwert123", BigDecimal.valueOf(1000), "127.0.0.0.1", null, null);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        clientService.delete(clientId);
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    public void testDeleteNonExistClient() {
        Long nonExistClientId = 1L;
        when(clientRepository.findById(nonExistClientId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> clientService.delete(nonExistClientId));
        verify(clientRepository, never()).delete(any());
    }
}