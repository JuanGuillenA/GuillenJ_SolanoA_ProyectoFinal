package ec.edu.ups.citas.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class CitaMedicoDTO {
    private String medicoNombre;
    private String especialidad;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;

    public CitaMedicoDTO(String medicoNombre, String especialidad,
                         LocalDate fecha, LocalTime hora, String estado) {
        this.medicoNombre = medicoNombre;
        this.especialidad = especialidad;
        this.fecha       = fecha;
        this.hora        = hora;
        this.estado      = estado;
    }

    // getters únicamente (no lógica)
    public String      getMedicoNombre() { return medicoNombre; }
    public String      getEspecialidad()  { return especialidad; }
    public LocalDate   getFecha()         { return fecha; }
    public LocalTime   getHora()          { return hora; }
    public String      getEstado()        { return estado; }
}