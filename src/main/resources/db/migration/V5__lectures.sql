-- Ma'ruza materiallari
CREATE TABLE IF NOT EXISTS lectures (
                                        id          BIGSERIAL PRIMARY KEY,
                                        course_id   BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    content     TEXT,
    file_url    VARCHAR(500),
    video_url   VARCHAR(500),
    order_num   INT NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
    );

-- Foydalanuvchi bloklash uchun
ALTER TABLE users ADD COLUMN IF NOT EXISTS blocked BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS blocked_reason VARCHAR(500);

CREATE INDEX IF NOT EXISTS idx_lectures_course ON lectures(course_id);