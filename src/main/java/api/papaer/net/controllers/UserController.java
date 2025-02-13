package api.papaer.net.controllers;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.UserDto;
import api.papaer.net.entities.UserEntity;
import api.papaer.net.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/paper/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponseDto> executeSaveUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult){
        ApiResponseDto response = this.userService.executeSaveUser(userDto, bindingResult);
        return  new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping("/alls")
    public ResponseEntity<Page<UserEntity>> executeGetListUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        Page<UserEntity> users = this.userService.executeGetListUsers(page, size);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponseDto> executeGetUser(@PathVariable String id){
        ApiResponseDto response = this.userService.executeGetUser(id);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponseDto> executeUpdateUser(@PathVariable String id, @RequestBody UserDto userDto, BindingResult bindingResult){
        ApiResponseDto response = this.userService.executeUpdateUser(id, userDto, bindingResult);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<ApiResponseDto> executeDeleteUser(@PathVariable String id){
        ApiResponseDto response = this.userService.executeDeleteUser(id);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }


}
