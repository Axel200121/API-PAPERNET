package api.papaer.net.services.impl;


import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.RoleDto;
import api.papaer.net.dtos.ValidateInputDto;
import api.papaer.net.entities.PermissionEntity;
import api.papaer.net.entities.RoleEntity;
import api.papaer.net.mappers.RoleMapper;
import api.papaer.net.repositories.RoleRepository;
import api.papaer.net.services.PermissionService;
import api.papaer.net.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Page<RoleEntity> executeGetListRoles(int page, int size){
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<RoleEntity> listRoles = this.roleRepository.findAll(pageable);

            if (listRoles.isEmpty())
                throw  new RuntimeException("No hay registros");

            return listRoles;

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
