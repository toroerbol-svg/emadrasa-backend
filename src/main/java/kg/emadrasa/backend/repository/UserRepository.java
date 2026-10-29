package kg.emadrasa.backend.repository;

import kg.emadrasa.backend.entity.User;
import kg.emadrasa.backend.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findAllByGroupIdAndRoleOrderByFullNameAsc(Long groupId, Role role);
    List<User> findAllByRoleOrderByFullNameAsc(Role role);
}