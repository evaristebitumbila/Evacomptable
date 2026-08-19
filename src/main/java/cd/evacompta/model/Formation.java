package cd.evacompta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * Une Formation = une carte du carrousel 3D de la page d'accueil.
 * Elle contient une liste de Modules, chacun contenant des Sessions.
 */
@Entity
@Table(name = "formations")
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Column(nullable = false)
    private String titre;

    /** Badge affiche sur la carte, ex : "Formation · 150 $" */
    private String badge;

    /** Description courte affichee sur la carte du carrousel */
    @Column(length = 600)
    private String description;

    /** Contenu enrichi (HTML) affiche dans le detail de la formation */
    @Lob
    @Column(columnDefinition = "CLOB")
    private String contenu;

    /** Nom du fichier image televerse (ex : actu-ohada.jpg) */
    private String image;

    /** Ordre d'affichage dans le carrousel (petit = en premier) */
    private int position = 0;

    /**
     * Les modules de la formation. cascade = ALL : sauver/supprimer la formation
     * sauve/supprime aussi ses modules. orphanRemoval : un module retire de la
     * liste est supprime en base.
     */
    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC, id ASC")
    private List<Module> modules = new ArrayList<>();

    public Formation() {}

    // Ajoute un module en maintenant le lien bidirectionnel
    public void addModule(Module m) {
        m.setFormation(this);
        this.modules.add(m);
    }

    // ---- Getters / Setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public List<Module> getModules() { return modules; }
    public void setModules(List<Module> modules) { this.modules = modules; }
}
