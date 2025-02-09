package api.papaer.net.mappers;


import api.papaer.net.dtos.RoleDto;
import api.papaer.net.entities.RoleEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    @Autowired
    private ModelMapper modelMapper;

    public RoleDto convertToDto(RoleEntity role){
        return this.modelMapper.map(role, RoleDto.class);
    }

    public RoleEntity convertToEntity(RoleDto roleDto){
        return this.modelMapper.map(roleDto, RoleEntity.class);
    }
}
