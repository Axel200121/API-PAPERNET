package api.papaer.net.services.impl;


import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.PermissionDto;
import api.papaer.net.dtos.ValidateInputDto;
import api.papaer.net.entities.PermissionEntity;
import api.papaer.net.mappers.PermissionMapper;
import api.papaer.net.repositories.PermissionRepository;
import api.papaer.net.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionMapper permissionMapper;


    @Override
    public Page<PermissionEntity> executeGetListPermissions(int page, int size) {
        try {

            Pageable pageable = PageRequest.of(page, size);
            Page<PermissionEntity> listPermissions =this.permissionRepository.findAll(pageable);

            if (listPermissions.isEmpty())
                throw new RuntimeException("No hay registros");

            return listPermissions;

        } catch (Exception exception){
            throw  new RuntimeException("Error inesperado"+exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeGetPermission(String idPermission) {
        if (idPermission == null || idPermission.trim().isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campo invalido",null);
        try{
            PermissionEntity permissionBD = this.permissionRepository.findById(idPermission).orElse(null);
            if (permissionBD == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe el permiso",null);

            return new ApiResponseDto(HttpStatus.OK.value(),"Información detallada",this.permissionMapper.convertToDto(permissionBD));

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado",exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeSavePermission(PermissionDto permissionDto, BindingResult bindingResult) {
        List<ValidateInputDto> validateInputs = this.validateInputs(bindingResult);
        if (!validateInputs.isEmpty())
            return  new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos invalidos",validateInputs);
        try {
            PermissionEntity permission = this.permissionMapper.convertToEntity(permissionDto);
            PermissionEntity permissionSave = this.permissionRepository.save(permission);
            return new ApiResponseDto(HttpStatus.CREATED.value(),"Permiso creado exitosamente", this.permissionMapper.convertToDto(permissionSave));
        } catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeUpdatePermission(String idPermission, PermissionDto permissionDto, BindingResult bindingResult) {

        List<ValidateInputDto> validateInputs = this.validateInputs(bindingResult);
        if (!validateInputs.isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos Invalidos",validateInputs);

        try {
            PermissionEntity permissionBD = this.permissionRepository.findById(idPermission).orElse(null);
            if (permissionBD == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No esxite este permiso");
            permissionBD.setName(permissionDto.getName());
            permissionBD.setDescription(permissionDto.getDescription());
            permissionBD.setStatus(permissionDto.getStatus());
            PermissionEntity permissionUpdate = this.permissionRepository.save(permissionBD);

            return new ApiResponseDto(HttpStatus.OK.value(),"Registro Actualizado correctamente", this.permissionMapper.convertToDto(permissionUpdate));
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeDeletePermission(String idPermission) {
        if (idPermission == null  || idPermission.trim().isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campo invalido");

        try {
            this.permissionRepository.deleteById(idPermission);
            return new ApiResponseDto(HttpStatus.NO_CONTENT.value(),"Registro eliminado");
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public List<PermissionEntity> listPermissionsValidate(List<PermissionDto> permissionsValidate) {

        if (permissionsValidate.isEmpty())
            return null;

        List<PermissionEntity> permissions = new ArrayList<>();
        for (PermissionDto permissionDto: permissionsValidate){
            PermissionEntity permission = this.getPermissionById(permissionDto.getId());
            permissions.add(permission);
        }
        return permissions;
    }

    public PermissionEntity getPermissionById(String idPermission) {
        Optional<PermissionEntity> permissionBD = permissionRepository.findById(idPermission);
        return permissionBD.orElse(null);
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
