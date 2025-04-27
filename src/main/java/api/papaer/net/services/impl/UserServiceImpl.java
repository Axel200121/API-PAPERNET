package api.papaer.net.services.impl;

import api.papaer.net.dtos.*;
import api.papaer.net.entities.PermissionEntity;
import api.papaer.net.entities.UserEntity;
import api.papaer.net.mappers.UserMapper;
import api.papaer.net.repositories.RoleRepository;
import api.papaer.net.repositories.UserRepository;
import api.papaer.net.security.JwtService;
import api.papaer.net.services.RoleService;
import api.papaer.net.services.UserService;
import api.papaer.net.utils.StatusRegister;
import api.papaer.net.utils.StatusUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public Page<UserEntity> executeGetListUsers(int page, int size, Optional<String> role) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserEntity> listUsers;

            if (role.isPresent() && !role.get().isEmpty()) {
                listUsers = this.userRepository.findByRoleId(role.get(), pageable);
            } else {
                listUsers = this.userRepository.findAll(pageable);
            }

            if (listUsers.isEmpty()) {
                throw new RuntimeException("No hay registros");
            }

            return listUsers;

        } catch (Exception exception) {
            throw new RuntimeException("Error inesperado: " + exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeGetUser(String idUser) {
        try {
            UserEntity userBD = this.userRepository.findById(idUser).orElse(null);
            if (userBD == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe este usuario");

            return new ApiResponseDto(HttpStatus.OK.value(),"Información detallada", this.userMapper.convertToDto(userBD));
        } catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
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

            if (!Objects.equals(userDto.getPassword(), userDto.getConfirmPassword()))
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
        List<ValidateInputDto> inputs = this.validateInputs(bindingResult);
        if (!inputs.isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos invalidos", inputs);
        System.out.println("password endoce " + userDto.getPassword());
        try {
            UserEntity userBD = this.userRepository.findById(idUser).orElse(null);
            if (userBD == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "Usuario no encontrado");

            if (!Objects.equals(userDto.getPassword(), userDto.getConfirmPassword()))
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"La confirmación de credenciales no es correcta", null);

            userBD.setName(userDto.getName());
            userBD.setLastName(userDto.getLastName());
            userBD.setPassword(passwordEncoder.encode(userDto.getPassword()));
            userBD.setEmail(userDto.getEmail());
            userBD.setPhone(userDto.getPhone());
            userBD.setAddress(userDto.getAddress());
            userBD.setStatus(StatusUser.PENDING_ACTIVATION);
            userBD.setRole(this.roleService.getRoleById(userDto.getRole().getId()));
            UserEntity userUpdate = this.userRepository.save(userBD);

            return new ApiResponseDto(HttpStatus.OK.value(),"Se actualizo correctamente",this.userMapper.convertToDto(userUpdate));

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeDeleteUser(String idUser) {
        if (idUser == null || idUser.trim().isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campo invalido");
        try {
            this.userRepository.deleteById(idUser);
            return new ApiResponseDto(HttpStatus.NO_CONTENT.value(),"Registro eliminado");
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto login(LoginDto loginDto, BindingResult bindingResult) {

        List<ValidateInputDto> inputsValidate = this.validateInputs(bindingResult);
        if (!inputsValidate.isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos invalidos", inputsValidate);

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

    @Override
    public UserEntity getByUser(String idUser) {
        Optional<UserEntity> user = this.userRepository.findById(idUser);
        return user.orElse(null);
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
