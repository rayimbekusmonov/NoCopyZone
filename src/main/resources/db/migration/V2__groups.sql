-- ===== GROUPS =====
CREATE TABLE IF NOT EXISTS groups (
                                      id         BIGSERIAL PRIMARY KEY,
                                      name       VARCHAR(100) NOT NULL UNIQUE,  -- AXB-4, CSB-2
    faculty    VARCHAR(255),
    year       INT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
    );

-- ===== STUDENT-GROUP =====
CREATE TABLE IF NOT EXISTS student_groups (
                                              id         BIGSERIAL PRIMARY KEY,
                                              student_id BIGINT NOT NULL REFERENCES users(id),
    group_id   BIGINT NOT NULL REFERENCES groups(id),
    UNIQUE (student_id, group_id)
    );

-- ===== COURSE-GROUP =====
CREATE TABLE IF NOT EXISTS course_groups (
                                             id         BIGSERIAL PRIMARY KEY,
                                             course_id  BIGINT NOT NULL REFERENCES courses(id),
    group_id   BIGINT NOT NULL REFERENCES groups(id),
    UNIQUE (course_id, group_id)
    );

-- ===== INDEXES =====
CREATE INDEX IF NOT EXISTS idx_student_groups_student ON student_groups(student_id);
CREATE INDEX IF NOT EXISTS idx_student_groups_group ON student_groups(group_id);
CREATE INDEX IF NOT EXISTS idx_course_groups_course ON course_groups(course_id);
CREATE INDEX IF NOT EXISTS idx_course_groups_group ON course_groups(group_id);