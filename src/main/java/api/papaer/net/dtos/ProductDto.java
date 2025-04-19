package api.papaer.net.dtos;

import api.papaer.net.entities.CategoryEntity;
import api.papaer.net.entities.ProviderEntity;
import api.papaer.net.utils.StatusRegister;
import api.papaer.net.utils.StatusSale;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

public class ProductDto {

    private String id;

    @NotNull(message = "Código de producto es obligatorio")
    private String codeProduct;

    @NotNull(message = "Nombre es obligatorio")
    private String name;

    @NotNull(message = "Descripción es obligatorio")
    private String description;

    @NotNull(message = "Precio de compra es obligatorio")
    private BigDecimal buyPrice;

    @NotNull(message = "Precio de venta es obligatorio")
    private BigDecimal salePrice;

    @NotNull(message = "Stock es obligatorio")
    private Integer stock;

    @NotNull(message = "Stock minimo es obligatorio")
    private Integer minimumStock;

    private String urlImage;

    private StatusRegister status;

    @NotNull(message = "Categoria es obligatorio")
    private CategoryDto category;

    @NotNull(message = "Descripción es obligatorio")
    private ProviderDto provider;

    private Date createdAt;

    private Date updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodeProduct() {
        return codeProduct;
    }

    public void setCodeProduct(String codeProduct) {
        this.codeProduct = codeProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(Integer minimumStock) {
        this.minimumStock = minimumStock;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public StatusRegister getStatus() {
        return status;
    }

    public void setStatus(StatusRegister status) {
        this.status = status;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
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
}
