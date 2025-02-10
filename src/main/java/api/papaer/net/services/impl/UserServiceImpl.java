package api.papaer.net.services.impl;

import api.papaer.net.dtos.*;
import api.papaer.net.entities.UserEntity;
import api.papaer.net.mappers.UserMapper;
import api.papaer.net.repositories.RoleRepository;
import api.papaer.net.repositories.UserRepository;
import api.papaer.net.security.JwtService;
import api.papaer.net.services.RoleService;
import api.papaer.net.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public ApiResponseDto executeGetListUsers() {
        return null;
    }

    @Override
    public ApiResponseDto executeGetUser(String idUser) {
        return null;
    }

    @Override
    public ApiResponseDto executeSaveUser(UserDto userDto, BindingResult bindingResult) {

        List<ValidateInputDto> validateInputs = this.validateInputs(bindingResult);
        if (!validateInputs.isEmpty())
            return  new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos invalidos",validateInputs);
        try {
            Optional<UserEntity> userBD = this.userRepository.findByEmail(userDto.getEmail());
            if (userBD.isPresent())
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Ya existe un usuario con ese correo",null);

            if (Objects.equals(userDto.getEmail(), userDto.getConfirmPassword()))
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"La confirmación de credenciales no son correctos",null);

            UserEntity user = this.userMapper.convertToEntity(userDto);
            user.setRole(this.roleService.getRoleById(userDto.getRole().getId()));
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            UserEntity userSave = this.userRepository.save(user);
            return new ApiResponseDto(HttpStatus.CREATED.value(),"Usuario creado exitosamente", this.userMapper.convertToDto(userSave));
        } catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeUpdateUser(String idUser, UserDto userDto, BindingResult bindingResult) {
        return null;
    }

    @Override
    public ApiResponseDto executeDeleteUser(String idUser) {
        return null;
    }

    @Override
    public ApiResponseDto login(LoginDto loginDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        UserEntity userBD = this.userRepository.findByEmail(loginDto.getEmail()).orElse(null);
        if (userBD == null)
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe el correo");

        String token = jwtService.generateToken(userBD);
        LoginResponseDto login = new LoginResponseDto();
        login.setName(userBD.getName());
        login.setEmail(userBD.getEmail());
        login.setToken(token);
        login.setExpiresIn(jwtService.getExpirationTime());
        return new ApiResponseDto(HttpStatus.OK.value(),"Bienvenido al sistema",login);
    }

    private List<ValidateInputDto> validateInputs(BindingResult bindingResult){
        List<ValidateInputDto> validateFieldDTOList = new ArrayList<>();
        if (bindingResult.hasErrors()){
            bindingResult.getFieldErrors().forEach(fieldError -> {
                ValidateInputDto validateInputDto = new ValidateInputDto();
                validateInputDto.setInputValidated(fieldError.getField());
                validateInputDto.setInputValidatedMessage(fieldError.getDefaultMessage());
                validateFieldDTOList.add(validateInputDto);
            });
        }
        return validateFieldDTOList;
    }
}
