package ec.edu.ups.citas.service;

import java.io.IOException;
import java.io.InputStream;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Singleton
@Startup
public class FirebaseInit {

  @PostConstruct
  public void init() {
    try (InputStream in = getClass()
                         .getClassLoader()
                         .getResourceAsStream("proyectoguillensolano-firebase-adminsdk-fbsvc-f740cd7e2d.json")) {
      if (in == null) {
        throw new IllegalStateException("No encuentro serviceAccountKey.json en resources");
      }
      FirebaseOptions options = FirebaseOptions.builder()
          .setCredentials(GoogleCredentials.fromStream(in))
          .build();
      if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options);
      }
    } catch (IOException e) {
      throw new IllegalStateException("Error leyendo las credenciales de Firebase", e);
    }
  }
}
