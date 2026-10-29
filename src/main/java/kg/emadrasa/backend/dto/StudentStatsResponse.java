package kg.emadrasa.backend.dto;

public record StudentStatsResponse(
        Long studentId,
        String fullName,
        String groupName,
        long totalLessonsMarked,
        long presentCount,
        long lateCount,
        long excusedCount,
        long absentCount,
        double attendancePercentage,
        double averageGrade,
        long hifzSabakCount,
        boolean warningAlert,
        String statusMessage
) {}