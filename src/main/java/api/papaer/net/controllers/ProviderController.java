package api.papaer.net.controllers;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.ProviderDto;
import api.papaer.net.entities.ProviderEntity;
import api.papaer.net.services.ProviderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/paper/providers")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponseDto> executeSaveProvider(@Valid @RequestBody ProviderDto providerDto, BindingResult bindingResult){
        ApiResponseDto response = this.providerService.executeSaveProvider(providerDto, bindingResult);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping("/alls")
    public ResponseEntity<Page<ProviderEntity>> executeGetListProviders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<ProviderEntity> providers = this.providerService.executeGetListProviders(page, size);
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/get/{idProvider}")
    public ResponseEntity<ApiResponseDto> executeGetProvider(@PathVariable String idProvider){
        ApiResponseDto response = this.providerService.executeGetProvider(idProvider);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping("/alls/select")
    public ResponseEntity<ApiResponseDto> executeGetAllProvidersBySelect(){
        ApiResponseDto response = this.providerService.executeGetListProviderForSelect();
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @PutMapping("/update/{idProvider}")
    public ResponseEntity<ApiResponseDto> executeUpdateRole(@PathVariable String idProvider, @RequestBody ProviderDto providerDto, BindingResult bindingResult){
        ApiResponseDto response = this.providerService.executeUpdateProvider(idProvider, providerDto, bindingResult);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @DeleteMapping("/delete/{idProvider}")
    public ResponseEntity<ApiResponseDto> executeDeleteProvider(@PathVariable String idProvider){
        ApiResponseDto response = this.providerService.executeDeleteProvider(idProvider);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }
}
