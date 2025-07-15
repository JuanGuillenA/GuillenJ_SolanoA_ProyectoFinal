package ec.edu.ups.citas.dao;

import ec.edu.ups.citas.modelo.Horario;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

@Stateless
public class HorarioDAO {

    @PersistenceContext(unitName = "citasPersistenceUnit")
    private EntityManager em;

    public void crear(Horario h) {
        em.persist(h);
    }

    public Horario actualizar(Horario h) {
        return em.merge(h);
    }

    public void eliminar(Long id) {
        Horario h = em.find(Horario.class, id);
        if (h != null) {
            em.remove(h);
        }
    }

    public Horario buscarPorId(Long id) {
        return em.find(Horario.class, id);
    }

    public List<Horario> listarPorMedico(Long medicoId) {
        TypedQuery<Horario> q = em.createQuery(
            "SELECT h FROM Horario h WHERE h.medico.id = :medId", Horario.class);
        q.setParameter("medId", medicoId);
        return q.getResultList();
    }

    public List<Horario> listarPorMedicoYFecha(Long medicoId, LocalDate fecha) {
        TypedQuery<Horario> q = em.createQuery(
            "SELECT h FROM Horario h WHERE h.medico.id = :medId AND h.fecha = :fecha",
            Horario.class);
        q.setParameter("medId", medicoId);
        q.setParameter("fecha", fecha);
        return q.getResultList();
    }
}
