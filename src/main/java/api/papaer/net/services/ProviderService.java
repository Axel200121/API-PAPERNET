package api.papaer.net.services;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.ProviderDto;
import api.papaer.net.entities.ProviderEntity;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

public interface ProviderService {

    Page<ProviderDto> executeGetListProviders(int page, int size, String idProvider, String status);
    ApiResponseDto executeGetProvider(String idProvider);
    ApiResponseDto executeSaveProvider(ProviderDto providerDto, BindingResult bindingResult);
    ApiResponseDto executeUpdateProvider(String idProvider, ProviderDto providerDto, BindingResult bindingResult);
    ApiResponseDto executeDeleteProvider(String idProvider);
    ProviderEntity getProviderById(String idProvider);
    ApiResponseDto executeGetListProviderForSelect();
}
