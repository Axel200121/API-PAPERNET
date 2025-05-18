package api.papaer.net.services.impl;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.ProductDto;
import api.papaer.net.dtos.ValidateInputDto;
import api.papaer.net.entities.CategoryEntity;
import api.papaer.net.entities.ProductEntity;
import api.papaer.net.entities.ProviderEntity;
import api.papaer.net.mappers.CategoryMapper;
import api.papaer.net.mappers.ProductMapper;
import api.papaer.net.mappers.ProviderMapper;
import api.papaer.net.repositories.ProductReposiory;
import api.papaer.net.services.CategoryService;
import api.papaer.net.services.ProductService;
import api.papaer.net.services.ProviderService;
import api.papaer.net.utils.StatusRegister;
import api.papaer.net.utils.filters.ProductSpecification;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductReposiory productReposiory;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProviderMapper providerMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProviderService providerService;

    @Override
    public Page<ProductDto> executeGetListProducts(int page, int size, String idProduct, String idCategory, String idProvider, String status) {
        try{

            Pageable pageable = PageRequest.of(page,size);
            Specification<ProductEntity> spec = ProductSpecification.withFilter(idProduct, idCategory, idProvider, status);
            Page<ProductEntity> listProduct = this.productReposiory.findAll(spec, pageable);
            if (listProduct.isEmpty())
                throw new BadRequestException("No hay registros");

            return listProduct.map(product -> this.productMapper.convertToDto(product));
        }catch (Exception ex){
            throw  new InternalException("Error inesperado {} "+ ex.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeGetProduct(String idProduct) {
        try {
            ProductEntity product = this.getProductById(idProduct);
            if (product == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe este registro");

            return new ApiResponseDto(HttpStatus.OK.value(),"Información detallada", this.productMapper.convertToDto(product));

        }catch (Exception ex){
            throw  new InternalException("Error inesperado {} "+ ex.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeSaveProducts(ProductDto productDto, BindingResult bindingResult) {
        try {
            List<ValidateInputDto> inputs = this.validateInputs(bindingResult);
            if (!inputs.isEmpty())
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos invalidos",inputs);

            Optional<ProductEntity> productBD = this.productReposiory.findByCodeProduct(productDto.getCodeProduct());
            if (productBD.isPresent())
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Ya existe un producto con este codigo");

            CategoryEntity category = this.categoryService.getCategoryById(productDto.getCategory().getId());
            if (category == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe esta category");

            ProviderEntity provider = this.providerService.getProviderById(productDto.getProvider().getId());
            if (provider == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe este proveedor");

            productDto.setStatus(StatusRegister.ACTIVE);

            ProductEntity productEntity = this.productReposiory.save(this.productMapper.convertToEntity(productDto));
            ProductDto product = this.productMapper.convertToDto(productEntity);
            product.setCategory(categoryMapper.convertToDto(category));
            product.setProvider(providerMapper.convertToDto(provider));
            return new ApiResponseDto(HttpStatus.CREATED.value(),"Registro creado exitosamente",product);

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeUpdateProducts(String idProduct, ProductDto productDto, BindingResult bindingResult) {
        try {
            List<ValidateInputDto> inputs = this.validateInputs(bindingResult);
            if (!inputs.isEmpty())
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "Campos invalidos", inputs);

            ProductEntity productBD = this.getProductById(idProduct);
            if (productBD == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "No existe este producto");

            CategoryEntity category = this.categoryService.getCategoryById(productDto.getCategory().getId());
            if (category == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "No existe esta category");

            ProviderEntity provider = this.providerService.getProviderById(productDto.getProvider().getId());
            if (provider == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "No existe este proveedor");


            productBD.setCodeProduct(productDto.getCodeProduct());
            productBD.setName(productDto.getName());
            productBD.setDescription(productDto.getDescription());
            productBD.setBuyPrice(productDto.getBuyPrice());
            productBD.setSalePrice(productDto.getSalePrice());
            productBD.setStock(productDto.getStock());
            productBD.setMinimumStock(productDto.getMinimumStock());
            productBD.setUrlImage(productDto.getUrlImage());
            //productBD.setStatus(productDto.getStatus());
            productBD.setCategory(category);
            productBD.setProvider(provider);

            ProductEntity productSave = this.productReposiory.save(productBD);
            return new ApiResponseDto(HttpStatus.CREATED.value(),"Registro creado exitosamente",this.productMapper.convertToDto(productSave));

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeDeleteProducts(String idProduct) {
        try {
            ProductEntity product = this.getProductById(idProduct);
            if (product == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe este registro");

            this.productReposiory.deleteById(idProduct);
            return new ApiResponseDto(HttpStatus.NO_CONTENT.value(),"Registro eliminado correctamente");

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ProductEntity getProductById(String idProduct) {
        Optional<ProductEntity> product = this.productReposiory.findById(idProduct);
        return product.orElse(null);
    }

    @Override
    public ApiResponseDto executeGetListProductsForSelect() {
        try {
            List<ProductEntity> productEntityList = this.productReposiory.findAll();
            if (productEntityList.isEmpty())
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No hay registros");

            List<ProductDto> listProductsDto = productEntityList.stream().map(productMapper::convertToDto).collect(Collectors.toList());
            return new ApiResponseDto(HttpStatus.OK.value(),"Listado de productos", listProductsDto);
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    private List<ValidateInputDto> validateInputs(BindingResult bindingResult){
        List<ValidateInputDto> validateInputDtoList = new ArrayList<>();
        if (bindingResult.hasErrors()){
            bindingResult.getFieldErrors().forEach(inputError ->{
                ValidateInputDto validateInputDto = new ValidateInputDto();
                validateInputDto.setInputValidated(inputError.getField());
                validateInputDto.setInputValidatedMessage(inputError.getDefaultMessage());
                validateInputDtoList.add(validateInputDto);
            });
        }
        return validateInputDtoList;
    }
}
