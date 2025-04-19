package api.papaer.net.services;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.ShoppingDto;
import org.springframework.validation.BindingResult;

public interface ShoppingService {

    ApiResponseDto executeSaveShopping(ShoppingDto shoppingDto, BindingResult bindingResult);
}
