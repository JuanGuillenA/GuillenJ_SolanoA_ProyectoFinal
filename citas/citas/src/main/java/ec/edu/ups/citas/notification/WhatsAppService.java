package ec.edu.ups.citas.notification;

import jakarta.ejb.Stateless;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Stateless
public class WhatsAppService {

    private static final String 1   = "";
    private static final String 2= "";
    private static final String 3= "";
    private static final String 4= "";

    public void enviarPlantillaWhatsApp(String numeroDestino, Date fechaHora) {
        String fecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                          .format(fechaHora.toInstant()
                                            .atZone(TimeZone.getDefault().toZoneId())
                                            .toLocalDate());
        String hora  = DateTimeFormatter.ofPattern("HH:mm")
                          .format(fechaHora.toInstant()
                                            .atZone(TimeZone.getDefault().toZoneId())
                                            .toLocalTime());

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(
                "https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID + "/Messages.json"
            );

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("To",                "whatsapp:" + numeroDestino));
            params.add(new BasicNameValuePair("From",              FROM));
            params.add(new BasicNameValuePair("ContentSid",        CONTENT_SID));
            params.add(new BasicNameValuePair(
                "ContentVariables",
                "{\"1\":\"" + fecha + "\",\"2\":\"" + hora + "\"}"
            ));

            post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

            String auth        = ACCOUNT_SID + ":" + AUTH_TOKEN;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            post.setHeader("Authorization", "Basic " + encodedAuth);

            int statusCode = client.execute(post).getCode();
            if (statusCode == 201) {
                System.out.println("✅ WhatsApp enviado a " + numeroDestino);
            } else {
                System.out.println("⚠️ Error WhatsApp, HTTP " + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}