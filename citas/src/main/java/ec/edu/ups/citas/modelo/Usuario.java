package ec.edu.ups.citas.modelo;

import jakarta.persistence.*;

@Entity
@Table(name="usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                  // ← PK interno

    @Column(name = "firebase_uid", unique = true, nullable = false)
    private String firebaseUid;        // ← UID de Firebase

    @Column(name = "display_name")
    private String displayName;

    @Column(unique = true, nullable = false)
    private String email;

    private String telefono;

    @Enumerated(EnumType.STRING)
    private Rol role;

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirebaseUid() { return firebaseUid; }
    public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Rol getRole() { return role; }
    public void setRole(Rol role) { this.role = role; }
}