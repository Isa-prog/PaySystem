package kg.devcats.internlabs.admin.dto.response;

import java.math.BigDecimal;

public record ServicesDTO(
        Long id,
        String name,
        BigDecimal min,
        BigDecimal max,
        Boolean isCancelable,
        String logo
) {
}
