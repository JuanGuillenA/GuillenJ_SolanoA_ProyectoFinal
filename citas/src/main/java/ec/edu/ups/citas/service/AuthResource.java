package ec.edu.ups.citas.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import ec.edu.ups.citas.business.UsuarioBusiness;
import ec.edu.ups.citas.dto.UsuarioDTO;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
        public String id;    // firebaseUid
        public String email;
        public String role;
    }

    @POST
    @Path("/verify")
    public Response verify(TokenRequest req) {
        try {
            FirebaseToken token = FirebaseAuth.getInstance()
                                              .verifyIdToken(req.idToken);
            String uid = token.getUid();

            // buscamos el usuario por firebaseUid
            UsuarioDTO u = usuarioBus.buscarPorFirebaseUid(uid);

            AuthResponse resp = new AuthResponse();
            resp.id    = uid;
            resp.email = token.getEmail();
            resp.role  = (u != null ? u.getRole() : "Paciente");
            return Response.ok(resp).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity("Token inválido")
                           .build();
        }
    }
}