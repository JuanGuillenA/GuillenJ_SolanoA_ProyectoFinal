package ec.edu.ups.citas.service;

import ec.edu.ups.citas.filters.CorsFilter;
import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Punto de entrada JAX-RS: registra recursos y filtros.
 */
@ApplicationPath("/rest")
@DeclareRoles({"admin","medico","paciente"})
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> clases = new HashSet<>();
        // recursos JAX-RS
        clases.add(AuthResource.class);
        clases.add(CitaResource.class);
        clases.add(EspecialidadResource.class);
        clases.add(HorarioResource.class);
        clases.add(MedicoResource.class);
        clases.add(ReportResource.class);
        clases.add(UsuarioResource.class);
        // filtro CORS
        clases.add(CorsFilter.class);
        return clases;
    }
}
