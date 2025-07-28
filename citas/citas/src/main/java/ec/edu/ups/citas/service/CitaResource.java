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

@Path("/citas")
@RolesAllowed("paciente")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CitaResource {
    @Inject
    private CitaBusiness citaBus;

    @POST
    public Response crear(CitaDTO dto, @Context UriInfo uri) {
      CitaDTO creado = citaBus.crear(dto);
      URI location = uri.getAbsolutePathBuilder()
                        .path(creado.getId().toString())
                        .build();
      return Response.created(location).entity(creado).build();
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
        LocalDate desde = (isoDesde  != null) ? LocalDate.parse(isoDesde) : null;
        LocalDate hasta = (isoHasta  != null) ? LocalDate.parse(isoHasta) : null;
        List<CitaDTO> lista;

        if (pacienteId != null && desde != null && hasta != null) {
            lista = citaBus.listarPorPacienteYFechas(pacienteId, desde, hasta);

        } else if (medicoId != null && desde != null && hasta != null) {
            lista = citaBus.listarPorMedicoYFechas(medicoId, desde, hasta);

        } else if (pacienteId != null && estado != null) {
            lista = citaBus.listarPorPacienteYEstado(pacienteId, estado);

        } else if (medicoId != null && estado != null) {
        	lista = citaBus.listarPorMedicoYEstado(medicoId, estado);

        } else if (medicoId != null && isoDesde == null && isoHasta == null) {
            lista = citaBus.listarPorMedico(medicoId);

        } else if (pacienteId != null) {
            lista = citaBus.listarPorPaciente(pacienteId);

        } else if (estado != null) {
            lista = citaBus.listarPorEstado(estado);

        } else if (especialidad != null) {
            lista = citaBus.listarPorEspecialidad(especialidad);

        } else if (desde!=null && hasta!=null) {
            lista = citaBus.listarPorFechas(desde, hasta);
        } else {
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
        CitaDTO actualizado = citaBus.actualizar(dto);
        if (actualizado == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(actualizado).build();
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
