package ec.edu.ups.citas.dao;

import ec.edu.ups.citas.modelo.Rol;
import ec.edu.ups.citas.modelo.Usuario;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class UsuarioDAO {

    @PersistenceContext(unitName = "citasPersistenceUnit")
    private EntityManager em;

    public void crear(Usuario usuario) {
        em.persist(usuario);
    }

    public Usuario actualizar(Usuario usuario) {
        return em.merge(usuario);
    }

    public boolean eliminar(Long id) {
        Usuario u = em.find(Usuario.class, id);
        if (u != null) {
            em.remove(u);
            return true;
        }
		return false;
    }

    public Usuario buscarPorId(Long id) {
        return em.find(Usuario.class, id);
    }

    public List<Usuario> listarTodos() {
        return em.createQuery("SELECT u FROM Usuario u", Usuario.class)
                 .getResultList();
    }

    public Usuario buscarPorEmail(String email) {
        TypedQuery<Usuario> q = em.createQuery(
            "SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class);
        q.setParameter("email", email);
        List<Usuario> resultados = q.getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public List<Usuario> listarPorRol(Rol rol) {
        TypedQuery<Usuario> q = em.createQuery(
            "SELECT u FROM Usuario u WHERE u.rol = :rol", Usuario.class);
        q.setParameter("rol", rol);
        return q.getResultList();
    }

    public List<Usuario> buscarPorNombreOEmail(String texto) {
        String pattern = "%" + texto.toLowerCase() + "%";
        TypedQuery<Usuario> q = em.createQuery(
            "SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE :pat OR LOWER(u.email) LIKE :pat",
            Usuario.class);
        q.setParameter("pat", pattern);
        return q.getResultList();
    }
}
