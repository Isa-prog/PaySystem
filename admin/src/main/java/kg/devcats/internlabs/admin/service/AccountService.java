package kg.devcats.internlabs.admin.service;

import kg.devcats.internlabs.admin.dto.response.AccountDTO;
import kg.devcats.internlabs.core.entity.Account;
import kg.devcats.internlabs.core.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public Page<AccountDTO> getAccounts(int pageNumber, int pageSize, String sortField, String sortOrder, String requisite, BigDecimal minBalance, BigDecimal maxBalance, String fullName, Boolean isBlocked) {
        Pageable pageable;
        if ((requisite != null && !requisite.isEmpty()) || minBalance != null || maxBalance != null || (fullName != null && !fullName.isEmpty()) || isBlocked != null) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.fromString(sortOrder), sortField);
            return accountRepository.findAccount(
                            requisite, minBalance, maxBalance, fullName, isBlocked, pageable)
                    .map(this::convertToDTO);
        } else {
            Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
            pageable = PageRequest.of(pageNumber, pageSize, sort);
            return accountRepository.findByDeletedAtIsNull(pageable).map(this::convertToDTO);
        }
    }
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public AccountDTO getAccountByRequisite(String requisite) {
        Optional<Account> optionalAccount = accountRepository.findByRequisite(requisite);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            if (account.getDeletedAt() == null) {
                return convertToDTO(account);
            }
        }
        return null;
    }

    public AccountDTO createAccount(AccountDTO accountDTO) {
       Optional<Account> existingAccountOptional = accountRepository.findByRequisite(accountDTO.requisite());
        if (existingAccountOptional.isPresent()) {
            Account existingAccount = existingAccountOptional.get();
            if (existingAccount.getDeletedAt() != null) {
                Account account = convertToEntity(accountDTO);
                account = accountRepository.save(account);
                return convertToDTO(account);
            } else {
                return null;
            }
        } else {
            Account account = convertToEntity(accountDTO);
            account = accountRepository.save(account);
            return convertToDTO(account);
        }
    }

    public AccountDTO updateAmount(String requisite, BigDecimal newAmount) {
        Account existingAccount = accountRepository.findByRequisite(requisite)
                .orElseThrow(() -> new NoSuchElementException("Account not found"));
        if (existingAccount.getDeletedAt() == null) {
            existingAccount.setBalance(newAmount);
            Account updatedAccount = accountRepository.save(existingAccount);
            return convertToDTO(updatedAccount);
        }
        return null;
    }

    public AccountDTO updateAccount(String requisite, AccountDTO updatedAccount) {
        Account existingAccount = accountRepository.findByRequisite(requisite).orElse(null);
        if (existingAccount != null && existingAccount.getDeletedAt() == null) {
            if (updatedAccount.requisite() != null) {
                existingAccount.setRequisite(updatedAccount.requisite());
            }
            if (updatedAccount.balance() != null) {
                existingAccount.setBalance(updatedAccount.balance());
            }
            if (updatedAccount.fullName() != null) {
                existingAccount.setFullName(updatedAccount.fullName()); // TODO refactor
            }
            if (updatedAccount.isBlocked() != null) {
                existingAccount.setBlocked(updatedAccount.isBlocked());
            }
            return convertToDTO(accountRepository.save(existingAccount));
        }
        return null;
    }

    public void delete(String requisite) {
        Account account = accountRepository.findByRequisite(requisite).orElseThrow(() -> new NoSuchElementException("Account not found"));
        account.setDeletedAt(LocalDateTime.now());
        accountRepository.save(account);
    }


    public AccountDTO convertToDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .requisite(account.getRequisite())
                .balance(account.getBalance())
                .fullName(account.getFullName())
                .isBlocked(account.isBlocked())
                .deletedAt(account.getDeletedAt())
                .build();
    }

    public Account convertToEntity(AccountDTO accountDTO) {
        Account account = new Account();
        account.setId(accountDTO.id());
        account.setRequisite(accountDTO.requisite());
        account.setBalance(accountDTO.balance());
        account.setFullName(accountDTO.fullName());
        account.setBlocked(accountDTO.isBlocked());
        account.setDeletedAt(accountDTO.deletedAt());
        return account;
    }

    public Page<Account> findAccount(String requisite, BigDecimal minBalance, BigDecimal maxBalance, String fullName, Boolean isBlocked, Pageable pageable) {
        return accountRepository.findAccount(requisite, minBalance, maxBalance, fullName, isBlocked, pageable);
    }
}
