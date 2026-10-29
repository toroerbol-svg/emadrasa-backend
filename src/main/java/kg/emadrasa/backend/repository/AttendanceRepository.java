package kg.emadrasa.backend.repository;

import kg.emadrasa.backend.entity.Attendance;
import kg.emadrasa.backend.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findAllByLessonId(Long lessonId);
    List<Attendance> findAllByStudentIdOrderByMarkedAtDesc(Long studentId);
    Optional<Attendance> findByLessonIdAndStudentId(Long lessonId, Long studentId);
    long countByStudentIdAndStatus(Long studentId, AttendanceStatus status);
}