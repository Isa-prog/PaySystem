package kg.devcats.internlabs.core.repository;


import feign.Param;
import kg.devcats.internlabs.core.dto.PaymentDetailsDTO;
import kg.devcats.internlabs.core.entity.Account;
import kg.devcats.internlabs.core.entity.Client;
import kg.devcats.internlabs.core.entity.Payment;
import kg.devcats.internlabs.core.entity.PaymentStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p.paymentStatus FROM Payment p WHERE p.id = :paymentId")
    PaymentStatusEnum findPaymentStatusNameById(Long paymentId);

//    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paidAt BETWEEN :paymentTime AND :rollbackTime AND p.id = :paymentId AND p.paymentStatus = 'SUCCESS'")
//    Long countSuccessfulPaymentsBetween(@Param("paymentTime") LocalDateTime paymentTime, @Param("rollbackTime") LocalDateTime rollbackTime, @Param("paymentId") Long paymentId);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.rollbackDate BETWEEN :startDate AND :endDate")
    Long countByRollbackDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new kg.devcats.internlabs.core.dto.PaymentDetailsDTO(p.id, p.createdAt, p.paidAt, p.paymentStatus) FROM Payment p WHERE p.client = :client AND p.paidAt >= :startDate AND p.paidAt <= :endDate")
    List<PaymentDetailsDTO> findByClientAndPaidAtBetween(@Param("client") Client client, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Optional<Payment> findById(Long id);

    Payment getPaymentById(Long id);

    // Метод для подсчета количества отмененных платежей за последние 30 дней для указанного клиента
    int countByClientAndPaymentStatusAndPaidAtAfter(Client client, PaymentStatusEnum paymentStatus, LocalDateTime dateTime);

@Query("SELECT p FROM Payment p " +
        "JOIN p.account a " +
        "JOIN p.services s " +
        "JOIN p.client c " +
        "WHERE LOWER(a.requisite) LIKE CONCAT(LOWER(:requisitePrefix), '%') " +
        "AND (:minAmount IS NULL OR p.amount >= :minAmount) " +
        "AND (:maxAmount IS NULL OR p.amount <= :maxAmount) " +
        "AND LOWER(s.name) LIKE CONCAT(LOWER(:serviceName), '%') " +
        "AND LOWER(c.fullName) LIKE CONCAT(LOWER(:clientFullName), '%') " +
        "AND LOWER(p.paymentStatus) LIKE CONCAT(LOWER(:paymentStatusPrefix), '%') " +
        "AND p.createdAt >= :startDate " +
        "AND p.createdAt <= :endDate " +
        "AND p.paidAt >= :startDatePaid " +
        "AND p.paidAt <= :endDatePaid ")
Page<Payment> findByFilters(String requisitePrefix,
                            BigDecimal minAmount,
                            BigDecimal maxAmount,
                            String serviceName,
                            String clientFullName,
                            String paymentStatusPrefix,
                            LocalDateTime startDate,
                            LocalDateTime endDate,
                            LocalDateTime startDatePaid,
                            LocalDateTime endDatePaid,
                            Pageable pageable);

    Optional<Payment> findByExternalIdAndClient(Long externalId, Client client);

    List<Payment> findByPaymentStatusAndCreatedAtAfter(PaymentStatusEnum paymentStatusEnum, LocalDateTime twoHoursAgo);
}
