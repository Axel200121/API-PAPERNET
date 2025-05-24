package api.papaer.net.mappers;

import api.papaer.net.dtos.InventoryMovementDto;
import api.papaer.net.entities.InventoryMovementEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InventoryMovementMapper {

    @Autowired
    private ModelMapper modelMapper;

    public InventoryMovementDto convertToDto(InventoryMovementEntity inventoryMovementEntity){
        return this.modelMapper.map(inventoryMovementEntity, InventoryMovementDto.class);
    }

    public InventoryMovementEntity convertToEntity(InventoryMovementDto inventoryMovementDto){
        return this.modelMapper.map(inventoryMovementDto, InventoryMovementEntity.class);
    }
}
