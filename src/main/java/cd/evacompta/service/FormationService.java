package cd.evacompta.service;

import cd.evacompta.model.Formation;
import cd.evacompta.repository.FormationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Logique metier autour des formations. */
@Service
public class FormationService {

    private final FormationRepository repo;

    public FormationService(FormationRepository repo) {
        this.repo = repo;
    }

    /**
     * Liste toutes les formations. @Transactional garde la session ouverte
     * pendant qu'on parcourt les modules/sessions dans les pages (evite
     * l'erreur LazyInitializationException).
     */
    @Transactional(readOnly = true)
    public List<Formation> findAll() {
        List<Formation> list = repo.findAllByOrderByPositionAscIdAsc();
        // On force le chargement des modules et sessions tant que la session est ouverte
        list.forEach(f -> f.getModules().forEach(m -> m.getSessions().size()));
        return list;
    }

    @Transactional(readOnly = true)
    public Formation findById(Long id) {
        Formation f = repo.findById(id).orElse(null);
        if (f != null) {
            f.getModules().forEach(m -> m.getSessions().size());
        }
        return f;
    }

    @Transactional
    public Formation save(Formation f) {
        return repo.save(f);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    public long count() {
        return repo.count();
    }
}
