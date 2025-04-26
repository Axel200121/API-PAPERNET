package api.papaer.net.services.impl;

import api.papaer.net.dtos.*;
import api.papaer.net.dtos.graphics.CountShopByProviderDto;
import api.papaer.net.dtos.graphics.CountShopByStatusDto;
import api.papaer.net.dtos.graphics.TotalShoppingByMonthDto;
import api.papaer.net.entities.ProviderEntity;
import api.papaer.net.entities.ShoppingEntity;
import api.papaer.net.entities.UserEntity;
import api.papaer.net.mappers.ProviderMapper;
import api.papaer.net.mappers.ShoppingMapper;
import api.papaer.net.mappers.UserMapper;
import api.papaer.net.repositories.ShoppingRepository;
import api.papaer.net.services.ItemShoppingService;
import api.papaer.net.services.ProviderService;
import api.papaer.net.services.ShoppingService;
import api.papaer.net.services.UserService;
import api.papaer.net.utils.StatusShopping;
import api.papaer.net.utils.filters.ShoppingSpecificationShopping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShoppingServiceImpl implements ShoppingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemShoppingServiceImpl.class);

    @Autowired
    private ShoppingRepository shoppingRepository;

    @Autowired
    private ItemShoppingService itemShoppingService;

    @Autowired
    private ShoppingMapper shoppingMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProviderMapper providerMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ProviderService providerService;

    //@Autowired
    //private ReportShopping reportShopping;


    @Override
    public ApiResponseDto executeSaveShopping(ShoppingDto shoppingDto, BindingResult bindingResult) {
        try {
            if (!this.validateInputs(bindingResult).isEmpty())
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Campos invalidos",this.validateInputs(bindingResult));

            UserEntity user = validateUser(shoppingDto.getUser().getId());
            if (user == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Usuario que efectúa la compra no existe");

            ProviderEntity provider = validateProvider(shoppingDto.getProvider().getId());
            if (provider == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"Proveedor no existe");

            ShoppingEntity shoppingToSave = buildShoppingEntity(shoppingDto, user, provider);
            ShoppingEntity shoppingSaved = this.shoppingRepository.save(shoppingToSave);

            ApiResponseDto itemSaveResponse = this.itemShoppingService.executeSaveItemShopping(
                    shoppingDto.getItems(), this.shoppingMapper.convertToDto(shoppingSaved)
            );

            shoppingSaved.setTotal(this.itemShoppingService.calculateTotal((List<ItemShoppingDto>) itemSaveResponse.getData()));
            ShoppingEntity finalSavedShopping = this.shoppingRepository.save(shoppingSaved);

            return new ApiResponseDto(HttpStatus.CREATED.value(),"Registro creado exitosamente",this.shoppingMapper.convertToDto(finalSavedShopping));
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }


    @Override
    public ShoppingEntity getByIdShopping(String idShopping) {
        Optional<ShoppingEntity> shopping = this.shoppingRepository.findById(idShopping);
        return shopping.orElse(null);
    }

    @Override
    public Page<ShoppingDto> executeGetListShoppings(String idShopping, String idUser, String idProvider, String status, Date startDate, Date endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        Specification<ShoppingEntity> spec = ShoppingSpecificationShopping.withFilter(idShopping, idUser, idProvider, status, startDate, endDate);

        return shoppingRepository.findAll(spec, pageable).map(shoppingMapper::convertToDto);
    }

    @Override
    public ApiResponseDto executeGetInformationShopping(String idShopping) {
        try {
            ShoppingEntity shopping = this.getByIdShopping(idShopping);
            if (shopping == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe esta compra");

            ShoppingDto informationShopping = this.shoppingMapper.convertToDto(shopping);
            informationShopping.setItems(this.itemShoppingService.executeListItemsByIdShopping(idShopping));
            return new ApiResponseDto(HttpStatus.OK.value(),"Información de la compra", informationShopping);
        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeUpdateStatus(String idShopping, PatchStatusDto patchStatusDto) {
        try {
            ShoppingEntity shopping = this.getByIdShopping(idShopping);
            if (shopping == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe esta compra");

            if (shopping.getStatus() == StatusShopping.PAID)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No se puede cambiar el estado de una compra ya pagada");

            shopping.setStatus(StatusShopping.valueOf(patchStatusDto.getStatus()));
            ShoppingEntity shoppingSaved = this.shoppingRepository.save(shopping);

            ShoppingDto informationShopping = this.shoppingMapper.convertToDto(shoppingSaved);
            informationShopping.setItems(this.itemShoppingService.executeListItemsByIdShopping(idShopping));

            return new ApiResponseDto(HttpStatus.OK.value(),"Se actualizo el estado correctamente", informationShopping);

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeDeleteShopping(String idShopping) {
        try {
            ShoppingEntity shopping = this.getByIdShopping(idShopping);
            if (shopping == null)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No existe esta compra");

            if (shopping.getStatus() != StatusShopping.PENDING)
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No se puede eliminar una compra con status diferente de Pendiente");

            this.shoppingRepository.deleteById(idShopping);

            return new ApiResponseDto(HttpStatus.NO_CONTENT.value(),"Compra eliminada correctamente");

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeGetTotalByMonth() {
        try {

            List<Object[]> totalByMonth = this.shoppingRepository.findTotalByMonth();
            if (totalByMonth.isEmpty())
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No hay registros");

            List<TotalShoppingByMonthDto> returnedData = totalByMonth.stream().map(data -> {
                TotalShoppingByMonthDto totalShoppingByMonth = new TotalShoppingByMonthDto();
                totalShoppingByMonth.setMonth((String) data[0]);
                totalShoppingByMonth.setTotal((BigDecimal) data[1]);
                return totalShoppingByMonth;
            }).collect(Collectors.toList());

            return new ApiResponseDto(HttpStatus.OK.value(),"Información recuperada", returnedData);

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeGetShoppingByProvider() {
        try {
         List<Object[]> countShopByProvider = this.shoppingRepository.findShoppingByProvider();

         if (countShopByProvider.isEmpty())
             return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No hay registros");

         List<CountShopByProviderDto> returnedData = countShopByProvider.stream().map(data ->{
             CountShopByProviderDto dto = new CountShopByProviderDto();
             dto.setName((String) data[0]);
             dto.setQuantity((Long) data[1]);
             return dto;
         }).collect(Collectors.toList());

         return new ApiResponseDto(HttpStatus.OK.value(),"Información recuperada", returnedData);

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public ApiResponseDto executeGetShoppingByStatus() {
        try {
            List<Object[]> countByStatus = this.shoppingRepository.findShoppingByStatus();
            if (countByStatus.isEmpty())
                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),"No hay registros");

            List<CountShopByStatusDto> returnedData = countByStatus.stream().map(data -> {
                CountShopByStatusDto dto = new CountShopByStatusDto();
                dto.setStatus(((StatusShopping) data[0]).name());
                dto.setQuantity((Long) data[1]);
                return dto;
            }).collect(Collectors.toList());

            return new ApiResponseDto(HttpStatus.OK.value(),"Información recuperada", returnedData);

        }catch (Exception exception){
            return new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Error inesperado", exception.getMessage());
        }
    }

    @Override
    public List<ShoppingEntity> getShoppingsFiltered(String idShopping, String idUser, String idProvider, String status, Date startDate, Date endDate) {
        Specification<ShoppingEntity> spec = ShoppingSpecificationShopping.withFilter(idShopping, idUser, idProvider, status, startDate, endDate);
        return shoppingRepository.findAll(spec);
    }

    /*@Override
    public byte[] exportToPdf(List<ShoppingEntity> shoppings) throws IOException {
        try {

            return reportShopping.exportToPdf(shoppings);

        } catch (IOException e) {

            throw new RuntimeException("Error al generar el PDF", e);
        }
    }*/

    @Override
    public byte[] exportToPdf(List<ShoppingEntity> shoppings) throws IOException {
        /*ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);
        document.setMargins(30, 20, 30, 20);

        // Estilos
        Color teal = new DeviceRgb(6, 148, 162);
        Color red = new DeviceRgb(240, 82, 82);
        Color grayLight = new DeviceRgb(245, 245, 245);
        Color grayDark = new DeviceRgb(100, 100, 100);
        Color orange = new DeviceRgb(255, 179, 71);

        // Fecha generada debajo de la tabla de título
        String generatedDate = java.time.LocalDateTime.now().toString().replace("T", " ");
        Paragraph generatedDateParagraph = new Paragraph("Fecha generada: " + generatedDate.substring(0, 19))
                .setFontSize(14).setTextAlignment(TextAlignment.RIGHT).setMarginTop(5);
        document.add(generatedDateParagraph);

        // HEADER: Logo + Título
        Resource resource = new ClassPathResource("images/report-shopping.png");
        ImageData data = ImageDataFactory.create(resource.getInputStream().readAllBytes());
        Image image = new Image(data).setWidth(100).setAutoScaleHeight(true);

        Table headerTable = new Table(2).setWidth(UnitValue.createPercentValue(100));
        headerTable.addCell(new Cell().add(image).setBorder(Border.NO_BORDER));

        Paragraph title = new Paragraph("REPORTE DE COMPRAS")
                .setFont(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD))
                .setFontSize(28).setTextAlignment(TextAlignment.CENTER).setMarginTop(50);
        headerTable.addCell(new Cell().add(title).setBorder(Border.NO_BORDER));
        document.add(headerTable);

        // DESCRIPCIÓN: Texto explicativo antes de la tabla
        Paragraph description = new Paragraph("Este reporte contiene información detallada sobre las compras realizadas, incluyendo los proveedores, montos y el estado de cada compra.")
                .setFontSize(12).setTextAlignment(TextAlignment.LEFT).setMarginTop(20);
        document.add(description);

        // TABLE: Datos
        float[] columnWidths = {70f, 100f, 70f, 150f, 80f, 80f};
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).setWidth(UnitValue.createPercentValue(100));
        String[] headers = {"ID", "Fecha", "Hora", "Proveedor", "Total", "Estado"};

        for (String header : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(header).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(teal).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPadding(5));
        }

        DecimalFormat df = new DecimalFormat("#,##0.00");
        boolean alternate = false;
        BigDecimal totalGeneral = BigDecimal.ZERO;
        int count = 0;

        // EnumMap para mejor performance con enums
        Map<StatusShopping, Integer> statusCount = new EnumMap<>(StatusShopping.class);

        for (ShoppingEntity shopping : shoppings) {
            Color bgColor = alternate ? grayLight : ColorConstants.WHITE;
            alternate = !alternate;
            count++;

            // Corrección de suma con BigDecimal
            totalGeneral = totalGeneral.add(shopping.getTotal());

            statusCount.put(shopping.getStatus(), statusCount.getOrDefault(shopping.getStatus(), 0) + 1);

            // Padding común
            float padding = 5;

            // ID de la compra
            table.addCell(new Cell().add(new Paragraph(shopping.getId().substring(shopping.getId().lastIndexOf("-") + 1)))
                    .setBackgroundColor(bgColor)
                    .setBorder(Border.NO_BORDER)
                    .setPadding(padding)
                    .setTextAlignment(TextAlignment.CENTER)); // Padding común

            // Fecha de la compra (solo la fecha)
            table.addCell(new Cell().add(new Paragraph(shopping.getDate().toString().substring(0, 10)))
                    .setBackgroundColor(bgColor)
                    .setBorder(Border.NO_BORDER)
                    .setPadding(padding)
                    .setTextAlignment(TextAlignment.CENTER)); // Padding común

            // Hora de la compra
            table.addCell(new Cell().add(new Paragraph(shopping.getDate().toString().substring(10, 19)))
                    .setBackgroundColor(bgColor)
                    .setBorder(Border.NO_BORDER)
                    .setPadding(padding)
                    .setTextAlignment(TextAlignment.CENTER)); // Padding común

            // Proveedor
            table.addCell(new Cell().add(new Paragraph(shopping.getProvider() != null ? shopping.getProvider().getName() : "N/A"))
                    .setBackgroundColor(bgColor)
                    .setBorder(Border.NO_BORDER)
                    .setPadding(padding)
                    .setTextAlignment(TextAlignment.CENTER)); // Padding común

            // Total de la compra
            table.addCell(new Cell().add(new Paragraph("$ " + df.format(shopping.getTotal())))
                    .setBackgroundColor(bgColor)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(Border.NO_BORDER)
                    .setPadding(padding)
                    .setTextAlignment(TextAlignment.CENTER)); // Padding común


            StatusShopping status = shopping.getStatus();
            Color statusColor = switch (status) {
                case PENDIENTE -> red;
                case RECIBIDO, FACTURADA, PAGADO -> teal;
                case CANCELADA -> grayDark;
                case DEVUELTA -> orange;
            };

            // Etiqueta de estado con solo el texto coloreado
            Paragraph statusLabel = new Paragraph(status.toString())
                    .setFontColor(ColorConstants.WHITE)
                    .setBackgroundColor(statusColor)
                    .setFontSize(9)
                    .setPadding(3)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMargin(3)
                    .setBorderRadius(new BorderRadius(6));
            table.addCell(new Cell().add(statusLabel).setBackgroundColor(bgColor).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        }

        document.add(table);

        // RESUMEN DE TOTALES CON ESTILO
        document.add(new Paragraph("\nResumen")
                .setFontSize(16).setBold().setTextAlignment(TextAlignment.LEFT).setMarginTop(15));

        Table resumenTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(10);

        resumenTable.addCell(new Cell().add(new Paragraph("Total de compras"))
                .setBold()
                .setBackgroundColor(grayLight)
                .setPadding(5)
                .setBorder(Border.NO_BORDER));

        resumenTable.addCell(new Cell().add(new Paragraph(String.valueOf(count)))
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(5)
                .setBorder(Border.NO_BORDER));

        resumenTable.addCell(new Cell().add(new Paragraph("Monto total"))
                .setBold()
                .setBackgroundColor(grayLight)
                .setPadding(5)
                .setBorder(Border.NO_BORDER));

        resumenTable.addCell(new Cell().add(new Paragraph("$ " + df.format(totalGeneral)))
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(5)
                .setBorder(Border.NO_BORDER));

        for (Map.Entry<StatusShopping, Integer> entry : statusCount.entrySet()) {
            resumenTable.addCell(new Cell().add(new Paragraph(entry.getKey().toString()))
                    .setBackgroundColor(grayLight).setPadding(5).setBorder(Border.NO_BORDER));
            resumenTable.addCell(new Cell().add(new Paragraph(entry.getValue() + " compras"))
                    .setTextAlignment(TextAlignment.RIGHT).setPadding(5).setBorder(Border.NO_BORDER));
        }

        document.add(resumenTable);

        // PIE DE PÁGINA
        Paragraph footer = new Paragraph("Reporte generado automáticamente el " + generatedDate.substring(0, 19))
                .setFontSize(9).setTextAlignment(TextAlignment.CENTER).setPaddingTop(30);
        document.add(footer);

        document.close();
        return byteArrayOutputStream.toByteArray();*/
        return null;
    }



    private UserEntity validateUser(String idUser) {
        return this.userService.getByUser(idUser);
    }

    private ProviderEntity validateProvider(String idProvider) {
        return this.providerService.getProviderById(idProvider);
    }

    private ShoppingEntity buildShoppingEntity(ShoppingDto dto, UserEntity user, ProviderEntity provider) {
        dto.setDate(new Date());
        dto.setStatus(StatusShopping.PAID);
        dto.setUser(this.userMapper.convertToDto(user));
        dto.setProvider(this.providerMapper.convertToDto(provider));
        return this.shoppingMapper.convertToEntity(dto);
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
