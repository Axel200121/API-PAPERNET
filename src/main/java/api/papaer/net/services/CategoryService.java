package api.papaer.net.services;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.CategoryDto;
import api.papaer.net.entities.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

public interface CategoryService {

    Page<CategoryDto> executeGetListCategories(int page, int size, String idCategory, String status);
    ApiResponseDto executeGetCategory(String idCategory);
    ApiResponseDto executeSaveCategory(CategoryDto categoryDto, BindingResult bindingResult);
    ApiResponseDto executeUpdateCategory(String idCategory, CategoryDto categoryDto, BindingResult bindingResult);
    ApiResponseDto executeDeleteCategory(String idCategory);
    CategoryEntity getCategoryById(String idCategory);
    ApiResponseDto executeGetAllCategoriesSelect();
}
