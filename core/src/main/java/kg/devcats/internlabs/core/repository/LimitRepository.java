package kg.devcats.internlabs.core.repository;

import kg.devcats.internlabs.core.entity.Limit;
import kg.devcats.internlabs.core.entity.LimitPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {
    boolean existsByPeriod(LimitPeriod period);
    Optional<Limit> findByPeriodAndAmount(LimitPeriod limitPeriod, Long amount);
}
