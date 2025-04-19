package api.papaer.net.services;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.ProductDto;
import api.papaer.net.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

public interface ProductService {

    Page<ProductDto> executeGetListProducts(int size, int page);
    ApiResponseDto executeGetProduct(String idProduct);
    ApiResponseDto executeSaveProducts(ProductDto productDto, BindingResult bindingResult);
    ApiResponseDto executeUpdateProducts(String idProduct, ProductDto productDto, BindingResult bindingResult);
    ApiResponseDto executeDeleteProducts(String idProduct);
    ProductEntity getProductById(String idProduct);
    ApiResponseDto executeGetListProductsForSelect();
}
