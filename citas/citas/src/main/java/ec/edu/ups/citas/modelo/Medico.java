package ec.edu.ups.citas.modelo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicos")
public class Medico implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @ManyToOne
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidad;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Horario> horarios = new ArrayList<>();

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas = new ArrayList<>();
    
    public void addHorario(Horario horario) {
        horarios.add(horario);
        horario.setMedico(this);
    }

    public void removeHorario(Horario horario) {
        horarios.remove(horario);
        horario.setMedico(null);
    }

    public void addCita(Cita cita) {
        citas.add(cita);
        cita.setMedico(this);
    }

    public void removeCita(Cita cita) {
        citas.remove(cita);
        cita.setMedico(null);
    }

	public Medico() {
	}

	public Medico(Long id, String nombre, String apellido, String email, Especialidad especialidad,
			List<Horario> horarios, List<Cita> citas) {
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
		this.especialidad = especialidad;
		this.horarios = horarios;
		this.citas = citas;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Especialidad getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(Especialidad especialidad) {
		this.especialidad = especialidad;
	}

	public List<Horario> getHorarios() {
		return horarios;
	}

	public void setHorarios(List<Horario> horarios) {
		this.horarios = horarios;
	}

	public List<Cita> getCitas() {
		return citas;
	}

	public void setCitas(List<Cita> citas) {
		this.citas = citas;
	}

    
}

