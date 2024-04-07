package kg.devcats.internlabs.core.repository;

import kg.devcats.internlabs.core.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByRequisite(String requisite);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> getAccountByRequisite(String requisite);

    @Query("SELECT a FROM Account a WHERE " +
            "(LOWER(a.requisite) LIKE LOWER(CONCAT(?1, '%'))) AND " +
            "(a.balance >= COALESCE(?2, a.balance)) AND " +
            "(a.balance <= COALESCE(?3, a.balance)) AND " +
            "(LOWER(a.fullName) LIKE LOWER(CONCAT(?4, '%'))) AND " +
            "(a.isBlocked = COALESCE(?5, a.isBlocked)) AND " +
            "(a.deletedAt IS NULL)")
    Page<Account> findAccount(
            String requisite, BigDecimal minBalance, BigDecimal maxBalance, String fullName, Boolean isBlocked, Pageable pageable
    );

    Page<Account> findByDeletedAtIsNull(Pageable pageable);

}