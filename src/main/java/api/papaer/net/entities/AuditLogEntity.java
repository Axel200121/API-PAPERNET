package api.papaer.net.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "audit_log")
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String entityName;   // Nombre de la entidad afectada

    private String entityId;     // ID del registro afectado

    private String action;       // CREATE, UPDATE, DELETE, etc.

    @Column(length = 1000)
    private String description;

    private String ipAddress;

    private String userAgent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
