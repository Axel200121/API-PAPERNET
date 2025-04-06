package api.papaer.net.controllers;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.CategoryDto;
import api.papaer.net.entities.CategoryEntity;
import api.papaer.net.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/paper/categories")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;


    @PostMapping("/save")
    public ResponseEntity<ApiResponseDto> executeSaveCategory(@Valid @RequestBody CategoryDto categoryDto, BindingResult bindingResult){
        ApiResponseDto response = this.categoryService.executeSaveCategory(categoryDto, bindingResult);
        return  new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping("/alls")
    public ResponseEntity<Page<CategoryEntity>> executeGetListCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<CategoryEntity> categories = this.categoryService.executeGetListCategories(page, size);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponseDto> executeGetCategory(@PathVariable String id){
        ApiResponseDto response = this.categoryService.executeGetCategory(id);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponseDto> executeUpdateCategory(@PathVariable String id, @RequestBody CategoryDto categoryDto, BindingResult bindingResult){
        ApiResponseDto response = this.categoryService.executeUpdateCategory(id, categoryDto, bindingResult);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseDto> executeDeleteCategory(@PathVariable String id){
        ApiResponseDto response = this.categoryService.executeDeleteCategory(id);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping("/select")
    public ResponseEntity<ApiResponseDto> executeGetAllCategoriesBySelect(){
        ApiResponseDto response = this.categoryService.executeGetAllCategoriesSelect();
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

}
