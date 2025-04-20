package api.papaer.net.services;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.PatchStatusDto;
import api.papaer.net.dtos.ShoppingDto;
import api.papaer.net.entities.ShoppingEntity;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

import java.util.Date;

public interface ShoppingService {

    ApiResponseDto executeSaveShopping(ShoppingDto shoppingDto, BindingResult bindingResult);
    ShoppingEntity getByIdShopping(String idShopping);
    Page<ShoppingDto> executeGetListShoppings(String idShopping, String idUser, String idProvider, String status, Date startDate, Date endDate, int page, int size);
    ApiResponseDto executeGetInformationShopping(String idShopping);
    ApiResponseDto executeUpdateStatus(String idShopping, PatchStatusDto patchStatusDto);
    ApiResponseDto executeDeleteShopping(String idShopping);
}
