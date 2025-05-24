package api.papaer.net.dtos;

import api.papaer.net.entities.ProductEntity;
import api.papaer.net.entities.UserEntity;
import api.papaer.net.utils.StatusInventoryMovementType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

public class InventoryMovementDto {
    private String id;

    private StatusInventoryMovementType type; // IN, OUT, ADJUSTMENT

    private String reason;

    private Integer quantity;

    private BigDecimal unitPrice;

    private ProductEntity product;

    private UserEntity user;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "America/Mexico_City")
    private Date createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "America/Mexico_City")
    private Date updatedAt;
}
