package kg.devcats.internlabs.admin.dto.response;


import kg.devcats.internlabs.core.entity.LimitPeriod;

// Достаточно отсавить лишь id
public record LimitDTO(
        Long id,
        LimitPeriod period,
        Long amount
) {
}
