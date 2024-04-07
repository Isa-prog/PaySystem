package kg.devcats.internlabs.core.service.impl;

import kg.devcats.internlabs.core.entity.Account;
import kg.devcats.internlabs.core.entity.Client;
import kg.devcats.internlabs.core.entity.Payment;
import kg.devcats.internlabs.core.exception.NotEnoughBalanceException;
import kg.devcats.internlabs.core.repository.AccountRepository;
import kg.devcats.internlabs.core.repository.ClientRepository;
import kg.devcats.internlabs.core.repository.PaymentRepository;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentOperationService {
    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final MessageSource messageSource;

    public PaymentOperationService(PaymentRepository paymentRepository, ClientRepository clientRepository, AccountRepository accountRepository, MessageSource messageSource) {
        this.paymentRepository = paymentRepository;
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.messageSource = messageSource;
    }

    @Transactional
    public void makeOperation(Payment payment) throws NotEnoughBalanceException {
        try {
            Client client = payment.getClient();
            Account account = payment.getAccount();
            BigDecimal amount = payment.getAmount();
            if (client.getBalance().compareTo(amount) < 0) {
                throw new NotEnoughBalanceException();
            }
            client.setBalance(client.getBalance().subtract(amount));
            account.setBalance(account.getBalance().add(amount));
            accountRepository.save(account);
            clientRepository.save(client);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Failed to update balances due to a data access problem", ex);
        }
    }
}
