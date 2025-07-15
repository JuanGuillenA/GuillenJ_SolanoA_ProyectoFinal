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

    public void eliminar(Long id) {
        Cita c = em.find(Cita.class, id);
        if (c != null) {
            em.remove(c);
        }
    }

    public Cita buscarPorId(Long id) {
        return em.find(Cita.class, id);
    }

    public List<Cita> listarTodos() {
        return em.createQuery("SELECT c FROM Cita c", Cita.class)
                 .getResultList();
    }

    public List<Cita> listarPorPaciente(Long pacienteId) {
        TypedQuery<Cita> q = em.createQuery(
            "SELECT c FROM Cita c WHERE c.paciente.id = :pacId", Cita.class);
        q.setParameter("pacId", pacienteId);
        return q.getResultList();
    }

    public List<Cita> listarPorMedico(Long medicoId) {
        TypedQuery<Cita> q = em.createQuery(
            "SELECT c FROM Cita c WHERE c.medico.id = :medId", Cita.class);
        q.setParameter("medId", medicoId);
        return q.getResultList();
    }

    public List<Cita> listarPorEstado(Estado estado) {
        TypedQuery<Cita> q = em.createQuery(
            "SELECT c FROM Cita c WHERE c.estado = :est", Cita.class);
        q.setParameter("est", estado);
        return q.getResultList();
    }

    public List<Cita> listarPorPacienteYEstado(Long pacienteId, Estado estado) {
        TypedQuery<Cita> q = em.createQuery(
            "SELECT c FROM Cita c WHERE c.paciente.id = :pacId AND c.estado = :est",
            Cita.class);
        q.setParameter("pacId", pacienteId);
        q.setParameter("est", estado);
        return q.getResultList();
    }

    public List<Cita> listarPorMedicoYFecha(Long medicoId, LocalDate fecha) {
        TypedQuery<Cita> q = em.createQuery(
            "SELECT c FROM Cita c WHERE c.medico.id = :medId AND c.fecha = :fecha",
            Cita.class);
        q.setParameter("medId", medicoId);
        q.setParameter("fecha", fecha);
        return q.getResultList();
    }
}
