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

    @Inject private UsuarioDAO usuarioDAO;

    public UsuarioDTO crear(UsuarioDTO dto) {
        Usuario u = toEntity(dto);
        usuarioDAO.crear(u);
        return toDTO(u);
    }

    public UsuarioDTO actualizar(UsuarioDTO dto) {
        Usuario u = toEntity(dto);
        u = usuarioDAO.actualizar(u);
        return toDTO(u);
    }

    public boolean eliminar(Long id) {
    	return usuarioDAO.eliminar(id);
        
    }

    public UsuarioDTO buscarPorId(Long id) {
        Usuario u = usuarioDAO.buscarPorId(id);
        return u == null ? null : toDTO(u);
    }

    public List<UsuarioDTO> listarTodos() {
        return usuarioDAO.listarTodos().stream()
                 .map(this::toDTO)
                 .collect(Collectors.toList());
    }

    public UsuarioDTO buscarPorEmail(String email) {
        Usuario u = usuarioDAO.buscarPorEmail(email);
        return u == null ? null : toDTO(u);
    }

    public List<UsuarioDTO> listarPorRol(Rol rol) {
        return usuarioDAO.listarPorRol(rol).stream()
                 .map(this::toDTO)
                 .collect(Collectors.toList());
    }

    public List<UsuarioDTO> buscarPorNombreOEmail(String texto) {
        return usuarioDAO.buscarPorNombreOEmail(texto).stream()
                 .map(this::toDTO)
                 .collect(Collectors.toList());
    }

    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setEmail(u.getEmail());
        dto.setPassword(u.getPassword());
        dto.setRol(u.getRol().name());
        return dto;
    }

    private Usuario toEntity(UsuarioDTO dto) {
        Usuario u = dto.getId() != null
                ? usuarioDAO.buscarPorId(dto.getId())
                : new Usuario();
        u.setNombre(dto.getNombre());
        u.setApellido(dto.getApellido());
        u.setEmail(dto.getEmail());
        u.setPassword(dto.getPassword());
        u.setRol(Rol.valueOf(dto.getRol()));
        return u;
    }
}
