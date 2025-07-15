package ec.edu.ups.citas.dto;

import java.io.Serializable;

public class UsuarioDTO implements Serializable {
	
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String rol;

    public UsuarioDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
