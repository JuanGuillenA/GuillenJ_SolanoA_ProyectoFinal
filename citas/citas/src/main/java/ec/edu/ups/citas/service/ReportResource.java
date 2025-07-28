package ec.edu.ups.citas.service;

import ec.edu.ups.citas.business.ReportService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.time.LocalDate;

@Path("/reportes")
@RolesAllowed("Admin")
public class ReportResource {

    @Inject
    private ReportService reportService;

    // — PDF de ocupación por médico —
    @GET
    @Path("/ocupacion/pdf")
    @Produces("application/pdf")
    public Response ocupacionPdf(
        @QueryParam("medicoId") Long medicoId,
        @QueryParam("desde")   String isoDesde,
        @QueryParam("hasta")   String isoHasta
    ) throws Exception {
        LocalDate desde = LocalDate.parse(isoDesde);
        LocalDate hasta = LocalDate.parse(isoHasta);

        byte[] pdf = reportService.generarReporteOcupacionPdf(medicoId, desde, hasta);

        // Nombre dinámico: ocupacion_medico_{id}_{desde}_{hasta}.pdf
        String filename = String.format(
            "ocupacion_medico_%d_%s_%s.pdf",
            medicoId,
            isoDesde.replaceAll(":", "-"),
            isoHasta.replaceAll(":", "-")
        );

        return Response.ok(pdf)
                       .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                       .build();
    }

    // — Excel de ocupación por médico —
    @GET
    @Path("/ocupacion/excel")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response ocupacionExcel(
        @QueryParam("medicoId") Long medicoId,
        @QueryParam("desde")   String isoDesde,
        @QueryParam("hasta")   String isoHasta
    ) throws Exception {
        LocalDate desde = LocalDate.parse(isoDesde);
        LocalDate hasta = LocalDate.parse(isoHasta);

        byte[] xlsx = reportService.generarReporteOcupacionExcel(medicoId, desde, hasta);

        // Nombre dinámico: ocupacion_medico_{id}_{desde}_{hasta}.xlsx
        String filename = String.format(
            "ocupacion_medico_%d_%s_%s.xlsx",
            medicoId,
            isoDesde.replaceAll(":", "-"),
            isoHasta.replaceAll(":", "-")
        );

        return Response.ok(xlsx)
                       .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                       .build();
    }

    // — PDF de citas por especialidad —
    @GET
    @Path("/especialidades/pdf")
    @Produces("application/pdf")
    public Response especialidadPdf(
        @QueryParam("desde") String isoDesde,
        @QueryParam("hasta") String isoHasta
    ) throws Exception {
        LocalDate desde = LocalDate.parse(isoDesde);
        LocalDate hasta = LocalDate.parse(isoHasta);

        byte[] pdf = reportService.generarReporteOcupacionEspecialidadPdf(desde, hasta);

        // Nombre dinámico: citas_especialidades_{desde}_{hasta}.pdf
        String filename = String.format(
            "citas_especialidades_%s_%s.pdf",
            isoDesde.replaceAll(":", "-"),
            isoHasta.replaceAll(":", "-")
        );

        return Response.ok(pdf)
                       .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                       .build();
    }

    // — Excel de citas por especialidad —
    @GET
    @Path("/especialidades/excel")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response especialidadExcel(
        @QueryParam("desde") String isoDesde,
        @QueryParam("hasta") String isoHasta
    ) throws Exception {
        LocalDate desde = LocalDate.parse(isoDesde);
        LocalDate hasta = LocalDate.parse(isoHasta);

        byte[] xlsx = reportService.generarReporteOcupacionEspecialidadExcel(desde, hasta);

        // Nombre dinámico: citas_especialidades_{desde}_{hasta}.xlsx
        String filename = String.format(
            "citas_especialidades_%s_%s.xlsx",
            isoDesde.replaceAll(":", "-"),
            isoHasta.replaceAll(":", "-")
        );

        return Response.ok(xlsx)
                       .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                       .build();
    }

    // — PDF de citas agrupadas por médico —
    @GET
    @Path("/medico/{id}/pdf")
    @Produces("application/pdf")
    public Response citasPorMedicoPdf(
        @PathParam("id") Long medicoId,
        @QueryParam("desde") String isoDesde,
        @QueryParam("hasta") String isoHasta
    ) throws Exception {
        LocalDate desde = LocalDate.parse(isoDesde);
        LocalDate hasta = LocalDate.parse(isoHasta);

        byte[] pdf = reportService.generarReporteCitasPorMedicoPdf(medicoId, desde, hasta);

        // Nombre dinámico: citas_medico_{id}_{desde}_{hasta}.pdf
        String filename = String.format(
            "citas_medico_%d_%s_%s.pdf",
            medicoId,
            isoDesde.replaceAll(":", "-"),
            isoHasta.replaceAll(":", "-")
        );

        return Response.ok(pdf)
                       .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                       .build();
    }

    // — Excel de citas agrupadas por médico —
    @GET
    @Path("/medico/{id}/excel")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response citasPorMedicoExcel(
        @PathParam("id") Long medicoId,
        @QueryParam("desde") String isoDesde,
        @QueryParam("hasta") String isoHasta
    ) throws Exception {
        LocalDate desde = LocalDate.parse(isoDesde);
        LocalDate hasta = LocalDate.parse(isoHasta);

        byte[] xlsx = reportService.generarReporteCitasPorMedicoExcel(medicoId, desde, hasta);

        // Nombre dinámico: citas_medico_{id}_{desde}_{hasta}.xlsx
        String filename = String.format(
            "citas_medico_%d_%s_%s.xlsx",
            medicoId,
            isoDesde.replaceAll(":", "-"),
            isoHasta.replaceAll(":", "-")
        );

        return Response.ok(xlsx)
                       .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                       .build();
    }
}
