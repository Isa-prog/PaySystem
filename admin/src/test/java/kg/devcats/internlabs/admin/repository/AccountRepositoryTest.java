package kg.devcats.internlabs.admin.repository;

import kg.devcats.internlabs.core.entity.Account;
import kg.devcats.internlabs.core.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    Account account;

    @BeforeEach
    public void setup() {
        account = new Account("996111222333", new BigDecimal(555), "Akmal", false, LocalDateTime.now());
    }

    @Test
    public void testSaveUser() {
        accountRepository.save(account);
        assertThat(account).isNotNull();
        accountRepository.delete(accountRepository.findByRequisite(account.getRequisite()).orElseThrow(() -> new NoSuchElementException("Account not found")));
    }

    @Test
    public void testDeleteAccount() {
        accountRepository.save(account);
        accountRepository.delete(accountRepository.findByRequisite("996111222333").orElseThrow(() -> new NoSuchElementException("Account not found")));
        Optional<Account> account1 = accountRepository.findByRequisite(account.getRequisite());
        assertThat(account1).isEmpty();
    }
}
