package backendAdministradorCompetenciasFutbolisticas.Security.Service;


import backendAdministradorCompetenciasFutbolisticas.Security.Entity.Rol;
import backendAdministradorCompetenciasFutbolisticas.Security.Repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class RolService {

    @Autowired
    RolRepository rolRepository;
    Optional<Rol> getRolByNombre(String nombreRol){
        return rolRepository.findByRolNombre(nombreRol);
    }

}
