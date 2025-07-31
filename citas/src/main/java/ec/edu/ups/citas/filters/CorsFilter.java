package ec.edu.ups.citas.filters;

import java.io.IOException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

/**
 * Filtro JAX-RS para habilitar CORS (preflight y respuestas normales).
 */
@Provider
@PreMatching
@Priority(Priorities.HEADER_DECORATOR)
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String ALLOWED_ORIGIN  = "*";
    private static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS";
    private static final String ALLOWED_HEADERS =
        "Content-Type, Authorization, ngrok-skip-browser-warning";
    private static final String ALLOW_CREDENTIALS = "true";

    // Abortamos el preflight con todas las cabeceras CORS
    @Override
    public void filter(ContainerRequestContext req) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            req.abortWith(
                Response.ok()
                    .header("Access-Control-Allow-Origin", ALLOWED_ORIGIN)
                    .header("Access-Control-Allow-Methods", ALLOWED_METHODS)
                    .header("Access-Control-Allow-Headers", ALLOWED_HEADERS)
                    .header("Access-Control-Allow-Credentials", ALLOW_CREDENTIALS)
                    .build()
            );
        }
    }

    // Añadimos las cabeceras en todas las respuestas normales
    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) throws IOException {
        res.getHeaders().putSingle("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        res.getHeaders().putSingle("Access-Control-Allow-Methods", ALLOWED_METHODS);
        res.getHeaders().putSingle("Access-Control-Allow-Headers", ALLOWED_HEADERS);
        res.getHeaders().putSingle("Access-Control-Allow-Credentials", ALLOW_CREDENTIALS);
    }
}
