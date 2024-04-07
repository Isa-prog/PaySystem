package kg.devcats.internlabs.admin.service;

import kg.devcats.internlabs.admin.dto.mapper.ClientDTOMapper;
import kg.devcats.internlabs.admin.dto.response.ClientDTO;
import kg.devcats.internlabs.core.entity.*;
import kg.devcats.internlabs.core.repository.ClientRepository;
import kg.devcats.internlabs.core.repository.LimitPeriodRepository;
import kg.devcats.internlabs.core.repository.LimitRepository;
import kg.devcats.internlabs.core.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final ClientDTOMapper clientDTOMapper;
    private final LimitPeriodRepository limitPeriodRepository;
    private final LimitRepository limitRepository;


    public Page<ClientDTO> getClients(int pageNumber, int pageSize, String sortField, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        Page<Client> clientsPage = clientRepository.findAll(pageRequest);
        return clientsPage.map(client -> clientDTOMapper.apply(client));
    }

    public Page<ClientDTO> findByFilters(String fullNamePrefix, String loginPrefix, BigDecimal minBalance, BigDecimal maxBalance, String period, Long amount, PageRequest pageable) {
        Page<Client> clients = clientRepository.findByFilters(fullNamePrefix, loginPrefix, minBalance, maxBalance, period, amount, pageable);
        return clients.map(client -> clientDTOMapper.apply(client));
    }

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(clientDTOMapper)
                .collect(Collectors.toList());
    }

    public Optional<ClientDTO> getClientById(Long id) {
        return clientRepository.findById(id).map(clientDTOMapper);
    }

    @Transactional
    public Client createClient(ClientDTO clientDTO) {
        Client client = convertToEntity(clientDTO, new Client());
        boolean existsByLogin = clientRepository.existsByLogin(client.getLogin());
        if(existsByLogin){
            throw new DataIntegrityViolationException("Client with login '" + client.getLogin() + "' already exists.");
        }
        return clientRepository.save(client);
    }

    @Transactional
    public Client updateClient(Long id, ClientDTO clientDTO) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        if (clientOptional.isPresent()) {
            Client client = convertToEntity(clientDTO, clientOptional.get());
            return clientRepository.save(client);
        }
        return null;
    }

    public void delete(Long id) {
        clientRepository.delete(clientRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Client not found")));
    }


    private Client convertToEntity(ClientDTO clientDTO, Client client) {
        Optional<LimitPeriod> period = limitPeriodRepository.findById(clientDTO.period());
        Optional<Role> role = roleRepository.findByName(ERole.ROLE_CLIENT);
        Optional<Limit> limitOptional = limitRepository.findByPeriodAndAmount(period.get(), clientDTO.amount());
        Limit limit = null;
        if (limitOptional.isPresent()) {
            limit = limitOptional.get();
        } else {
            limit = new Limit(period.get(), clientDTO.amount());
            limitRepository.save(limit);
        }
        client.setFullName(clientDTO.fullName());
        client.setLogin(clientDTO.login());
        if (clientDTO.password() != null){
            client.setPassword(encoder.encode(clientDTO.password()));
        }
        client.setBalance(clientDTO.balance());
        client.setRole(role.get());
        client.setLimit(limit);
        client.setIpAddresses(clientDTO.ipAddresses());

        return client;
    }
}