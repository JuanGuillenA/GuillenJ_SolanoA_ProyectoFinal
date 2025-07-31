package ec.edu.ups.citas.notification;

import ec.edu.ups.citas.dao.CitaDAO;
import ec.edu.ups.citas.modelo.Cita;
import ec.edu.ups.citas.modelo.Estado;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.Schedule;
import jakarta.inject.Inject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Startup
public class NotificationService {

    @Inject private CitaDAO          citaDAO;
    @Inject private EmailService     emailService;
    @Inject private WhatsAppService  whatsappService;

    // Para evitar envíos duplicados en este despliegue
    private final Set<Long> notifiedDayBefore   = ConcurrentHashMap.newKeySet();
    private final Set<Long> notifiedHoursBefore = ConcurrentHashMap.newKeySet();
    private final Set<Long> notified10min       = ConcurrentHashMap.newKeySet();

    /**
     * Cada minuto chequea todas las citas pendientes y dispara notificaciones en:
     * - 24 h antes
     * - 3 h antes
     * - 10 min antes (opcional)
     * - 5 min antes (opcional)
     */
    @Schedule(hour="*", minute="*", second="0", persistent=false)
    public void verificarCitas() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Cita> citas = citaDAO.listarPorEstado(Estado.PENDIENTE);

        for (Cita c : citas) {
            LocalDateTime citaDT = LocalDateTime.of(c.getFecha(), c.getHora());
            long minutos = Duration.between(ahora, citaDT).toMinutes();
            Long id = c.getId();

            // 24 horas antes = 1440 minutos
            if (minutos == 24 * 60 && notifiedDayBefore.add(id)) {
                sendReminder(c,
                    "🗓️ Recordatorio: tu cita es mañana a las " + c.getHora(),
                    citaDT
                );
            }
            // 3 horas antes = 180 minutos
            else if (minutos == 3 * 60 && notifiedHoursBefore.add(id)) {
                sendReminder(c,
                    "⏰ Aviso: tu cita es en 3 horas, a las " + c.getHora(),
                    citaDT
                );
            }
            // 10 min antes (opcional)
            else if (minutos == 10 && notified10min.add(id)) {
                sendReminder(c,
                    "🕙 Recordatorio: tu cita es en 10 minutos",
                    citaDT
                );
            }
        }
    }

    /**
     * Envía correo y WhatsApp usando los servicios inyectados.
     *
     * @param c      Entidad cita
     * @param asunto Asunto o plantilla corta
     * @param citaDT Fecha+hora de la cita como LocalDateTime
     */
    private void sendReminder(Cita c, String asunto, LocalDateTime citaDT) {
        String textoEmail = String.format(
            "Hola %s,\n\n%s el %s a las %s.\n\n¡Saludos!",
            c.getPaciente().getDisplayName(),
            asunto,
            c.getFecha(),
            c.getHora()
        );

        // Envío por correo
        emailService.enviarCorreo(
            c.getPaciente().getEmail(),
            asunto,
            textoEmail
        );

        // **Envío por WhatsApp adaptado a la firma (String, String, String)**
        DateTimeFormatter dfFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dfHora  = DateTimeFormatter.ofPattern("HH:mm");

        String fechaStr = citaDT.format(dfFecha);
        String horaStr  = citaDT.format(dfHora);

        whatsappService.enviarPlantillaWhatsApp(
            c.getPaciente().getTelefono(),
            fechaStr,
            horaStr
        );
    }
}