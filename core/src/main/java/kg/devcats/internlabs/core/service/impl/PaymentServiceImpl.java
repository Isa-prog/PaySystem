package kg.devcats.internlabs.core.service.impl;

import kg.devcats.internlabs.core.dto.PaymentDetailsDTO;
import kg.devcats.internlabs.core.dto.response.*;
import kg.devcats.internlabs.core.entity.*;
import kg.devcats.internlabs.core.exception.NotEnoughBalanceException;
import kg.devcats.internlabs.core.exception.UpdateBalanceException;
import kg.devcats.internlabs.core.repository.*;
import kg.devcats.internlabs.core.service.PaymentBotService;
import kg.devcats.internlabs.core.service.PaymentService;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static kg.devcats.internlabs.core.dto.response.GetFromToResult.DetailError.*;
import static kg.devcats.internlabs.core.dto.response.GetRequisiteResult.DetailError.*;
import static kg.devcats.internlabs.core.dto.response.GetStatusResult.DetailError.REQUEST_IS_NULL;
import static kg.devcats.internlabs.core.dto.response.PaymentResult.DetailError.ACCOUNT_IS_BLOCKED;
import static kg.devcats.internlabs.core.dto.response.PaymentResult.DetailError.*;
import static kg.devcats.internlabs.core.dto.response.PaymentResult.DetailError.IP_ADDRESS_FORBIDDEN;
import static kg.devcats.internlabs.core.dto.response.PaymentResult.Status.EXISTS;
import static kg.devcats.internlabs.core.dto.response.PaymentResult.Status.SUCCESS;
import static kg.devcats.internlabs.core.dto.response.PutRollbackResult.DetailError.*;
import static kg.devcats.internlabs.core.dto.response.PutRollbackResult.DetailError.PAYMENT_NOT_FOUND;
import static kg.devcats.internlabs.core.entity.PaymentStatusEnum.PAYMENT_DENIED;
import static kg.devcats.internlabs.core.entity.PaymentStatusEnum.PAYMENT_IN_PROGRESS;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;
    private final ServiceRepository serviceRepository;
    private final ClientRepository clientRepository;
    private final PaymentBotService paymentBotService;
    private final PaymentLogRepository paymentLogRepository;
    private final EntityManager entityManager;
    private final MessageSource messageSource;
    private final PaymentOperationService paymentOperationService;
    private final PaymentInProgressService paymentInProgressService;


    public PaymentServiceImpl(PaymentRepository paymentRepository, AccountRepository accountRepository, ServiceRepository serviceRepository,
                              ClientRepository clientRepository, PaymentBotService paymentBotService, MessageSource messageSource, PaymentOperationService paymentOperationService,
                              PaymentInProgressService paymentInProgressService, PaymentLogRepository paymentLogRepository, EntityManager entityManager) {
        this.paymentRepository = paymentRepository;
        this.accountRepository = accountRepository;
        this.serviceRepository = serviceRepository;
        this.clientRepository = clientRepository;
        this.paymentBotService = paymentBotService;
        this.messageSource = messageSource;
        this.paymentOperationService = paymentOperationService;
        this.paymentInProgressService = paymentInProgressService;
        this.paymentLogRepository = paymentLogRepository;
        this.entityManager = entityManager;
    }
    //todo - перекинуть все общие исключения в 1 detail error - SERVICE_NOT_FOUND


    @Override
    @PreAuthorize("hasAuthority('account.read')")
    public GetRequisiteResult validateRequisite(String requisite, String clientLogin, Long serviceId, String ipAddress) {
        final Client client = clientRepository.findByLogin(clientLogin).get();
        if (!isIPAddressAllowed(client, ipAddress)) {
            return new GetRequisiteResult(GetRequisiteResult.DetailError.IP_ADDRESS_FORBIDDEN);
        }
        String ownerFullName = getOwnerFullName(requisite);
        Optional<Account> accountOptional = accountRepository.findByRequisite(requisite);
        if (!accountOptional.isPresent()) {
            return new GetRequisiteResult(REQUISITE_NOT_FOUND, messageSource.getMessage(REQUISITE_NOT_FOUND.getMessage(), null, null), false);
        }
        if (requisite == null) {
            return new GetRequisiteResult(REQUISITE_IS_NULL, messageSource.getMessage(REQUEST_IS_NULL.getMessage(), null, null), false);
        }
        if (serviceId == null) {
            return new GetRequisiteResult(SERVICE_IS_NULL, messageSource.getMessage(SERVICE_IS_NULL.getMessage(), null, null), false);
        }

        Account account = accountOptional.get();
        if (account.isBlocked()) {
            return new GetRequisiteResult(GetRequisiteResult.DetailError.ACCOUNT_IS_BLOCKED, messageSource.getMessage(GetRequisiteResult.DetailError.ACCOUNT_IS_BLOCKED.getMessage(), null, null), false);
        }
        if (!serviceRepository.findById(serviceId).isPresent()) {
            return new GetRequisiteResult(GetRequisiteResult.DetailError.SERVICE_NOT_FOUND, messageSource.getMessage(GetRequisiteResult.DetailError.SERVICE_NOT_FOUND.getMessage(), null, null), false);
        }
        return new GetRequisiteResult(GetRequisiteResult.Status.SUCCESS, ownerFullName, true);
    }
    @Override
    @PreAuthorize("hasAuthority('account.read')")
    public GetFromToResult validateFromTo(LocalDateTime fromDate, LocalDateTime toDate, String clientLogin, String ipAddress){

        final Client client = clientRepository.findByLogin(clientLogin).get();

        if (!isIPAddressAllowed(client, ipAddress)) {
            return new GetFromToResult(GetFromToResult.DetailError.IP_ADDRESS_FORBIDDEN);
        }

        if (!isValidDateRange(fromDate, toDate)) {
            return new GetFromToResult(INVALID_DATE_RANGE, messageSource.getMessage(INVALID_DATE_RANGE.getMessage(), null, null), false);
        }

        toDate = toDate.withHour(23).withMinute(59).withSecond(59).withNano(0);

        List<PaymentDetailsDTO> paymentDetailsList = paymentRepository.findByClientAndPaidAtBetween(client, fromDate, toDate);

        if (paymentDetailsList.isEmpty()) {
            return new GetFromToResult(NO_PAYMENTS_BY_CLIENT, messageSource.getMessage(NO_PAYMENTS_BY_CLIENT.getMessage(), null, null), false);
        }

        System.out.println(fromDate + "  " + toDate);
        String requisites = paymentDetailsList.toString();
        return new GetFromToResult(GetFromToResult.Status.SUCCESS, "Payments from " + fromDate + " to " + toDate +" are " + requisites,true);
    }

    @Override
    @PreAuthorize("hasAuthority('payment.status.get')")
    public GetStatusResult validateStatus(Long id, String clientLogin, String ipAddress) {
        final Client client = clientRepository.findByLogin(clientLogin).get();
        if (!isIPAddressAllowed(client, ipAddress)) {
            return new GetStatusResult(GetStatusResult.DetailError.IP_ADDRESS_FORBIDDEN);
        }
        Long paymentId = id;
        PaymentStatusEnum statusName = paymentRepository.findPaymentStatusNameById(paymentId);
        if (id == null) {
            return new GetStatusResult(REQUEST_IS_NULL, messageSource.getMessage(REQUEST_IS_NULL.getMessage(), null, null));
        }
        if (!paymentRepository.findById(paymentId).isPresent()) {
            return new GetStatusResult(GetStatusResult.DetailError.PAYMENT_NOT_FOUND, messageSource.getMessage(GetStatusResult.DetailError.PAYMENT_NOT_FOUND.getMessage(), null, null));
        }
        return new GetStatusResult(GetStatusResult.Status.SUCCESS, statusName.name());
    }


    @Override
    @PreAuthorize("hasAuthority('payment.create')")
    @Transactional
    public PaymentResult makePayment(String clientLogin, String requisite, BigDecimal amount, Long serviceId, Long externalId, String ipAddress) {
        final Client client = clientRepository.getClientByLogin(clientLogin);
        if (!isIPAddressAllowed(client, ipAddress)) {
            return new PaymentResult(IP_ADDRESS_FORBIDDEN);
        }
        final Optional<Services> service = serviceRepository.getServicesById(serviceId);
        if (service.isEmpty()) {
            return new PaymentResult(PaymentResult.DetailError.SERVICE_NOT_FOUND, messageSource.getMessage(PaymentResult.DetailError.SERVICE_NOT_FOUND.getMessage(), null, null));
        }
        final Optional<Account> account = accountRepository.getAccountByRequisite(requisite);
        if (account.isEmpty() || account.get().getDeletedAt() != null) {
            return new PaymentResult(ACCOUNT_NOT_FOUND, messageSource.getMessage(ACCOUNT_NOT_FOUND.getMessage(), null, null));
        }
        if (account.get().isBlocked()) {
            return new PaymentResult(ACCOUNT_IS_BLOCKED, messageSource.getMessage(ACCOUNT_IS_BLOCKED.getMessage(), null, null));
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0 || amount.stripTrailingZeros().scale() > 2) {
            return new PaymentResult(AMOUNT_NOT_CORRECT, messageSource.getMessage(AMOUNT_NOT_CORRECT.getMessage(), null, null));
        }
        if (!isValidMaxServiceAmount(amount, service.get())) {
            return new PaymentResult(TOO_BIG_AMOUNT, messageSource.getMessage(TOO_BIG_AMOUNT.getMessage(), null, null));
        }
        if (!isValidMinServiceAmount(amount, service.get())) {
            return new PaymentResult(TOO_SMALL_AMOUNT, messageSource.getMessage(TOO_SMALL_AMOUNT.getMessage(), null, null));
        }

        Optional<Payment> paymentByExternalId = paymentRepository.findByExternalIdAndClient(externalId, client);
        if (paymentByExternalId.isPresent()) {
            return handleExistingPayment(paymentByExternalId.get());
        }

        return createPayment(account.get(), service.get(), client, amount, externalId);
    }

    private PaymentResult handleExistingPayment(Payment payment) {
        if (payment.getPaymentStatus() == PAYMENT_IN_PROGRESS) {
            return makeOperation(payment);
        }
        return new PaymentResult(EXISTS, payment.getId(), payment.getPaymentStatus());
    }

    @Override
    @PreAuthorize("hasAuthority('payment.cancel')")
    public PutRollbackResult validateRollback(Long paymentId, boolean isLimited, String clientLogin, String ipAddress) {
        if (ipAddress != null) {
            if (!isIPAddressAllowed(clientRepository.findByLogin(clientLogin).get(), ipAddress)) {
                return new PutRollbackResult(PutRollbackResult.DetailError.IP_ADDRESS_FORBIDDEN);
            }
        }
        LocalDateTime startDate;

        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
        if (!paymentOptional.isPresent()) {
            return new PutRollbackResult(PAYMENT_NOT_FOUND, messageSource.getMessage(PAYMENT_NOT_FOUND.getMessage(), null, null));
        }
        Payment payment = paymentOptional.get();


        Services services = serviceRepository.findById(payment.getService().getId()).orElseThrow(() -> new RuntimeException("Service not found"));
        if (!services.getIsCancelable()) {
            return new PutRollbackResult(PAYMENT_NOT_CANCELLABLE, messageSource.getMessage(PAYMENT_NOT_CANCELLABLE.getMessage(), null, null));
        }

        Client client = clientRepository.findById(payment.getClient().getId()).orElseThrow(() -> new RuntimeException("Client not found"));

        if (isLimited) {
            Limit limit = client.getLimit();

            Long amount = limit.getAmount();

            LimitPeriod period = limit.getPeriod();
            LocalDateTime now = LocalDateTime.now();

            switch (period.getName()) {
                case "Minute":
                    startDate = now.minusMinutes(1);
                    break;
                case "Day":
                    startDate = now.minusDays(1);
                    break;
                case "Week":
                    startDate = now.minusWeeks(1);
                    break;
                case "Month":
                    startDate = now.minusMonths(1);
                    break;
                case "Year":
                    startDate = now.minusYears(1);
                    break;
                default:
                    throw new RuntimeException("Invalid period name: " + period.getName());
            }

            Long rollbackCount = paymentRepository.countByRollbackDateBetween(startDate, now);
            if (!client.getLogin().equals(clientLogin)) {
                // Если client_id не совпадает с clientLogin, то выдаем ошибку
                return new PutRollbackResult(ROLLBACK_NOT_ALLOWED, messageSource.getMessage(ROLLBACK_NOT_ALLOWED.getMessage(), null, null));
            }
            if (payment.getPaymentStatus() != PaymentStatusEnum.PAYMENT_SUCCESS) {
                return new PutRollbackResult(PAYMENT_STATUS_NOT_SUCCESS, messageSource.getMessage(PAYMENT_STATUS_NOT_SUCCESS.getMessage(), null, null));
            }

            if (rollbackCount < amount) {
                payment.setRollbackDate(now);
                try {
                    updateBalanceRollback(payment.getAccount(), client, payment.getAmount());
                } catch (UpdateBalanceException e) {
                    payment.setPaymentStatus(PaymentStatusEnum.CANCEL_DENIED);
                    paymentRepository.save(payment);
                    return new PutRollbackResult(NOT_ENOUGH_ACCOUNT_BALANCE, messageSource.getMessage(NOT_ENOUGH_ACCOUNT_BALANCE.getMessage(), null, null));
                }
                payment.setPaymentStatus(PaymentStatusEnum.PAYMENT_CANCELLED);
                paymentRepository.save(payment);

                return new PutRollbackResult(PutRollbackResult.Status.SUCCESS, payment.getPaymentStatus().toString());
            } else {
                return new PutRollbackResult(ROLLBACK_AMOUNT_LIMIT, messageSource.getMessage(ROLLBACK_AMOUNT_LIMIT.getMessage(), null, null));
            }
        } else {
            payment.setPaymentStatus(PaymentStatusEnum.PAYMENT_CANCELLED);
            payment.setRollbackDate(LocalDateTime.now());
            try {
                updateBalanceRollback(payment.getAccount(), client, payment.getAmount());
            } catch (UpdateBalanceException e) {
                payment.setPaymentStatus(PaymentStatusEnum.CANCEL_DENIED);
                paymentRepository.save(payment);
                return new PutRollbackResult(NOT_ENOUGH_ACCOUNT_BALANCE, messageSource.getMessage(NOT_ENOUGH_ACCOUNT_BALANCE.getMessage(), null, null));
            }
            paymentRepository.save(payment);

            return new PutRollbackResult(PutRollbackResult.Status.SUCCESS, payment.getPaymentStatus().toString());
        }
    }

    private boolean isIPAddressAllowed(Client client, String ipAddress) {
        String allowedIPAddresses = client.getIpAddresses();
        return allowedIPAddresses.contains(ipAddress);
    }


    private String getOwnerFullName(String requisite) {
        Optional<Account> optionalAccount = accountRepository.findByRequisite(requisite);
        if (optionalAccount.isPresent()) {
            return optionalAccount.get().getFullName();
        }
        return null;
    }

    private boolean isValidMinServiceAmount(BigDecimal amount, Services service) {
        BigDecimal min = service.getMin();
        return amount.compareTo(min) >= 0;
    }

    private boolean isValidMaxServiceAmount(BigDecimal amount, Services service) {
        BigDecimal max = service.getMax();
        return amount.compareTo(max) <= 0;
    }

    private void updateBalanceRollback(Account account, Client client, BigDecimal amount) throws UpdateBalanceException {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new UpdateBalanceException("Not enough funds to rollback the payment.");

        } else {
            account.setBalance(account.getBalance().subtract(amount));
            client.setBalance(client.getBalance().add(amount));
            accountRepository.save(account);
            clientRepository.save(client);
        }
    }


    private PaymentResult createPayment(Account account, Services service, Client client, BigDecimal amount, Long externalId) {
        Payment payment;
        try {
            payment = paymentInProgressService.savePaymentInProgress(account, service, client, amount, externalId);
        } catch (IllegalArgumentException ex) {
            return new PaymentResult(OTHER, messageSource.getMessage(OTHER.getMessage(), null, null));
        }

        return makeOperation(payment);
    }

    @Override
    @PreAuthorize("hasAuthority('payment.create')")
    public PaymentResult makeOperation(Payment payment) {
        try {
            paymentOperationService.makeOperation(payment);
//            throw new RuntimeException();
        } catch (NotEnoughBalanceException ex) {
            payment.setPaymentStatus(PAYMENT_DENIED);
            paymentRepository.save(payment);
            return new PaymentResult(NOT_ENOUGH_MONEY, messageSource.getMessage(NOT_ENOUGH_MONEY.getMessage(), null, null),
                    payment.getId(), payment.getPaymentStatus());
        } catch (RuntimeException ex) {
            paymentBotService.sendNotification("Платеж "+ payment.getId() +" успешно проведен.");
            // TODO отправить уведомление в телеграм группу, что платеж завис
            return new PaymentResult(OTHER, messageSource.getMessage(OTHER.getMessage(), null, null),
                    payment.getId(), payment.getPaymentStatus());
        }

        payment.setPaymentStatus(PaymentStatusEnum.PAYMENT_SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);//

        // TODO TODO отправить уведомление в телеграм группу, что платеж успешно проведен
        paymentBotService.sendNotification("Платеж "+ payment.getId() +" успешно проведен.");
        return new PaymentResult(SUCCESS, payment.getId(), payment.getPaymentStatus());

    }


    // Метод для проверки корректности диапазона дат
    private boolean isValidDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        return !fromDate.isAfter(toDate);
    }
}
