package backend.auth.user.repository;

import backend.auth.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndApplicationId(String email, UUID applicationId);
    boolean existsByEmailAndApplicationId(String email, UUID applicationId);
}
