package backend.application.repository;

import backend.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByDeveloper(UUID developerId);
    Optional<Application> findByIdAndDeveloperId(UUID id, UUID developerId);
}
