package kg.emadrasa.backend.repository;

import kg.emadrasa.backend.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findAllByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<Grade> findAllByLessonId(Long lessonId);
}