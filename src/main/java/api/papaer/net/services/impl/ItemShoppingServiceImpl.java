package api.papaer.net.services.impl;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.ItemShoppingDto;
import api.papaer.net.dtos.ShoppingDto;
import api.papaer.net.dtos.ValidateInputDto;
import api.papaer.net.entities.ItemShoppingEntity;
import api.papaer.net.entities.ProductEntity;
import api.papaer.net.mappers.ItemShoppingMapper;
import api.papaer.net.mappers.ProductMapper;
import api.papaer.net.mappers.ShoppingMapper;
import api.papaer.net.repositories.ItemShoppingRepository;
import api.papaer.net.services.ItemShoppingService;
import api.papaer.net.services.ProductService;
import api.papaer.net.utils.StatusSale;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemShoppingServiceImpl implements ItemShoppingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemShoppingServiceImpl.class);

    @Autowired
    private ItemShoppingRepository itemShoppingRepository;

    @Autowired
    private ItemShoppingMapper itemShoppingMapper;

    @Autowired
    private ShoppingMapper shoppingMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ApiResponseDto executeSaveItemShopping(List<ItemShoppingDto> itemShoppingEntityList, ShoppingDto shoppingDto) {
        try {
            List<ItemShoppingEntity> itemsShoppings = new ArrayList<>();

            for (ItemShoppingDto itemShopping: itemShoppingEntityList){
                ItemShoppingEntity item = new ItemShoppingEntity();

                ProductEntity product = this.productService.getProductById(itemShopping.getProduct().getId());
                if (product == null){
                    return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Producto de la lista Numero "+itemShopping.getId() + "no existe");
                }
                BigDecimal total = itemShopping.getUnitPrice().multiply(BigDecimal.valueOf(itemShopping.getQuantity()));
                itemShopping.setTotal(total);
                itemShopping.setStatus(StatusSale.PAGADO);
                itemShopping.setShopping(shoppingDto);
                itemShopping.setProduct(this.productMapper.convertToDto(product));

                item = this.itemShoppingMapper.convertToEntity(itemShopping);
                itemsShoppings.add(item);
            }
            List<ItemShoppingEntity> listItemsSave = this.itemShoppingRepository.saveAll(itemsShoppings);
            List<ItemShoppingDto> listItemsSaveDto = listItemsSave.stream().map(itemShoppingMapper::convertToDto).collect(Collectors.toList());
            return new ApiResponseDto(HttpStatus.CREATED.value(),"Iteraciones guardadas",listItemsSaveDto);
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public BigDecimal calculateTotal(List<ItemShoppingDto> itemShoppingEntityList) {
        BigDecimal totalShopping = BigDecimal.ZERO;
        for (ItemShoppingDto item : itemShoppingEntityList) {
            BigDecimal itemTotal = item.getTotal() != null ? item.getTotal() : BigDecimal.ZERO;
            totalShopping = totalShopping.add(itemTotal);
        }
        LOGGER.info("TOTAL DE LA COMPRA {}", totalShopping);
        return totalShopping;
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
