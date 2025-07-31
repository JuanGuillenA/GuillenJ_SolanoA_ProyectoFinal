package ec.edu.ups.citas.modelo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "especialidades")
public class Especialidad implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @OneToMany(mappedBy = "especialidad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medico> medicos = new ArrayList<>();
    
    public void addMedico(Medico medico) {
        medicos.add(medico);
        medico.setEspecialidad(this);
    }

    public void removeMedico(Medico medico) {
        medicos.remove(medico);
        medico.setEspecialidad(null);
    }

	public Especialidad() {
	}

	public Especialidad(Long id, String nombre, List<Medico> medicos) {
		this.id = id;
		this.nombre = nombre;
		this.medicos = medicos;
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

	public List<Medico> getMedicos() {
		return medicos;
	}

	public void setMedicos(List<Medico> medicos) {
		this.medicos = medicos;
	}

	
    
}

