package api.papaer.net.services.impl;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.CategoryDto;
import api.papaer.net.dtos.ValidateInputDto;
import api.papaer.net.entities.CategoryEntity;
import api.papaer.net.entities.RoleEntity;
import api.papaer.net.mappers.CategoryMapper;
import api.papaer.net.repositories.CategoryRepository;
import api.papaer.net.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Page<CategoryEntity> executeGetListCategories(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CategoryEntity> listCategories = this.categoryRepository.findAll(pageable);

            if (listCategories.isEmpty())
                throw new RuntimeException("No hay registros");

            return listCategories;

        }catch (Exception exception){
            throw new RuntimeException("Error Inesperado {} "+ exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeGetCategory(String idCategory) {
        if (idCategory == null || idCategory.trim().isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campo invalido");
        try {
            CategoryEntity categoryBD = this.categoryRepository.findById(idCategory).orElse(null);
            if (categoryBD == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe este registro");

            return new ApiResponseDto(HttpStatus.OK.value(),"Información del registro", this.categoryMapper.convertToDto(categoryBD));
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeSaveCategory(CategoryDto categoryDto, BindingResult bindingResult) {
        List<ValidateInputDto> validateInputDtoList = this.validateInputs(bindingResult);
        if (!validateInputDtoList.isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos Invalidos",validateInputDtoList);

        try {
            CategoryEntity categorySave = this.categoryRepository.save(this.categoryMapper.convertToEntity(categoryDto));
            return new ApiResponseDto(HttpStatus.CREATED.value(),"Categoria creada exitosamente",this.categoryMapper.convertToDto(categorySave));
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeUpdateCategory(String idCategory, CategoryDto categoryDto, BindingResult bindingResult) {
        try {
            CategoryEntity categoryBD = this.getCategoryById(idCategory);
            if (categoryBD == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe este registro");

            List<ValidateInputDto> validateInputDtoList = this.validateInputs(bindingResult);
            if (!validateInputDtoList.isEmpty())
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos Invalidos",validateInputDtoList);

            categoryBD.setName(categoryDto.getName());
            categoryBD.setDescription(categoryDto.getDescription());
            categoryBD.setStatus(categoryDto.getStatus());
            CategoryEntity categoryUpdate = this.categoryRepository.save(categoryBD);

            return new ApiResponseDto(HttpStatus.CREATED.value(),"Categoria actualizada exitosamente",this.categoryMapper.convertToDto(categoryUpdate));
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }

    }

    @Override
    public ApiResponseDto executeDeleteCategory(String idCategory) {
        if (idCategory == null || idCategory.trim().isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campo invalido");
        try {
            this.categoryRepository.deleteById(idCategory);
            return new ApiResponseDto(HttpStatus.NO_CONTENT.value(),"Registro eliminado");
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado",exception.getMessage());
        }
    }

    @Override
    public CategoryEntity getCategoryById(String idCategory) {
        Optional<CategoryEntity> categoryBD = this.categoryRepository.findById(idCategory);
        return categoryBD.orElse(null);
    }

    @Override
    public ApiResponseDto executeGetAllCategoriesSelect() {
        try {
            List<CategoryEntity> categoryEntityList = this.categoryRepository.findAll();
            if (categoryEntityList.isEmpty())
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No hay categorias");

            List<CategoryDto> categoryDtoList = categoryEntityList.stream().map(categoryMapper::convertToDto).collect(Collectors.toList());
            return new ApiResponseDto(HttpStatus.OK.value(),"Listado de categorias", categoryDtoList);
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado",exception.getMessage());
        }
    }

    private List<ValidateInputDto> validateInputs(BindingResult bindingResult){
        List<ValidateInputDto> validateFieldInputs = new ArrayList<>();
        if (bindingResult.hasErrors()){
            bindingResult.getFieldErrors().forEach(inputError ->{
                ValidateInputDto validateInputDto = new ValidateInputDto();
                validateInputDto.setInputValidated(inputError.getField());
                validateInputDto.setInputValidatedMessage(inputError.getDefaultMessage());
                validateFieldInputs.add(validateInputDto);
            });
        }
        return validateFieldInputs;
    }
}
