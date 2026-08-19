package cd.evacompta.repository;

import cd.evacompta.model.Associe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Acces base pour les associes, tries par position. */
public interface AssocieRepository extends JpaRepository<Associe, Long> {
    List<Associe> findAllByOrderByPositionAscIdAsc();
}
