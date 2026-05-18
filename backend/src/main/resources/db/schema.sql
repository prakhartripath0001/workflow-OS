-- =============================================================================
--  WORKFLOW OS — DATABASE SCHEMA
--  File   : schema.sql
--  Purpose: Create all tables, types, and indexes for Workflow OS
--
--  NOTE: Statements are delimited by ^^ (not ;) because Spring Boot's
--        ScriptUtils splits on ; which breaks dollar-quoted PL/pgSQL blocks.
-- =============================================================================

-- ─── Extensions ───────────────────────────────────────────────────────────────
CREATE EXTENSION IF NOT EXISTS "pgcrypto"
^^

-- ─── ENUM Types ───────────────────────────────────────────────────────────────
DO $$ BEGIN
    CREATE TYPE workflow_status AS ENUM ('DRAFT', 'ACTIVE', 'PAUSED', 'COMPLETED', 'ARCHIVED');
EXCEPTION WHEN duplicate_object THEN NULL; END $$
^^

DO $$ BEGIN
    CREATE TYPE task_status AS ENUM ('TODO', 'IN_PROGRESS', 'BLOCKED', 'DONE', 'CANCELLED');
EXCEPTION WHEN duplicate_object THEN NULL; END $$
^^

-- =============================================================================
--  TABLE: users
-- =============================================================================
CREATE TABLE IF NOT EXISTS users (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(120) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL DEFAULT '',
    avatar_url      TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_users_email UNIQUE (email)
)
^^

COMMENT ON TABLE  users                  IS 'Application users'
^^
COMMENT ON COLUMN users.email            IS 'Unique email address used for login'
^^
COMMENT ON COLUMN users.password_hash    IS 'BCrypt-hashed password — never store plaintext'
^^
COMMENT ON COLUMN users.avatar_url       IS 'Optional URL to user profile picture'
^^

-- ─── Index ─────────────────────────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email)
^^

-- =============================================================================
--  TABLE: user_sessions
--  Tracks active login sessions; one row per session (supports multi-device)
-- =============================================================================
CREATE TABLE IF NOT EXISTS user_sessions (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token        VARCHAR(512) NOT NULL,
    expires_at   TIMESTAMPTZ  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_session_token UNIQUE (token)
)
^^

COMMENT ON TABLE  user_sessions            IS 'Active login sessions — one row per logged-in device/browser'
^^
COMMENT ON COLUMN user_sessions.token      IS 'Opaque session token returned to the client as Bearer token'
^^
COMMENT ON COLUMN user_sessions.expires_at IS 'Session expiry timestamp (default 24 h from login)'
^^

CREATE INDEX IF NOT EXISTS idx_sessions_user_id    ON user_sessions (user_id)
^^
CREATE INDEX IF NOT EXISTS idx_sessions_token      ON user_sessions (token)
^^
CREATE INDEX IF NOT EXISTS idx_sessions_expires_at ON user_sessions (expires_at)
^^

-- =============================================================================
--  TABLE: workflows
-- =============================================================================
CREATE TABLE IF NOT EXISTS workflows (
    id          UUID              PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(200)      NOT NULL,
    description TEXT,
    status      workflow_status   NOT NULL DEFAULT 'DRAFT',
    owner_id    UUID              NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ       NOT NULL DEFAULT NOW()
)
^^

COMMENT ON TABLE  workflows          IS 'Top-level workflow definitions owned by a user'
^^
COMMENT ON COLUMN workflows.status   IS 'Lifecycle state: DRAFT→ACTIVE→PAUSED/COMPLETED/ARCHIVED'
^^
COMMENT ON COLUMN workflows.owner_id IS 'FK to users — the user who created this workflow'
^^

-- ─── Indexes ────────────────────────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_workflows_owner_id ON workflows (owner_id)
^^
CREATE INDEX IF NOT EXISTS idx_workflows_status   ON workflows (status)
^^

-- =============================================================================
--  TABLE: workflow_tasks
-- =============================================================================
CREATE TABLE IF NOT EXISTS workflow_tasks (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_id  UUID         NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    title        VARCHAR(300) NOT NULL,
    description  TEXT,
    status       task_status  NOT NULL DEFAULT 'TODO',
    position     INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
)
^^

COMMENT ON TABLE  workflow_tasks          IS 'Individual tasks that belong to a workflow'
^^
COMMENT ON COLUMN workflow_tasks.position IS 'Zero-based ordering index within the parent workflow'
^^

-- ─── Indexes ────────────────────────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_wf_tasks_workflow_id ON workflow_tasks (workflow_id)
^^
CREATE INDEX IF NOT EXISTS idx_wf_tasks_status      ON workflow_tasks (status)
^^

-- =============================================================================
--  FUNCTION + TRIGGER: auto-update updated_at on any row change
-- =============================================================================
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql
^^

-- Apply trigger to all tables with updated_at
DO $$ BEGIN
    CREATE TRIGGER trg_users_updated_at
        BEFORE UPDATE ON users
        FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$
^^

DO $$ BEGIN
    CREATE TRIGGER trg_workflows_updated_at
        BEFORE UPDATE ON workflows
        FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$
^^

DO $$ BEGIN
    CREATE TRIGGER trg_workflow_tasks_updated_at
        BEFORE UPDATE ON workflow_tasks
        FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$
^^
