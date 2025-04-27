package api.papaer.net.services.impl;


import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.AuditLogDto;
import api.papaer.net.dtos.RoleDto;
import api.papaer.net.dtos.ValidateInputDto;
import api.papaer.net.entities.PermissionEntity;
import api.papaer.net.entities.RoleEntity;
import api.papaer.net.entities.UserEntity;
import api.papaer.net.mappers.RoleMapper;
import api.papaer.net.repositories.RoleRepository;
import api.papaer.net.services.PermissionService;
import api.papaer.net.services.RoleService;
import api.papaer.net.utils.StatusAuditLog;
import api.papaer.net.utils.filters.RoleSpecification;
import api.papaer.net.utils.logs.LogsInsert;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private LogsInsert logsInsert;

    @Override
    public Page<RoleDto> executeGetListRoles(String idRole, String status, int page, int size){
        try {
            Pageable pageable = PageRequest.of(page, size);
            Specification<RoleEntity> spec = RoleSpecification.withFilter(idRole,status);

            Page<RoleEntity> listRoles = this.roleRepository.findAll(spec, pageable);

            if (listRoles.isEmpty())
                throw new BadRequestException("No hay registros");

            this.saveLog(String.valueOf(StatusAuditLog.READ_ALL),"Consulta lista de roles","ID*");
            return listRoles.map(roleMapper::convertToDto);

        }catch (Exception exception){
            throw  new RuntimeException("Error inesperado {} " + exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeGetRole(String idRole) {
        if (idRole == null || idRole.trim().isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "Campo invalido");
        try {
            RoleEntity roleBD = this.roleRepository.findById(idRole).orElse(null);
            if (roleBD == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe este Rol");

            this.saveLog(String.valueOf(StatusAuditLog.READ_REGISTER),"Detalle de información del registro",idRole);

            return new ApiResponseDto(HttpStatus.OK.value(),"Información detallada",this.roleMapper.convertToDto(roleBD));
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeSaveRole(RoleDto roleDto, BindingResult bindingResult) {
        List<ValidateInputDto> validateInputs = this.validateInputs(bindingResult);
        if (!validateInputs.isEmpty())
            return  new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos invalidos",validateInputs);
        try {
            List<PermissionEntity> permission = this.permissionService.listPermissionsValidate(roleDto.getPermissions());
            RoleEntity role = this.roleMapper.convertToEntity(roleDto);
            role.setPermissions(permission);
            RoleEntity roleSave = this.roleRepository.save(role);
            this.saveLog(String.valueOf(StatusAuditLog.CREATE),"Creación de registro",roleSave.getId());

            return new ApiResponseDto(HttpStatus.CREATED.value(),"Rol creado exitosamente", this.roleMapper.convertToDto(roleSave));
        } catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeUpdateRole(String idRole, RoleDto roleDto, BindingResult bindingResult) {
        try {
            RoleEntity roleBD = this.roleRepository.findById(idRole).orElse(null);
            if (roleBD == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe este registro");

            roleBD.setName(roleDto.getName());
            roleBD.setDescription(roleDto.getDescription());
            roleBD.setStatus(roleDto.getStatus());
            roleBD.setPermissions(this.permissionService.listPermissionsValidate(roleDto.getPermissions()));
            RoleEntity roleUpdate = this.roleRepository.save(roleBD);
            this.saveLog(String.valueOf(StatusAuditLog.UPDATE),"Actualización de registro",roleUpdate.getId());

            return new ApiResponseDto(HttpStatus.OK.value(),"Registro Actualizado correctamente", this.roleMapper.convertToDto(roleUpdate));

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado",exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeDeleteRole(String idRole) {
        if (idRole == null || idRole.trim().isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campo invalido");

        try {
            this.roleRepository.deleteById(idRole);
            this.saveLog(String.valueOf(StatusAuditLog.DELETE),"Elimincación de registro",idRole);

            return new ApiResponseDto(HttpStatus.NO_CONTENT.value(),"Registro eliminado");
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado",exception.getMessage());
        }
    }

    @Override
    public RoleEntity getRoleById(String idRole) {
        Optional<RoleEntity> roleBD = this.roleRepository.findById(idRole);
        return roleBD.orElse(null);
    }

    @Override
    public ApiResponseDto executeGetAllRolesBySelect() {
        List<RoleEntity> roles = this.roleRepository.findAll();
        if (roles.isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No hay roles");

        this.saveLog(String.valueOf(StatusAuditLog.READ_ALL),"Consulta listado de roles","ID*");
        List<RoleDto> rolesDto = roles.stream().map(roleMapper::convertToDto).collect(Collectors.toList());
        return new ApiResponseDto(HttpStatus.OK.value(),"Roles del sistema", rolesDto);
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
