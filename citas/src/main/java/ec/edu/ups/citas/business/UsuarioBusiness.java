package ec.edu.ups.citas.business;

import ec.edu.ups.citas.dao.UsuarioDAO;
import ec.edu.ups.citas.dto.UsuarioDTO;
import ec.edu.ups.citas.modelo.Rol;
import ec.edu.ups.citas.modelo.Usuario;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class UsuarioBusiness {

    @Inject
    private UsuarioDAO dao;

    public UsuarioDTO crear(UsuarioDTO dto) {
        Usuario u = toEntity(dto);
        dao.crear(u);
        return toDTO(u);
    }

    public UsuarioDTO actualizar(UsuarioDTO dto) {
        Usuario u = toEntity(dto);
        u = dao.actualizar(u);
        return toDTO(u);
    }

    public boolean eliminar(Long id) {
        return dao.eliminar(id);
    }

    public UsuarioDTO buscarPorId(Long id) {
        return toDTO(dao.buscarPorId(id));
    }

    public UsuarioDTO buscarPorFirebaseUid(String uid) {
        return toDTO(dao.buscarPorFirebaseUid(uid));
    }

    public List<UsuarioDTO> listarTodos() {
        return dao.listarTodos()
                  .stream()
                  .map(this::toDTO)
                  .collect(Collectors.toList());
    }

    public List<UsuarioDTO> listarPorRol(Rol rol) {
        return dao.listarPorRol(rol)
                  .stream()
                  .map(this::toDTO)
                  .collect(Collectors.toList());
    }

    private Usuario toEntity(UsuarioDTO dto) {
        // Si dto.getId()==null → nueva
        Usuario u = (dto.getId() != null)
            ? dao.buscarPorId(dto.getId())
            : new Usuario();
        u.setFirebaseUid(dto.getFirebaseUid());
        u.setDisplayName(dto.getDisplayName());
        u.setEmail(dto.getEmail());
        u.setTelefono(dto.getTelefono());
        String raw = dto.getRole().trim().toUpperCase();
        u.setRole(Rol.valueOf(raw)); 
        return u;
    }

    public UsuarioDTO actualizarParcial(UsuarioDTO dto) {
        // 1. Traigo la entidad existente
        Usuario u = dao.buscarPorId(dto.getId());
        if (u == null) {
            return null;
        }

        // 2. Solo seteo los campos que vienen no nulos en el DTO
        if (dto.getDisplayName() != null) {
            u.setDisplayName(dto.getDisplayName());
        }
        if (dto.getEmail() != null) {
            u.setEmail(dto.getEmail());
        }
        if (dto.getTelefono() != null) {
            u.setTelefono(dto.getTelefono());
        }
        if (dto.getRole() != null) {
            // dto.getRole() es String, conviértelo a Rol enum
            u.setRole(Rol.valueOf(dto.getRole()));
        }

        // 3. Guardo cambios
        u = dao.actualizar(u);

        // 4. Devuelvo DTO
        return toDTO(u);
    }

    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setFirebaseUid(u.getFirebaseUid());
        dto.setDisplayName(u.getDisplayName());
        dto.setEmail(u.getEmail());
        dto.setTelefono(u.getTelefono());
        dto.setRole(u.getRole().name());
        return dto;
    }
}