package api.papaer.net.services.impl;

import api.papaer.net.dtos.*;
import api.papaer.net.entities.UserEntity;
import api.papaer.net.mappers.UserMapper;
import api.papaer.net.repositories.UserRepository;
import api.papaer.net.security.JwtService;
import api.papaer.net.services.RoleService;
import api.papaer.net.services.UserService;
import api.papaer.net.utils.StatusAuditLog;
import api.papaer.net.utils.StatusUser;
import api.papaer.net.utils.filters.UserSpecification;
import api.papaer.net.utils.logs.LogsInsert;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

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

    @Autowired
    private LogsInsert logsInsert;

    @Override
    public Page<UserDto> executeGetListUsers(String idUser, String status, String idRole, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Specification<UserEntity> spec = UserSpecification.withFilter(idUser, status, idRole);

            Page<UserEntity> listUsers = this.userRepository.findAll(spec,pageable);

            if (listUsers.isEmpty()) {
                throw new BadRequestException("No hay registros");
            }

            this.saveLog(String.valueOf(StatusAuditLog.READ_ALL),"Consulta todos los usuarios","ID*");
            return listUsers.map(userMapper::convertToDto);

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

            this.saveLog(String.valueOf(StatusAuditLog.READ_REGISTER),"Detalle de información del registro",userBD.getId());

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
            user.setStatus(StatusUser.PENDING_ACTIVATION);
            UserEntity userSave = this.userRepository.save(user);

            this.saveLog(String.valueOf(StatusAuditLog.CREATE),"Creación de Usuario",userSave.getId());

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
            userBD.setStatus(userDto.getStatus());
            userBD.setRole(this.roleService.getRoleById(userDto.getRole().getId()));

            UserEntity userUpdate = this.userRepository.save(userBD);
            this.saveLog(String.valueOf(StatusAuditLog.UPDATE),"Actualización de registro",userBD.getId());

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
            this.saveLog(String.valueOf(StatusAuditLog.DELETE),"Eliminación de registro",idUser);

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
        this.saveLog(String.valueOf(StatusAuditLog.LOGIN),"Inicio de sesión",userBD.getId());

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

    private void saveLog(String action, String description,String objectId){
        AuditLogDto log = new AuditLogDto();
        log.setEntityName(UserEntity.class.getSimpleName());
        log.setAction(action);
        log.setEntityId(objectId);
        log.setDescription(description);
        this.logsInsert.saveLog(log);
    }



}
