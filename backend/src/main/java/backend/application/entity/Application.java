package backend.application.entity;

import backend.developer.entity.Developer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(unique = true, nullable = false)
    private String apiKey;

    @Column(unique = true, nullable = false)
    private String clientId;

    @Column(nullable =false)
    private String clientSecretHash;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @ManyToOne
    @JoinColumn(name = "developer_id", nullable = false)
    private Developer developer;

    @PrePersist
    public void setCreated_at() {
        this.created_at = LocalDateTime.now();
    }
}
