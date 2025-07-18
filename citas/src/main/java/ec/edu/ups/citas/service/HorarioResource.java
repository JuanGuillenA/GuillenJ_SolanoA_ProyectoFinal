package ec.edu.ups.citas.service;

import ec.edu.ups.citas.business.HorarioBusiness;
import ec.edu.ups.citas.dto.HorarioDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Path("/horarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HorarioResource {

    @Inject
    private HorarioBusiness horBus;

    @POST
    public Response crear(HorarioDTO dto, @Context UriInfo uriInfo) {
        HorarioDTO creado = horBus.crear(dto);
        URI uri = uriInfo.getAbsolutePathBuilder()
                         .path(creado.getId().toString())
                         .build();
        return Response.created(uri)
                       .entity(creado)
                       .build();
    }

    @GET
    public Response listar(
        @QueryParam("medicoId") Long medicoId,
        @QueryParam("fecha")    String fechaIso
    ) {
        List<HorarioDTO> lista;
        if (medicoId != null && fechaIso != null) {
            LocalDate f = LocalDate.parse(fechaIso);
            lista = horBus.listarPorMedicoYFecha(medicoId, f);
        } else if (medicoId != null) {
            lista = horBus.listarPorMedico(medicoId);
        } else {
            lista = horBus.listarTodos();
        }
        return Response.ok(lista).build();
    }

    @GET @Path("{id}")
    public Response ver(@PathParam("id") Long id) {
        HorarioDTO dto = horBus.buscarPorId(id);
        if (dto == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(dto).build();
    }

    @PUT @Path("{id}")
    public Response modificar(@PathParam("id") Long id, HorarioDTO dto) {
        dto.setId(id);
        HorarioDTO actualizado = horBus.actualizar(dto);
        if (actualizado == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(actualizado).build();
    }

    @DELETE @Path("{id}")
    public Response eliminar(@PathParam("id") Long id) {
        boolean borrado = horBus.eliminar(id);
        return (borrado)
            ? Response.noContent().build()
            : Response.status(Response.Status.NOT_FOUND).build();
    }
}
