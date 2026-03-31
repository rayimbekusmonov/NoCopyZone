-- Admin rolini qo'shish
INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT DO NOTHING;

-- Quiz savollari jadvali
CREATE TABLE IF NOT EXISTS quiz_questions (
                                              id            BIGSERIAL PRIMARY KEY,
                                              task_id       BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    option_a      VARCHAR(500) NOT NULL,
    option_b      VARCHAR(500) NOT NULL,
    option_c      VARCHAR(500) NOT NULL,
    option_d      VARCHAR(500) NOT NULL,
    correct_answer CHAR(1) NOT NULL CHECK (correct_answer IN ('A','B','C','D')),
    order_num     INT NOT NULL DEFAULT 0
    );

-- Quiz javoblari
CREATE TABLE IF NOT EXISTS quiz_answers (
                                            id           BIGSERIAL PRIMARY KEY,
                                            submission_id BIGINT NOT NULL REFERENCES submissions(id) ON DELETE CASCADE,
    question_id  BIGINT NOT NULL REFERENCES quiz_questions(id),
    selected     CHAR(1) CHECK (selected IN ('A','B','C','D')),
    is_correct   BOOLEAN DEFAULT FALSE,
    answered_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (submission_id, question_id)
    );

-- Tasks jadvaliga vaqt chegarasi qo'shish
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS duration_minutes INT DEFAULT NULL;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS task_file_url VARCHAR(500) DEFAULT NULL;

CREATE INDEX IF NOT EXISTS idx_quiz_questions_task ON quiz_questions(task_id);
CREATE INDEX IF NOT EXISTS idx_quiz_answers_submission ON quiz_answers(submission_id);