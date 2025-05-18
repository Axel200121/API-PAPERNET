package api.papaer.net.services;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.ProductDto;
import api.papaer.net.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {

    Page<ProductDto> executeGetListProducts(int page, int size, String idProduct, String idCategory, String idProvider, String status);
    ApiResponseDto executeGetProduct(String idProduct);
    ApiResponseDto executeSaveProducts(ProductDto productDto, BindingResult bindingResult);
    ApiResponseDto executeUpdateProducts(String idProduct, ProductDto productDto, BindingResult bindingResult);
    ApiResponseDto executeDeleteProducts(String idProduct);
    ProductEntity getProductById(String idProduct);
    ApiResponseDto executeGetListProductsForSelect();
    ApiResponseDto importProductsFromExcel(MultipartFile file) throws IOException;
}
