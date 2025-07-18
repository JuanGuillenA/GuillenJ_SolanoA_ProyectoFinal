package ec.edu.ups.citas.business;

import ec.edu.ups.citas.dao.HorarioDAO;
import ec.edu.ups.citas.dao.MedicoDAO;
import ec.edu.ups.citas.dto.HorarioDTO;
import ec.edu.ups.citas.modelo.Horario;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class HorarioBusiness {

    @Inject private HorarioDAO horarioDAO;
    @Inject private MedicoDAO  medicoDAO;

    public HorarioDTO crear(HorarioDTO dto) {
        Horario h = toEntity(dto);
        horarioDAO.crear(h);
        return toDTO(h);
    }

    public HorarioDTO actualizar(HorarioDTO dto) {
        Horario h = toEntity(dto);
        h = horarioDAO.actualizar(h);
        return toDTO(h);
    }

    public boolean eliminar(Long id) {
        return horarioDAO.eliminar(id);
    }

    public HorarioDTO buscarPorId(Long id) {
        Horario h = horarioDAO.buscarPorId(id);
        return h == null ? null : toDTO(h);
    }

    public List<HorarioDTO> listarPorMedico(Long medId) {
        return horarioDAO.listarPorMedico(medId).stream()
                 .map(this::toDTO)
                 .collect(Collectors.toList());
    }

    public List<HorarioDTO> listarPorMedicoYFecha(Long medId, LocalDate fecha) {
        return horarioDAO.listarPorMedicoYFecha(medId, fecha).stream()
                 .map(this::toDTO)
                 .collect(Collectors.toList());
    }

    private HorarioDTO toDTO(Horario h) {
        HorarioDTO dto = new HorarioDTO();
        dto.setId(h.getId());
        dto.setFecha(h.getFecha());
        dto.setHoraInicio(h.getHoraInicio());
        dto.setHoraFin(h.getHoraFin());
        dto.setMedicoId(h.getMedico().getId());
        return dto;
    }

    private Horario toEntity(HorarioDTO dto) {
        Horario h = dto.getId() != null
                ? horarioDAO.buscarPorId(dto.getId())
                : new Horario();
        h.setFecha(dto.getFecha());
        h.setHoraInicio(dto.getHoraInicio());
        h.setHoraFin(dto.getHoraFin());
        h.setMedico(medicoDAO.buscarPorId(dto.getMedicoId()));
        return h;
    }
    public List<HorarioDTO> listarTodos() {
        return horarioDAO.listarTodos().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}
