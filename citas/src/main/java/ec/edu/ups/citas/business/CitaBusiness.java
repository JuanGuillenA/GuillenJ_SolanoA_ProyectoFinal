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
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class CitaBusiness {

    @Inject private CitaDAO citaDAO;
    @Inject private HorarioDAO horarioDAO;
    @Inject private MedicoDAO medicoDAO;
    @Inject private UsuarioDAO usuarioDAO;

    public CitaDTO crear(CitaDTO dto) {
        // 1) Validar paciente, horario y médico
        Usuario u = usuarioDAO.buscarPorFirebaseUid(dto.getPacienteId());
        if (u == null) throw new IllegalArgumentException("Usuario no encontrado");

        Horario h = horarioDAO.buscarPorId(dto.getHorarioId());
        Medico  m = medicoDAO.buscarPorId(dto.getMedicoId());
        if (h == null || m == null) throw new IllegalArgumentException("Horario o Médico no encontrados");

        // 2) Chequear duplicado: mismo médico, misma fecha y hora
        LocalDate fecha = h.getFecha();
        LocalTime hora  = h.getHoraInicio();
        if (citaDAO.existeCitaParaMedicoEnHorario(m.getId(), fecha, hora)) {
            throw new IllegalStateException(
              "Ya existe una cita para el médico " + m.getNombre() + " " + m.getApellido() +
              " en la fecha " + fecha + " a las " + hora);
        }

        // 3) Construir entidad y persistir
        Cita c = new Cita();
        c.setPaciente(u);
        c.setHorario(h);
        c.setMedico(m);
        c.setEstado(Estado.PENDIENTE);
        c.setFecha(fecha);
        c.setHora(hora);

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


    public List<CitaDTO> listarPorMedicoYFechas(Long medId, LocalDate desde, LocalDate hasta) {
        return citaDAO.listarPorMedicoYFechas(medId, desde, hasta)
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
    
 // 1. Estado + rango de fechas
    public List<CitaDTO> listarPorEstadoYFechas(
            Estado estado, LocalDate desde, LocalDate hasta) {
        return citaDAO.listarPorEstadoYFechas(estado, desde, hasta)
                      .stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    // 2. Estado + especialidad
    public List<CitaDTO> listarPorEstadoYEspecialidad(
            Estado estado, String especialidad) {
        return citaDAO.listarPorEstadoYEspecialidad(estado, especialidad)
                      .stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }

    // 3. Especialidad + médico
    public List<CitaDTO> listarPorEspecialidadYMedico(
            String especialidad, Long medicoId) {
        return citaDAO.listarPorEspecialidadYMedico(especialidad, medicoId)
                      .stream()
                      .map(this::toDTO)
                      .collect(Collectors.toList());
    }
    
 // 1) Estado + fechas + médico
    public List<CitaDTO> listarPorEstadoYFechasYMedico(
            Estado estado, LocalDate desde, LocalDate hasta, Long medicoId) {
        return citaDAO.listarPorEstadoYFechasYMedico(estado, desde, hasta, medicoId)
                      .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // 2) Estado + fechas + especialidad
    public List<CitaDTO> listarPorEstadoYFechasYEspecialidad(
            Estado estado, LocalDate desde, LocalDate hasta, String especialidad) {
        return citaDAO.listarPorEstadoYFechasYEspecialidad(
                         estado, desde, hasta, especialidad)
                      .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // 3) Fechas + especialidad + médico
    public List<CitaDTO> listarPorFechasYEspecialidadYMedico(
            LocalDate desde, LocalDate hasta, String especialidad, Long medicoId) {
        return citaDAO.listarPorFechasYEspecialidadYMedico(
                         desde, hasta, especialidad, medicoId)
                      .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // 4) Estado + fechas + especialidad + médico
    public List<CitaDTO> listarPorEstadoYFechasYEspecialidadYMedico(
            Estado estado, LocalDate desde, LocalDate hasta,
            String especialidad, Long medicoId) {
        return citaDAO.listarPorEstadoYFechasYEspecialidadYMedico(
                         estado, desde, hasta, especialidad, medicoId)
                      .stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    public List<CitaDTO> listarPorPaciente(Long pacId) {
        return citaDAO.listarPorPaciente(pacId)
                      .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorPacienteYFechas(Long pacId,
                                                  LocalDate desde,
                                                  LocalDate hasta) {
        return citaDAO.listarPorPacienteYFechas(pacId, desde, hasta)
                      .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorPacienteYMedico(Long pacId,
                                                  Long medId) {
        return citaDAO.listarPorPacienteYMedico(pacId, medId)
                      .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorPacienteYEspecialidad(Long pacId,
                                                        String esp) {
        return citaDAO.listarPorPacienteYEspecialidad(pacId, esp)
                      .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorPacienteYEstado(Long pacId,
                                                  Estado est) {
        return citaDAO.listarPorPacienteYEstado(pacId, est)
                      .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorPacienteYMedicoYFechas(Long pacId,
                                                         Long medId,
                                                         LocalDate desde,
                                                         LocalDate hasta) {
        return citaDAO.listarPorPacienteYMedicoYFechas(pacId, medId, desde, hasta)
                      .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorPacienteYEspecialidadYFechas(Long pacId,
                                                               String esp,
                                                               LocalDate desde,
                                                               LocalDate hasta) {
        return citaDAO.listarPorPacienteYEspecialidadYFechas(pacId, esp, desde, hasta)
                      .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorPacienteYEstadoYFechas(Long pacId,
                                                         Estado est,
                                                         LocalDate desde,
                                                         LocalDate hasta) {
        return citaDAO.listarPorPacienteYEstadoYFechas(pacId, est, desde, hasta)
                      .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CitaDTO> listarPorPacienteYEstadoFechasEspecialidadMedico(
            Long pacId,
            Estado est,
            LocalDate desde,
            LocalDate hasta,
            String esp,
            Long medId) {
        return citaDAO.listarPorPacienteYEstadoFechasEspecialidadMedico(
                     pacId, est, desde, hasta, esp, medId)
                      .stream().map(this::toDTO).collect(Collectors.toList());
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