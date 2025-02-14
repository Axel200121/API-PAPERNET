package api.papaer.net.mappers;

import api.papaer.net.dtos.CustomerDto;
import api.papaer.net.entities.CustomerEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    @Autowired
    private ModelMapper modelMapper;

    public CustomerDto convertToDto(CustomerEntity customerEntity){
        return this.modelMapper.map(customerEntity, CustomerDto.class);
    }

    public CustomerEntity convertToEntity(CustomerDto customerDto){
        return this.modelMapper.map(customerDto, CustomerEntity.class);
    }
}
