package cd.evacompta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Une Session est une ligne d'un module (ex : "Definition du metier").
 * C'est le plus petit element du programme de formation.
 */
@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'intitule de la session est obligatoire")
    @Column(nullable = false)
    private String intitule;

    /** Ordre d'affichage dans le module */
    private int position = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    public Session() {}

    public Session(String intitule) { this.intitule = intitule; }

    // ---- Getters / Setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIntitule() { return intitule; }
    public void setIntitule(String intitule) { this.intitule = intitule; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }
}
