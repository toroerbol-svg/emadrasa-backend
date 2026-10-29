package kg.emadrasa.backend.repository;

import kg.emadrasa.backend.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findAllByGroupIdAndLessonDateOrderByStartTimeAsc(Long groupId, LocalDate lessonDate);
}