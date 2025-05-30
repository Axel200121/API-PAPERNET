package api.papaer.net.controllers;

import api.papaer.net.dtos.ApiResponseDto;
import api.papaer.net.dtos.PatchStatusDto;
import api.papaer.net.dtos.RoleDto;
import api.papaer.net.dtos.ShoppingDto;
import api.papaer.net.entities.ShoppingEntity;
import api.papaer.net.services.ShoppingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/paper/shopping")
public class ShoppingController {

    @Autowired
    private ShoppingService shoppingService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponseDto> executeSaveShopping(@Valid @RequestBody ShoppingDto shoppingDto, BindingResult bindingResult){
        ApiResponseDto response = this.shoppingService.executeSaveShopping(shoppingDto, bindingResult);
        return  new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping("/alls")
    public ResponseEntity<Page<ShoppingDto>> listShoppings(
            @RequestParam(required = false) String idShopping,
            @RequestParam(required = false) String idUser,
            @RequestParam(required = false) String idProvider,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ShoppingDto> result = shoppingService.executeGetListShoppings(idShopping, idUser, idProvider, status, startDate, endDate, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponseDto> executeGetShopping(@PathVariable String id){
        ApiResponseDto response = this.shoppingService.executeGetInformationShopping(id);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @PatchMapping("/update/{id}/status")
    public ResponseEntity<ApiResponseDto> updateStatus(
            @PathVariable String id,
            @RequestBody PatchStatusDto patchStatusDto
    ) {
        ApiResponseDto response = shoppingService.executeUpdateStatus(id, patchStatusDto);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseDto> executeDeleteShopping(@PathVariable String id){
        ApiResponseDto response = this.shoppingService.executeDeleteShopping(id);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping("get/total-by-month")
    public ResponseEntity<ApiResponseDto> executeGetTotalByMonths() {
        ApiResponseDto response = this.shoppingService.executeGetTotalByMonth();
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping("get/shopping-by-provider")
    public ResponseEntity<ApiResponseDto> executeGetShoppingByProvider() {
        ApiResponseDto response = this.shoppingService.executeGetShoppingByProvider();
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping("get/products-more-shopping")
    public ResponseEntity<ApiResponseDto> executeGetProductsMoreShoppings() {
        ApiResponseDto response = this.shoppingService.executeGetShoppingByStatus();
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @GetMapping(value = "/export",  produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportShoppings(
            @RequestParam(required = false) String idShopping,
            @RequestParam(required = false) String idUser,
            @RequestParam(required = false) String idProvider,
            @RequestParam(required = false) String status ,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) throws IOException {

        // Obtener compras filtradas
        List<ShoppingEntity> shoppings = shoppingService.getShoppingsFiltered(idShopping, idUser, idProvider, status, startDate, endDate);

        // Exportar compras a PDF usando el servicio
        byte[] fileContent = shoppingService.exportToPdf(shoppings);

        // Configurar cabeceras para la respuesta (descarga del archivo)
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=shoppings_report.pdf");

        // Retornar el archivo PDF generado
        return ResponseEntity.ok().headers(headers).body(fileContent);
    }
}
