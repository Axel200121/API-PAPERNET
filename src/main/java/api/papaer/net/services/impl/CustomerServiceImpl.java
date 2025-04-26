package api.papaer.net.services.impl;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.CustomerDto;
import api.papaer.net.dtos.ValidateInputDto;
import api.papaer.net.entities.CustomerEntity;
import api.papaer.net.mappers.CustomerMapper;
import api.papaer.net.repositories.CustomerRepository;
import api.papaer.net.services.CustomerService;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public Page<CustomerEntity> executeGetListCustomers(int size, int page) {
        try {
            Pageable pageable = PageRequest.of(size, page);
            Page<CustomerEntity> listCustomers = this.customerRepository.findAll(pageable);
            if (listCustomers.isEmpty())
                throw  new BadRequestException("No hay registros");

            return listCustomers;
        }catch (Exception exception){
            throw  new InternalException("Error inesperado {} " + exception.getMessage());
        }

    }

    @Override
    public ApiResponseDto executeGetCustomer(String idCustomer) {
        if (idCustomer == null || idCustomer.trim().isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "Campo invalido");
        try {
            CustomerEntity customer = this.getCustomerByid(idCustomer);
            if (customer== null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe este registro");

            return new ApiResponseDto(HttpStatus.OK.value(),"Información detallada",this.customerMapper.convertToDto(customer));
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeSaveCustomer(CustomerDto customerDto, BindingResult bindingResult) {
        List<ValidateInputDto> validateInputs = this.validateInputs(bindingResult);
        if (!validateInputs.isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "Campos invalidos", validateInputs);
        try {
            CustomerEntity customerBD = this.customerRepository.findByEmail(customerDto.getEmail()).orElse(null);
            if (customerBD != null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Ya existe Cliente con este Correo Electronico");

            CustomerEntity customerSave = this.customerRepository.save(this.customerMapper.convertToEntity(customerDto));
            return new ApiResponseDto(HttpStatus.CREATED.value(),"Registro creado exitosamente", this.customerMapper.convertToDto(customerSave));
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado",exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeUpdateCustomer(String idCustomer, CustomerDto customerDto, BindingResult bindingResult) {
        List<ValidateInputDto> validateInputs = this.validateInputs(bindingResult);
        if (!validateInputs.isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos invalidos",validateInputs);

        try {
            CustomerEntity customerBD = this.getCustomerByid(idCustomer);
            if (customerBD == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe este registro");

            customerBD.setFullName(customerDto.getFullName());
            customerBD.setEmail(customerDto.getEmail());
            customerBD.setPhone(customerDto.getPhone());
            customerBD.setAddress(customerDto.getAddress());
            //customerBD.setStatus(customerDto.getStatus());
            CustomerEntity customerUpdate = this.customerRepository.save(customerBD);

            return new ApiResponseDto(HttpStatus.CREATED.value(),"Registro creado exitosamente", this.customerMapper.convertToDto(customerUpdate));
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeDeleteCustomer(String idCustomer) {
        if (idCustomer==null || idCustomer.trim().isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campo invalido");

        try {
            this.customerRepository.deleteById(idCustomer);
            return new ApiResponseDto(HttpStatus.NO_CONTENT.value(),"Registro eliminado");
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public CustomerEntity getCustomerByid(String idCustomer) {
        return this.customerRepository.findById(idCustomer).orElse(null);
    }

    private List<ValidateInputDto> validateInputs(BindingResult bindingResult){
        List<ValidateInputDto> validateFieldDTOList = new ArrayList<>();
        if (bindingResult.hasErrors()){
            bindingResult.getFieldErrors().forEach(fieldError -> {
                ValidateInputDto validateInputDto = new ValidateInputDto();
                validateInputDto.setInputValidated(fieldError.getField());
                validateInputDto.setInputValidatedMessage(fieldError.getDefaultMessage());
                validateFieldDTOList.add(validateInputDto);
            });
        }
        return validateFieldDTOList;
    }
}
