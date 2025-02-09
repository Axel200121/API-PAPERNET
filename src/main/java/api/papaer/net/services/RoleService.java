package api.papaer.net.services;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.RoleDto;
import api.papaer.net.entities.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

public interface RoleService {
    Page<RoleEntity> executeGetListRoles(int page, int size);
    ApiResponseDto executeGetRole(String idRole);
    ApiResponseDto executeSaveRole(RoleDto roleDto, BindingResult bindingResult);
    ApiResponseDto executeUpdateRole(String idRole, RoleDto roleDto, BindingResult bindingResult);
    ApiResponseDto executeDeleteRole(String idRole);
    RoleEntity getRoleById(String idRole);
}
