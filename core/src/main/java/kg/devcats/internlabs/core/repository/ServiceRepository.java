package kg.devcats.internlabs.core.repository;

import kg.devcats.internlabs.core.entity.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Services, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Services> getServicesById(Long id);

    @Query("SELECT s FROM Services s WHERE " +
            "(LOWER(s.name) LIKE LOWER(CONCAT(:namePrefix, '%'))) AND " +
            "(:minPrice IS NULL OR s.min >= :minPrice) AND " +
            "(:maxPrice IS NULL OR s.max <= :maxPrice) AND " +
            "(:isCancelable IS NULL OR s.isCancelable = :isCancelable)")
    Page<Services> findByFilters(String namePrefix, BigDecimal minPrice, BigDecimal maxPrice, Boolean isCancelable, PageRequest pageable);
}
