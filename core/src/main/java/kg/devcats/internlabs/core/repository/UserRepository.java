package kg.devcats.internlabs.core.repository;

import kg.devcats.internlabs.core.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.username LIKE CONCAT(?1, '%') AND (u.email LIKE CONCAT(?2, '%') OR COALESCE(?2, '') = '') AND r.id = COALESCE(?3, r.id)")
    Page<User> findByFilters(String usernamePrefix, String emailPrefix, Integer roleId, Pageable pageable);

}
