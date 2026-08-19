package cd.evacompta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Un Associe = une carte de la section "Les associes a vos cotes"
 * de la page A propos (/cabinet).
 *
 * IMPORTANT : la section publique n'apparait QUE s'il existe au moins
 * un associe enregistre depuis le back office. Tant que rien n'est saisi,
 * rien ne s'affiche sur le site.
 */
@Entity
@Table(name = "associes")
public class Associe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom complet de l'associe (obligatoire) */
    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    /** Fonction affichee sous le nom, ex : "Associe(e) - Expertise comptable" */
    private String role;

    /** Presentation courte : parcours, specialites, domaines d'intervention */
    @Column(length = 900)
    private String bio;

    /** Nom du fichier photo televerse (facultatif). Si vide -> initiales. */
    private String photo;

    /** Ordre d'affichage (petit = en premier) */
    private int position = 0;

    public Associe() {}

    /**
     * Initiales calculees a partir du nom (ex : "Eva Sambi" -> "ES").
     * Utilisees quand aucune photo n'a ete televersee.
     */
    @Transient
    public String getInitiales() {
        if (nom == null || nom.isBlank()) return "";
        String[] parts = nom.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty() && sb.length() < 2) {
                sb.append(Character.toUpperCase(part.charAt(0)));
            }
        }
        return sb.toString();
    }

    // ---- Getters / Setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
