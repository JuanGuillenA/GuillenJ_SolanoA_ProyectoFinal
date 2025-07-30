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

    // ----------------------------------------------------
    // 1) Ocupación GLOBAL de todos los médicos
    // ----------------------------------------------------

    @GET
    @Path("/ocupacion/medicos/pdf")
    @Produces("application/pdf")
    public Response ocupacionMedicosPdf(
        @QueryParam("desde") String isoDesde,
        @QueryParam("hasta") String isoHasta
    ) throws Exception {
        LocalDate desde = LocalDate.parse(isoDesde);
        LocalDate hasta = LocalDate.parse(isoHasta);

        byte[] pdf = reportService.generarReporteOcupacionMedicoPdf(desde, hasta);

        String filename = String.format(
            "ocupacion_medicos_%s_%s.pdf",
            isoDesde.replaceAll(":", "-"),
            isoHasta.replaceAll(":", "-")
        );

        return Response.ok(pdf)
                       .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                       .build();
    }

    @GET
    @Path("/ocupacion/medicos/excel")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response ocupacionMedicosExcel(
        @QueryParam("desde") String isoDesde,
        @QueryParam("hasta") String isoHasta
    ) throws Exception {
        LocalDate desde = LocalDate.parse(isoDesde);
        LocalDate hasta = LocalDate.parse(isoHasta);

        byte[] xlsx = reportService.generarReporteOcupacionMedicoExcel(desde, hasta);

        String filename = String.format(
            "ocupacion_medicos_%s_%s.xlsx",
            isoDesde.replaceAll(":", "-"),
            isoHasta.replaceAll(":", "-")
        );

        return Response.ok(xlsx)
                       .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                       .build();
    }

    // ----------------------------------------------------
    // 2) Detalle de “Citas por Médico” (un solo doctor)
    // ----------------------------------------------------

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

    // ----------------------------------------------------
    // 3) Ocupación por Especialidad (igual que antes)
    // ----------------------------------------------------

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

        String filename = String.format(
            "citas_especialidades_%s_%s.pdf",
            isoDesde.replaceAll(":", "-"),
            isoHasta.replaceAll(":", "-")
        );

        return Response.ok(pdf)
                       .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                       .build();
    }

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

        String filename = String.format(
            "citas_especialidades_%s_%s.xlsx",
            isoDesde.replaceAll(":", "-"),
            isoHasta.replaceAll(":", "-")
        );

        return Response.ok(xlsx)
                       .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                       .build();
    }
}
