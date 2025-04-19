package api.papaer.net.mappers;

import api.papaer.net.dtos.ShoppingDto;
import api.papaer.net.entities.ShoppingEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShoppingMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ShoppingDto convertToDto(ShoppingEntity shoppingEntity){
        return this.modelMapper.map(shoppingEntity,ShoppingDto.class);
    }

    public ShoppingEntity convertToEntity(ShoppingDto shoppingDto){
        return this.modelMapper.map(shoppingDto,ShoppingEntity.class);
    }

}
