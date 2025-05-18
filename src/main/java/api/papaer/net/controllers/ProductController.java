package api.papaer.net.controllers;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.ProductDto;
import api.papaer.net.entities.ProductEntity;
import api.papaer.net.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/paper/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponseDto> executeSaveProduct(@Valid @RequestBody ProductDto productDto, BindingResult bindingResult){
        ApiResponseDto response = this.productService.executeSaveProducts(productDto, bindingResult);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping("/alls")
    public ResponseEntity<Page<ProductDto>> executeGetListProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String idProduct,
            @RequestParam(required = false) String idCategory,
            @RequestParam(required = false) String idProvider,
            @RequestParam(required = false) String status
    ){
        Page<ProductDto> products = this.productService.executeGetListProducts(page, size, idProduct, idCategory, idProvider, status);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/get/{idProduct}")
    public ResponseEntity<ApiResponseDto> executeGetProduct(@PathVariable String idProduct){
        ApiResponseDto response = this.productService.executeGetProduct(idProduct);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping("/alls/select")
    public ResponseEntity<ApiResponseDto> executeGetAllProductBySelect(){
        ApiResponseDto response = this.productService.executeGetListProductsForSelect();
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @PutMapping("/update/{idProduct}")
    public ResponseEntity<ApiResponseDto> executeUpdateProduct(@PathVariable String idProduct, @RequestBody ProductDto productDto, BindingResult bindingResult){
        ApiResponseDto response = this.productService.executeUpdateProducts(idProduct, productDto, bindingResult);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @DeleteMapping("/delete/{idProduct}")
    public ResponseEntity<ApiResponseDto> executeDeleteProduct(@PathVariable String idProduct){
        ApiResponseDto response = this.productService.executeDeleteProducts(idProduct);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }
}
