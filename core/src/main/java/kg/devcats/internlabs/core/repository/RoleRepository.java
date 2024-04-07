package kg.devcats.internlabs.core.repository;

import kg.devcats.internlabs.core.entity.ERole;
import kg.devcats.internlabs.core.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
