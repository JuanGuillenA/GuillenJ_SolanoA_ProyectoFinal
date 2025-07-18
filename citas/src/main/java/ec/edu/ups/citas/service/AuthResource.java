package ec.edu.ups.citas.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import ec.edu.ups.citas.dao.UsuarioDAO;
import ec.edu.ups.citas.modelo.Usuario;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    public static class TokenRequest {
        public String idToken;
    }

    public static class AuthResponse {
        public String uid;
        public String email;
        public String role;
    }

    @Inject
    private UsuarioDAO usuarioDAO;

    @POST
    @Path("/verify")
    public Response verify(TokenRequest req) {
        try {
            FirebaseToken token = FirebaseAuth
                .getInstance()
                .verifyIdToken(req.idToken);

            AuthResponse resp = new AuthResponse();
            resp.uid   = token.getUid();
            resp.email = token.getEmail();

            // buscamos el usuario en nuestra BD por email
            Usuario u = usuarioDAO.buscarPorEmail(resp.email);
            resp.role = (u != null ? u.getRol().name() : "ROLE_PACIENTE");

            return Response.ok(resp).build();
        } catch (Exception e) {
            return Response
                     .status(Response.Status.UNAUTHORIZED)
                     .entity("Token inválido")
                     .build();
        }
    }
}
