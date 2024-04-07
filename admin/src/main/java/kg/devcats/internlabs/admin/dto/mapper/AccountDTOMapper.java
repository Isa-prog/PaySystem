package kg.devcats.internlabs.admin.dto.mapper;

import kg.devcats.internlabs.admin.dto.response.AccountDTO;
import kg.devcats.internlabs.core.entity.Account;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AccountDTOMapper implements Function<Account, AccountDTO> {
    @Override
    public AccountDTO apply(Account account) {
        return new AccountDTO(
                account.getId(),
                account.getRequisite(),
                account.getBalance(),
                account.getFullName(),
                account.isBlocked(),
                account.getDeletedAt()
        );
    }
}
