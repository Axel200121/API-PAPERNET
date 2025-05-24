package api.papaer.net.services;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.InventoryMovementDto;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

import java.util.Date;

public interface InventoryMovementService {
    ApiResponseDto executeSavenInventoryMovement(InventoryMovementDto inventoryMovementDto, BindingResult bindingResult);
    Page<InventoryMovementDto> executeListMovements(int page, int size, String type, String idProduct, String idUser, Date dateFrom, Date dateTo);
    ApiResponseDto executeGetMovement(String idMovement);
    ApiResponseDto executeUpdateMovement(String idMovement, InventoryMovementDto inventoryMovementDto, BindingResult bindingResult);
    ApiResponseDto executeRportsMoevements();


}
