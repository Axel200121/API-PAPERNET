package api.papaer.net.services.impl;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.ItemShoppingDto;
import api.papaer.net.dtos.ShoppingDto;
import api.papaer.net.dtos.ValidateInputDto;
import api.papaer.net.entities.ItemShoppingEntity;
import api.papaer.net.entities.ProviderEntity;
import api.papaer.net.entities.ShoppingEntity;
import api.papaer.net.entities.UserEntity;
import api.papaer.net.mappers.ProviderMapper;
import api.papaer.net.mappers.ShoppingMapper;
import api.papaer.net.mappers.UserMapper;
import api.papaer.net.repositories.ShoppingRepository;
import api.papaer.net.services.ItemShoppingService;
import api.papaer.net.services.ProviderService;
import api.papaer.net.services.ShoppingService;
import api.papaer.net.services.UserService;
import api.papaer.net.utils.StatusShopping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingServiceImpl implements ShoppingService {

    @Autowired
    private ShoppingRepository shoppingRepository;

    @Autowired
    private ItemShoppingService itemShoppingService;

    @Autowired
    private ShoppingMapper shoppingMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProviderMapper providerMapper;

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

            shoppingDto.setDate(new Date());
            shoppingDto.setStatus(StatusShopping.valueOf(StatusShopping.PENDIENTE.toString()));
            shoppingDto.setUser(this.userMapper.convertToDto(user));
            shoppingDto.setProvider(this.providerMapper.convertToDto(provider));

            ShoppingEntity shoppingSaveFirst = this.shoppingRepository.save(this.shoppingMapper.convertToEntity(shoppingDto));
            ApiResponseDto responseItemSave = this.itemShoppingService.executeSaveItemShopping(shoppingDto.getItems(),this.shoppingMapper.convertToDto(shoppingSaveFirst));

            shoppingSaveFirst.setTotal(this.itemShoppingService.calculateTotal((List<ItemShoppingDto>) responseItemSave.getData()));
            ShoppingEntity shoppingSaveSecond = this.shoppingRepository.save(shoppingSaveFirst);

            return new ApiResponseDto(HttpStatus.CREATED.value(),"Registro creado exitosamente",this.shoppingMapper.convertToDto(shoppingSaveSecond));
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ShoppingEntity getByIdShopping(String idShopping) {
        Optional<ShoppingEntity> shopping = this.shoppingRepository.findById(idShopping);
        return shopping.orElse(null);
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
