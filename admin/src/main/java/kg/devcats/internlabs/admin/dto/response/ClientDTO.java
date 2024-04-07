package kg.devcats.internlabs.admin.dto.response;

import java.math.BigDecimal;

public record ClientDTO(
        Long id,
        String fullName,
        String login,
        String password,
        BigDecimal balance,
        String period,
        Long amount,
        String ipAddresses
) {
}
