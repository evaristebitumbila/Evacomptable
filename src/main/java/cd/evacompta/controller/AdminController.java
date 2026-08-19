package cd.evacompta.controller;

import cd.evacompta.model.*;
import cd.evacompta.model.Module;
import cd.evacompta.repository.*;
import cd.evacompta.service.FileStorageService;
import cd.evacompta.service.FormationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Back office (accessible seulement apres connexion, voir SecurityConfig).
 * Gere le CRUD complet : formations (cartes 3D), modules et sessions,
 * ainsi que le changement de mot de passe.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final FormationService formationService;
    private final ModuleRepository moduleRepo;
    private final SessionRepository sessionRepo;
    private final AdminUserRepository adminRepo;
    private final AssocieRepository associeRepo;
    private final FileStorageService fileStorage;
    private final PasswordEncoder encoder;

    public AdminController(FormationService formationService, ModuleRepository moduleRepo,
                           SessionRepository sessionRepo, AdminUserRepository adminRepo,
                           AssocieRepository associeRepo,
                           FileStorageService fileStorage, PasswordEncoder encoder) {
        this.formationService = formationService;
        this.moduleRepo = moduleRepo;
        this.sessionRepo = sessionRepo;
        this.adminRepo = adminRepo;
        this.associeRepo = associeRepo;
        this.fileStorage = fileStorage;
        this.encoder = encoder;
    }

    /* ===================== TABLEAU DE BORD ===================== */
    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        model.addAttribute("formations", formationService.findAll());
        model.addAttribute("page", "admin");
        return "admin/dashboard";
    }

    /* ===================== FORMATIONS (CRUD) ===================== */

    /** Formulaire de creation. */
    @GetMapping("/formations/new")
    public String newFormation(Model model) {
        model.addAttribute("formation", new Formation());
        model.addAttribute("page", "admin");
        return "admin/formation-form";
    }

    /** Formulaire de modification. */
    @GetMapping("/formations/{id}/edit")
    public String editFormation(@PathVariable Long id, Model model) {
        Formation f = formationService.findById(id);
        if (f == null) return "redirect:/admin";
        model.addAttribute("formation", f);
        model.addAttribute("page", "admin");
        return "admin/formation-form";
    }

    /** Enregistrement (creation ou modification). */
    @PostMapping("/formations/save")
    public String saveFormation(@Valid @ModelAttribute("formation") Formation formation,
                                BindingResult binding,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                Model model,
                                RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("page", "admin");
            return "admin/formation-form";
        }

        // Si on modifie, on recupere l'existant pour conserver modules et image
        if (formation.getId() != null) {
            Formation existing = formationService.findById(formation.getId());
            if (existing != null) {
                existing.setTitre(formation.getTitre());
                existing.setBadge(formation.getBadge());
                existing.setDescription(formation.getDescription());
                existing.setContenu(formation.getContenu());
                existing.setPosition(formation.getPosition());
                String stored = fileStorage.store(imageFile);
                if (stored != null) existing.setImage(stored);
                formationService.save(existing);
                ra.addFlashAttribute("msg", "Formation modifiée avec succès.");
                return "redirect:/admin";
            }
        }

        // Creation
        String stored = fileStorage.store(imageFile);
        if (stored != null) formation.setImage(stored);
        formationService.save(formation);
        ra.addFlashAttribute("msg", "Formation publiée avec succès.");
        return "redirect:/admin";
    }

    /** Suppression d'une formation (et de ses modules/sessions en cascade). */
    @PostMapping("/formations/{id}/delete")
    public String deleteFormation(@PathVariable Long id, RedirectAttributes ra) {
        formationService.delete(id);
        ra.addFlashAttribute("msg", "Formation supprimée.");
        return "redirect:/admin";
    }

    /* ===================== MODULES & SESSIONS ===================== */

    /** Page de gestion des modules et sessions d'une formation. */
    @GetMapping("/formations/{id}/modules")
    public String manageModules(@PathVariable Long id, Model model) {
        Formation f = formationService.findById(id);
        if (f == null) return "redirect:/admin";
        model.addAttribute("formation", f);
        model.addAttribute("page", "admin");
        return "admin/modules";
    }

    /** Ajouter un module a la formation. */
    @PostMapping("/formations/{id}/modules/add")
    public String addModule(@PathVariable Long id, @RequestParam String titre, RedirectAttributes ra) {
        Formation f = formationService.findById(id);
        if (f != null && titre != null && !titre.isBlank()) {
            Module m = new Module();
            m.setTitre(titre.trim());
            m.setPosition(f.getModules().size());
            f.addModule(m);
            formationService.save(f);
            ra.addFlashAttribute("msg", "Module ajouté.");
        }
        return "redirect:/admin/formations/" + id + "/modules";
    }

    /** Renommer un module. */
    @PostMapping("/modules/{moduleId}/rename")
    public String renameModule(@PathVariable Long moduleId, @RequestParam String titre,
                               @RequestParam Long formationId, RedirectAttributes ra) {
        moduleRepo.findById(moduleId).ifPresent(m -> {
            if (titre != null && !titre.isBlank()) { m.setTitre(titre.trim()); moduleRepo.save(m); }
        });
        ra.addFlashAttribute("msg", "Module modifié.");
        return "redirect:/admin/formations/" + formationId + "/modules";
    }

    /** Supprimer un module. */
    @PostMapping("/modules/{moduleId}/delete")
    public String deleteModule(@PathVariable Long moduleId, @RequestParam Long formationId, RedirectAttributes ra) {
        moduleRepo.deleteById(moduleId);
        ra.addFlashAttribute("msg", "Module supprimé.");
        return "redirect:/admin/formations/" + formationId + "/modules";
    }

    /** Ajouter une session a un module. */
    @PostMapping("/modules/{moduleId}/sessions/add")
    public String addSession(@PathVariable Long moduleId, @RequestParam String intitule,
                             @RequestParam Long formationId, RedirectAttributes ra) {
        moduleRepo.findById(moduleId).ifPresent(m -> {
            if (intitule != null && !intitule.isBlank()) {
                Session s = new Session(intitule.trim());
                s.setPosition(m.getSessions().size());
                m.addSession(s);
                moduleRepo.save(m);
            }
        });
        ra.addFlashAttribute("msg", "Session ajoutée.");
        return "redirect:/admin/formations/" + formationId + "/modules";
    }

    /** Renommer une session. */
    @PostMapping("/sessions/{sessionId}/rename")
    public String renameSession(@PathVariable Long sessionId, @RequestParam String intitule,
                                @RequestParam Long formationId, RedirectAttributes ra) {
        sessionRepo.findById(sessionId).ifPresent(s -> {
            if (intitule != null && !intitule.isBlank()) { s.setIntitule(intitule.trim()); sessionRepo.save(s); }
        });
        ra.addFlashAttribute("msg", "Session modifiée.");
        return "redirect:/admin/formations/" + formationId + "/modules";
    }

    /** Supprimer une session. */
    @PostMapping("/sessions/{sessionId}/delete")
    public String deleteSession(@PathVariable Long sessionId, @RequestParam Long formationId, RedirectAttributes ra) {
        sessionRepo.deleteById(sessionId);
        ra.addFlashAttribute("msg", "Session supprimée.");
        return "redirect:/admin/formations/" + formationId + "/modules";
    }


    /* ===================== ASSOCIES (CRUD) ===================== */
    /*
     * Les associes alimentent la section "Les associes a vos cotes" de la page
     * A propos. Tant qu'aucun associe n'est enregistre ici, la section entiere
     * reste invisible sur le site public.
     */

    /** Liste des associes. */
    @GetMapping("/associes")
    public String associes(Model model) {
        model.addAttribute("associes", associeRepo.findAllByOrderByPositionAscIdAsc());
        model.addAttribute("page", "admin");
        return "admin/associes";
    }

    /** Formulaire de creation. */
    @GetMapping("/associes/new")
    public String newAssocie(Model model) {
        model.addAttribute("associe", new Associe());
        model.addAttribute("page", "admin");
        return "admin/associe-form";
    }

    /** Formulaire de modification. */
    @GetMapping("/associes/{id}/edit")
    public String editAssocie(@PathVariable Long id, Model model) {
        Associe a = associeRepo.findById(id).orElse(null);
        if (a == null) return "redirect:/admin/associes";
        model.addAttribute("associe", a);
        model.addAttribute("page", "admin");
        return "admin/associe-form";
    }

    /** Enregistrement (creation ou modification). */
    @PostMapping("/associes/save")
    public String saveAssocie(@Valid @ModelAttribute("associe") Associe associe,
                              BindingResult binding,
                              @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                              Model model,
                              RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("page", "admin");
            return "admin/associe-form";
        }

        // Modification : on repart de l'existant pour conserver la photo si aucune n'est envoyee
        if (associe.getId() != null) {
            Associe existing = associeRepo.findById(associe.getId()).orElse(null);
            if (existing != null) {
                existing.setNom(associe.getNom());
                existing.setRole(associe.getRole());
                existing.setBio(associe.getBio());
                existing.setPosition(associe.getPosition());
                String stored = fileStorage.store(photoFile);
                if (stored != null) existing.setPhoto(stored);
                associeRepo.save(existing);
                ra.addFlashAttribute("msg", "Associé modifié avec succès.");
                return "redirect:/admin/associes";
            }
        }

        // Creation
        String stored = fileStorage.store(photoFile);
        if (stored != null) associe.setPhoto(stored);
        associeRepo.save(associe);
        ra.addFlashAttribute("msg", "Associé ajouté. Il apparaît maintenant sur la page À propos.");
        return "redirect:/admin/associes";
    }

    /** Suppression d'un associe. */
    @PostMapping("/associes/{id}/delete")
    public String deleteAssocie(@PathVariable Long id, RedirectAttributes ra) {
        associeRepo.deleteById(id);
        ra.addFlashAttribute("msg", "Associé supprimé.");
        return "redirect:/admin/associes";
    }

    /* ===================== MOT DE PASSE ===================== */

    @GetMapping("/password")
    public String passwordForm(Model model) {
        model.addAttribute("page", "admin");
        return "admin/password";
    }

    @PostMapping("/password")
    public String changePassword(@RequestParam String ancien,
                                 @RequestParam String nouveau,
                                 @RequestParam String confirmation,
                                 Authentication auth,
                                 RedirectAttributes ra) {
        AdminUser user = adminRepo.findByEmailIgnoreCase(auth.getName()).orElse(null);
        if (user == null) { ra.addFlashAttribute("err", "Utilisateur introuvable."); return "redirect:/admin/password"; }
        if (!encoder.matches(ancien, user.getPassword())) {
            ra.addFlashAttribute("err", "L'ancien mot de passe est incorrect.");
        } else if (nouveau.length() < 6) {
            ra.addFlashAttribute("err", "Le nouveau mot de passe doit contenir au moins 6 caractères.");
        } else if (!nouveau.equals(confirmation)) {
            ra.addFlashAttribute("err", "Les deux mots de passe ne correspondent pas.");
        } else {
            user.setPassword(encoder.encode(nouveau));
            adminRepo.save(user);
            ra.addFlashAttribute("msg", "Mot de passe modifié avec succès.");
        }
        return "redirect:/admin/password";
    }
}
