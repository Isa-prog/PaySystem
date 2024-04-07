package kg.devcats.internlabs.core.service;

import kg.devcats.internlabs.core.entity.Payment;
import kg.devcats.internlabs.core.entity.PaymentStatusEnum;
import kg.devcats.internlabs.core.repository.PaymentRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
public class PaymentSuccessSchedule {

    private final PaymentRepository paymentRepository;
    private final PaymentBotService paymentBotService;

    public PaymentSuccessSchedule(PaymentRepository paymentRepository, PaymentBotService paymentBotService) {
        this.paymentRepository = paymentRepository;
        this.paymentBotService = paymentBotService;
    }


    @Scheduled(fixedRate = 7200000)
    public void handelPaymentSuccessCheck() {
        boolean successfulPaymentsExist = checkSuccessfulPayments();

//        TODO Telegram notification
        if (!successfulPaymentsExist) {
            paymentBotService.sendNotification("Успешных платежей нет 2 часа. Проверьте работу приложения.");
        }
    }


    private boolean checkSuccessfulPayments() {
        LocalDateTime twoHoursAgo = LocalDateTime.now().minusHours(2);

        List<Payment> successfulPayments = paymentRepository.findByPaymentStatusAndCreatedAtAfter(
                PaymentStatusEnum.PAYMENT_SUCCESS, twoHoursAgo);

        return !successfulPayments.isEmpty();
    }
}
