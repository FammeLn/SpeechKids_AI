CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    email text NOT NULL UNIQUE,
    password_hash text NOT NULL,
    full_name text NOT NULL,
    role text NOT NULL,
    status text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE refresh_tokens (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token text NOT NULL UNIQUE,
    expires_at timestamptz NOT NULL,
    revoked_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE children (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    parent_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name text NOT NULL,
    age integer NOT NULL,
    birth_date date,
    gender text NOT NULL,
    native_language text NOT NULL,
    speech_goal text NOT NULL,
    model_profile_status text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE exercises (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    title text NOT NULL,
    type text NOT NULL,
    age_min integer NOT NULL,
    age_max integer NOT NULL,
    description text NOT NULL,
    is_active boolean NOT NULL DEFAULT true
);

CREATE TABLE exercise_items (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    exercise_id uuid NOT NULL REFERENCES exercises(id) ON DELETE CASCADE,
    image_url text NOT NULL,
    target_word text NOT NULL,
    target_phonemes text NOT NULL,
    difficulty text NOT NULL
);

CREATE TABLE sessions (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    child_id uuid NOT NULL REFERENCES children(id) ON DELETE CASCADE,
    exercise_id uuid NOT NULL REFERENCES exercises(id) ON DELETE CASCADE,
    status text NOT NULL,
    mode text NOT NULL,
    started_at timestamptz NOT NULL DEFAULT now(),
    finished_at timestamptz,
    total_score integer NOT NULL DEFAULT 0,
    xp_earned integer NOT NULL DEFAULT 0
);

CREATE TABLE attempts (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id uuid NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
    child_id uuid NOT NULL REFERENCES children(id) ON DELETE CASCADE,
    exercise_item_id uuid NOT NULL REFERENCES exercise_items(id) ON DELETE CASCADE,
    target_word text NOT NULL,
    recognized_text text NOT NULL,
    is_correct boolean NOT NULL,
    score integer NOT NULL,
    xp_earned integer NOT NULL,
    problem_phonemes text NOT NULL,
    recommendation text,
    audio_url text,
    processing_time_ms bigint,
    mode text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE phoneme_scores (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    attempt_id uuid NOT NULL REFERENCES attempts(id) ON DELETE CASCADE,
    phoneme text NOT NULL,
    accuracy integer NOT NULL
);

CREATE TABLE notifications (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title text NOT NULL,
    message text NOT NULL,
    type text NOT NULL,
    is_read boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE recommendations (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    child_id uuid NOT NULL REFERENCES children(id) ON DELETE CASCADE,
    text text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_children_parent ON children(parent_id);
CREATE INDEX idx_sessions_child ON sessions(child_id);
CREATE INDEX idx_attempts_child ON attempts(child_id);
CREATE INDEX idx_notifications_user ON notifications(user_id);
