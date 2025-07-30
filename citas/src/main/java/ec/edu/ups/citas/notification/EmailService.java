package ec.edu.ups.citas.notification;

import jakarta.ejb.Stateless;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

@Stateless
public class EmailService {

    private final String remitente = "arisolri1@gmail.com";    // tu cuenta
    private final String claveApp  = "dcwt xkfm ubdx pwyp";    // tu app-password

    public void enviarCorreo(String destinatario, String asunto, String mensajeTexto) {
        Properties props = new Properties();
        props.put("mail.smtp.auth",           "true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.host",           "smtp.gmail.com");
        props.put("mail.smtp.port",           "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, claveApp);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(remitente));
            msg.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(destinatario)
            );
            msg.setSubject(asunto);
            msg.setText(mensajeTexto);
            Transport.send(msg);
            System.out.println("✅ Correo enviado a " + destinatario);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
