package backendAdministradorCompetenciasFutbolisticas.Controller;

import backendAdministradorCompetenciasFutbolisticas.Dtos.Mensaje;
import backendAdministradorCompetenciasFutbolisticas.Dtos.SustitucionDto;
import backendAdministradorCompetenciasFutbolisticas.Entity.Club;
import backendAdministradorCompetenciasFutbolisticas.Entity.Jugador;
import backendAdministradorCompetenciasFutbolisticas.Entity.Partido;
import backendAdministradorCompetenciasFutbolisticas.Entity.Sustitucion;
import backendAdministradorCompetenciasFutbolisticas.Excepciones.BadRequestException;
import backendAdministradorCompetenciasFutbolisticas.Excepciones.InvalidDataException;
import backendAdministradorCompetenciasFutbolisticas.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/sustituciones")
@CrossOrigin("*")
public class SustitucionController {

    @Autowired
    private ClubService clubService;

    @Autowired
    private PartidoService partidoService;

    @Autowired
    private SustitucionService sustitucionService;

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private AnotacionService anotacionService;

    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO_DE_TORNEOS')")
    public ResponseEntity<?> crearSustitucion(@Valid @RequestBody SustitucionDto nuevaSustitucion, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new InvalidDataException(bindingResult);
        }
        Partido partido = partidoService.getDetallePartido(nuevaSustitucion.getIdPartido());
        Jugador jugadorSale = jugadorService.getJugador(nuevaSustitucion.getIdJugadorSale());
        Jugador jugadorEntra = jugadorService.getJugador(nuevaSustitucion.getIdJugadorEntra());
        Club clubSustituye = clubService.getClub(nuevaSustitucion.getIdClubSustituye());
        Sustitucion sustitucion = new Sustitucion(partido,clubSustituye,nuevaSustitucion.getMinuto(),jugadorSale,jugadorEntra);
        sustitucionService.guardarSustitucion(sustitucion);
        return new ResponseEntity<>(new Mensaje("Sustitucion guardada correctamente"), HttpStatus.CREATED);
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO_DE_TORNEOS')")
    public ResponseEntity<?> eliminarSustitucion(@PathVariable Long id){
        Sustitucion sustitucion = sustitucionService.getDetalleSustitucion(id);
        if (anotacionService.existeAnotacionEnPartidoDeJugador(sustitucion.getPartido().getId(), sustitucion.getJugadorEntra().getId())) {

            throw new BadRequestException("No se puede eliminar la sustitucion porque el jugador que entro marco un gol");
        }
        sustitucionService.eliminarSustitucion(sustitucion.getId());
        return new ResponseEntity<>(new Mensaje("Sustitucion eliminada correctamente"),HttpStatus.OK);
    }

}
