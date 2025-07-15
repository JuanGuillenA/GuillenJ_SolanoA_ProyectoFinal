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

    public MedicoDTO crear(MedicoDTO dto) {
        Medico m = toEntity(dto);
        medicoDAO.crear(m);
        return toDTO(m);
    }

    public MedicoDTO actualizar(MedicoDTO dto) {
        Medico m = toEntity(dto);
        m = medicoDAO.actualizar(m);
        return toDTO(m);
    }

    public void eliminar(Long id) {
        medicoDAO.eliminar(id);
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

    private MedicoDTO toDTO(Medico m) {
        MedicoDTO dto = new MedicoDTO();
        dto.setId(m.getId());
        dto.setNombre(m.getNombre());
        dto.setApellido(m.getApellido());
        dto.setEmail(m.getEmail());
        dto.setEspecialidadId(m.getEspecialidad().getId());
        return dto;
    }

    private Medico toEntity(MedicoDTO dto) {
        Medico m = dto.getId() != null
                ? medicoDAO.buscarPorId(dto.getId())
                : new Medico();
        m.setNombre(dto.getNombre());
        m.setApellido(dto.getApellido());
        m.setEmail(dto.getEmail());
        m.setEspecialidad(especialidadDAO.buscarPorId(dto.getEspecialidadId()));
        return m;
    }
}
