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
    public ApiResponseDto executeSaveItemShopping(List<ItemShoppingDto> itemShoppingDtoList, ShoppingDto shoppingDto) {
        try {
            List<ItemShoppingEntity> itemsToSave = new ArrayList<>();

            for (ItemShoppingDto dto : itemShoppingDtoList) {

                ProductEntity product = productService.getProductById(dto.getProduct().getId());
                if (product == null)
                    return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "Producto de la lista con ID " + dto.getId() + " no existe");

                dto.setTotal(dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
                dto.setStatus(StatusSale.PAID);
                dto.setShopping(shoppingDto);
                dto.setProduct(productMapper.convertToDto(product));

                itemsToSave.add(itemShoppingMapper.convertToEntity(dto));
            }

            List<ItemShoppingEntity> savedItems = itemShoppingRepository.saveAll(itemsToSave);
            List<ItemShoppingDto> savedDtos = savedItems.stream().map(itemShoppingMapper::convertToDto).collect(Collectors.toList());

            return new ApiResponseDto(HttpStatus.CREATED.value(), "Iteraciones guardadas", savedDtos);

        } catch (Exception e) {
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error inesperado", e.getMessage());
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

    @Override
    public List<ItemShoppingDto> executeListItemsByIdShopping(String idShopping) {
        List<ItemShoppingEntity> listItems = this.itemShoppingRepository.findByShoppingId(idShopping).orElse(null);
        return listItems.stream().map(itemShoppingMapper::convertToDto).collect(Collectors.toList());
    }


    @Override
    public ApiResponseDto executeDeleteItemsByIdShopping(String idShopping) {
        try {
            List<ItemShoppingEntity> itemsBD = this.itemShoppingRepository.findByShoppingId(idShopping).orElse(null);
            if (itemsBD == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No se encuentran items relacionado con la compra");

            this.itemShoppingRepository.deleteByShoppingId(idShopping);
            return new ApiResponseDto(HttpStatus.NO_CONTENT.value(),"Items eliminados");
        }catch (Exception e) {
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error inesperado", e.getMessage());
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
