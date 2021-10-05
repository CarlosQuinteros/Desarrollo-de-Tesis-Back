package backendAdministradorCompetenciasFutbolisticas.Excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@ResponseBody
public class RecursoNotFoundException extends RuntimeException {

    public RecursoNotFoundException(String mensaje){
        super(mensaje);
    }
}
