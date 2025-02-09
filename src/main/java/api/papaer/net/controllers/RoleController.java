package api.papaer.net.controllers;


import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.RoleDto;
import api.papaer.net.services.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
