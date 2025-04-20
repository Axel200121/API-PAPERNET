package api.papaer.net.services.impl;

import api.papaer.net.dtos.*;
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
import api.papaer.net.utils.filters.ShoppingSpecificationShopping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

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
            if (!this.validateInputs(bindingResult).isEmpty())
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos invalidos",this.validateInputs(bindingResult));

            UserEntity user = validateUser(shoppingDto.getUser().getId());
            if (user == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Usuario que efectúa la compra no existe");

            ProviderEntity provider = validateProvider(shoppingDto.getProvider().getId());
            if (provider == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Proveedor no existe");

            ShoppingEntity shoppingToSave = buildShoppingEntity(shoppingDto, user, provider);
            ShoppingEntity shoppingSaved = this.shoppingRepository.save(shoppingToSave);

            ApiResponseDto itemSaveResponse = this.itemShoppingService.executeSaveItemShopping(
                    shoppingDto.getItems(), this.shoppingMapper.convertToDto(shoppingSaved)
            );

            shoppingSaved.setTotal(this.itemShoppingService.calculateTotal((List<ItemShoppingDto>) itemSaveResponse.getData()));
            ShoppingEntity finalSavedShopping = this.shoppingRepository.save(shoppingSaved);

            return new ApiResponseDto(HttpStatus.CREATED.value(),"Registro creado exitosamente",this.shoppingMapper.convertToDto(finalSavedShopping));
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }


    @Override
    public ShoppingEntity getByIdShopping(String idShopping) {
        Optional<ShoppingEntity> shopping = this.shoppingRepository.findById(idShopping);
        return shopping.orElse(null);
    }

    @Override
    public Page<ShoppingDto> executeGetListShoppings(String idShopping, String idUser, String idProvider, String status, Date startDate, Date endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        Specification<ShoppingEntity> spec = ShoppingSpecificationShopping.withFilter(idShopping, idUser, idProvider, status, startDate, endDate);

        return shoppingRepository.findAll(spec, pageable).map(shoppingMapper::convertToDto);
    }

    @Override
    public ApiResponseDto executeGetInformationShopping(String idShopping) {
        try {
            ShoppingEntity shopping = this.getByIdShopping(idShopping);
            if (shopping == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe esta compra");

            ShoppingDto informationShopping = this.shoppingMapper.convertToDto(shopping);
            informationShopping.setItems(this.itemShoppingService.executeListItemsByIdShopping(idShopping));
            return new ApiResponseDto(HttpStatus.OK.value(),"Información de la compra", informationShopping);
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeUpdateStatus(String idShopping, PatchStatusDto patchStatusDto) {
        try {
            ShoppingEntity shopping = this.getByIdShopping(idShopping);
            if (shopping == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe esta compra");

            if (shopping.getStatus() == StatusShopping.PAGADO)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No se puede cambiar el estado de una compra ya pagada");

            shopping.setStatus(StatusShopping.valueOf(patchStatusDto.getStatus()));
            ShoppingEntity shoppingSaved = this.shoppingRepository.save(shopping);

            ShoppingDto informationShopping = this.shoppingMapper.convertToDto(shoppingSaved);
            informationShopping.setItems(this.itemShoppingService.executeListItemsByIdShopping(idShopping));

            return new ApiResponseDto(HttpStatus.OK.value(),"Se actualizo el estado correctamente", informationShopping);

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeDeleteShopping(String idShopping) {
        try {
            ShoppingEntity shopping = this.getByIdShopping(idShopping);
            if (shopping == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe esta compra");

            if (shopping.getStatus() != StatusShopping.PENDIENTE)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No se puede eliminar una compra con status diferente de Pendiente");

            this.shoppingRepository.deleteById(idShopping);

            return new ApiResponseDto(HttpStatus.NO_CONTENT.value(),"Compra eliminada correctamente");

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    private UserEntity validateUser(String idUser) {
        return this.userService.getByUser(idUser);
    }

    private ProviderEntity validateProvider(String idProvider) {
        return this.providerService.getProviderById(idProvider);
    }

    private ShoppingEntity buildShoppingEntity(ShoppingDto dto, UserEntity user, ProviderEntity provider) {
        dto.setDate(new Date());
        dto.setStatus(StatusShopping.PENDIENTE);
        dto.setUser(this.userMapper.convertToDto(user));
        dto.setProvider(this.providerMapper.convertToDto(provider));
        return this.shoppingMapper.convertToEntity(dto);
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
