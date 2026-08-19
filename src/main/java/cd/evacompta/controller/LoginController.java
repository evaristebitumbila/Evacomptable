package cd.evacompta.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Affiche la page de connexion personnalisee. */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Adresse e-mail ou mot de passe incorrect.");
        }
        model.addAttribute("page", "login");
        return "login";
    }
}
