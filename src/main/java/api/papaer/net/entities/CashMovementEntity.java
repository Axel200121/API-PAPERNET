package api.papaer.net.entities;


import api.papaer.net.utils.StatusCashMovementType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "cash_movement")
public class CashMovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String description;

    @Enumerated(EnumType.STRING)
    private StatusCashMovementType type; // INCOME, EXPENSE

    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;


}
