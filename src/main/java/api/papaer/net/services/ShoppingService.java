package api.papaer.net.services;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.ShoppingDto;
import api.papaer.net.entities.ShoppingEntity;
import org.springframework.validation.BindingResult;

public interface ShoppingService {

    ApiResponseDto executeSaveShopping(ShoppingDto shoppingDto, BindingResult bindingResult);
    ShoppingEntity getByIdShopping(String idShopping);
}
