package ec.edu.ups.citas.service;

import ec.edu.ups.citas.business.CitaBusiness;
import ec.edu.ups.citas.dto.CitaDTO;
import ec.edu.ups.citas.modelo.Estado;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Path("/citas")
@RolesAllowed("paciente")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CitaResource {
    @Inject
    private CitaBusiness citaBus;

    @POST
    public Response crear(CitaDTO dto, @Context UriInfo uri) {
        try {
            CitaDTO creado = citaBus.crear(dto);
            URI location = uri.getAbsolutePathBuilder()
                              .path(creado.getId().toString())
                              .build();
            return Response.created(location)
                           .entity(creado)
                           .build();

        } catch (IllegalArgumentException e) {
            // datos inválidos de paciente/horario/médico
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("error", e.getMessage()))
                           .build();
        } catch (IllegalStateException e) {
            // ya existe otra cita para ese médico/fecha/hora
            return Response.status(Response.Status.CONFLICT)
                           .entity(Map.of("error", e.getMessage()))
                           .build();
        }
    }

    @GET
    @PermitAll
    @Path("/historial")
    public Response historial(
        @QueryParam("pacienteId")   Long pacienteId,
        @QueryParam("medicoId")     Long medicoId,
        @QueryParam("estado")       Estado estado,
        @QueryParam("desde")        String isoDesde,
        @QueryParam("hasta")        String isoHasta,
        @QueryParam("especialidad") String especialidad
    ) {
        LocalDate desde  = isoDesde  != null ? LocalDate.parse(isoDesde)  : null;
        LocalDate hasta  = isoHasta  != null ? LocalDate.parse(isoHasta)  : null;
        List<CitaDTO> lista;

        // ───── FILTROS CENTRADOS EN PACIENTE ─────

        // 1) Solo paciente
        if (pacienteId != null
            && medicoId == null && estado == null
            && desde == null  && hasta == null
            && especialidad == null) {
            lista = citaBus.listarPorPaciente(pacienteId);

        // 2) Paciente + Fecha
        } else if (pacienteId != null
            && desde != null && hasta != null
            && medicoId == null && estado == null
            && especialidad == null) {
            lista = citaBus.listarPorPacienteYFechas(pacienteId, desde, hasta);

        // 3) Paciente + Médico
        } else if (pacienteId != null
            && medicoId != null
            && desde == null  && hasta == null
            && estado == null && especialidad == null) {
            lista = citaBus.listarPorPacienteYMedico(pacienteId, medicoId);

        // 4) Paciente + Especialidad
        } else if (pacienteId != null
            && especialidad != null
            && medicoId == null && estado == null
            && desde == null  && hasta == null) {
            lista = citaBus.listarPorPacienteYEspecialidad(pacienteId, especialidad);

        // 5) Paciente + Estado
        } else if (pacienteId != null
            && estado != null
            && medicoId == null && especialidad == null
            && desde == null  && hasta == null) {
            lista = citaBus.listarPorPacienteYEstado(pacienteId, estado);

        // 6) Paciente + Médico + Fecha
        } else if (pacienteId != null
            && medicoId != null
            && desde  != null && hasta  != null
            && estado == null && especialidad == null) {
            lista = citaBus.listarPorPacienteYMedicoYFechas(
                        pacienteId, medicoId, desde, hasta);

        // 7) Paciente + Especialidad + Fecha
        } else if (pacienteId != null
            && especialidad != null
            && desde  != null && hasta  != null
            && medicoId == null && estado == null) {
            lista = citaBus.listarPorPacienteYEspecialidadYFechas(
                        pacienteId, especialidad, desde, hasta);

        // 8) Paciente + Estado + Fecha
        } else if (pacienteId != null
            && estado       != null
            && desde  != null && hasta  != null
            && medicoId == null && especialidad == null) {
            lista = citaBus.listarPorPacienteYEstadoYFechas(
                        pacienteId, estado, desde, hasta);

        // 9) Paciente + Estado + Fecha + Especialidad + Médico
        } else if (pacienteId != null
            && estado       != null
            && desde  != null && hasta  != null
            && especialidad != null
            && medicoId     != null) {
            lista = citaBus.listarPorPacienteYEstadoFechasEspecialidadMedico(
                        pacienteId, estado, desde, hasta,
                        especialidad, medicoId);

        // ───── OTROS FILTROS GENERALES ─────

        } else if (estado != null && desde != null && hasta != null && medicoId != null) {
            // Estado + Fechas + Médico (sin paciente)
            lista = citaBus.listarPorEstadoYFechasYMedico(estado, desde, hasta, medicoId);

        } else if (estado != null && desde != null && hasta != null && especialidad != null) {
            // Estado + Fechas + Especialidad (sin paciente)
            lista = citaBus.listarPorEstadoYFechasYEspecialidad(estado, desde, hasta, especialidad);

        } else if (desde != null && hasta != null && especialidad != null && medicoId != null) {
            // Fechas + Especialidad + Médico (sin paciente)
            lista = citaBus.listarPorFechasYEspecialidadYMedico(desde, hasta, especialidad, medicoId);

        } else if (estado != null && desde != null && hasta != null
                   && especialidad != null && medicoId != null) {
            // Estado + Fechas + Especialidad + Médico (sin paciente)
            lista = citaBus.listarPorEstadoYFechasYEspecialidadYMedico(
                        estado, desde, hasta, especialidad, medicoId);

        } else if (estado != null && desde != null && hasta != null) {
            lista = citaBus.listarPorEstadoYFechas(estado, desde, hasta);

        } else if (estado != null && especialidad != null) {
            lista = citaBus.listarPorEstadoYEspecialidad(estado, especialidad);

        } else if (medicoId != null && especialidad != null) {
            lista = citaBus.listarPorEspecialidadYMedico(especialidad, medicoId);

        } else if (medicoId != null && desde != null && hasta != null) {
            lista = citaBus.listarPorMedicoYFechas(medicoId, desde, hasta);

        } else if (pacienteId == null && medicoId != null && estado != null) {
            lista = citaBus.listarPorMedicoYEstado(medicoId, estado);

        } else if (medicoId != null) {
            lista = citaBus.listarPorMedico(medicoId);

        } else if (pacienteId == null && estado != null) {
            lista = citaBus.listarPorEstado(estado);

        } else if (especialidad != null) {
            lista = citaBus.listarPorEspecialidad(especialidad);

        } else if (desde != null && hasta != null) {
            lista = citaBus.listarPorFechas(desde, hasta);

        } else {
            // Si no hay filtros, devuelvo todo
            lista = citaBus.listarTodos();
        }

        return Response.ok(lista).build();
    }

    @GET
    @Path("{id}")
    public Response ver(@PathParam("id") Long id) {
        CitaDTO dto = citaBus.buscarPorId(id);
        if (dto == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(dto).build();
    }

    @PUT
    @Path("{id}")
    public Response modificar(@PathParam("id") Long id, CitaDTO dto) {
        dto.setId(id);
        try {
            CitaDTO actualizado = citaBus.actualizar(dto);
            return Response.ok(actualizado).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("error", e.getMessage()))
                           .build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                           .entity(Map.of("error", e.getMessage()))
                           .build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") Long id) {
        boolean borrado = citaBus.eliminar(id);
        if (borrado) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    @GET
    @RolesAllowed("PACIENTE")
    public Response listarTodos() {
        List<CitaDTO> lista = citaBus.listarTodos();
        return Response.ok(lista).build();
    }
}