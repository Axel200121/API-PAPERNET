package api.papaer.net.controllers;


import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.CustomerDto;
import api.papaer.net.entities.CustomerEntity;
import api.papaer.net.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/paper/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/alls")
    public ResponseEntity<Page<CustomerDto>> executeGetListCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String idCustomer,
            @RequestParam(required = false) String status
    ){
       Page<CustomerDto> listCustomers = this.customerService.executeGetListCustomers(page, size, idCustomer, status);
       return ResponseEntity.ok(listCustomers);
    }

    @GetMapping("/alls/select")
    public ResponseEntity<ApiResponseDto> executeGetListCustomersBySelect(){
        ApiResponseDto response = this.customerService.executeListCustomerBySelect();
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponseDto> executeGetCustomer(@PathVariable String id){
        ApiResponseDto response = this.customerService.executeGetCustomer(id);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponseDto> executeSaveCustomer(@RequestBody CustomerDto customerDto, BindingResult bindingResult){
        ApiResponseDto response = this.customerService.executeSaveCustomer(customerDto, bindingResult);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponseDto> executeUpdateCustomer(@PathVariable String id, @RequestBody CustomerDto customerDto, BindingResult bindingResult){
        ApiResponseDto response = this.customerService.executeUpdateCustomer(id, customerDto, bindingResult);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseDto> executeDeleteCustomer(@PathVariable String id){
        ApiResponseDto response = this.customerService.executeDeleteCustomer(id);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }






}
