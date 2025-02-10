package api.papaer.net.controllers;


import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.LoginDto;
import api.papaer.net.dtos.UserDto;
import api.papaer.net.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("api/paper/auth")
@RestController
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto> login(@RequestBody LoginDto loginDto){
        ApiResponseDto response = this.userService.login(loginDto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponseDto> executeSaveUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult){
        ApiResponseDto response = this.userService.executeSaveUser(userDto, bindingResult);
        return  new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }
}
