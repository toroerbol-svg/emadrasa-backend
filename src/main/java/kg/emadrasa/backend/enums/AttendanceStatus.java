package kg.emadrasa.backend.enums;

public enum AttendanceStatus {
    PRESENT, // Присутствовал
    ABSENT,  // Отсутствовал без причины (пропуск)
    LATE,    // Опоздал
    EXCUSED  // Отсутствовал по уважительной причине (болел / отпросился)
}