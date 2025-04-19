package api.papaer.net.dtos;

import api.papaer.net.utils.StatusSale;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

public class ItemShoppingDto {

    private String id;

    @NotNull(message = "Cantidad es obligatorio")
    private Integer quantity;

    @NotNull(message = "Precio unitario es obligatorio")
    private BigDecimal unitPrice;

    @NotNull(message = "Monto total es obligatorio")
    private BigDecimal total;

    @NotNull(message = "Status es obligatorio")
    private StatusSale status;

    private Date createdAt;

    private Date updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public StatusSale getStatus() {
        return status;
    }

    public void setStatus(StatusSale status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
