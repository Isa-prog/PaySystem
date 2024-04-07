package kg.devcats.internlabs.admin.service;

import kg.devcats.internlabs.admin.dto.response.AccountDTO;
import kg.devcats.internlabs.core.entity.Account;
import kg.devcats.internlabs.core.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    public void testGetAccountByRequisite() {
        String requisite = "996111222333";
        Account mockAccount = new Account("996111222333", new BigDecimal(555), "Akmal", false, null);
        mockAccount.setRequisite(requisite);
        when(accountRepository.findByRequisite(requisite)).thenReturn(Optional.of(mockAccount));
        AccountDTO accountDTO = accountService.getAccountByRequisite(requisite);
        assertNotNull(accountDTO);
        assertEquals("Akmal", accountDTO.fullName());
    }

    @Test
    public void testGetAccountByRequisiteButDeleted() {
        String requisite = "996111222333";
        Account mockDeletedAccount = new Account("996111222333", new BigDecimal(555), "Akmal", false, LocalDateTime.now());
        mockDeletedAccount.setRequisite(requisite);
        mockDeletedAccount.setDeletedAt(LocalDateTime.now());
        when(accountRepository.findByRequisite(requisite)).thenReturn(Optional.of(mockDeletedAccount));
        AccountDTO resultDTO = accountService.getAccountByRequisite(requisite);
        assertNull(resultDTO);
    }

    @Test
    public void testGetNotExistAccountByRequisite() {
        String requisite = "1234567890";
        when(accountRepository.findByRequisite(requisite)).thenReturn(Optional.empty());
        AccountDTO resultDTO = accountService.getAccountByRequisite(requisite);
        assertNull(resultDTO);
    }

    @Test
    public void testGetAllAccounts() {
        List<Account> mockAccounts = new ArrayList<>();
        mockAccounts.add(new Account("996111222333", new BigDecimal(555), "Akmal", false, LocalDateTime.now()));
        mockAccounts.add(new Account("996111222444", new BigDecimal(555), "Akmal2", false, LocalDateTime.now()));
        when(accountRepository.findAll()).thenReturn(mockAccounts);
        List<Account> actualAccounts = accountService.getAllAccounts();
        assertEquals(mockAccounts.size(), actualAccounts.size());
        for (int i = 0; i < mockAccounts.size(); i++) {
            assertEquals(mockAccounts.get(i), actualAccounts.get(i));
        }
    }

    @Test
    public void testDelete() {
        String requisite = "996111222333";
        Account account = new Account("996111222333", new BigDecimal(555), "Akmal", false, LocalDateTime.now());
        when(accountRepository.findByRequisite(requisite)).thenReturn(Optional.of(account));
        accountService.delete(requisite);
        verify(accountRepository, times(1)).findByRequisite(requisite);
        verify(accountRepository, times(1)).save(account);
        assertNotNull(account.getDeletedAt());
    }

    @Test
    public void testDelete_NonExistingAccount() {
        String requisite = "noExistRequisite";
        when(accountRepository.findByRequisite(requisite)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> accountService.delete(requisite));
    }

    @Test
    void testConvertToEntity() {
        AccountDTO accountDTO = new AccountDTO(1L,"996111222333", new BigDecimal(555), "Akmal", false, LocalDateTime.now());
        Account account = accountService.convertToEntity(accountDTO);
        assertEquals("996111222333", account.getRequisite());
        assertEquals(new BigDecimal("555"), account.getBalance());
        assertEquals("Akmal", account.getFullName());
        assertEquals(false, account.isBlocked());
    }

    @Test
    void testConvertToDTO() {
        Account account = new Account("996111222333", new BigDecimal(555), "Akmal", false, LocalDateTime.now());
        AccountDTO accountDTO = accountService.convertToDTO(account);
        assertEquals("996111222333", accountDTO.requisite());
        assertEquals(new BigDecimal("555"), accountDTO.balance());
        assertEquals("Akmal", accountDTO.fullName());
        assertEquals(false, accountDTO.isBlocked());
    }
}
