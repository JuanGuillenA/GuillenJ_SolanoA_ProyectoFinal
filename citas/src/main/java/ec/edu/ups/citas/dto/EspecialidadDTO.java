package ec.edu.ups.citas.dto;

import java.io.Serializable;

public class EspecialidadDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;

    public EspecialidadDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
