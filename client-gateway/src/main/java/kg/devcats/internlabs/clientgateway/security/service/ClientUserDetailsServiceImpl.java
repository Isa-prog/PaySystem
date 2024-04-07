package kg.devcats.internlabs.clientgateway.security.service;

import kg.devcats.internlabs.core.entity.Client;
import kg.devcats.internlabs.core.repository.ClientRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ClientUserDetailsServiceImpl implements UserDetailsService {

    private final ClientRepository clientRepository;

    public ClientUserDetailsServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<Client> optionalClient = clientRepository.findByLogin(login);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            return ClientUserDetailsImpl.build(client);
        } else {
                throw new UsernameNotFoundException("Client Not Found with login: " + login);
        }
    }
}

