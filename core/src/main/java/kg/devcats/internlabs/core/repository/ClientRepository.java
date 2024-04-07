package kg.devcats.internlabs.core.repository;


import kg.devcats.internlabs.core.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByLogin(String login);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Client getClientByLogin(String login);

    boolean existsByLogin(String login);

    @Query("SELECT c FROM Client c WHERE LOWER(c.fullName) LIKE CONCAT(LOWER(:fullNamePrefix), '%') " +
            "AND LOWER(c.login) LIKE CONCAT(LOWER(:loginPrefix), '%') " +
            "AND (:minBalance IS NULL OR c.balance >= :minBalance) " +
            "AND (:maxBalance IS NULL OR c.balance <= :maxBalance) " +
            "AND (:period IS NULL OR LOWER(c.limit.period.name) LIKE CONCAT(LOWER(:period), '%')) " +
            "AND (:amount IS NULL OR c.limit.amount >= :amount)")
    Page<Client> findByFilters(String fullNamePrefix, String loginPrefix, BigDecimal minBalance, BigDecimal maxBalance, String period, Long amount, Pageable pageable);
}
