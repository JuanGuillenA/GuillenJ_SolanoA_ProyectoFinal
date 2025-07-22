package ec.edu.ups.citas.service;

import ec.edu.ups.citas.business.UsuarioBusiness;
import ec.edu.ups.citas.dto.UsuarioDTO;
import ec.edu.ups.citas.modelo.Rol;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;
import java.util.List;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    private UsuarioBusiness usrBus;

    @POST
    public Response crear(UsuarioDTO dto, @Context UriInfo uriInfo) {
        UsuarioDTO creado = usrBus.crear(dto);
        URI uri = uriInfo.getAbsolutePathBuilder()
                         .path(String.valueOf(creado.getId()))
                         .build();
        return Response.created(uri).entity(creado).build();
    }

    @GET
    public Response listar(@QueryParam("rol") Rol rol) {
        List<UsuarioDTO> lista = (rol == null)
            ? usrBus.listarTodos()
            : usrBus.listarPorRol(rol);
        return Response.ok(lista).build();
    }

    @GET
    @Path("{id}")
    public Response ver(@PathParam("id") Long id) {
        UsuarioDTO dto = usrBus.buscarPorId(id);
        if (dto == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(dto).build();
    }

    @GET
    @Path("firebase/{uid}")
    public Response verPorFirebaseUid(@PathParam("uid") String uid) {
        UsuarioDTO dto = usrBus.buscarPorFirebaseUid(uid);
        if (dto == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(dto).build();
    }

    @PUT @Path("{id}")
    public Response modificar(@PathParam("id") Long id, UsuarioDTO dto) {
        dto.setId(id);
        UsuarioDTO actualizado = usrBus.actualizar(dto);
        if (actualizado == null) {
          return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(actualizado).build();
    }

    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") Long id) {
        boolean borrado = usrBus.eliminar(id);
        if (borrado) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}