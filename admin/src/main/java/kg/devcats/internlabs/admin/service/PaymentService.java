package kg.devcats.internlabs.admin.service;

import kg.devcats.internlabs.admin.dto.mapper.PaymentDTOMapper;
import kg.devcats.internlabs.admin.dto.response.PaymentDTO;
import kg.devcats.internlabs.core.dto.response.PaymentResult;
import kg.devcats.internlabs.core.dto.response.PutRollbackResult;
import kg.devcats.internlabs.core.entity.Payment;
import kg.devcats.internlabs.core.entity.PaymentStatusEnum;
import kg.devcats.internlabs.core.repository.PaymentRepository;
import kg.devcats.internlabs.core.service.PaymentBotService;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PaymentService {

    private final PaymentBotService paymentBotService;
    private final PaymentRepository paymentRepository;
    private final PaymentDTOMapper paymentDTOMapper;
    private final kg.devcats.internlabs.core.service.PaymentService paymentServiceCore;
    private final MessageSource messageSource;
    public PaymentService(PaymentBotService paymentBotService, PaymentRepository paymentRepository, PaymentDTOMapper paymentDTOMapper, kg.devcats.internlabs.core.service.PaymentService paymentServiceCore, MessageSource messageSource) {
        this.paymentBotService = paymentBotService;
        this.paymentRepository = paymentRepository;
        this.paymentDTOMapper = paymentDTOMapper;
        this.paymentServiceCore = paymentServiceCore;
        this.messageSource = messageSource;
    }

    public Page<PaymentDTO> getPayments(int pageNumber, int pageSize, String sortField, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        Page<Payment> paymentsPage = paymentRepository.findAll(pageRequest);
        return paymentsPage.map(payment -> paymentDTOMapper.apply(payment));
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentDTOMapper)
                .collect(Collectors.toList());
    }

    public Optional<PaymentDTO> getPaymentById(Long id) {
        return paymentRepository.findById(id).map(paymentDTOMapper);
    }

    public Page<PaymentDTO> findByFilters(String requisite, BigDecimal minAmount, BigDecimal maxAmount, String serviceName, String clientFullName, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime startDatePaid, LocalDateTime endDatePaid, String paymentStatus, Pageable pageable) {
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        if (endDatePaid == null) {
            endDatePaid = LocalDateTime.now();
        }
        Page<Payment> payments = paymentRepository.findByFilters(requisite, minAmount, maxAmount, serviceName, clientFullName, paymentStatus, startDate, endDate, startDatePaid, endDatePaid, pageable);
        return payments.map(payment -> paymentDTOMapper.apply(payment));
    }

    public PutRollbackResult putRollbackById(Long paymentId, boolean isLimited) {
        System.out.println("cancellation made by admin");
        paymentBotService.sendNotification("Финализация платежа");
        return paymentServiceCore.validateRollback(paymentId, false, null, null);
    }

    public PaymentResult putCompleteById(Long paymentId) {

        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);

        if (paymentOptional.isPresent()){
            Payment payment = paymentOptional.get();
            paymentBotService.sendNotification("Finalize payment");
            if(payment.getPaymentStatus().equals(PaymentStatusEnum.PAYMENT_IN_PROGRESS)) {
                return paymentServiceCore.makeOperation(payment);
            }

            return new PaymentResult(PaymentResult.DetailError.PAYMENT_STATUS_NOT_IN_PROGRESS,messageSource.getMessage(PaymentResult.DetailError.PAYMENT_STATUS_NOT_IN_PROGRESS.getMessage(), null, null));

        }
        return new PaymentResult(PaymentResult.DetailError.PAYMENT_NOT_FOUND, messageSource.getMessage(PaymentResult.DetailError.PAYMENT_NOT_FOUND.getMessage(), null, null));
    }

}
