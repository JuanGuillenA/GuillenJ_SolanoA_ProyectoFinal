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

    @Inject private ReportService reportService;

    // — PDF de ocupación por médico (ya tenías este) —
    @GET @Path("/ocupacion/pdf")
    @Produces("application/pdf")
    public Response ocupacionPdf(
      @QueryParam("medicoId") Long medId,
      @QueryParam("desde")   String isoDesde,
      @QueryParam("hasta")   String isoHasta) throws Exception {
      LocalDate desde = LocalDate.parse(isoDesde);
      LocalDate hasta = LocalDate.parse(isoHasta);
      byte[] pdf = reportService.generarReporteOcupacionPdf(medId, desde, hasta);
      return Response.ok(pdf)
        .header("Content-Disposition", "attachment; filename=\"ocupacion_medico.pdf\"")
        .build();
    }

    // — Excel de ocupación por médico —
    @GET @Path("/ocupacion/excel")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response ocupacionExcel(
      @QueryParam("medicoId") Long medId,
      @QueryParam("desde")   String isoDesde,
      @QueryParam("hasta")   String isoHasta) throws Exception {
      LocalDate desde = LocalDate.parse(isoDesde);
      LocalDate hasta = LocalDate.parse(isoHasta);
      byte[] xlsx = reportService.generarReporteOcupacionExcel(medId, desde, hasta);
      return Response.ok(xlsx)
        .header("Content-Disposition", "attachment; filename=\"ocupacion_medico.xlsx\"")
        .build();
    }

    // — PDF de citas por especialidad —
    @GET @Path("/especialidades/pdf")
    @Produces("application/pdf")
    public Response especialidadPdf(
      @QueryParam("desde") String isoDesde,
      @QueryParam("hasta") String isoHasta) throws Exception {
      LocalDate desde = LocalDate.parse(isoDesde);
      LocalDate hasta = LocalDate.parse(isoHasta);
      byte[] pdf = reportService.generarReporteEspecialidadPdf(desde, hasta);
      return Response.ok(pdf)
        .header("Content-Disposition", "attachment; filename=\"citas_especialidades.pdf\"")
        .build();
    }

    // — Excel de citas por especialidad —
    @GET @Path("/especialidades/excel")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response especialidadExcel(
      @QueryParam("desde") String isoDesde,
      @QueryParam("hasta") String isoHasta) throws Exception {
      LocalDate desde = LocalDate.parse(isoDesde);
      LocalDate hasta = LocalDate.parse(isoHasta);
      byte[] xlsx = reportService.generarReporteEspecialidadExcel(desde, hasta);
      return Response.ok(xlsx)
        .header("Content-Disposition", "attachment; filename=\"citas_especialidades.xlsx\"")
        .build();
    }
}