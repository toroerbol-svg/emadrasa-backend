-- 1. Таблица учебных групп (курсов)
CREATE TABLE study_groups (
                              id BIGSERIAL PRIMARY KEY,
                              name VARCHAR(100) NOT NULL UNIQUE,
                              course_year INT NOT NULL,
                              description VARCHAR(255),
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Таблица пользователей (Админы, Устазы, Студенты)
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       full_name VARCHAR(150) NOT NULL,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL, -- ADMIN, USTAZ, STUDENT
                       phone VARCHAR(30),
                       parent_phone VARCHAR(30),
                       telegram_chat_id BIGINT,
                       group_id BIGINT REFERENCES study_groups(id) ON DELETE SET NULL,
                       active BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Таблица предметов (Таджвид, Хифз, Фикх, Арабский язык и т.д.)
CREATE TABLE subjects (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL UNIQUE,
                          description VARCHAR(255)
);

-- 4. Таблица расписания (конкретные уроки)
CREATE TABLE lessons (
                         id BIGSERIAL PRIMARY KEY,
                         group_id BIGINT NOT NULL REFERENCES study_groups(id) ON DELETE CASCADE,
                         subject_id BIGINT NOT NULL REFERENCES subjects(id) ON DELETE RESTRICT,
                         ustaz_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
                         lesson_date DATE NOT NULL,
                         start_time TIME NOT NULL,
                         end_time TIME NOT NULL,
                         topic VARCHAR(255),
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. Таблица посещаемости
CREATE TABLE attendances (
                             id BIGSERIAL PRIMARY KEY,
                             lesson_id BIGINT NOT NULL REFERENCES lessons(id) ON DELETE CASCADE,
                             student_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                             status VARCHAR(20) NOT NULL, -- PRESENT, ABSENT, LATE, EXCUSED
                             comment VARCHAR(255),
                             marked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT uq_attendance_lesson_student UNIQUE (lesson_id, student_id)
);

-- 6. Таблица оценок (включая сдачу Хифза)
CREATE TABLE grades (
                        id BIGSERIAL PRIMARY KEY,
                        lesson_id BIGINT NOT NULL REFERENCES lessons(id) ON DELETE CASCADE,
                        student_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        score INT NOT NULL CHECK (score BETWEEN 1 AND 5),
                        grade_type VARCHAR(30) NOT NULL, -- CLASSWORK, HOMEWORK, HIFZ_SABAK, HIFZ_MANZIL, EXAM
                        surah_and_ayahs VARCHAR(100),    -- Например: "Сура Аль-Мульк, аяты 1-15"
                        comment VARCHAR(255),
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Начальные тестовые данные (чтобы база сразу не была пустой)
INSERT INTO study_groups (name, course_year, description)
VALUES ('1 курс — Группа А', 1, 'Начальная группа (Арабский язык и Таджвид)');

INSERT INTO subjects (name, description) VALUES
                                             ('Таджвид', 'Правила чтения Корана'),
                                             ('Хифз (Заучивание Корана)', 'Заучивание новых страниц (Сабак) и повторение (Манзил)'),
                                             ('Арабский язык (Сарф и Нахв)', 'Грамматика и морфология арабского языка'),
                                             ('Фикх', 'Исламское право');