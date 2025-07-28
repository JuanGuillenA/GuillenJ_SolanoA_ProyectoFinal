package ec.edu.ups.citas.dto;

public class OcupacionEspecialidadDTO {
    private String especialidad;
    private long   totalCitas;
    private double nivelOcupacion; // p.ej. porcentaje

    public OcupacionEspecialidadDTO(String especialidad, long totalCitas, double nivelOcupacion) {
        this.especialidad    = especialidad;
        this.totalCitas      = totalCitas;
        this.nivelOcupacion  = nivelOcupacion;
    }

    public String getEspecialidad()   { return especialidad;    }
    public long   getTotalCitas()     { return totalCitas;      }
    public double getNivelOcupacion() { return nivelOcupacion;  }
}
