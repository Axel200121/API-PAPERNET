package api.papaer.net.services.impl;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.InventoryMovementDto;
import api.papaer.net.dtos.ValidateInputDto;
import api.papaer.net.entities.InventoryMovementEntity;
import api.papaer.net.mappers.InventoryMovementMapper;
import api.papaer.net.repositories.InventoryMovementRepository;
import api.papaer.net.services.InventoryMovementService;
import api.papaer.net.utils.filters.InventoryMovementSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryMovementServiceImpl implements InventoryMovementService {

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private InventoryMovementMapper inventoryMovementMapper;

    @Override
    public ApiResponseDto executeSavenInventoryMovement(InventoryMovementDto inventoryMovementDto, BindingResult bindingResult) {
        try {
            List<ValidateInputDto> inputs = this.validateInputs(bindingResult);
            if (!inputs.isEmpty())
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos invalidos", inputs);

            InventoryMovementEntity movement = this.inventoryMovementRepository.save(this.inventoryMovementMapper.convertToEntity(inventoryMovementDto));
            return new ApiResponseDto(HttpStatus.CREATED.value(),"Movimiento registrado", this.inventoryMovementMapper.convertToDto(movement));

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado",exception.getMessage());
        }
    }

    @Override
    public Page<InventoryMovementDto> executeListMovements(int page, int size, String type, String idProduct, String idUser, Date dateFrom, Date dateTo) {
        try {
            Pageable pageable = PageRequest.of(page,size);
            Specification<InventoryMovementEntity> spec = InventoryMovementSpecification.withFilters(type, idProduct, idUser, dateFrom, dateTo);
            Page<InventoryMovementEntity> movementsList = this.inventoryMovementRepository.findAll(spec,pageable);
            if (movementsList.isEmpty())
                throw new RuntimeException("No hay registros");

            return movementsList.map(inventoryMovementMapper::convertToDto);
        }catch (Exception exception){
            throw new RuntimeException("Error Inesperado {} "+ exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeGetMovement(String idMovement) {
        try {
            InventoryMovementEntity movement = this.inventoryMovementRepository.findById(idMovement).orElse(null);
            if (movement == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe este movimiento de stock");

            return new ApiResponseDto(HttpStatus.OK.value(),"Detalle del registro", this.inventoryMovementMapper.convertToDto(movement));

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado",exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeUpdateMovement(String idMovement, InventoryMovementDto inventoryMovementDto, BindingResult bindingResult) {
        try {
            List<ValidateInputDto> inputs = this.validateInputs(bindingResult);
            if (!inputs.isEmpty())
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos invalidos", inputs);

            InventoryMovementEntity movement = this.inventoryMovementRepository.save(this.inventoryMovementMapper.convertToEntity(inventoryMovementDto));
            return new ApiResponseDto(HttpStatus.CREATED.value(),"Movimiento registrado", this.inventoryMovementMapper.convertToDto(movement));

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado",exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeRportsMoevements() {
        return null;
    }


    private List<ValidateInputDto> validateInputs(BindingResult bindingResult){
        List<ValidateInputDto> validateFieldInputs = new ArrayList<>();
        if (bindingResult.hasErrors()){
            bindingResult.getFieldErrors().forEach(inputError ->{
                ValidateInputDto validateInputDto = new ValidateInputDto();
                validateInputDto.setInputValidated(inputError.getField());
                validateInputDto.setInputValidatedMessage(inputError.getDefaultMessage());
                validateFieldInputs.add(validateInputDto);
            });
        }
        return validateFieldInputs;
    }
}
