package ec.edu.ups.citas.business;

import ec.edu.ups.citas.dao.CitaDAO;
import ec.edu.ups.citas.dao.HorarioDAO;
import ec.edu.ups.citas.dao.MedicoDAO;
import ec.edu.ups.citas.dao.UsuarioDAO;
import ec.edu.ups.citas.dto.CitaDTO;
import ec.edu.ups.citas.modelo.Cita;
import ec.edu.ups.citas.modelo.Estado;
import ec.edu.ups.citas.modelo.Horario;
import ec.edu.ups.citas.modelo.Medico;
import ec.edu.ups.citas.modelo.Usuario;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class CitaBusiness {

    @Inject private CitaDAO citaDAO;
    @Inject private HorarioDAO horarioDAO;
    @Inject private MedicoDAO medicoDAO;
    @Inject private UsuarioDAO usuarioDAO;

    public CitaDTO crear(CitaDTO dto) {
        Usuario u = usuarioDAO.buscarPorFirebaseUid(dto.getPacienteId());
        if (u == null) throw new IllegalArgumentException("Usuario no encontrado");

        Horario h = horarioDAO.buscarPorId(dto.getHorarioId());
        Medico  m = medicoDAO.buscarPorId(dto.getMedicoId());
        if (h == null || m == null) throw new IllegalArgumentException("Horario o Médico no encontrados");

        Cita c = new Cita();
        c.setPaciente(u);
        c.setHorario(h);
        c.setMedico(m);
        c.setEstado(Estado.PENDIENTE);

        // **¡MUY IMPORTANTE!** asignar fecha y hora desde el slot:
        c.setFecha( h.getFecha() );
        c.setHora(  h.getHoraInicio() );

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
        return citaDAO.listarTodos()
                      .stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorPacienteYFechas(Long pacId, LocalDate desde, LocalDate hasta) {
        return citaDAO.listarPorPacienteYFechas(pacId, desde, hasta)
                      .stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorMedicoYFechas(Long medId, LocalDate desde, LocalDate hasta) {
        return citaDAO.listarPorMedicoYFechas(medId, desde, hasta)
                      .stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorPacienteYEstado(Long pacId, Estado estado) {
        return citaDAO.listarPorPacienteYEstado(pacId, estado)
                      .stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorMedicoYEstado(Long medId, Estado estado) {
        return citaDAO.listarPorMedicoYEstado(medId, estado)
                      .stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorEstado(Estado estado) {
        return citaDAO.listarPorEstado(estado)
                      .stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorMedicoYFecha(Long medId, LocalDate fecha) {
        return citaDAO.listarPorMedicoYFecha(medId, fecha)
                      .stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorEspecialidad(String esp) {
        return citaDAO.listarPorEspecialidad(esp)
                      .stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorPaciente(Long pacId) {
        return citaDAO.listarPorPaciente(pacId)
                      .stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorMedico(Long medId) {
        return citaDAO.listarPorMedico(medId)
                      .stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }
    
    public List<CitaDTO> listarPorFechas(LocalDate desde, LocalDate hasta) {
        return citaDAO.listarPorFechas(desde, hasta)
                      .stream()
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
        // devolvemos de vuelta el UID de Firebase
        dto.setPacienteId(c.getPaciente().getFirebaseUid());
        return dto;
    }

    private Cita toEntity(CitaDTO dto) {
        Cita c = (dto.getId() != null)
            ? citaDAO.buscarPorId(dto.getId())
            : new Cita();

        if (dto.getPacienteId() != null) {
            Usuario u = usuarioDAO.buscarPorFirebaseUid(dto.getPacienteId());
            c.setPaciente(u);
        }
        if (dto.getHorarioId() != null) {
            c.setHorario(horarioDAO.buscarPorId(dto.getHorarioId()));
        }
        if (dto.getMedicoId() != null) {
            c.setMedico(medicoDAO.buscarPorId(dto.getMedicoId()));
        }
        if (dto.getEstado() != null) {
            c.setEstado(Estado.valueOf(dto.getEstado()));
        }
        if (dto.getFecha() != null) {
            c.setFecha(dto.getFecha());
        }
        if (dto.getHora() != null) {
            c.setHora(dto.getHora());
        }
        return c;
    }
}