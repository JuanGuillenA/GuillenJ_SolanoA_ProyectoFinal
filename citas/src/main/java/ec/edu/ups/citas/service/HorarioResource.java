package ec.edu.ups.citas.service;

import ec.edu.ups.citas.business.HorarioBusiness;
import ec.edu.ups.citas.dto.HorarioDTO;
import ec.edu.ups.citas.dto.RecurrenciaHorarioDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;

@Path("/horarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HorarioResource {

    @Inject private HorarioBusiness horBus;

    /** Crea todos los slots según recurrencia. */
    @POST @Path("/recurrencias")
    public Response crearRecurrencia(RecurrenciaHorarioDTO dto) {
        horBus.crearRecurrencia(dto);
        return Response.noContent().build();
    }

    /** Lista todos los slots de un médico (medicoId obligatorio). */
    @GET
    public Response listar(@QueryParam("medicoId") Long medicoId) {
        if (medicoId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Falta parámetro medicoId")
                           .build();
        }
        List<HorarioDTO> lista = horBus.listarPorMedico(medicoId);
        return Response.ok(lista).build();
    }

    
    @GET @Path("{id}")
    public Response ver(@PathParam("id") Long id) {
        HorarioDTO dto = horBus.buscarPorId(id);
        return dto == null
             ? Response.status(Response.Status.NOT_FOUND).build()
             : Response.ok(dto).build();
    }

   
    @PUT @Path("{id}")
    public Response modificarParcial(
            @PathParam("id") Long id,
            HorarioDTO dto
    ) {
        dto.setId(id);
        HorarioDTO mod = horBus.actualizarParcial(dto);
        return mod == null
             ? Response.status(Response.Status.NOT_FOUND).build()
             : Response.ok(mod).build();
    }

    /** Elimina un slot por su ID. */
    @DELETE @Path("{id}")
    public Response eliminar(@PathParam("id") Long id) {
        boolean ok = horBus.eliminar(id);
        return ok
             ? Response.noContent().build()
             : Response.status(Response.Status.NOT_FOUND).build();
    }
}