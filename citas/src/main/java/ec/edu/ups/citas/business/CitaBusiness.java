package ec.edu.ups.citas.business;

import ec.edu.ups.citas.dao.CitaDAO;
import ec.edu.ups.citas.dao.HorarioDAO;
import ec.edu.ups.citas.dao.MedicoDAO;
import ec.edu.ups.citas.dao.UsuarioDAO;
import ec.edu.ups.citas.dto.CitaDTO;
import ec.edu.ups.citas.modelo.Cita;
import ec.edu.ups.citas.modelo.Estado;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class CitaBusiness {

    @Inject
    private CitaDAO citaDAO;
    @Inject
    private HorarioDAO horarioDAO;
    @Inject
    private MedicoDAO medicoDAO;
    @Inject
    private UsuarioDAO usuarioDAO;

    public CitaDTO crear(CitaDTO dto) {
        Cita c = toEntity(dto);
        citaDAO.crear(c);
        return toDTO(c);
    }

    public CitaDTO actualizar(CitaDTO dto) {
        Cita c = toEntity(dto);
        c = citaDAO.actualizar(c);
        return toDTO(c);
    }

    public boolean eliminar(Long id) {
        return citaDAO.eliminar(id);
    }

    public CitaDTO buscarPorId(Long id) {
        return toDTO(citaDAO.buscarPorId(id));
    }

    public List<CitaDTO> listarTodos() {
        return citaDAO.listarTodos().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
    }

 // Historial y filtros
    public List<CitaDTO> listarPorPacienteYFechas(Long pacId, LocalDate desde, LocalDate hasta) {
        return citaDAO.listarPorPacienteYFechas(pacId, desde, hasta).stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorMedicoYFechas(Long medId, LocalDate desde, LocalDate hasta) {
        return citaDAO.listarPorMedicoYFechas(medId, desde, hasta).stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorPacienteYEstado(Long pacId, Estado estado) {
        return citaDAO.listarPorPacienteYEstado(pacId, estado).stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorMedicoYEstado(Long medId, Estado estado) {
        return citaDAO.listarPorMedicoYEstado(medId, estado).stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorEstado(Estado estado) {
        return citaDAO.listarPorEstado(estado).stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }
    

    public List<CitaDTO> listarPorMedicoYFecha(Long medId, LocalDate fecha) {
        return citaDAO.listarPorMedicoYFecha(medId, fecha).stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorEspecialidad(String especialidad) {
        return citaDAO.listarPorEspecialidad(especialidad).stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }
    
    public List<CitaDTO> listarPorPaciente(Long pacId) {
        return citaDAO.listarPorPaciente(pacId).stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    
    public List<CitaDTO> listarPorMedico(Long medId) {
        return citaDAO.listarPorMedico(medId).stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    private CitaDTO toDTO(Cita c) {
        if (c == null) return null;
        CitaDTO dto = new CitaDTO();
        dto.setId(c.getId());
        dto.setEstado(c.getEstado().name());
        dto.setFecha(c.getFecha());
        dto.setHora(c.getHora());
        dto.setHorarioId(c.getHorario().getId());
        dto.setMedicoId(c.getMedico().getId());
        dto.setPacienteId(c.getPaciente().getId());
        return dto;
    }

    private Cita toEntity(CitaDTO dto) {
        Cita c = (dto.getId() != null)
            ? citaDAO.buscarPorId(dto.getId())
            : new Cita();

        c.setFecha(dto.getFecha());
        c.setHora(dto.getHora());
        c.setEstado(Estado.valueOf(dto.getEstado()));

        c.setHorario(horarioDAO.buscarPorId(dto.getHorarioId()));
        c.setMedico (medicoDAO.buscarPorId   (dto.getMedicoId()));
        c.setPaciente(usuarioDAO.buscarPorId  (dto.getPacienteId()));

        return c;
    }
}