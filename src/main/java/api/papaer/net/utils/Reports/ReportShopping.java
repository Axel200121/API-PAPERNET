package api.papaer.net.utils.Reports;

import api.papaer.net.entities.ShoppingEntity;
import api.papaer.net.utils.StatusShopping;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Component;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

@Component
public class ReportShopping {

    // Método principal para generar el PDF
    public byte[] exportToPdf(List<ShoppingEntity> shoppings) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Inicializar PdfWriter, PdfDocument y Document
        PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);
        document.setMargins(30, 20, 30, 20);

        // Agregar la cabecera al documento
        addHeader(document);

        // Agregar la tabla con las compras al documento
        addShoppingTable(shoppings, document);

        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    // Función para agregar la cabecera (logo e título) al documento
    private void addHeader(Document document) throws IOException {
        // Cargar imagen desde el classpath
        Resource resource = new ClassPathResource("images/report-shopping.png");
        ImageData data = ImageDataFactory.create(resource.getInputStream().readAllBytes());
        Image image = new Image(data);
        image.setWidth(130); // Ajusta el tamaño de la imagen
        image.setAutoScaleHeight(true);

        // Crear una tabla con dos columnas para la imagen y el título en una misma línea
        Table headerTable = new Table(2);
        headerTable.setWidth(100);
        headerTable.setTextAlignment(TextAlignment.CENTER);

        // Celda de la imagen
        Cell imageCell = new Cell().add(image).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
        headerTable.addCell(imageCell);

        // Título centrado
        Paragraph title = new Paragraph("REPORTE DE COMPRAS")
                .setFont(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD))
                .setFontSize(28)
                .setMarginTop(50)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        Cell titleCell = new Cell().add(title).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
        headerTable.addCell(titleCell);

        // Agregar la tabla de encabezado al documento
        headerTable.setMarginBottom(20);
        document.add(headerTable);
    }

    // Función para agregar la tabla con los datos de las compras
    private void addShoppingTable(List<ShoppingEntity> shoppings, Document document) {
        // Definir colores
        DeviceRgb teal = new DeviceRgb(6, 148, 162);
        DeviceRgb red = new DeviceRgb(240, 82, 82);
        DeviceRgb grayLight = new DeviceRgb(245, 245, 245);
        DeviceRgb grayDark = new DeviceRgb(100, 100, 100);
        DeviceRgb orange = new DeviceRgb(255, 179, 71); // para devuelta

        // Definir el ancho de las columnas
        float[] columnWidths = {70f, 100f, 70f, 150f, 80f, 80f};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(100);

        // Cabecera de la tabla con fondo gris
        String[] headers = {"ID", "Fecha", "Hora", "Proveedor", "Total", "Estado"};
        for (String header : headers) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(header).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(teal)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(5)
                    .setBorder(Border.NO_BORDER);
            table.addHeaderCell(headerCell);
        }

        // Formato de fecha y total
        DecimalFormat df = new DecimalFormat("#,##0.00");

        // Añadir los datos de las compras
        boolean alternate = false;
        for (ShoppingEntity shopping : shoppings) {
            DeviceRgb bgColor = alternate ? grayLight : (DeviceRgb) ColorConstants.WHITE;
            alternate = !alternate;

            // Añadir celdas con los datos de la compra
            table.addCell(new Cell().add(new Paragraph(shopping.getId().substring(shopping.getId().lastIndexOf("-") + 1)))
                    .setBackgroundColor(bgColor).setBorder(Border.NO_BORDER).setPadding(4));
            table.addCell(new Cell().add(new Paragraph(shopping.getDate().toString().substring(0, 10)))
                    .setBackgroundColor(bgColor).setBorder(Border.NO_BORDER).setPadding(4));
            table.addCell(new Cell().add(new Paragraph(shopping.getDate().toString().substring(10, 19)))
                    .setBackgroundColor(bgColor).setBorder(Border.NO_BORDER).setPadding(4));
            table.addCell(new Cell().add(new Paragraph(shopping.getProvider() != null ? shopping.getProvider().getName() : "N/A"))
                    .setBackgroundColor(bgColor).setBorder(Border.NO_BORDER).setPadding(4));
            table.addCell(new Cell().add(new Paragraph("$ " + df.format(shopping.getTotal())))
                    .setBackgroundColor(bgColor).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPadding(4));

            // Colorear estado como etiqueta (badge visual)
            StatusShopping status = shopping.getStatus();
            DeviceRgb statusColor = switch (status) {
                case PENDIENTE -> red;
                case RECIBIDO, FACTURADA, PAGADO -> teal;
                case CANCELADA -> grayDark;
                case DEVUELTA -> orange;
            };

            // Crear etiqueta visual con color de fondo y texto blanco
            Paragraph statusLabel = new Paragraph(status.toString())
                    .setFontColor(ColorConstants.WHITE)
                    .setBackgroundColor(statusColor)
                    .setFontSize(9)
                    .setPadding(3)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMargin(3)
                    .setBorderRadius(new BorderRadius(5));

            // Añadir celda que contiene la etiqueta centrada
            table.addCell(new Cell()
                    .add(statusLabel)
                    .setBackgroundColor(bgColor) // fondo alternado de la fila
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
        }
        table.setMarginTop(10);
        document.add(table);
    }
}
