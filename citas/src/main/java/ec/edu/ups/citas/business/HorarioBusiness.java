package ec.edu.ups.citas.business;

import ec.edu.ups.citas.dao.HorarioDAO;
import ec.edu.ups.citas.dao.MedicoDAO;
import ec.edu.ups.citas.dto.HorarioDTO;
import ec.edu.ups.citas.dto.RecurrenciaHorarioDTO;
import ec.edu.ups.citas.modelo.Horario;
import ec.edu.ups.citas.modelo.Medico;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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
    
    public void crearRecurrencia(RecurrenciaHorarioDTO dto) {
        Medico m = medicoDAO.buscarPorId(dto.getMedicoId());
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

        for (String diaEs : dto.getDiasSemana()) {
            DayOfWeek dow = parseDia(diaEs);
            // obtenemos la próxima fecha (o hoy si coincide)
            LocalDate fecha = hoy.with(TemporalAdjusters.nextOrSame(dow));

            for (String horaTxt : dto.getHoras()) {
                LocalTime inicio = LocalTime.parse(horaTxt, fmt);
                Horario h = new Horario();
                h.setMedico(m);
                h.setFecha(fecha);
                h.setHoraInicio(inicio);
                // cada slot dura 30 minutos (ajusta si quieres otra duración)
                h.setHoraFin(inicio.plusMinutes(30));
                horarioDAO.crear(h);
            }
        }
    }
    
    private DayOfWeek parseDia(String diaEs) {
        switch (diaEs.toLowerCase()) {
            case "lunes":      return DayOfWeek.MONDAY;
            case "martes":     return DayOfWeek.TUESDAY;
            case "miercoles":
            case "miércoles":  return DayOfWeek.WEDNESDAY;
            case "jueves":     return DayOfWeek.THURSDAY;
            case "viernes":    return DayOfWeek.FRIDAY;
            case "sabado":
            case "sábado":     return DayOfWeek.SATURDAY;
            case "domingo":    return DayOfWeek.SUNDAY;
            default:
                throw new IllegalArgumentException("Día inválido: " + diaEs);
        }
    }
}
