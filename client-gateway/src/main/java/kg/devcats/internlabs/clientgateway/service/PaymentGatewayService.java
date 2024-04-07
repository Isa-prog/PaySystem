package kg.devcats.internlabs.clientgateway.service;

import kg.devcats.internlabs.clientgateway.dto.PaymentData;
import kg.devcats.internlabs.clientgateway.dto.Status;
import kg.devcats.internlabs.clientgateway.dto.response.*;
import kg.devcats.internlabs.core.dto.response.*;
import kg.devcats.internlabs.core.entity.Payment;
import kg.devcats.internlabs.core.entity.PaymentLog;
import kg.devcats.internlabs.core.repository.PaymentLogRepository;
import kg.devcats.internlabs.core.repository.PaymentRepository;
import kg.devcats.internlabs.core.service.PaymentService;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static kg.devcats.internlabs.clientgateway.dto.Status.*;

@Service
public class PaymentGatewayService {
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final MessageSource messageSource;

    public PaymentGatewayService(PaymentService paymentService, PaymentRepository paymentRepository,
                                 PaymentLogRepository paymentLogRepository, MessageSource messageSource) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.paymentLogRepository = paymentLogRepository;
        this.messageSource = messageSource;
    }

    public RequisiteResponse getRequisite(String requisite, String clientLogin, Long serviceId, String ipAddress) {
        GetRequisiteResult response = paymentService.validateRequisite(requisite, clientLogin, serviceId, ipAddress);
        RequisiteResponse requisiteResponse;
        if (response.getStatus().equals(GetRequisiteResult.Status.SUCCESS)) {
            requisiteResponse = new RequisiteResponse(REQUISITE_FOUND,
                    messageSource.getMessage(REQUISITE_FOUND.getMessage(), null, null),
                    response.getName(), true);
        } else {
            switch (response.getDetailError()) {
                case IP_ADDRESS_FORBIDDEN:
                    logGetRequisite(requisite, serviceId, "Access Forbidden", ipAddress);
                    throw new AccessDeniedException("Access Forbidden");
                case REQUISITE_IS_NULL:
                    requisiteResponse = new RequisiteResponse(Status.REQUISITE_IS_NULL, response.getDetailMessageError(), false);
                    break;
                case SERVICE_IS_NULL:
                    requisiteResponse = new RequisiteResponse(SERVICE_IS_NULL, response.getDetailMessageError(), false);
                    break;
                case REQUISITE_NOT_FOUND:
                    requisiteResponse = new RequisiteResponse(REQUISITE_NOT_FOUND, response.getDetailMessageError(), false);
                    break;
                case SERVICE_NOT_FOUND:
                    requisiteResponse = new RequisiteResponse(SERVICE_NOT_FOUND, response.getDetailMessageError(), false);
                    break;
                case ACCOUNT_IS_BLOCKED:
                    requisiteResponse = new RequisiteResponse(ACCOUNT_IS_BLOCKED, response.getDetailMessageError(), false);
                    break;
                default:
                    requisiteResponse = new RequisiteResponse(OTHER, response.getDetailMessageError(), false);
            }
        }
        logGetRequisite(requisite, serviceId, requisiteResponse.toString(), ipAddress);

        return requisiteResponse;
    }

    public PaymentFromToResponse getPaymentsFromTo(String fromDate, String toDate, String clientLogin, String ipAddress) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate parsedFromDate = LocalDate.parse(fromDate, inputFormatter);
        LocalDate parsedToDate = LocalDate.parse(toDate, inputFormatter);

        LocalDateTime startDateTime = parsedFromDate.atStartOfDay();
        LocalDateTime endDateTime = parsedToDate.atTime(LocalTime.MAX);

        System.out.println(startDateTime.toString() + "   " + endDateTime.toString());
        GetFromToResult response = paymentService.validateFromTo(startDateTime, endDateTime, clientLogin, ipAddress);
        PaymentFromToResponse paymentFromToResponse;

        if(response.getStatus().equals(GetFromToResult.Status.SUCCESS)){
            paymentFromToResponse = new PaymentFromToResponse(PAYMENTS_FOUND, messageSource.getMessage(PAYMENTS_FOUND.getMessage(), null, null),
                    response.getName(), true);
        } else{
            switch (response.getDetailError()) {
                case IP_ADDRESS_FORBIDDEN:
                    logGetFromTo(fromDate, toDate, null, ipAddress);
                    throw new AccessDeniedException("Access Forbidden");
                case PAYMENTS_NOT_FOUND:
                    paymentFromToResponse = new PaymentFromToResponse(PAYMENTS_NOT_FOUND, response.getDetailMessageError(), false);
                    break;
                case INVALID_DATE_RANGE:
                    paymentFromToResponse = new PaymentFromToResponse(INVALID_DATE_RANGE, response.getDetailMessageError(), false);
                    break;
                case NO_PAYMENTS_BY_CLIENT:
                    paymentFromToResponse = new PaymentFromToResponse(NO_PAYMENTS_BY_CLIENT, response.getDetailMessageError(), false);
                    break;
                default:
                    paymentFromToResponse = new PaymentFromToResponse(OTHER, response.getDetailMessageError(), false);
            }
        }
        logGetFromTo(fromDate, toDate, paymentFromToResponse.toString(), ipAddress);
        return paymentFromToResponse;
    }

    public PaymentStatusResponse getPaymentStatus(Long id, String clientLogin, String ipAddress) {
        GetStatusResult result = paymentService.validateStatus(id, clientLogin, ipAddress);

        PaymentStatusResponse paymentStatusResponse;
        if (result.getStatus().equals(GetStatusResult.Status.SUCCESS)) {
            paymentStatusResponse = new PaymentStatusResponse(STATUS_FOUND,
                    messageSource.getMessage(STATUS_FOUND.getMessage(), null, null),
                    result.getPaymentStatus());
        } else{
            switch(result.getDetailError()) {
                case IP_ADDRESS_FORBIDDEN:
                    logGetStatus(id, null, ipAddress);
                    throw new AccessDeniedException("Access Forbidden");
                case REQUEST_IS_NULL:
                    paymentStatusResponse = new PaymentStatusResponse(Status.REQUEST_IS_NULL, result.getDetailMessageError());
                    break;
                case PAYMENT_NOT_FOUND:
                    paymentStatusResponse = new PaymentStatusResponse(PAYMENT_NOT_FOUND, result.getDetailMessageError());
                    break;
                default:
                    paymentStatusResponse = new PaymentStatusResponse(OTHER, result.getDetailMessageError());
                    break;
            }
        }
        logGetStatus(id, paymentStatusResponse.toString(), ipAddress);
        return paymentStatusResponse;
    }
    public PaymentResponse makePayment(String clientLogin, String requisite, BigDecimal amount, Long serviceId, Long externalId, String ipAddress) {
        PaymentResult result = paymentService.makePayment(clientLogin, requisite, amount, serviceId, externalId, ipAddress);
        handleForbiddenIpAddress(result.getDetailError(), requisite, amount, serviceId, ipAddress);
        PaymentResponse paymentResponse = getPaymentResponse(result);
        logMakePayment(paymentResponse.toString(), requisite, amount, serviceId, result.getPaymentId(), ipAddress);
        return paymentResponse;
    }

    private void handleForbiddenIpAddress(PaymentResult.DetailError detailError, String requisite, BigDecimal amount, Long serviceId, String ipAddress) {
        if (detailError == PaymentResult.DetailError.IP_ADDRESS_FORBIDDEN) {
            logMakePayment(null, requisite, amount, serviceId, null, ipAddress);
            throw new AccessDeniedException("Access Forbidden");
        }
    }

    private PaymentResponse getPaymentResponse(PaymentResult result) {
        return switch (result.getStatus()) {
            case SUCCESS, EXISTS -> getSuccessResponse(result);
            default -> getFailResponse(result);
        };
    }

    private PaymentResponse getSuccessResponse(PaymentResult result) {
        return new PaymentResponse(
                result.getStatus().equals(PaymentResult.Status.SUCCESS) ? PAYMENT_COMPLETED : PAYMENT_EXISTS,
                messageSource.getMessage(result.getStatus().equals(PaymentResult.Status.SUCCESS) ? PAYMENT_COMPLETED.getMessage() : PAYMENT_EXISTS.getMessage(), null, null),
                new PaymentData(result.getPaymentId(), result.getPaymentStatus()));
    }

    private PaymentResponse getFailResponse(PaymentResult result) {
        return switch (result.getDetailError()) {
            case ACCOUNT_IS_BLOCKED -> new PaymentResponse(ACCOUNT_IS_BLOCKED, result.getDetailMessageError());
            case ACCOUNT_NOT_FOUND -> new PaymentResponse(ACCOUNT_NOT_FOUND, result.getDetailMessageError());
            case SERVICE_NOT_FOUND -> new PaymentResponse(SERVICE_NOT_FOUND, result.getDetailMessageError());
            case SERVICE_LIMIT -> new PaymentResponse(SERVICE_LIMIT, result.getDetailMessageError());
            case AMOUNT_NOT_CORRECT -> new PaymentResponse(AMOUNT_NOT_CORRECT, result.getDetailMessageError());
            case TOO_BIG_AMOUNT -> new PaymentResponse(TOO_BIG_AMOUNT, result.getDetailMessageError());
            case TOO_SMALL_AMOUNT -> new PaymentResponse(TOO_SMALL_AMOUNT, result.getDetailMessageError());
            case NOT_ENOUGH_MONEY -> new PaymentResponse(NOT_ENOUGH_MONEY, result.getDetailMessageError(),
                    new PaymentData(result.getPaymentId(), result.getPaymentStatus()));
            default -> new PaymentResponse(OTHER, result.getDetailMessageError(),
                    new PaymentData(result.getPaymentId(), result.getPaymentStatus()));
        };
    }

    public RollbackResponse rollbackPayment(Long id, boolean isLimited, String clientLogin, String ipAddress) {
        PutRollbackResult response = paymentService.validateRollback(id, isLimited, clientLogin, ipAddress);

        RollbackResponse rollbackResponse;
        if(response.getStatus().equals(PutRollbackResult.Status.SUCCESS)){
            rollbackResponse = new RollbackResponse(ROLLBACK_COMPLETED,
                    messageSource.getMessage(ROLLBACK_COMPLETED.getMessage(), null, null),
                    response.getPaymentStatus());
        } else{
            switch(response.getDetailError()){
                case IP_ADDRESS_FORBIDDEN:
                    logRollbackPayment(id, isLimited, null, ipAddress);
                    throw new AccessDeniedException("Access Forbidden");
                case PAYMENT_NOT_FOUND: rollbackResponse = new RollbackResponse(PAYMENT_NOT_FOUND,
                        response.getDetailMessageError());
                    break;
                case ROLLBACK_AMOUNT_LIMIT: rollbackResponse = new RollbackResponse(ROLLBACK_AMOUNT_LIMIT,
                        response.getDetailMessageError());
                    break;
                case PAYMENT_STATUS_NOT_SUCCESS: rollbackResponse = new RollbackResponse(PAYMENT_STATUS_NOT_SUCCESS,
                        response.getDetailMessageError());
                    break;
                case ROLLBACK_NOT_ALLOWED: rollbackResponse = new RollbackResponse(ROLLBACK_NOT_ALLOWED,
                        response.getDetailMessageError());
                    break;
                case NOT_ENOUGH_ACCOUNT_BALANCE: rollbackResponse = new RollbackResponse(NOT_ENOUGH_ACCOUNT_BALANCE,
                        response.getDetailMessageError());
                    break;
                case PAYMENT_NOT_CANCELLABLE: rollbackResponse = new RollbackResponse(PAYMENT_NOT_CANCELLABLE,
                        response.getDetailMessageError());
                    break;
                default: rollbackResponse = new RollbackResponse(OTHER,
                        response.getDetailMessageError());

            }
        }
        logRollbackPayment(id, isLimited, rollbackResponse.toString(), ipAddress);
        return rollbackResponse;
    }

    private void logGetRequisite(String requisite, Long serviceId, String response, String ipAddress) {
        PaymentLog log = new PaymentLog();
        log.setAction("GetRequisite");
        log.setDatetime(LocalDateTime.now());
        log.setRequestParameters("requisite=" + requisite + ", serviceId=" + serviceId);
        log.setDetails("Ip address - " + ipAddress);
        log.setResponse(response);
        paymentLogRepository.save(log);
    }

    private void logGetStatus(Long id, String response, String ipAddress){
        PaymentLog log = new PaymentLog();
        log.setAction("GetStatus");
        log.setDatetime(LocalDateTime.now());
        log.setRequestParameters("id=" + id);
        log.setResponse(response);
        log.setDetails("Ip address - " + ipAddress);
        Payment payment = paymentRepository.getPaymentById(id);
        log.setPayment(payment);
        paymentLogRepository.save(log);
    }

    private void logMakePayment(String response, String requisite, BigDecimal amount, Long serviceId, Long paymentId, String ipAddress){
        PaymentLog log = new PaymentLog();
        log.setAction("MakePayment");
        log.setDatetime(LocalDateTime.now());
        log.setDetails("Ip address - " + ipAddress);
        Payment payment = paymentRepository.getPaymentById(paymentId);
        log.setPayment(payment);
        log.setResponse(response);
        log.setRequestParameters("requisite=" + requisite + ", Amount=" + amount + ", ServiceId" + serviceId );
        paymentLogRepository.save(log);
    }

    private void logRollbackPayment(Long id, boolean isLimited, String response, String ipAddress){
        PaymentLog log = new PaymentLog();
        log.setAction("RollbackPayment");
        log.setDatetime(LocalDateTime.now());
        log.setDetails("Ip address - " + ipAddress);
        log.setRequestParameters("id=" + id + ", isLimited=" + isLimited);
        Payment payment = paymentRepository.getPaymentById(id);
        log.setPayment(payment);
        log.setResponse(response);
        paymentLogRepository.save(log);
    }
    private void logGetFromTo(String fromDate, String toDate, String response, String ipAddress){
        PaymentLog log = new PaymentLog();
        log.setAction("GetPaymentsFromToDate");
        log.setDatetime(LocalDateTime.now());
        log.setDetails("Ip address - " + ipAddress);
        log.setRequestParameters(fromDate + toDate);
        log.setResponse(response);
        paymentLogRepository.save(log);
    }
}
