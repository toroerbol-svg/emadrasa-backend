package kg.emadrasa.backend.repository;

import kg.emadrasa.backend.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    boolean existsByName(String name);
}