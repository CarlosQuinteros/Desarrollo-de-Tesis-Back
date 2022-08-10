package backendAdministradorCompetenciasFutbolisticas.Controller;

import backendAdministradorCompetenciasFutbolisticas.Dtos.CompetenciaDto;
import backendAdministradorCompetenciasFutbolisticas.Dtos.Interface.IGoleador;
import backendAdministradorCompetenciasFutbolisticas.Dtos.Mensaje;
import backendAdministradorCompetenciasFutbolisticas.Dtos.Posicion;
import backendAdministradorCompetenciasFutbolisticas.Entity.*;
import backendAdministradorCompetenciasFutbolisticas.Enums.TipoGenero;
import backendAdministradorCompetenciasFutbolisticas.Excepciones.InvalidDataException;
import backendAdministradorCompetenciasFutbolisticas.Security.Entity.Usuario;
import backendAdministradorCompetenciasFutbolisticas.Security.Service.UsuarioService;
import backendAdministradorCompetenciasFutbolisticas.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/competencias")
@CrossOrigin("*")
public class CompetenciaController {

    @Autowired
    private CompetenciaService competenciaService;

    @Autowired
    private JornadaService jornadaService;

    @Autowired
    private AsociacionDeportivaService asociacionDeportivaService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private GeneroService generoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private AnotacionService anotacionService;

    @Autowired
    private LogService logService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO_DE_TORNEOS')")
    public ResponseEntity<?> crearCompetencia(@Valid @RequestBody CompetenciaDto competenciaDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new InvalidDataException(bindingResult);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioService.getUsuarioLogueado(auth);
        AsociacionDeportiva asociacionDeportiva = asociacionDeportivaService.getById(competenciaDto.getIdAsociacionDeportiva());
        Categoria categoria = categoriaService.getDetalleCategoria(competenciaDto.getIdCategoria());
        TipoGenero tipoGenero = generoService.getTipoGeneroPorNombre(competenciaDto.getGenero());
        Competencia nuevaCompetencia =
                new Competencia(asociacionDeportiva,
                        categoria,
                        tipoGenero,
                        competenciaDto.getNombre(),
                        competenciaDto.getTemporada(),
                        competenciaDto.getDescripcion(),
                        competenciaDto.getTarjetasAmarillasPermitidas()
                );
        List<Club> clubesParticipantes = competenciaDto.getClubesParticipantes()
                .stream()
                .map(club -> clubService.getClub(club.getId()))
                .distinct()
                .collect(Collectors.toList());
        nuevaCompetencia.setClubesParticipantes(clubesParticipantes);
        competenciaService.guardarCompetencia(nuevaCompetencia);
        logService.guardarLogCreacionCompetencia(nuevaCompetencia, usuario);
        return new ResponseEntity<>(new Mensaje("Nueva competencia guardada correctamente"), HttpStatus.CREATED);
    }

    @PutMapping("/editar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO_DE_TORNEOS')")
    public ResponseEntity<?> editarCompetencia(@PathVariable Long id, @Valid @RequestBody CompetenciaDto competenciaDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new InvalidDataException(bindingResult);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioService.getUsuarioLogueado(auth);
        Competencia competencia = competenciaService.getCompetencia(id);
        AsociacionDeportiva asociacionDeportiva = asociacionDeportivaService.getById(competenciaDto.getIdAsociacionDeportiva());
        Categoria categoria = categoriaService.getDetalleCategoria(competenciaDto.getIdCategoria());
        TipoGenero tipoGenero = generoService.getTipoGeneroPorNombre(competenciaDto.getGenero());

        List<Club> clubesParticipantes = competenciaDto.getClubesParticipantes()
                .stream()
                .map(club -> clubService.getClub(club.getId()))
                .distinct()
                .collect(Collectors.toList());

        competencia.setCategoria(categoria);
        competencia.setAsociacionDeportiva(asociacionDeportiva);
        competencia.setGenero(tipoGenero);
        competencia.setNombre(competenciaDto.getNombre());
        competencia.setTemporada(competenciaDto.getTemporada());
        competencia.setDescripcion(competenciaDto.getDescripcion());
        competencia.setTarjetasAmarillasPermitidas(competenciaDto.getTarjetasAmarillasPermitidas());
        competencia.setClubesParticipantes(clubesParticipantes);
        competenciaService.guardarCompetencia(competencia);
        logService.guardarLogEdicionCompetencia(competencia, usuario);
        return new ResponseEntity<>(new Mensaje("Competencia guardada correctamente"), HttpStatus.OK);
    }

    @GetMapping("/detalle/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO_DE_TORNEOS')")
    public ResponseEntity<Competencia> listadoDeCompetencias(@PathVariable Long id){
        Competencia competencia = competenciaService.getCompetencia(id);
        return new ResponseEntity<>(competencia,HttpStatus.OK);
    }

    @GetMapping("/listado")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO_DE_TORNEOS')")
    public ResponseEntity<List<Competencia>> listadoDeCompetencias(){
        List<Competencia> competencias = competenciaService.getListadoCompetencias();
        return new ResponseEntity<>(competencias,HttpStatus.OK);
    }

    @GetMapping("/{id}/jornadas")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO_DE_TORNEOS')")
    public ResponseEntity<List<Jornada>> listadoJornadasDeCompetencia(@PathVariable Long id){
        Competencia competencia = competenciaService.getCompetencia(id);
        List<Jornada> jornadas = jornadaService.getListadoJornadasPorCompetencia(competencia.getId());
        return new ResponseEntity<>(jornadas, HttpStatus.OK);
    }

    @GetMapping("/{id}/goleadores")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO_DE_TORNEOS')")
    public ResponseEntity<List<IGoleador>> listadoDeGoleadoresDeCompetencia(@PathVariable Long id){
        List<IGoleador> goleadores = competenciaService.goleadoresDeUnaCompetencia(id);
        return new ResponseEntity<>(goleadores, HttpStatus.OK);
    }

    @GetMapping("/{id}/tabla-posiciones")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO_DE_TORNEOS')")
    public ResponseEntity<List<Posicion>> tablaDePosicionesDeCompetencia(@PathVariable Long id){
        List<Posicion> tablaDePosiciones = competenciaService.tablaPosicionesDeUnaCompetencia(id);
        return new ResponseEntity<>(tablaDePosiciones, HttpStatus.OK);
    }

    @GetMapping("/{id}/goles-mas-anotados")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO_DE_TORNEOS')")
    public ResponseEntity<?> tipoDeGolesMasAnotadosDeCompetencia(@PathVariable Long id){
        Map<String, Long> golesMasAnotados = competenciaService.tiposDegolesMasAnotadosDeUnaCompetencia(id);
        return new ResponseEntity<>(golesMasAnotados, HttpStatus.OK);
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO_DE_TORNEOS')")
    public ResponseEntity<?> eliminarCompetencia(@PathVariable Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioService.getUsuarioLogueado(auth);
        competenciaService.eliminarCompetencia(id);
        logService.guardarLogEliminacionCompetencia(id, usuario);
        return new ResponseEntity<>(new Mensaje("Competencia eliminada correctamente"),HttpStatus.OK);
    }

    @GetMapping("/cantidad-total")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO_DE_TORNEOS')")
    public ResponseEntity<Integer> cantidadTotalCompetencias(){
        Integer cantidadTotal = competenciaService.cantidadTotalCompetencias();
        return new ResponseEntity<>(cantidadTotal,HttpStatus.OK);
    }



}
