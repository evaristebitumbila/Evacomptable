package cd.evacompta.repository;

import cd.evacompta.model.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Acces base pour les formations, triees par position. */
public interface FormationRepository extends JpaRepository<Formation, Long> {
    List<Formation> findAllByOrderByPositionAscIdAsc();
}
