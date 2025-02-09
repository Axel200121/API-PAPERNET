package api.papaer.net.mappers;


import api.papaer.net.dtos.PermissionDto;
import api.papaer.net.entities.PermissionEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    @Autowired
    private ModelMapper modelMapper;

    public PermissionDto convertToDto(PermissionEntity permission){
        return this.modelMapper.map(permission, PermissionDto.class);
    }

    public PermissionEntity convertToEntity(PermissionDto permissionDto){
        return this.modelMapper.map(permissionDto, PermissionEntity.class);
    }
}
