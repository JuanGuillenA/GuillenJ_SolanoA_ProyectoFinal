package ec.edu.ups.citas.dao;

import ec.edu.ups.citas.modelo.Medico;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class MedicoDAO {

    @PersistenceContext(unitName = "citasPersistenceUnit")
    private EntityManager em;

    public void crear(Medico m) {
        em.persist(m);
    }

    public Medico actualizar(Medico m) {
        return em.merge(m);
    }

    public void eliminar(Long id) {
        Medico m = em.find(Medico.class, id);
        if (m != null) {
            em.remove(m);
        }
    }

    public Medico buscarPorId(Long id) {
        return em.find(Medico.class, id);
    }

    public List<Medico> listarTodos() {
        return em.createQuery("SELECT m FROM Medico m", Medico.class)
                 .getResultList();
    }

    public List<Medico> listarPorEspecialidad(Long especialidadId) {
        TypedQuery<Medico> q = em.createQuery(
            "SELECT m FROM Medico m WHERE m.especialidad.id = :espId", Medico.class);
        q.setParameter("espId", especialidadId);
        return q.getResultList();
    }

    public List<Medico> buscarPorNombre(String texto) {
        String pattern = "%" + texto.toLowerCase() + "%";
        TypedQuery<Medico> q = em.createQuery(
            "SELECT m FROM Medico m WHERE LOWER(m.nombre) LIKE :pat OR LOWER(m.apellido) LIKE :pat",
            Medico.class);
        q.setParameter("pat", pattern);
        return q.getResultList();
    }
}
