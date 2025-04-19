package api.papaer.net.mappers;

import api.papaer.net.dtos.ProductDto;
import api.papaer.net.entities.ProductEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ProductDto convertToDto(ProductEntity productEntity){
        return this.modelMapper.map(productEntity, ProductDto.class);
    }

    public ProductEntity convertToEntity(ProductDto productDto){
        return this.modelMapper.map(productDto, ProductEntity.class);
    }
}
