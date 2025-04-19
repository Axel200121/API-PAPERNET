package api.papaer.net.services.impl;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.ProviderDto;
import api.papaer.net.dtos.ValidateInputDto;
import api.papaer.net.entities.ProviderEntity;
import api.papaer.net.mappers.ProviderMapper;
import api.papaer.net.repositories.ProviderRepository;
import api.papaer.net.services.ProviderService;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProviderServiceImpl implements ProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ProviderMapper providerMapper;


    @Override
    public Page<ProviderEntity> executeGetListProviders(int size, int page) {
        try{
            Pageable pageable = PageRequest.of(size, page);
            Page<ProviderEntity> listProvider = this.providerRepository.findAll(pageable);
            if (listProvider.isEmpty())
                throw  new BadRequestException("No hay registros");

            return listProvider;
        }catch (Exception ex){
            throw  new InternalException("Error inesperado {} "+ ex.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeGetProvider(String idProvider) {
        if (idProvider == null || idProvider.trim().isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campo Invalido, verifica tus datos");
        try {
            ProviderEntity provider = this.getProviderById(idProvider);
            if (provider ==  null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Proveedor no encontrado");

            return new ApiResponseDto(HttpStatus.OK.value(),"Información recuperada", providerMapper.convertToDto(provider));

        }catch (Exception ex){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado",ex.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeSaveProvider(ProviderDto providerDto, BindingResult bindingResult) {
        List<ValidateInputDto> inputsValidateList = this.validateInputs(bindingResult);
        if (!inputsValidateList.isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos invalidos");
        try {
            ProviderEntity providerBD = this.providerRepository.findByEmail(providerDto.getEmail()).orElse(null);
            if (providerBD != null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Este provedor ya existe");

            ProviderEntity provider = this.providerRepository.save(this.providerMapper.convertToEntity(providerDto));
            return new ApiResponseDto(HttpStatus.CREATED.value(),"Registro creado exitosamente", this.providerMapper.convertToDto(provider));
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeUpdateProvider(String idProvider, ProviderDto providerDto, BindingResult bindingResult) {
        List<ValidateInputDto> inputsValidateList = this.validateInputs(bindingResult);
        if (!inputsValidateList.isEmpty())
            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos invalidos");
        try {
            ProviderEntity providerBD = this.getProviderById(idProvider);
            if (providerBD == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe este registro");

            providerBD.setName(providerDto.getName());
            providerBD.setAddress(providerDto.getAddress());
            providerBD.setPhone(providerDto.getPhone());
            providerBD.setEmail(providerDto.getEmail());
            providerBD.setStatus(providerDto.getStatus());

            ProviderEntity provider = this.providerRepository.save(this.providerMapper.convertToEntity(providerDto));

            return new ApiResponseDto(HttpStatus.CREATED.value(),"Registro actualizado exitosamente", this.providerMapper.convertToDto(provider));

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeDeleteProvider(String idProvider) {
        try {
            ProviderEntity providerBD = this.getProviderById(idProvider);
            if (providerBD == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe este registro");

            this.providerRepository.deleteById(idProvider);
            return new ApiResponseDto(HttpStatus.NO_CONTENT.value(),"Registro eliminado exitosamente");

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ProviderEntity getProviderById(String idProvider) {
        return this.providerRepository.findById(idProvider).orElse(null);
    }

    @Override
    public ApiResponseDto executeGetListProviderForSelect() {
        try {
            List<ProviderEntity> providers =this.providerRepository.findAll();
            if (providers.isEmpty())
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existen proveedores");

            List<ProviderDto> providersListDto =providers.stream().map(providerMapper::convertToDto).collect(Collectors.toList());
            return new ApiResponseDto(HttpStatus.OK.value(), "Lista de proveedores", providersListDto);

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    private List<ValidateInputDto> validateInputs(BindingResult bindingResult){
        List<ValidateInputDto> validateInputDtoList = new ArrayList<>();
        if (bindingResult.hasErrors()){
            bindingResult.getFieldErrors().forEach(inputError ->{
                ValidateInputDto validateInputDto = new ValidateInputDto();
                validateInputDto.setInputValidated(inputError.getField());
                validateInputDto.setInputValidatedMessage(inputError.getDefaultMessage());
                validateInputDtoList.add(validateInputDto);
            });
        }
        return validateInputDtoList;
    }
}
