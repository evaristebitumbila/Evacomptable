package cd.evacompta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * Un Module appartient a une Formation (ex : "Module 1 : Introduction au metier
 * de caissier(ere)"). Il contient une liste de Sessions (les lignes cochees).
 */
@Entity
@Table(name = "modules")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre du module est obligatoire")
    @Column(nullable = false)
    private String titre;

    /** Ordre d'affichage du module dans la formation */
    private int position = 0;

    /** Formation parente (cote plusieurs de la relation) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    private Formation formation;

    /** Les sessions du module (les points de la liste) */
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC, id ASC")
    private List<Session> sessions = new ArrayList<>();

    public Module() {}

    public void addSession(Session s) {
        s.setModule(this);
        this.sessions.add(s);
    }

    /** Nombre de sessions, utilise dans l'affichage "5 sessions" */
    @Transient
    public int getNombreSessions() { return sessions == null ? 0 : sessions.size(); }

    // ---- Getters / Setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public Formation getFormation() { return formation; }
    public void setFormation(Formation formation) { this.formation = formation; }
    public List<Session> getSessions() { return sessions; }
    public void setSessions(List<Session> sessions) { this.sessions = sessions; }
}
