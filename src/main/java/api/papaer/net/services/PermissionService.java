package api.papaer.net.services;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.PermissionDto;
import api.papaer.net.entities.PermissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface PermissionService {
    Page<PermissionDto> executeGetListPermissions(String idPermission, String status, int page, int size);
    ApiResponseDto executeGetPermission(String idPermission);
    ApiResponseDto executeSavePermission(PermissionDto permissionDto, BindingResult bindingResult);
    ApiResponseDto executeUpdatePermission(String idPermission, PermissionDto permissionDto, BindingResult bindingResult);
    ApiResponseDto executeDeletePermission(String idPermission);
    List<PermissionEntity> listPermissionsValidate(List<PermissionDto> permissionsValidate);
}
