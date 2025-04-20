package api.papaer.net.services;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.ItemShoppingDto;
import api.papaer.net.dtos.ShoppingDto;

import java.math.BigDecimal;
import java.util.List;

public interface ItemShoppingService {
    ApiResponseDto executeSaveItemShopping(List<ItemShoppingDto> itemShoppingEntityList, ShoppingDto shoppingDto);
    BigDecimal calculateTotal(List<ItemShoppingDto> itemShoppingEntityList);
    List<ItemShoppingDto> executeListItemsByIdShopping(String idShopping);
    ApiResponseDto executeDeleteItemsByIdShopping(String idShopping);
}
