package cd.evacompta.config;

import cd.evacompta.service.AdminUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Securite du site :
 *  - toutes les pages publiques (accueil, services, etc.) sont accessibles a tous
 *  - les pages /admin/** exigent une connexion (ROLE_ADMIN)
 *  - le bouton "Se connecter" de la navbar mene a /login
 *  - la deconnexion se fait via /logout
 */
@Configuration
public class SecurityConfig {

    private final AdminUserDetailsService userDetailsService;

    public SecurityConfig(AdminUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .userDetailsService(userDetailsService)
            .authorizeHttpRequests(auth -> auth
                // ressources statiques (a la racine) + pages publiques + images uploadees
                .requestMatchers(
                        "/", "/formations/**", "/services", "/cabinet", "/faq", "/contact",
                        "/*.css", "/*.js", "/*.png", "/*.jpg", "/*.jpeg", "/*.ico", "/*.svg",
                        "/uploads/**", "/webjars/**", "/login", "/h2-console/**"
                ).permitAll()
                // tout le back office est protege
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")                 // notre page de connexion personnalisee
                .loginProcessingUrl("/login")        // l'URL qui recoit le formulaire
                .defaultSuccessUrl("/admin", true)   // apres connexion -> back office
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?deconnecte")    // apres deconnexion -> accueil
                .permitAll()
            );

        // Necessaire pour que la console H2 s'affiche (elle utilise des frames)
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
