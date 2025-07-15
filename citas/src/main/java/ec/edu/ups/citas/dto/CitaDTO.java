package ec.edu.ups.citas.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class CitaDTO implements Serializable {
	
    private static final long serialVersionUID = 1L;

    private Long id;
    private String estado;
    private LocalDate fecha;
    private LocalTime hora;
    private Long horarioId;
    private Long medicoId;
    private Long pacienteId;

    public CitaDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public Long getHorarioId() { return horarioId; }
    public void setHorarioId(Long horarioId) { this.horarioId = horarioId; }
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
}
