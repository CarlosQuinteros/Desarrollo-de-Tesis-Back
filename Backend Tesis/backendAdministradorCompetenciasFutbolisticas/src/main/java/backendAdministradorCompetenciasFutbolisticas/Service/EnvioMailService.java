package backendAdministradorCompetenciasFutbolisticas.Service;

import backendAdministradorCompetenciasFutbolisticas.Security.Dto.UsuarioDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EnvioMailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailUsuarioCreado(UsuarioDto usuarioNuevo) {

        String asunto = "Nuevo Usuario Creado";
        String destinatario = usuarioNuevo.getEmail();
        String contenido = "Hola " + usuarioNuevo.getNombre() + " " + usuarioNuevo.getApellido() + "!\n"
                + "Sus credenciales de acceso al sistema son:\n\n" +
                "Usuario: "+ usuarioNuevo.getNombreUsuario() +"\n" +
                "Contraseña: "+ usuarioNuevo.getPassword() +
                "\n\nSe recomienda cambiar la contraseña luego de acceder al sistema.\n"+
                "\nSaliudos!\nate Administrador de Competencias Futbolisticas de Chilecito";
        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(destinatario);
        email.setSubject(asunto);
        email.setText(contenido);

        mailSender.send(email);
    }
}
