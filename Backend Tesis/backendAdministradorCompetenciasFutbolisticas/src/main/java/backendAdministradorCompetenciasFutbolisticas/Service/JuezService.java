package backendAdministradorCompetenciasFutbolisticas.Service;

import backendAdministradorCompetenciasFutbolisticas.Entity.Juez;
import backendAdministradorCompetenciasFutbolisticas.Excepciones.ResourceNotFoundException;
import backendAdministradorCompetenciasFutbolisticas.Repository.JuezRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class JuezService {

    @Autowired
    private JuezRepository juezRepository;

    public List<Juez> getListadoJueces() {
        return juezRepository.findByOrderByApellidos();
    }

    public boolean guardarNuevoJuez(Juez juez){
        return juezRepository.save(juez).getId() != null;
    }

    public Juez guardarJuez(Juez juez){
        return  juezRepository.save(juez);
    }

    public void eliminarJuez(Long id){
        juezRepository.deleteById(id);
    }

    public Juez getJuezPorId(Long id){
        return juezRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un juez con el ID: " + id));
    }

    public Juez getJuezPorDocumento(String documento){
        return juezRepository.findByDocumento(documento)
                .orElseThrow(()-> new ResourceNotFoundException("No existe un Juez con el documento" + documento));
    }

    public  Juez getJuezPorLegajo(String legajo){
        return juezRepository.findByLegajo(legajo)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un Juez con el legajo " + legajo));
    }

    public Juez getJuezPorDocumentoOrLegajo(String documentoOrLegajo){
        return juezRepository.findByDocumentoOrAndLegajo(documentoOrLegajo, documentoOrLegajo)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un Juez con el documento o legajo: " + documentoOrLegajo));
    }

    public Boolean existeJuezPorId(Long id){
        return juezRepository.existsById(id);
    }

    public Boolean existeJuezPorDocumento(String documento){
        return juezRepository.existsByDocumento(documento);
    }

    public Boolean existeJuezPorLegajo(String legajo){
        return juezRepository.existsByLegajo(legajo);
    }

    public Integer getCantidadTotalJueces(){
        return juezRepository.countJuezBy();
    }

}
