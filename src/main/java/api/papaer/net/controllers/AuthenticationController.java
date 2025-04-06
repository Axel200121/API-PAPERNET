package api.papaer.net.controllers;


import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.LoginDto;
import api.papaer.net.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RequestMapping("api/paper/auth")
@RestController
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto> login(@RequestBody LoginDto loginDto, BindingResult bindingResult){
        ApiResponseDto response = this.userService.login(loginDto, bindingResult);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }
}
