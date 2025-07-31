package ec.edu.ups.citas.dao;

import ec.edu.ups.citas.modelo.Cita;
import ec.edu.ups.citas.modelo.Estado;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Stateless
public class CitaDAO {

    @PersistenceContext(unitName = "citasPersistenceUnit")
    private EntityManager em;

    public void crear(Cita c) {
        em.persist(c);
    }

    public Cita actualizar(Cita c) {
        return em.merge(c);
    }

    public boolean eliminar(Long id) {
        Cita c = em.find(Cita.class, id);
        if (c != null) {
            em.remove(c);
            return true;
        }
        return false;
    }

    public Cita buscarPorId(Long id) {
        return em.find(Cita.class, id);
    }

    public List<Cita> listarTodos() {
        return em.createQuery("SELECT c FROM Cita c", Cita.class)
                 .getResultList();
    }



    public List<Cita> listarPorMedico(Long medicoId) {
        return em.createQuery(
            "SELECT c FROM Cita c WHERE c.medico.id = :medId", Cita.class)
            .setParameter("medId", medicoId)
            .getResultList();
    }

    public List<Cita> listarPorEstado(Estado estado) {
        return em.createQuery(
            "SELECT c FROM Cita c WHERE c.estado = :est", Cita.class)
            .setParameter("est", estado)
            .getResultList();
    }


    public List<Cita> listarPorMedicoYFecha(Long medicoId, LocalDate fecha) {
        return em.createQuery(
            "SELECT c FROM Cita c WHERE c.medico.id = :medId AND c.fecha = :fecha", Cita.class)
            .setParameter("medId", medicoId)
            .setParameter("fecha", fecha)
            .getResultList();
    }

    // Nuevos métodos para historial y filtros


    public List<Cita> listarPorMedicoYFechas(Long medicoId, LocalDate desde, LocalDate hasta) {
        return em.createQuery(
            "SELECT c FROM Cita c "
          + "WHERE c.medico.id = :medId "
          + "  AND c.fecha BETWEEN :desde AND :hasta", Cita.class)
            .setParameter("medId", medicoId)
            .setParameter("desde", desde)
            .setParameter("hasta", hasta)
            .getResultList();
    }

    public List<Cita> listarPorMedicoYEstado(Long medicoId, Estado estado) {
        TypedQuery<Cita> q = em.createQuery(
            "SELECT c FROM Cita c WHERE c.medico.id = :medId AND c.estado = :est",
            Cita.class);
        q.setParameter("medId", medicoId);
        q.setParameter("est", estado);
        return q.getResultList();
    }

    public List<Cita> listarPorEspecialidad(String especialidad) {
        return em.createQuery(
            "SELECT c FROM Cita c "
          + "WHERE LOWER(c.medico.especialidad.nombre) = LOWER(:esp)", Cita.class)
            .setParameter("esp", especialidad)
            .getResultList();
    }
    
    public List<Cita> listarPorFechas(LocalDate desde, LocalDate hasta) {
        return em.createQuery(
            "SELECT c FROM Cita c "
          + "WHERE c.fecha BETWEEN :desde AND :hasta", Cita.class)
          .setParameter("desde", desde)
          .setParameter("hasta", hasta)
          .getResultList();
    }
    
 // 1. Estado + rango de fechas
    public List<Cita> listarPorEstadoYFechas(
            Estado estado, LocalDate desde, LocalDate hasta) {
        return em.createQuery(
            "SELECT c FROM Cita c " +
            " WHERE c.estado = :est " +
            "   AND c.fecha BETWEEN :desde AND :hasta", Cita.class)
          .setParameter("est", estado)
          .setParameter("desde", desde)
          .setParameter("hasta", hasta)
          .getResultList();
    }

    // 2. Estado + especialidad
    public List<Cita> listarPorEstadoYEspecialidad(
            Estado estado, String especialidad) {
        return em.createQuery(
            "SELECT c FROM Cita c " +
            " WHERE c.estado = :est " +
            "   AND LOWER(c.medico.especialidad.nombre) = LOWER(:esp)", 
            Cita.class)
          .setParameter("est", estado)
          .setParameter("esp", especialidad)
          .getResultList();
    }

