package kg.devcats.internlabs.admin.dto.mapper;

import kg.devcats.internlabs.admin.dto.response.ClientDTO;
import kg.devcats.internlabs.admin.dto.response.LimitDTO;
import kg.devcats.internlabs.core.entity.Client;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ClientDTOMapper implements Function<Client, ClientDTO> {

    private final LimitDTOMapper limitDTOMapper;

    public ClientDTOMapper(LimitDTOMapper limitDTOMapper) {
        this.limitDTOMapper = limitDTOMapper;
    }

    @Override
    public ClientDTO apply(Client client) {
        LimitDTO limitDTO = limitDTOMapper.apply(client.getLimit());

        return new ClientDTO(
                client.getId(),
                client.getFullName(),
                client.getLogin(),
                client.getPassword(),
                client.getBalance(),
                client.getLimit().getPeriod().getName(),
                client.getLimit().getAmount(),
                client.getIpAddresses()
                );
    }
}
