package api.papaer.net.mappers;

import api.papaer.net.dtos.ItemShoppingDto;
import api.papaer.net.entities.ItemShoppingEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemShoppingMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ItemShoppingDto convertToDto(ItemShoppingEntity itemShoppingEntity){
        return this.modelMapper.map(itemShoppingEntity,ItemShoppingDto.class);
    }

    public ItemShoppingEntity convertToEntity(ItemShoppingDto itemShoppingDto){
        return this.modelMapper.map(itemShoppingDto, ItemShoppingEntity.class);
    }
}
