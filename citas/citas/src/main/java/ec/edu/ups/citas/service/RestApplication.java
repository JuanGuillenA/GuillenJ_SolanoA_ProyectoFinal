package ec.edu.ups.citas.service;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/rest")
@DeclareRoles({"admin","medico","paciente"})
public class RestApplication extends Application {}
