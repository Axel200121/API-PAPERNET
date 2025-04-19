package api.papaer.net.mappers;

import api.papaer.net.dtos.ProviderDto;
import api.papaer.net.entities.ProviderEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProviderMapper {

    @Autowired
    private ModelMapper modelMapper;


    public ProviderDto convertToDto(ProviderEntity providerEntity){
        return this.modelMapper.map(providerEntity, ProviderDto.class);
    }

    public ProviderEntity convertToEntity(ProviderDto providerDto){
        return this.modelMapper.map(providerDto, ProviderEntity.class);
    }
}
