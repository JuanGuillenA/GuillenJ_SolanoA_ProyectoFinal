// src/main/java/ec/edu/ups/citas/service/AuthResource.java
package ec.edu.ups.citas.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import ec.edu.ups.citas.business.UsuarioBusiness;
import ec.edu.ups.citas.dto.UsuarioDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private UsuarioBusiness usuarioBus;

    public static class TokenRequest {
        public String idToken;
    }

    public static class AuthResponse {
        public String id;      // firebaseUid
        public String email;
        public String role;
    }

    @POST @Path("/verify")
    public Response verify(TokenRequest req) {
        try {
            FirebaseToken token = FirebaseAuth.getInstance()
                                              .verifyIdToken(req.idToken);

            String uid    = token.getUid();
            String email  = token.getEmail();
            String name   = token.getName();

            // Nuevo: busca o crea paciente
            UsuarioDTO u = usuarioBus.buscarPorFirebaseUid(uid, email, name);

            AuthResponse resp = new AuthResponse();
            resp.id    = u.getFirebaseUid();
            resp.email = u.getEmail();
            resp.role  = u.getRole();
            return Response.ok(resp).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity("Token inválido o usuario no autorizado")
                           .build();
        }
    }
}