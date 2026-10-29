package kg.emadrasa.backend.service;

import kg.emadrasa.backend.dto.*;
import kg.emadrasa.backend.entity.*;
import kg.emadrasa.backend.enums.Role;
import kg.emadrasa.backend.exception.ResourceNotFoundException;
import kg.emadrasa.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MadrasaManagementService {

    private final StudyGroupRepository groupRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    // --- ГРУППЫ ---
    @Transactional(readOnly = true)
    public List<StudyGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    @Transactional
    public StudyGroup createGroup(GroupRequest request) {
        if (groupRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Группа с таким названием уже существует: " + request.name());
        }
        StudyGroup group = StudyGroup.builder()
                .name(request.name())
                .courseYear(request.courseYear())
                .description(request.description())
                .build();
        return groupRepository.save(group);
    }

    // --- ПРЕДМЕТЫ ---
    @Transactional(readOnly = true)
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    // --- ПОЛЬЗОВАТЕЛИ (УСТАЗЫ И СТУДЕНТЫ) ---
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с логином " + request.username() + " уже существует");
        }

        StudyGroup group = null;
        if (request.groupId() != null) {
            group = groupRepository.findById(request.groupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Группа не найдена с ID: " + request.groupId()));
        }

        User user = User.builder()
                .fullName(request.fullName())
                .username(request.username())
                .passwordHash(request.password()) // На 4-м этапе подключим шифрование BCrypt
                .role(request.role())
                .phone(request.phone())
                .parentPhone(request.parentPhone())
                .group(group)
                .active(true)
                .build();

        User saved = userRepository.save(user);
        return mapToUserResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getStudentsByGroup(Long groupId) {
        return userRepository.findAllByGroupIdAndRoleOrderByFullNameAsc(groupId, Role.STUDENT)
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findAllByRoleOrderByFullNameAsc(role)
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    // --- РАСПИСАНИЕ (УРОКИ) ---
    @Transactional
    public LessonResponse createLesson(LessonRequest request) {
        StudyGroup group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new ResourceNotFoundException("Группа не найдена с ID: " + request.groupId()));

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + request.subjectId()));

        User ustaz = userRepository.findById(request.ustazId())
                .orElseThrow(() -> new ResourceNotFoundException("Устаз не найден с ID: " + request.ustazId()));

        if (ustaz.getRole() != Role.USTAZ && ustaz.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Пользователь " + ustaz.getFullName() + " не является устазом!");
        }

        Lesson lesson = Lesson.builder()
                .group(group)
                .subject(subject)
                .ustaz(ustaz)
                .lessonDate(request.lessonDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .topic(request.topic())
                .build();

        return mapToLessonResponse(lessonRepository.save(lesson));
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getGroupSchedule(Long groupId, LocalDate date) {
        return lessonRepository.findAllByGroupIdAndLessonDateOrderByStartTimeAsc(groupId, date)
                .stream()
                .map(this::mapToLessonResponse)
                .toList();
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getRole(),
                user.getPhone(),
                user.getParentPhone(),
                user.getGroup() != null ? user.getGroup().getId() : null,
                user.getGroup() != null ? user.getGroup().getName() : null,
                user.getActive()
        );
    }

    private LessonResponse mapToLessonResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getGroup().getId(),
                lesson.getGroup().getName(),
                lesson.getSubject().getId(),
                lesson.getSubject().getName(),
                lesson.getUstaz().getId(),
                lesson.getUstaz().getFullName(),
                lesson.getLessonDate(),
                lesson.getStartTime(),
                lesson.getEndTime(),
                lesson.getTopic()
        );
    }
}