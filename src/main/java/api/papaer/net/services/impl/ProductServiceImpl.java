package api.papaer.net.services.impl;

import api.papaer.net.dtos.*;
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
import api.papaer.net.utils.StatusProduct;
import api.papaer.net.utils.filters.ProductSpecification;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.util.InternalException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);


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
            productBD.setStatus(productDto.getStatus());
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

    @Override
    public ApiResponseDto importProductsFromExcel(MultipartFile file) throws IOException {
        List<ProductEntity> products = new ArrayList<>();
        int nuevos = 0;
        int actualizados = 0;

        LOGGER.info("DATA DEL ARCHIVO EXCEL {}", file);

        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".xlsx")) {
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "Archivo inválido. Debe ser con extensión .xlsx");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (isRowEmpty(row)) continue;

                String code = getStringCell(row, 0);
                ProductEntity dataFromExcel = this.formatListInputsProducts(row);
                ProductEntity productBD = this.productReposiory.findByCodeProduct(code).orElse(null);

                if (productBD == null) {
                    // Nuevo producto
                    products.add(dataFromExcel);
                    nuevos++;
                } else {
                    // Actualizar campos del producto existente
                    productBD.setName(dataFromExcel.getName());
                    productBD.setDescription(dataFromExcel.getDescription());
                    productBD.setBuyPrice(dataFromExcel.getBuyPrice());
                    productBD.setSalePrice(dataFromExcel.getSalePrice());
                    productBD.setStock(dataFromExcel.getStock());
                    productBD.setMinimumStock(dataFromExcel.getMinimumStock());
                    productBD.setUrlImage(dataFromExcel.getUrlImage());
                    productBD.setCategory(dataFromExcel.getCategory());
                    productBD.setProvider(dataFromExcel.getProvider());
                    productBD.setStatus(dataFromExcel.getStatus());

                    products.add(productBD);
                    actualizados++;
                }
            }
            this.productReposiory.saveAll(products);
            return new ApiResponseDto(HttpStatus.CREATED.value(), String.format("Importación completada. Nuevos: %d, Actualizados: %d", nuevos, actualizados));
        }
    }

    private ProductEntity formatListInputsProducts(Row row){
        ProductEntity product = new ProductEntity();
        ProviderEntity provider = new ProviderEntity();
        CategoryEntity category = new CategoryEntity();
        provider.setId(getStringCell(row,9));
        category.setId(getStringCell(row,8));
        product.setCodeProduct(getStringCell(row, 0));
        product.setName(getStringCell(row, 1));
        product.setDescription(getStringCell(row, 2));
        product.setBuyPrice(BigDecimal.valueOf(getNumericCell(row, 3)));
        product.setSalePrice(BigDecimal.valueOf(getNumericCell(row, 4)));
        product.setStock((int) getNumericCell(row,5));
        product.setMinimumStock((int) getNumericCell(row,6));
        product.setUrlImage(getStringCell(row, 7));
        product.setCategory(category);
        product.setProvider(provider);
        String statusValue = getStringCell(row, 10);
        product.setStatus(!statusValue.isEmpty() ? StatusProduct.valueOf(statusValue) : StatusProduct.ACTIVE);

        return product;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK &&
                    (cell.getCellType() != CellType.STRING || !cell.getStringCellValue().trim().isEmpty())) {
                return false;
            }
        }
        return true;
    }


    private String getStringCell(Row row, int index) {
        Cell cell = row.getCell(index);
        return (cell != null) ? cell.getStringCellValue().trim() : "";
    }

    private double getNumericCell(Row row, int index) {
        Cell cell = row.getCell(index);
        return (cell != null && cell.getCellType() == CellType.NUMERIC) ? cell.getNumericCellValue() : 0.0;
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
