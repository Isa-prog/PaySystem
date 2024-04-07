package kg.devcats.internlabs.admin.dto.response;


import kg.devcats.internlabs.core.entity.PaymentStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDTO(
        Long id,
        BigDecimal amount,
        LocalDateTime createdAt,
        LocalDateTime paidAt,
        AccountDTO account,
        ServicesDTO services,
        ClientDTO client,
        PaymentStatusEnum paymentStatus
) {
}
