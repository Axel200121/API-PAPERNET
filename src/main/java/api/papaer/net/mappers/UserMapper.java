package api.papaer.net.mappers;


import api.papaer.net.dtos.UserDto;
import api.papaer.net.entities.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Autowired
    private ModelMapper modelMapper;

    public UserDto convertToDto(UserEntity user){
        return this.modelMapper.map(user, UserDto.class);
    }

    public UserEntity convertToEntity(UserDto userDto){
        return this.modelMapper.map(userDto, UserEntity.class);
    }
}
