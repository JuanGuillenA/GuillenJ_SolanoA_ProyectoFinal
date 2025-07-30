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

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Stateless
public class HorarioBusiness {

    @Inject private HorarioDAO horarioDAO;
    @Inject private MedicoDAO   medicoDAO;

    /**
     * Bulk-create franjas de 30' recorriendo desde fechaInicio hasta fechaFin,
     * solo los días de semana indicados y entre horaInicio–horaFin.
     */
    public void crearRecurrencia(RecurrenciaHorarioDTO dto) {
        Medico medico = medicoDAO.buscarPorId(dto.getMedicoId());
        LocalDate fecha = dto.getFechaInicio();

        while (!fecha.isAfter(dto.getFechaFin())) {
            DayOfWeek dow = fecha.getDayOfWeek();
            String nombreDia = dow.getDisplayName(TextStyle.FULL, new Locale("es"));
            // si este día está en la lista
            if (dto.getDiasSemana().stream()
                   .anyMatch(d -> d.equalsIgnoreCase(nombreDia))) {

                // crear slots de 30' entre horaInicio y horaFin
                LocalTime t = dto.getHoraInicio();
                while (t.isBefore(dto.getHoraFin())) {
                    Horario h = new Horario();
                    h.setMedico(medico);
                    h.setFecha(fecha);
                    h.setHoraInicio(t);
                    h.setHoraFin(t.plusMinutes(30));
                    horarioDAO.crear(h);
                    t = t.plusMinutes(30);
                }
            }
            fecha = fecha.plusDays(1);
        }
    }

    /** Devuelve todos los slots de 30' para un médico. */
    public List<HorarioDTO> listarPorMedico(Long medId) {
        return horarioDAO.listarPorMedico(medId)
                         .stream()
                         .map(this::toDTO)
                         .collect(Collectors.toList());
    }

    /** Busca un slot por su ID. */
    public HorarioDTO buscarPorId(Long id) {
        Horario h = horarioDAO.buscarPorId(id);
        return h == null ? null : toDTO(h);
    }

    /** Actualización parcial: sólo los campos no-null en el DTO. */
    public HorarioDTO actualizarParcial(HorarioDTO dto) {
        Horario h = horarioDAO.buscarPorId(dto.getId());
        if (h == null) return null;

        if (dto.getFecha()      != null) h.setFecha(dto.getFecha());
        if (dto.getHoraInicio() != null) h.setHoraInicio(dto.getHoraInicio());
        if (dto.getHoraFin()    != null) h.setHoraFin(dto.getHoraFin());

        h = horarioDAO.actualizar(h);
        return toDTO(h);
    }

    /** Elimina un slot por su ID. */
    public boolean eliminar(Long id) {
        return horarioDAO.eliminar(id);
    }

    // --- conversores entidad ↔ DTO ---

    private HorarioDTO toDTO(Horario h) {
        HorarioDTO dto = new HorarioDTO();
        dto.setId(h.getId());
        dto.setFecha(h.getFecha());
        dto.setHoraInicio(h.getHoraInicio());
        dto.setHoraFin(h.getHoraFin());
        dto.setMedicoId(h.getMedico().getId());
        return dto;
    }
}