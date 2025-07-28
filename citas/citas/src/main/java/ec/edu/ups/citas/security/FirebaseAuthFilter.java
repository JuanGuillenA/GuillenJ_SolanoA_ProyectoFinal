package ec.edu.ups.citas.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import ec.edu.ups.citas.business.UsuarioBusiness;
import ec.edu.ups.citas.dto.UsuarioDTO;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.security.Principal;

@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class FirebaseAuthFilter implements ContainerRequestFilter {

    @Inject
    private UsuarioBusiness usuarioBus;   // inyectamos tu business para buscar el rol real

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String auth = ctx.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        String tokenString = auth.substring(7);
        try {
            // 1) Verificamos el ID token con Firebase
            FirebaseToken decoded = FirebaseAuth.getInstance()
                                                .verifyIdToken(tokenString);
            String uid = decoded.getUid();

            // 2) Buscamos en la base de datos para obtener el rol verdadero
            UsuarioDTO usuario = usuarioBus.buscarPorFirebaseUid(uid);
            String role = (usuario != null && usuario.getRole() != null)
                          ? usuario.getRole().toLowerCase()
                          : "paciente";

            // 3) Creamos un SecurityContext que responde según ese rol
            SecurityContext sc = new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> uid;
                }

                @Override
                public boolean isUserInRole(String r) {
                    return role.equals(r.toLowerCase());
                }

                @Override
                public boolean isSecure() {
                    return "https".equals(ctx.getUriInfo()
                                             .getRequestUri()
                                             .getScheme());
                }

                @Override
                public String getAuthenticationScheme() {
                    return "Bearer";
                }
            };
            ctx.setSecurityContext(sc);

        } catch (Exception e) {
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
