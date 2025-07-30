package ec.edu.ups.citas.business;

import ec.edu.ups.citas.dao.EspecialidadDAO;
import ec.edu.ups.citas.dao.MedicoDAO;
import ec.edu.ups.citas.dto.MedicoDTO;
import ec.edu.ups.citas.modelo.Medico;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class MedicoBusiness {

    @Inject private MedicoDAO      medicoDAO;
    @Inject private EspecialidadDAO especialidadDAO;

 // 1) Create normal
    public MedicoDTO crear(MedicoDTO dto) {
        Medico m = toEntityCrear(dto);
        medicoDAO.crear(m);
        return toDTO(m);
    }

    // 2) Update full (opcional)
    public MedicoDTO actualizar(MedicoDTO dto) {
        Medico m = toEntityCrear(dto);
        m.setId(dto.getId());         // en tu DAO quizá no necesites esto
        m = medicoDAO.actualizar(m);
        return toDTO(m);
    }

    public boolean eliminar(Long id) {
        return medicoDAO.eliminar(id);
    }

    public MedicoDTO buscarPorId(Long id) {
        Medico m = medicoDAO.buscarPorId(id);
        return m == null ? null : toDTO(m);
    }

    public List<MedicoDTO> listarTodos() {
        return medicoDAO.listarTodos().stream()
                 .map(this::toDTO)
                 .collect(Collectors.toList());
    }

    public List<MedicoDTO> listarPorEspecialidad(Long espId) {
        return medicoDAO.listarPorEspecialidad(espId).stream()
                 .map(this::toDTO)
                 .collect(Collectors.toList());
    }

    public List<MedicoDTO> buscarPorNombre(String texto) {
        return medicoDAO.buscarPorNombre(texto).stream()
                 .map(this::toDTO)
                 .collect(Collectors.toList());
    }

 // 3) Partial update
    public MedicoDTO actualizarParcial(MedicoDTO dto) {
        Medico m = medicoDAO.buscarPorId(dto.getId());
        if (m == null) {
            return null;
        }
        if (dto.getNombre() != null) {
            m.setNombre(dto.getNombre());
        }
        if (dto.getApellido() != null) {
            m.setApellido(dto.getApellido());
        }
        if (dto.getEmail() != null) {
            m.setEmail(dto.getEmail());
        }
        // Cerrar el if anterior aquí:
        if (dto.getEspecialidadId() != null) {
            m.setEspecialidad(
                especialidadDAO.buscarPorId(dto.getEspecialidadId())
            );
        }
        // Ahora el teléfono se comprueba siempre, independientemente de especialidad:
        if (dto.getTelefono() != null) {
            m.setTelefono(dto.getTelefono());
        }

        m = medicoDAO.actualizar(m);
        return toDTO(m);
    }

    // Helper: convierte un DTO a una entidad NUEVA (para crear)
    private Medico toEntityCrear(MedicoDTO dto) {
        Medico m = new Medico();
        m.setNombre(dto.getNombre());
        m.setApellido(dto.getApellido());
        m.setEmail(dto.getEmail());
        m.setTelefono(dto.getTelefono());
        m.setEspecialidad(
            especialidadDAO.buscarPorId(dto.getEspecialidadId())
        );
        return m;
    }

    // Helper: convierte entidad a DTO
    private MedicoDTO toDTO(Medico m) {
        MedicoDTO dto = new MedicoDTO();
        dto.setId(m.getId());
        dto.setNombre(m.getNombre());
        dto.setApellido(m.getApellido());
        dto.setEmail(m.getEmail());
        dto.setEspecialidadId(m.getEspecialidad().getId());
        dto.setTelefono(m.getTelefono());
        return dto;
    }

    // resto de métodos (listar, buscar, eliminar) siguen igual…
}