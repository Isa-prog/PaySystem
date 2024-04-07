package kg.devcats.internlabs.admin.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Builder
public record AccountDTO(
        Long id,
        String requisite,
        BigDecimal balance,
        String fullName,
        Boolean isBlocked,
        LocalDateTime deletedAt) {
}
