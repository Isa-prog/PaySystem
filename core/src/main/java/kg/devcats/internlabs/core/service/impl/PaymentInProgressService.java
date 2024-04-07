package kg.devcats.internlabs.core.service.impl;

import kg.devcats.internlabs.core.entity.*;
import kg.devcats.internlabs.core.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentInProgressService {
    private final PaymentRepository paymentRepository;

    public PaymentInProgressService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    @Transactional
    public Payment savePaymentInProgress(Account account, Services service, Client client, BigDecimal amount, Long externalId) {
        final Payment payment = new Payment(amount, LocalDateTime.now(), account, service, client, PaymentStatusEnum.PAYMENT_IN_PROGRESS, externalId);
        return paymentRepository.save(payment);
    }
}
