package kg.devcats.internlabs.admin.dto.mapper;

import kg.devcats.internlabs.admin.dto.response.AccountDTO;
import kg.devcats.internlabs.admin.dto.response.ClientDTO;
import kg.devcats.internlabs.admin.dto.response.PaymentDTO;
import kg.devcats.internlabs.admin.dto.response.ServicesDTO;
import kg.devcats.internlabs.core.entity.Payment;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PaymentDTOMapper implements Function<Payment, PaymentDTO> {
    private final AccountDTOMapper accountDTOMapper;
    private final ClientDTOMapper clientDTOMapper;
    private final ServicesDTOMapper servicesDTOMapper;

    public PaymentDTOMapper(AccountDTOMapper accountDTOMapper, ClientDTOMapper clientDTOMapper, ServicesDTOMapper servicesDTOMapper) {
        this.accountDTOMapper = accountDTOMapper;
        this.clientDTOMapper = clientDTOMapper;
        this.servicesDTOMapper = servicesDTOMapper;
    }

    @Override
    public PaymentDTO apply(Payment payment) {
        AccountDTO accountDTO = accountDTOMapper.apply(payment.getAccount());
        ClientDTO clientDTO = clientDTOMapper.apply(payment.getClient());
        ServicesDTO servicesDTO = servicesDTOMapper.apply(payment.getService());

        return new PaymentDTO(
                payment.getId(),
                payment.getAmount(),
                payment.getCreatedAt(),
                payment.getPaidAt(),
                accountDTO,
                servicesDTO,
                clientDTO,
                payment.getPaymentStatus()
        );
    }
}
