package ec.edu.ups.citas.service;

import ec.edu.ups.citas.business.EspecialidadBusiness;
import ec.edu.ups.citas.dto.EspecialidadDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;
import java.util.List;

@Path("/especialidades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EspecialidadResource {

    @Inject
    private EspecialidadBusiness espBus;

    @POST
    public Response crear(EspecialidadDTO dto, @Context UriInfo uriInfo) {
        EspecialidadDTO creado = espBus.crear(dto);
        URI uri = uriInfo.getAbsolutePathBuilder()
                         .path(creado.getId().toString())
                         .build();
        return Response.created(uri)
                       .entity(creado)
                       .build();
    }

    @GET
    public Response listar() {
        List<EspecialidadDTO> lista = espBus.listarTodos();
        return Response.ok(lista).build();
    }

    @GET @Path("{id}")
    public Response ver(@PathParam("id") Long id) {
        EspecialidadDTO dto = espBus.buscarPorId(id);
        if (dto == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(dto).build();
    }

    @PUT @Path("{id}")
    public Response modificar(@PathParam("id") Long id, EspecialidadDTO dto) {
        dto.setId(id);
        EspecialidadDTO actualizado = espBus.actualizar(dto);
        if (actualizado == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(actualizado).build();
    }

    @DELETE @Path("{id}")
    public Response eliminar(@PathParam("id") Long id) {
        boolean borrado = espBus.eliminar(id);
        if (borrado) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
}
