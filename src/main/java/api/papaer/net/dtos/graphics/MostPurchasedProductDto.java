package api.papaer.net.dtos.graphics;

public class MostPurchasedProductDto {
    private String product;
    private Long quantity;

    public MostPurchasedProductDto(String product, Long quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
