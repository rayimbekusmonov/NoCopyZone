-- CHAR(1) ni VARCHAR(1) ga o'zgartirish
ALTER TABLE quiz_answers ALTER COLUMN selected TYPE VARCHAR(1);
ALTER TABLE quiz_questions ALTER COLUMN correct_answer TYPE VARCHAR(1);