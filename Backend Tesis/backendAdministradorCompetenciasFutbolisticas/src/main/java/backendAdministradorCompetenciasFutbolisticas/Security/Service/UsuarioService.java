package backendAdministradorCompetenciasFutbolisticas.Security.Service;

import backendAdministradorCompetenciasFutbolisticas.Entity.Log;
import backendAdministradorCompetenciasFutbolisticas.Excepciones.BadRequestException;
import backendAdministradorCompetenciasFutbolisticas.Security.Entity.Usuario;
import backendAdministradorCompetenciasFutbolisticas.Security.Repository.UsuarioRepository;
import backendAdministradorCompetenciasFutbolisticas.Service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    LogService logService;

    public Optional<Usuario> getByNombreUsuario(String nombreUsuario){
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }
    public Optional<Usuario> getByNombreUsuarioOrEmail(String nombreUsuarioOrEmail){
        return usuarioRepository.findByNombreUsuarioOrEmail(nombreUsuarioOrEmail,nombreUsuarioOrEmail);
    }

    public boolean existByNombreUsuario(String nombreUsuario){
        return usuarioRepository.existsByNombreUsuario(nombreUsuario);
    }

    public boolean existByEmail(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public void save(Usuario usuario){
        usuarioRepository.save(usuario);
    }

    public List<Usuario> list(){
        return usuarioRepository.findAllByOrderByApellidoAsc();
    }

    public Optional<Usuario> getById(Long id){
        return  usuarioRepository.findById(id);
    }

    public boolean existById(Long id){
        return usuarioRepository.existsById(id);
    }

    public void delete(Long id){
        logService.eliminarActividadDeUsuario(id);
        usuarioRepository.deleteById(id);
    }

    public void cambiarEstado(Long id){
        Usuario usuario = usuarioRepository.getById(id);
        usuario.cambiarEstado();
        usuarioRepository.save(usuario);
    }

    public boolean existByNombreUsuarioAndActivoIsTrue(String nombreUsuario){
        return  usuarioRepository.existsByNombreUsuarioAndActivoIsTrue(nombreUsuario);
    }

    public int cantidadUsuarios(){
        return usuarioRepository.findAll().size();
    }

    public int cantidadDeActivos(){
        return usuarioRepository.countUsuarioByActivoTrue();
    }
    public int cantidadDeInactivos(){

        return usuarioRepository.countUsuarioByActivoFalse();
    }

    public Optional<Usuario> getByTokenPassword(String tokenPassword){
        return usuarioRepository.findByTokenPassword(tokenPassword);
    }

    public boolean existByTokenPassword(String tokenPassword){
        return usuarioRepository.existsByTokenPassword(tokenPassword);
    }

    public Usuario getUsuarioLogueado(Authentication auth){
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Usuario usuario = usuarioRepository.findByNombreUsuario(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException("El usuario no existe"));
        return usuario;
    }

}
