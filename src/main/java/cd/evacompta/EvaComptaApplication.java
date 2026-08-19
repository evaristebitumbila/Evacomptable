package cd.evacompta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entree de l'application EVA Compta Consulting.
 * Lancez cette classe (ou `mvn spring-boot:run`) puis ouvrez http://localhost:8080
 */
@SpringBootApplication
public class EvaComptaApplication {
    public static void main(String[] args) {
        SpringApplication.run(EvaComptaApplication.class, args);
    }
}
