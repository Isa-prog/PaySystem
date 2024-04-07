package kg.devcats.internlabs.clientgateway.dto.request;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreatePaymentRequest(
        @NotNull
        String requisite,
        @NotNull
        BigDecimal amount,
        @NotNull
        Long serviceId,
        @NotNull
        Long externalId
) {



}