    // 3. Especialidad + médico
    public List<Cita> listarPorEspecialidadYMedico(
            String especialidad, Long medicoId) {
        return em.createQuery(
            "SELECT c FROM Cita c " +
            " WHERE c.medico.id = :medId " +
            "   AND LOWER(c.medico.especialidad.nombre) = LOWER(:esp)",
            Cita.class)
          .setParameter("medId", medicoId)
          .setParameter("esp", especialidad)
          .getResultList();
    }
    
 // 1) Estado + fechas + médico
    public List<Cita> listarPorEstadoYFechasYMedico(
            Estado estado, LocalDate desde, LocalDate hasta, Long medicoId) {
        return em.createQuery(
            "SELECT c FROM Cita c " +
            " WHERE c.estado = :est " +
            "   AND c.fecha BETWEEN :desde AND :hasta " +
            "   AND c.medico.id = :medId",
            Cita.class)
          .setParameter("est",   estado)
          .setParameter("desde", desde)
          .setParameter("hasta", hasta)
          .setParameter("medId", medicoId)
          .getResultList();
    }

    // 2) Estado + fechas + especialidad
    public List<Cita> listarPorEstadoYFechasYEspecialidad(
            Estado estado, LocalDate desde, LocalDate hasta, String especialidad) {
        return em.createQuery(
            "SELECT c FROM Cita c " +
            " WHERE c.estado = :est " +
            "   AND c.fecha BETWEEN :desde AND :hasta " +
            "   AND LOWER(c.medico.especialidad.nombre) = LOWER(:esp)",
            Cita.class)
          .setParameter("est",   estado)
          .setParameter("desde", desde)
          .setParameter("hasta", hasta)
          .setParameter("esp",   especialidad)
          .getResultList();
    }

    // 3) Fechas + especialidad + médico
    public List<Cita> listarPorFechasYEspecialidadYMedico(
            LocalDate desde, LocalDate hasta, String especialidad, Long medicoId) {
        return em.createQuery(
            "SELECT c FROM Cita c " +
            " WHERE c.fecha BETWEEN :desde AND :hasta " +
            "   AND LOWER(c.medico.especialidad.nombre) = LOWER(:esp) " +
            "   AND c.medico.id = :medId",
            Cita.class)
          .setParameter("desde", desde)
          .setParameter("hasta", hasta)
          .setParameter("esp",   especialidad)
          .setParameter("medId", medicoId)
          .getResultList();
    }

    // 4) Estado + fechas + especialidad + médico
    public List<Cita> listarPorEstadoYFechasYEspecialidadYMedico(
            Estado estado, LocalDate desde, LocalDate hasta,
            String especialidad, Long medicoId) {
        return em.createQuery(
            "SELECT c FROM Cita c " +
            " WHERE c.estado = :est " +
            "   AND c.fecha BETWEEN :desde AND :hasta " +
            "   AND LOWER(c.medico.especialidad.nombre) = LOWER(:esp) " +
            "   AND c.medico.id = :medId",
            Cita.class)
          .setParameter("est",   estado)
          .setParameter("desde", desde)
          .setParameter("hasta", hasta)
          .setParameter("esp",   especialidad)
          .setParameter("medId", medicoId)
          .getResultList();
    }
    
 // 1) Solo paciente
    public List<Cita> listarPorPaciente(Long pacId) {
        return em.createQuery(
            "SELECT c FROM Cita c WHERE c.paciente.id = :pacId",
            Cita.class)
          .setParameter("pacId", pacId)
          .getResultList();
    }

    // 2) Paciente + fecha
    public List<Cita> listarPorPacienteYFechas(Long pacId,
                                               LocalDate desde,
                                               LocalDate hasta) {
        return em.createQuery(
            "SELECT c FROM Cita c " +
            " WHERE c.paciente.id = :pacId " +
            "   AND c.fecha BETWEEN :desde AND :hasta",
            Cita.class)
          .setParameter("pacId", pacId)
          .setParameter("desde", desde)
          .setParameter("hasta", hasta)
          .getResultList();
    }

    // 3) Paciente + médico
    public List<Cita> listarPorPacienteYMedico(Long pacId,
                                               Long medId) {
        return em.createQuery(
            "SELECT c FROM Cita c " +
            " WHERE c.paciente.id = :pacId " +
            "   AND c.medico.id = :medId",
            Cita.class)
          .setParameter("pacId", pacId)
          .setParameter("medId", medId)
          .getResultList();
    }

