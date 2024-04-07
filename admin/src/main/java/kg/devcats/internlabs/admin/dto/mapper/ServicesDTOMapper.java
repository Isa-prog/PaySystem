package kg.devcats.internlabs.admin.dto.mapper;

import kg.devcats.internlabs.admin.dto.response.ServicesDTO;
import kg.devcats.internlabs.core.entity.Services;
import org.springframework.stereotype.Service;

import java.util.function.Function;


@Service
public class ServicesDTOMapper implements Function<Services, ServicesDTO> {
    @Override
    public ServicesDTO apply(Services services) {

        return new ServicesDTO(
                services.getId(),
                services.getName(),
                services.getMin(),
                services.getMax(),
                services.getIsCancelable(),
                services.getLogo()
        );
    }

}
