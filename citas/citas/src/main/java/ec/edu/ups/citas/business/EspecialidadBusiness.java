package ec.edu.ups.citas.business;

import ec.edu.ups.citas.dao.EspecialidadDAO;
import ec.edu.ups.citas.dto.EspecialidadDTO;
import ec.edu.ups.citas.modelo.Especialidad;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class EspecialidadBusiness {

    @Inject private EspecialidadDAO especialidadDAO;

    public EspecialidadDTO crear(EspecialidadDTO dto) {
        Especialidad e = toEntity(dto);
        especialidadDAO.crear(e);
        return toDTO(e);
    }

    public EspecialidadDTO actualizar(EspecialidadDTO dto) {
        Especialidad e = toEntity(dto);
        e = especialidadDAO.actualizar(e);
        return toDTO(e);
    }

    public boolean eliminar(Long id) {
        return especialidadDAO.eliminar(id);
    }

    public EspecialidadDTO buscarPorId(Long id) {
        Especialidad e = especialidadDAO.buscarPorId(id);
        return e == null ? null : toDTO(e);
    }

    public List<EspecialidadDTO> listarTodos() {
        return especialidadDAO.listarTodos().stream()
                 .map(this::toDTO)
                 .collect(Collectors.toList());
    }

    private EspecialidadDTO toDTO(Especialidad e) {
        EspecialidadDTO dto = new EspecialidadDTO();
        dto.setId(e.getId());
        dto.setNombre(e.getNombre());
        return dto;
    }

    private Especialidad toEntity(EspecialidadDTO dto) {
        Especialidad e = dto.getId() != null
                ? especialidadDAO.buscarPorId(dto.getId())
                : new Especialidad();
        e.setNombre(dto.getNombre());
        return e;
    }
}
