package api.papaer.net.controllers;


import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.RoleDto;
import api.papaer.net.entities.RoleEntity;
import api.papaer.net.services.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/paper/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponseDto> executeSaveRole(@Valid @RequestBody RoleDto roleDto, BindingResult bindingResult){
        ApiResponseDto response = this.roleService.executeSaveRole(roleDto, bindingResult);
        return  new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping("/alls")
    public ResponseEntity<Page<RoleEntity>> executeGetListRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<RoleEntity> roles = this.roleService.executeGetListRoles(page, size);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponseDto> executeGetRole(@PathVariable String id){
        ApiResponseDto response = this.roleService.executeGetRole(id);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponseDto> executeUpdateRole(@PathVariable String id, @RequestBody RoleDto roleDto, BindingResult bindingResult){
        ApiResponseDto response = this.roleService.executeUpdateRole(id, roleDto, bindingResult);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseDto> executeDeleteRole(@PathVariable String id){
        ApiResponseDto response = this.roleService.executeDeleteRole(id);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping("/select")
    public ResponseEntity<ApiResponseDto> executeGetAllRolesBySelect(){
        ApiResponseDto response = this.roleService.executeGetAllRolesBySelect();
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }
}
