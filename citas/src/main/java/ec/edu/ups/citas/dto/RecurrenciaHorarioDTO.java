package ec.edu.ups.citas.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class RecurrenciaHorarioDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long medicoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<String> diasSemana;  // ["Lunes","Miércoles",…]
    private LocalTime horaInicio;     // p. ej. 08:00
    private LocalTime horaFin;       // e.g. ["09:00","09:30","10:00"]

    public RecurrenciaHorarioDTO() {}

	public Long getMedicoId() {
		return medicoId;
	}

	public void setMedicoId(Long medicoId) {
		this.medicoId = medicoId;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}

	public List<String> getDiasSemana() {
		return diasSemana;
	}

	public void setDiasSemana(List<String> diasSemana) {
		this.diasSemana = diasSemana;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(LocalTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	public LocalTime getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(LocalTime horaFin) {
		this.horaFin = horaFin;
	}

    
}