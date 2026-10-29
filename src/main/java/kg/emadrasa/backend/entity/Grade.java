package kg.emadrasa.backend.entity;

import jakarta.persistence.*;
import kg.emadrasa.backend.enums.GradeType;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_type", nullable = false, length = 30)
    private GradeType gradeType;

    @Column(name = "surah_and_ayahs", length = 100)
    private String surahAndAyahs;

    @Column(length = 255)
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}