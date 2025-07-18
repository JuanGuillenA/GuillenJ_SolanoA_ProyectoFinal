package ec.edu.ups.citas.service;

import ec.edu.ups.citas.business.MedicoBusiness;
import ec.edu.ups.citas.dto.MedicoDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;
import java.util.List;

@Path("/medicos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MedicoResource {

    @Inject
    private MedicoBusiness medBus;

    @POST
    public Response crear(MedicoDTO dto, @Context UriInfo uriInfo) {
        MedicoDTO creado = medBus.crear(dto);
        URI uri = uriInfo.getAbsolutePathBuilder()
                         .path(creado.getId().toString())
                         .build();
        return Response.created(uri)
                       .entity(creado)
                       .build();
    }

    @GET
    public Response listar(
        @QueryParam("especialidadId") Long espId,
        @QueryParam("buscar")        String texto
    ) {
        List<MedicoDTO> lista;
        if (espId != null) {
            lista = medBus.listarPorEspecialidad(espId);
        } else if (texto != null && !texto.isBlank()) {
            lista = medBus.buscarPorNombre(texto);
        } else {
            lista = medBus.listarTodos();
        }
        return Response.ok(lista).build();
    }

    @GET @Path("{id}")
    public Response ver(@PathParam("id") Long id) {
        MedicoDTO dto = medBus.buscarPorId(id);
        if (dto == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(dto).build();
    }

    @PUT @Path("{id}")
    public Response modificar(@PathParam("id") Long id, MedicoDTO dto) {
        dto.setId(id);
        MedicoDTO actualizado = medBus.actualizar(dto);
        if (actualizado == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(actualizado).build();
    }

    @DELETE @Path("{id}")
    public Response eliminar(@PathParam("id") Long id) {
    	boolean borrado = medBus.eliminar(id);
        if (borrado) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
