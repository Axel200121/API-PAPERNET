package api.papaer.net.services;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.LoginDto;
import api.papaer.net.dtos.UserDto;
import api.papaer.net.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface UserService {

    Page<UserEntity> executeGetListUsers(int page, int size);
    ApiResponseDto executeGetUser(String idUser);
    ApiResponseDto executeSaveUser(UserDto userDto, BindingResult bindingResult);
    ApiResponseDto executeUpdateUser(String idUser, UserDto userDto, BindingResult bindingResult);
    ApiResponseDto executeDeleteUser(String idUser);
    ApiResponseDto login(LoginDto loginDto);
}
