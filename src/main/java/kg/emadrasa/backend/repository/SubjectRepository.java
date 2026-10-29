package kg.emadrasa.backend.repository;

import kg.emadrasa.backend.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}