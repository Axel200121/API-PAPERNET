package api.papaer.net.dtos;

import api.papaer.net.utils.StatusShopping;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ShoppingDto {

    private String id;

    @NotNull(message = "Fecha de la compra es obligatorio")
    private Date date;

    @NotNull(message = "Usuaio es obligatorio")
    private UserDto user;

    @NotNull(message = "Proveedor es obligatorio")
    private ProviderDto provider;

    List<ItemShoppingDto> items;

    private Date createdAt;

    private Date updatedAt;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public ProviderDto getProvider() {
        return provider;
    }

    public void setProvider(ProviderDto provider) {
        this.provider = provider;
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

    public List<ItemShoppingDto> getItems() {
        return items;
    }

    public void setItems(List<ItemShoppingDto> items) {
        this.items = items;
    }
}
