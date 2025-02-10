package api.papaer.net.services;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.LoginDto;
import api.papaer.net.dtos.UserDto;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface UserService {

    ApiResponseDto executeGetListUsers();
    ApiResponseDto executeGetUser(String idUser);
    ApiResponseDto executeSaveUser(UserDto userDto, BindingResult bindingResult);
    ApiResponseDto executeUpdateUser(String idUser, UserDto userDto, BindingResult bindingResult);
    ApiResponseDto executeDeleteUser(String idUser);
    ApiResponseDto login(LoginDto loginDto);
}
