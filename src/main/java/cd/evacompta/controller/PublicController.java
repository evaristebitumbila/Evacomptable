package cd.evacompta.controller;

import cd.evacompta.model.Formation;
import cd.evacompta.repository.AssocieRepository;
import cd.evacompta.service.FormationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pages publiques du site (vos pages HTML d'origine, servies par Thymeleaf).
 */
@Controller
public class PublicController {

    private final FormationService formationService;
    private final AssocieRepository associeRepo;

    public PublicController(FormationService formationService, AssocieRepository associeRepo) {
        this.formationService = formationService;
        this.associeRepo = associeRepo;
    }

    /**
     * Accueil : on prepare la liste des publications pour le carrousel 3D.
     * Chaque publication est une Map simple -> Thymeleaf la transforme en JSON
     * pour alimenter window.PUBLICATIONS (utilise par votre main.js d'origine).
     */
    @GetMapping("/")
    public String accueil(Model model) {
        List<Map<String, String>> pubs = new ArrayList<>();
        for (Formation f : formationService.findAll()) {
            Map<String, String> p = new LinkedHashMap<>();
            p.put("image", (f.getImage() != null && !f.getImage().isEmpty())
                    ? "/uploads/" + f.getImage() : "/logo.png");
            p.put("badge", f.getBadge() == null ? "" : f.getBadge());
            p.put("titre", f.getTitre() == null ? "" : f.getTitre());
            p.put("texte", f.getDescription() == null ? "" : f.getDescription());
            p.put("lien", "/formations/" + f.getId());
            p.put("lienTexte", "Voir le programme");
            pubs.add(p);
        }
        model.addAttribute("publications", pubs);
        return "index";
    }

    /** Detail d'une formation : affiche ses modules et sessions. */
    @GetMapping("/formations/{id}")
    public String formationDetail(@PathVariable Long id, Model model) {
        Formation f = formationService.findById(id);
        if (f == null) return "redirect:/";
        model.addAttribute("formation", f);
        return "formation-detail";
    }

    @GetMapping("/services") public String services() { return "services"; }
    /**
     * Page A propos. On transmet la liste des associes saisis dans le back office.
     * Si la liste est vide, le template masque completement la section
     * "Les associes a vos cotes" : rien n'apparait cote front.
     */
    @GetMapping("/cabinet")
    public String cabinet(Model model) {
        model.addAttribute("associes", associeRepo.findAllByOrderByPositionAscIdAsc());
        return "cabinet";
    }

    @GetMapping("/faq")      public String faq()      { return "faq"; }
    @GetMapping("/contact")  public String contact()  { return "contact"; }
}
