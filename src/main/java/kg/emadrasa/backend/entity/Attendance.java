package kg.emadrasa.backend.entity;

import jakarta.persistence.*;
import kg.emadrasa.backend.enums.AttendanceStatus;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    @Column(length = 255)
    private String comment;

    @Column(name = "marked_at", nullable = false)
    private LocalDateTime markedAt;

    @PrePersist
    protected void onCreate() {
        this.markedAt = LocalDateTime.now();
    }
}