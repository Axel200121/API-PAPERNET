package api.papaer.net.services;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.CustomerDto;
import api.papaer.net.entities.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

public interface CustomerService {
    Page<CustomerEntity> executeGetListCustomers(int size, int page);
    ApiResponseDto executeGetCustomer(String idCustomer);
    ApiResponseDto executeSaveCustomer(CustomerDto customerDto, BindingResult bindingResult);
    ApiResponseDto executeUpdateCustomer(String idCustomer, CustomerDto customerDto, BindingResult bindingResult);
    ApiResponseDto executeDeleteCustomer(String idCustomer);
    CustomerEntity getCustomerByid(String idCustomer);
}
