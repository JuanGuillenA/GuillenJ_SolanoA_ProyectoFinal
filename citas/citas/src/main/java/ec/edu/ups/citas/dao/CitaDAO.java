package ec.edu.ups.citas.dao;

import ec.edu.ups.citas.modelo.Cita;
import ec.edu.ups.citas.modelo.Estado;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
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

    public List<Cita> listarPorPaciente(Long pacienteId) {
        return em.createQuery(
            "SELECT c FROM Cita c WHERE c.paciente.id = :pacId", Cita.class)
            .setParameter("pacId", pacienteId)
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

    public List<Cita> listarPorPacienteYEstado(Long pacienteId, Estado estado) {
        return em.createQuery(
            "SELECT c FROM Cita c WHERE c.paciente.id = :pacId AND c.estado = :est", Cita.class)
            .setParameter("pacId", pacienteId)
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

    public List<Cita> listarPorPacienteYFechas(Long pacienteId, LocalDate desde, LocalDate hasta) {
        return em.createQuery(
            "SELECT c FROM Cita c "
          + "WHERE c.paciente.id = :pacId "
          + "  AND c.fecha BETWEEN :desde AND :hasta", Cita.class)
            .setParameter("pacId", pacienteId)
            .setParameter("desde", desde)
            .setParameter("hasta", hasta)
            .getResultList();
    }

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
}
