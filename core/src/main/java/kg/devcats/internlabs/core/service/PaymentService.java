package kg.devcats.internlabs.core.service;

import kg.devcats.internlabs.core.dto.response.*;
import kg.devcats.internlabs.core.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PaymentService {
    GetRequisiteResult validateRequisite(String requisite, String clientLogin, Long serviceId, String ipAddress);
    GetStatusResult validateStatus(Long id, String clientLogin, String ipAddress);
    PaymentResult makePayment(String clientLogin, String requisite, BigDecimal amount, Long serviceId, Long externalId, String ipAddress);
    PutRollbackResult validateRollback(Long id, boolean isLimited, String clientLogin, String ipAddress);
    GetFromToResult validateFromTo(LocalDateTime fromDate, LocalDateTime toDate, String ipAddress, String clientLogin);
    PaymentResult makeOperation(Payment payment);
}
