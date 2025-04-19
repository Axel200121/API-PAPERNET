package api.papaer.net.services.impl;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.ShoppingDto;
import api.papaer.net.dtos.ValidateInputDto;
import api.papaer.net.entities.ProviderEntity;
import api.papaer.net.entities.UserEntity;
import api.papaer.net.mappers.ShoppingMapper;
import api.papaer.net.repositories.ShoppingRepository;
import api.papaer.net.services.ProviderService;
import api.papaer.net.services.ShoppingService;
import api.papaer.net.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingServiceImpl implements ShoppingService {

    @Autowired
    private ShoppingRepository shoppingRepository;

    @Autowired
    private ShoppingMapper shoppingMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ProviderService providerService;


    @Override
    public ApiResponseDto executeSaveShopping(ShoppingDto shoppingDto, BindingResult bindingResult) {
        try {
            List<ValidateInputDto> inputsList = this.validateInputs(bindingResult);
            if (!inputsList.isEmpty())
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos invalidos",inputsList);

            UserEntity user = this.userService.getByUser(shoppingDto.getUser().getId());
            if (user == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Usuario que efectua la compra no existe");

            ProviderEntity provider = this.providerService.getProviderById(shoppingDto.getProvider().getId());
            if (provider==null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Proveedor no existe");



            return null;
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
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
