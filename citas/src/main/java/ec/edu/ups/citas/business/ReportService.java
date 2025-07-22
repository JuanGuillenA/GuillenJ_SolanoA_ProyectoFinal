package ec.edu.ups.citas.business;

import ec.edu.ups.citas.dao.CitaDAO;
import ec.edu.ups.citas.modelo.Cita;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Stateless
public class ReportService {

    @Inject
    private CitaDAO citaDAO;

    /** PDF de ocupación por médico */
    public byte[] generarReporteOcupacionPdf(Long medicoId, LocalDate desde, LocalDate hasta)
            throws Exception {
        List<Cita> citas = citaDAO.listarPorMedicoYFechas(medicoId, desde, hasta);
        InputStream jrxml = getClass().getResourceAsStream("/reports/ocupacion_medico.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxml);
        var ds = new JRBeanCollectionDataSource(citas);
        Map<String,Object> params = Map.of(
            "MedicoId", medicoId,
            "FechaDesde", desde,
            "FechaHasta", hasta
        );
        JasperPrint print = JasperFillManager.fillReport(jasperReport, params, ds);
        return JasperExportManager.exportReportToPdf(print);
    }

    /** PDF de citas agrupadas por especialidad */
    public byte[] generarReporteEspecialidadPdf(LocalDate desde, LocalDate hasta)
            throws Exception {
        List<Cita> citas = citaDAO.listarPorFechas(desde, hasta);
        InputStream jrxml = getClass().getResourceAsStream("/reports/ocupacion_especialidad.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxml);
        var ds = new JRBeanCollectionDataSource(citas);
        Map<String,Object> params = Map.of(
            "FechaDesde", desde,
            "FechaHasta", hasta
        );
        JasperPrint print = JasperFillManager.fillReport(jasperReport, params, ds);
        return JasperExportManager.exportReportToPdf(print);
    }

    /** Excel de ocupación por médico */
    public byte[] generarReporteOcupacionExcel(Long medicoId, LocalDate desde, LocalDate hasta)
            throws IOException {
        List<Cita> citas = citaDAO.listarPorMedicoYFechas(medicoId, desde, hasta);
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Ocupación Médico");
        // Cabecera
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Fecha");
        header.createCell(1).setCellValue("Hora");
        header.createCell(2).setCellValue("Paciente");
        header.createCell(3).setCellValue("Estado");
        // Datos
        int rowNum = 1;
        for (Cita c : citas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(c.getFecha().toString());
            row.createCell(1).setCellValue(c.getHora().toString());
            row.createCell(2).setCellValue(c.getPaciente().getDisplayName());
            row.createCell(3).setCellValue(c.getEstado().name());
        }
        // Total
        Row total = sheet.createRow(rowNum);
        total.createCell(2).setCellValue("Total citas:");
        total.createCell(3).setCellValue(citas.size());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        wb.close();
        return baos.toByteArray();
    }

    /** Excel de citas por especialidad */
    public byte[] generarReporteEspecialidadExcel(LocalDate desde, LocalDate hasta)
            throws IOException {
        List<Cita> citas = citaDAO.listarPorFechas(desde, hasta);
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Citas Especialidades");
        // Cabecera
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Especialidad");
        header.createCell(1).setCellValue("Médico");
        header.createCell(2).setCellValue("Paciente");
        header.createCell(3).setCellValue("Fecha");
        header.createCell(4).setCellValue("Hora");
        header.createCell(5).setCellValue("Estado");
        // Datos
        int rowNum = 1;
        for (Cita c : citas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(c.getMedico().getEspecialidad().getNombre());
            row.createCell(1).setCellValue(c.getMedico().getNombre() + " " + c.getMedico().getApellido());
            row.createCell(2).setCellValue(c.getPaciente().getDisplayName());
            row.createCell(3).setCellValue(c.getFecha().toString());
            row.createCell(4).setCellValue(c.getHora().toString());
            row.createCell(5).setCellValue(c.getEstado().name());
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        wb.close();
        return baos.toByteArray();
    }
}
