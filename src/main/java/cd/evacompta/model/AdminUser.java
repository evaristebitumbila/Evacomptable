package cd.evacompta.model;

import jakarta.persistence.*;

/**
 * Compte administrateur du back office.
 * Le mot de passe est stocke chiffre (BCrypt).
 */
@Entity
@Table(name = "admin_users")
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    /** Mot de passe chiffre avec BCrypt */
    @Column(nullable = false)
    private String password;

    public AdminUser() {}

    public AdminUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // ---- Getters / Setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
