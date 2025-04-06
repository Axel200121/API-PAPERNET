package api.papaer.net.mappers;


import api.papaer.net.dtos.CategoryDto;
import api.papaer.net.entities.CategoryEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    @Autowired
    private ModelMapper modelMapper;

    public CategoryDto convertToDto(CategoryEntity categoryEntity){
        return this.modelMapper.map(categoryEntity, CategoryDto.class);
    }

    public CategoryEntity convertToEntity(CategoryDto categoryDto){
        return this.modelMapper.map(categoryDto, CategoryEntity.class);
    }
}