    // 4) Paciente + especialidad
    public List<Cita> listarPorPacienteYEspecialidad(Long pacId,
                                                     String esp) {
        return em.createQuery(
            "SELECT c FROM Cita c " +
            " WHERE c.paciente.id = :pacId " +
            "   AND LOWER(c.medico.especialidad.nombre) = LOWER(:esp)",
            Cita.class)
          .setParameter("pacId", pacId)
          .setParameter("esp",   esp)
          .getResultList();
    }

    // 5) Paciente + estado
    public List<Cita> listarPorPacienteYEstado(Long pacId,
                                               Estado est) {
        return em.createQuery(
            "SELECT c FROM Cita c " +
            " WHERE c.paciente.id = :pacId " +
            "   AND c.estado = :est",
            Cita.class)
          .setParameter("pacId", pacId)
          .setParameter("est",   est)
          .getResultList();
    }

    // 6) Paciente + médico + fecha
    public List<Cita> listarPorPacienteYMedicoYFechas(Long pacId,
                                                      Long medId,
                                                      LocalDate desde,
                                                      LocalDate hasta) {
        return em.createQuery(
            "SELECT c FROM Cita c " +
            " WHERE c.paciente.id = :pacId " +
            "   AND c.medico.id = :medId " +
            "   AND c.fecha BETWEEN :desde AND :hasta",
            Cita.class)
          .setParameter("pacId", pacId)
          .setParameter("medId", medId)
          .setParameter("desde", desde)
          .setParameter("hasta", hasta)
          .getResultList();
    }

    // 7) Paciente + especialidad + fecha
    public List<Cita> listarPorPacienteYEspecialidadYFechas(Long pacId,
                                                            String esp,
                                                            LocalDate desde,
                                                            LocalDate hasta) {
        return em.createQuery(
            "SELECT c FROM Cita c " +
            " WHERE c.paciente.id = :pacId " +
            "   AND LOWER(c.medico.especialidad.nombre) = LOWER(:esp) " +
            "   AND c.fecha BETWEEN :desde AND :hasta",
            Cita.class)
          .setParameter("pacId", pacId)
          .setParameter("esp",   esp)
          .setParameter("desde", desde)
          .setParameter("hasta", hasta)
          .getResultList();
    }

    // 8) Paciente + estado + fecha
    public List<Cita> listarPorPacienteYEstadoYFechas(Long pacId,
                                                      Estado est,
                                                      LocalDate desde,
                                                      LocalDate hasta) {
        return em.createQuery(
            "SELECT c FROM Cita c " +
            " WHERE c.paciente.id = :pacId " +
            "   AND c.estado = :est " +
            "   AND c.fecha BETWEEN :desde AND :hasta",
            Cita.class)
          .setParameter("pacId", pacId)
          .setParameter("est",   est)
          .setParameter("desde", desde)
          .setParameter("hasta", hasta)
          .getResultList();
    }

    // 9) Paciente + estado + fecha + especialidad + médico
    public List<Cita> listarPorPacienteYEstadoFechasEspecialidadMedico(
            Long pacId,
            Estado est,
            LocalDate desde,
            LocalDate hasta,
            String esp,
            Long medId) {
        return em.createQuery(
            "SELECT c FROM Cita c " +
            " WHERE c.paciente.id = :pacId " +
            "   AND c.estado = :est " +
            "   AND c.fecha BETWEEN :desde AND :hasta " +
            "   AND LOWER(c.medico.especialidad.nombre) = LOWER(:esp) " +
            "   AND c.medico.id = :medId",
            Cita.class)
          .setParameter("pacId", pacId)
          .setParameter("est",   est)
          .setParameter("desde", desde)
          .setParameter("hasta", hasta)
          .setParameter("esp",   esp)
          .setParameter("medId", medId)
          .getResultList();
    }
    
    public boolean existeCitaParaMedicoEnHorario(Long medicoId,
    		LocalDate fecha,
    		LocalTime hora) {
			Long count = em.createQuery(
			"SELECT COUNT(c) FROM Cita c " +
			" WHERE c.medico.id = :medId " +
			"   AND c.fecha = :fecha " +
			"   AND c.hora = :hora", Long.class)
			.setParameter("medId", medicoId)
			.setParameter("fecha", fecha)
			.setParameter("hora",  hora)
			.getSingleResult();
			return count != null && count > 0;
			}

}
