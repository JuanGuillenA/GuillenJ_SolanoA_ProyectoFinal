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

    private static final String ACCOUNT_SID = "ACb2aad6bd68d6fedc1111909b7b75e1b6"; // Tu SID real
    private static final String AUTH_TOKEN = "84648e3f80252d4bc986799896d235e9";   // Tu token real
    private static final String FROM = "whatsapp:+14155238886";                   // Número sandbox
    private static final String CONTENT_SID = "HX9cc37b283068a9eaadcc986dac3df04d"; // Tu ContentSid real

    public void enviarPlantillaWhatsApp(String numeroDestino, String fecha, String hora) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID + "/Messages.json");

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("To", "whatsapp:" + numeroDestino));
            params.add(new BasicNameValuePair("From", FROM));
            params.add(new BasicNameValuePair("ContentSid", CONTENT_SID));

            String jsonVars = "{\"1\":\"" + fecha + "\", \"2\":\"" + hora + "\"}";
            params.add(new BasicNameValuePair("ContentVariables", jsonVars));

            post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

            String auth = ACCOUNT_SID + ":" + AUTH_TOKEN;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            post.setHeader("Authorization", "Basic " + encodedAuth);

            var response = client.execute(post);
            int statusCode = response.getCode();
            System.out.println("📨 Código HTTP de respuesta: " + statusCode);
            System.out.println("📦 Enviando JSON variables: " + jsonVars);

            if (statusCode != 201) {
                System.out.println("⚠️ Error al enviar WhatsApp. Revisa tu plantilla o SID/token.");
            } else {
                System.out.println("✅ WhatsApp enviado a " + numeroDestino);
            }

            response.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}