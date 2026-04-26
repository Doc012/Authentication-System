package backend.auth.user.entity;

import backend.application.entity.Application;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"email", "application_id"}
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    // Multi-tenant link
    @ManyToOne(optional = false)
    @JoinColumn(name = "application_id")
    private Application application;

    private LocalDateTime createdAt;
}
