package api.papaer.net.controllers;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.PermissionDto;
import api.papaer.net.entities.PermissionEntity;
import api.papaer.net.services.PermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/paper/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/alls")
    public ResponseEntity<Page<PermissionDto>> executeListPermissions(
            @RequestParam(required = false) String idPermission,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        Page<PermissionDto> permissions = this.permissionService.executeGetListPermissions(idPermission, status, page, size);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/alls/select")
    public ResponseEntity<ApiResponseDto> executeGetListPermissionsBySelect(){
        ApiResponseDto response = this.permissionService.executeGetListPermissions();
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponseDto> executeGetPermission(@PathVariable String id){
        ApiResponseDto response = this.permissionService.executeGetPermission(id);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponseDto> executeSavePermission(@Valid @RequestBody PermissionDto permissionDto, BindingResult bindingResult){
        ApiResponseDto response = this.permissionService.executeSavePermission(permissionDto, bindingResult);
        return  new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponseDto> executeUpdatePermission(@PathVariable String id, @RequestBody PermissionDto permissionDto, BindingResult bindingResult){
        ApiResponseDto response = this.permissionService.executeUpdatePermission(id, permissionDto, bindingResult);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseDto> executeDeletePermission(@PathVariable String id){
        ApiResponseDto response = this.permissionService.executeDeletePermission(id);
        return  new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

}
