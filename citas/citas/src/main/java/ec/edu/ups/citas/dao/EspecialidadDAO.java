package ec.edu.ups.citas.dao;

import ec.edu.ups.citas.modelo.Especialidad;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class EspecialidadDAO {

    @PersistenceContext(unitName = "citasPersistenceUnit")
    private EntityManager em;

    public void crear(Especialidad e) {
        em.persist(e);
    }

    public Especialidad actualizar(Especialidad e) {
        return em.merge(e);
    }

    public boolean eliminar(Long id) {
        Especialidad e = em.find(Especialidad.class, id);
        if (e != null) {
            em.remove(e);
            return true;
        }
        return false;
    }

    public Especialidad buscarPorId(Long id) {
        return em.find(Especialidad.class, id);
    }

    public List<Especialidad> listarTodos() {
        return em.createQuery("SELECT e FROM Especialidad e", Especialidad.class)
                 .getResultList();
    }
}
