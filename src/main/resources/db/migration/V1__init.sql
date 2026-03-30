-- ===== ROLES =====
CREATE TABLE IF NOT EXISTS roles (
                                     id      BIGSERIAL PRIMARY KEY,
                                     name    VARCHAR(50) NOT NULL UNIQUE
    );

INSERT INTO roles (name) VALUES ('ROLE_STUDENT'), ('ROLE_TEACHER'), ('ROLE_ADMIN')
    ON CONFLICT DO NOTHING;

-- ===== USERS =====
CREATE TABLE IF NOT EXISTS users (
                                     id         BIGSERIAL PRIMARY KEY,
                                     full_name  VARCHAR(255)        NOT NULL,
    email      VARCHAR(255)        NOT NULL UNIQUE,
    password   VARCHAR(255)        NOT NULL,
    role_id    BIGINT              NOT NULL REFERENCES roles(id),
    active     BOOLEAN             NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP           NOT NULL DEFAULT NOW()
    );

-- ===== COURSES =====
CREATE TABLE IF NOT EXISTS courses (
                                       id          BIGSERIAL PRIMARY KEY,
                                       name        VARCHAR(255)   NOT NULL,
    description TEXT,
    teacher_id  BIGINT         NOT NULL REFERENCES users(id),
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW()
    );

-- ===== COURSE ENROLLMENTS =====
CREATE TABLE IF NOT EXISTS enrollments (
                                           id         BIGSERIAL PRIMARY KEY,
                                           student_id BIGINT NOT NULL REFERENCES users(id),
    course_id  BIGINT NOT NULL REFERENCES courses(id),
    enrolled_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (student_id, course_id)
    );

-- ===== TASKS =====
CREATE TABLE IF NOT EXISTS tasks (
                                     id           BIGSERIAL PRIMARY KEY,
                                     title        VARCHAR(255)  NOT NULL,
    description  TEXT,
    type         VARCHAR(50)   NOT NULL, -- LAB, PRESENTATION, EXAM, CODE
    course_id    BIGINT        NOT NULL REFERENCES courses(id),
    teacher_id   BIGINT        NOT NULL REFERENCES users(id),
    deadline     TIMESTAMP,
    max_score    INT           NOT NULL DEFAULT 100,
    created_at   TIMESTAMP     NOT NULL DEFAULT NOW()
    );

-- ===== SUBMISSIONS =====
CREATE TABLE IF NOT EXISTS submissions (
                                           id             BIGSERIAL PRIMARY KEY,
                                           task_id        BIGINT        NOT NULL REFERENCES tasks(id),
    student_id     BIGINT        NOT NULL REFERENCES users(id),
    content        TEXT,
    file_url       VARCHAR(500),
    score          INT,
    integrity_score INT          NOT NULL DEFAULT 100,
    status         VARCHAR(50)   NOT NULL DEFAULT 'IN_PROGRESS', -- IN_PROGRESS, SUBMITTED, GRADED
    started_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    submitted_at   TIMESTAMP,
    UNIQUE (task_id, student_id)
    );

-- ===== PROCTORING LOGS =====
CREATE TABLE IF NOT EXISTS proctoring_logs (
                                               id             BIGSERIAL PRIMARY KEY,
                                               submission_id  BIGINT        NOT NULL REFERENCES submissions(id),
    student_id     BIGINT        NOT NULL REFERENCES users(id),
    event_type     VARCHAR(100)  NOT NULL, -- TAB_SWITCH, COPY_PASTE, FOCUS_LOST, SUSPICIOUS_TYPING
    severity       VARCHAR(20)   NOT NULL DEFAULT 'LOW', -- LOW, MEDIUM, HIGH
    details        TEXT,
    occurred_at    TIMESTAMP     NOT NULL DEFAULT NOW()
    );

-- ===== INDEXES =====
CREATE INDEX IF NOT EXISTS idx_proctoring_submission ON proctoring_logs(submission_id);
CREATE INDEX IF NOT EXISTS idx_proctoring_student ON proctoring_logs(student_id);
CREATE INDEX IF NOT EXISTS idx_submissions_task ON submissions(task_id);
CREATE INDEX IF NOT EXISTS idx_submissions_student ON submissions(student_id);
CREATE INDEX IF NOT EXISTS idx_tasks_course ON tasks(course_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_student ON enrollments(student_id);