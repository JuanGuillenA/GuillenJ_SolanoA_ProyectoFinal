package ec.edu.ups.citas.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import ec.edu.ups.citas.dao.CitaDAO;
import ec.edu.ups.citas.dto.CitaMedicoDTO;
import ec.edu.ups.citas.dto.OcupacionEspecialidadDTO;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class ReportService {

    @Inject
    private CitaDAO citaDAO;

    // ----------------------------------------
    // PDF: Citas por Médico
    // ----------------------------------------
    public byte[] generarReporteCitasPorMedicoPdf(Long medicoId, LocalDate desde, LocalDate hasta)
            throws DocumentException, IOException {

        // 1) Mapeo a DTO
        var datos = citaDAO
            .listarPorMedicoYFechas(medicoId, desde, hasta)
            .stream()
            .map(c -> new CitaMedicoDTO(
                c.getMedico().getNombre() + " " + c.getMedico().getApellido(),
                c.getMedico().getEspecialidad().getNombre(),
                c.getFecha(),
                c.getHora(),
                c.getEstado().name()
            ))
            .collect(Collectors.toList());

        // 2) Generación PDF con OpenPDF
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        // Título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        document.add(new Paragraph(
            "Reporte de Citas por Médico (" + desde + " → " + hasta + ")", 
            titleFont
        ));
        document.add(Chunk.NEWLINE);

        // Tabla
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        // Cabeceras con gris claro usando setGrayFill()
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        Arrays.asList("Médico", "Especialidad", "Fecha", "Hora", "Estado")
              .forEach(h -> {
                  PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                  cell.setGrayFill(0.9f);  // 90% blanco → gris claro
                  table.addCell(cell);
              });

        // Filas
        for (CitaMedicoDTO dto : datos) {
            table.addCell(dto.getMedicoNombre());
            table.addCell(dto.getEspecialidad());
            table.addCell(dto.getFecha().toString());
            table.addCell(dto.getHora().toString());
            table.addCell(dto.getEstado());
        }

        document.add(table);
        document.close();
        return baos.toByteArray();
    }

    // ----------------------------------------
    // Excel: Citas por Médico
    // ----------------------------------------
    public byte[] generarReporteCitasPorMedicoExcel(Long medicoId, LocalDate desde, LocalDate hasta)
            throws IOException {

        var datos = citaDAO
            .listarPorMedicoYFechas(medicoId, desde, hasta)
            .stream()
            .map(c -> new CitaMedicoDTO(
                c.getMedico().getNombre() + " " + c.getMedico().getApellido(),
                c.getMedico().getEspecialidad().getNombre(),
                c.getFecha(),
                c.getHora(),
                c.getEstado().name()
            ))
            .collect(Collectors.toList());

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Citas Médico");

        // Cabecera
        Row header = sheet.createRow(0);
        String[] cols = { "Médico", "Especialidad", "Fecha", "Hora", "Estado" };
        for (int i = 0; i < cols.length; i++) {
            header.createCell(i).setCellValue(cols[i]);
        }

        // Datos
        int rowNum = 1;
        for (CitaMedicoDTO dto : datos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dto.getMedicoNombre());
            row.createCell(1).setCellValue(dto.getEspecialidad());
            row.createCell(2).setCellValue(dto.getFecha().toString());
            row.createCell(3).setCellValue(dto.getHora().toString());
            row.createCell(4).setCellValue(dto.getEstado());
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        wb.close();
        return baos.toByteArray();
    }

    // ----------------------------------------
    // PDF: Ocupación por Especialidad
    // ----------------------------------------
    public byte[] generarReporteOcupacionEspecialidadPdf(LocalDate desde, LocalDate hasta)
            throws DocumentException, IOException {

        // 1) Conteo por especialidad
        Map<String, Long> conteo = citaDAO
            .listarPorFechas(desde, hasta)
            .stream()
            .collect(Collectors.groupingBy(
                c -> c.getMedico().getEspecialidad().getNombre(),
                Collectors.counting()
            ));

        long totalGeneral = conteo.values().stream().mapToLong(Long::longValue).sum();

        var datos = conteo.entrySet().stream()
            .map(e -> new OcupacionEspecialidadDTO(
                e.getKey(),
                e.getValue(),
                totalGeneral > 0
                  ? e.getValue() * 100.0 / totalGeneral
                  : 0.0
            ))
            .collect(Collectors.toList());

        // 2) Generación PDF
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        // Título
        document.add(new Paragraph(
            "Reporte de Ocupación por Especialidad (" + desde + " → " + hasta + ")", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)
        ));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        // Cabeceras con gris claro
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        Arrays.asList("Especialidad", "Total Citas", "% Ocupación")
              .forEach(h -> {
                  PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                  cell.setGrayFill(0.9f);
                  table.addCell(cell);
              });

        // Filas
        for (OcupacionEspecialidadDTO dto : datos) {
            table.addCell(dto.getEspecialidad());
            table.addCell(String.valueOf(dto.getTotalCitas()));
            table.addCell(String.format("%.2f%%", dto.getNivelOcupacion()));
        }

        document.add(table);
        document.close();
        return baos.toByteArray();
    }

    // ----------------------------------------
    // Excel: Ocupación por Especialidad
    // ----------------------------------------
    public byte[] generarReporteOcupacionEspecialidadExcel(LocalDate desde, LocalDate hasta)
            throws IOException {

        Map<String, Long> conteo = citaDAO
            .listarPorFechas(desde, hasta)
            .stream()
            .collect(Collectors.groupingBy(
                c -> c.getMedico().getEspecialidad().getNombre(),
                Collectors.counting()
            ));

        long totalGeneral = conteo.values().stream().mapToLong(Long::longValue).sum();

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Ocupación Especialidad");
        Row header = sheet.createRow(0);
        String[] cols = { "Especialidad", "Total Citas", "% Ocupación" };
        for (int i = 0; i < cols.length; i++) {
            header.createCell(i).setCellValue(cols[i]);
        }

        int rowNum = 1;
        for (var e : conteo.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(e.getKey());
            row.createCell(1).setCellValue(e.getValue());
            double porcentaje = totalGeneral > 0
                ? e.getValue() * 100.0 / totalGeneral
                : 0.0;
            row.createCell(2).setCellValue(String.format("%.2f%%", porcentaje));
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        wb.close();
        return baos.toByteArray();
    }
 // ----------------------------------------
 // PDF: Ocupación por Médico
 // ----------------------------------------
 public byte[] generarReporteOcupacionMedicoPdf(LocalDate desde, LocalDate hasta)
         throws DocumentException, IOException {

     // 1) Listar todas las citas en el rango
     var todas = citaDAO.listarPorFechas(desde, hasta);

     // 2) Agrupar por médico
     Map<String, Long> conteo = todas.stream()
         .collect(Collectors.groupingBy(
             c -> c.getMedico().getNombre() + " " + c.getMedico().getApellido(),
             Collectors.counting()
         ));

     long totalGeneral = conteo.values().stream().mapToLong(Long::longValue).sum();

     // 3) DTO con porcentaje
     List<OcupacionEspecialidadDTO> datos = conteo.entrySet().stream()
         .map(e -> new OcupacionEspecialidadDTO(
             e.getKey(),                // aquí uso el nombre de médico
             e.getValue(),
             totalGeneral > 0
               ? e.getValue() * 100.0 / totalGeneral
               : 0.0
         ))
         .collect(Collectors.toList());

     // 4) Generación PDF (muy parecido al de especialidad)
     Document document = new Document(PageSize.A4);
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     PdfWriter.getInstance(document, baos);
     document.open();

     document.add(new Paragraph(
       "Reporte de Ocupación por Médico (" + desde + " → " + hasta + ")",
       FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)
     ));
     document.add(Chunk.NEWLINE);

     PdfPTable table = new PdfPTable(3);
     table.setWidthPercentage(100);

     Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
     Arrays.asList("Médico", "Total Citas", "% Ocupación")
           .forEach(h -> {
             PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
             cell.setGrayFill(0.9f);
             table.addCell(cell);
           });

     for (OcupacionEspecialidadDTO dto : datos) {
       table.addCell(dto.getEspecialidad());             // aquí nombre del médico
       table.addCell(String.valueOf(dto.getTotalCitas()));
       table.addCell(String.format("%.2f%%", dto.getNivelOcupacion()));
     }

     document.add(table);
     document.close();
     return baos.toByteArray();
 }

 public byte[] generarReporteOcupacionMedicoExcel(LocalDate desde, LocalDate hasta)
	        throws IOException {
	    var todas = citaDAO.listarPorFechas(desde, hasta);
	    Map<String, Long> conteo = todas.stream()
	      .collect(Collectors.groupingBy(
	         c -> c.getMedico().getNombre() + " " + c.getMedico().getApellido(),
	         Collectors.counting()
	      ));
	    long totalGeneral = conteo.values().stream().mapToLong(Long::longValue).sum();

	    Workbook wb = new XSSFWorkbook();
	    Sheet sheet = wb.createSheet("Ocupación Médico");
	    Row header = sheet.createRow(0);
	    String[] cols = { "Médico", "Total Citas", "% Ocupación" };
	    for (int i = 0; i < cols.length; i++) {
	      header.createCell(i).setCellValue(cols[i]);
	    }

	    int rowNum = 1;
	    for (var e : conteo.entrySet()) {
	      Row row = sheet.createRow(rowNum++);
	      row.createCell(0).setCellValue(e.getKey());
	      row.createCell(1).setCellValue(e.getValue());
	      double porcentaje = totalGeneral > 0
	          ? e.getValue() * 100.0 / totalGeneral
	          : 0.0;
	      row.createCell(2).setCellValue(String.format("%.2f%%", porcentaje));
	    }

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    wb.write(baos);
	    wb.close();
	    return baos.toByteArray();
	}
}
