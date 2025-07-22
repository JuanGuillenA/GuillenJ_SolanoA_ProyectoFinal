package ec.edu.ups.citas.dto;

import java.io.Serializable;
import java.util.List;

public class RecurrenciaHorarioDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long medicoId;
    private List<String> diasSemana;  // e.g. ["Lunes","Miercoles"]
    private List<String> horas;       // e.g. ["09:00","09:30","10:00"]

    public RecurrenciaHorarioDTO() {}

    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }

    public List<String> getDiasSemana() { return diasSemana; }
    public void setDiasSemana(List<String> diasSemana) { this.diasSemana = diasSemana; }

    public List<String> getHoras() { return horas; }
    public void setHoras(List<String> horas) { this.horas = horas; }
}