// src/main/java/ec/edu/ups/citas/business/UsuarioBusiness.java
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
    
    /**
     * Actualiza solo los campos no nulos del DTO.
     */
    public UsuarioDTO actualizarParcial(UsuarioDTO dto) {
        if (dto.getId() == null) {
            return null;
        }
        Usuario u = dao.buscarPorId(dto.getId());
        if (u == null) {
            return null;
        }
        if (dto.getFirebaseUid() != null) {
            u.setFirebaseUid(dto.getFirebaseUid());
        }
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
            // Convierte a enum sólo si viene rol
            u.setRole(Rol.valueOf(dto.getRole().trim().toUpperCase()));
        }
        u = dao.actualizar(u);
        return toDTO(u);
    }
    

    public boolean eliminar(Long id) {
        return dao.eliminar(id);
    }

    public UsuarioDTO buscarPorId(Long id) {
        Usuario u = dao.buscarPorId(id);
        return u != null ? toDTO(u) : null;
    }

    // Versión antigua (solo busca)
    public UsuarioDTO buscarPorFirebaseUid(String uid) {
        Usuario u = dao.buscarPorFirebaseUid(uid);
        return u != null ? toDTO(u) : null;
    }

    // Versión nueva: busca y crea si no existe
    public UsuarioDTO buscarPorFirebaseUid(
            String uid, String email, String displayName) {

        Usuario u = dao.buscarPorFirebaseUid(uid);
        if (u == null) {
            UsuarioDTO nuevo = new UsuarioDTO();
            nuevo.setFirebaseUid(uid);
            nuevo.setEmail(email);
            nuevo.setDisplayName(displayName);
            nuevo.setRole(Rol.PACIENTE.name());
            u = toEntity(nuevo);
            dao.crear(u);
        }
        return toDTO(u);
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
        Usuario u = (dto.getId() != null)
            ? dao.buscarPorId(dto.getId())
            : new Usuario();

        if (dto.getFirebaseUid() != null) {
            u.setFirebaseUid(dto.getFirebaseUid());
        }
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
            u.setRole(Rol.valueOf(dto.getRole().trim().toUpperCase()));
        }
        return u;
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