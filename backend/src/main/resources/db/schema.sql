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
-- auth_provider: how the user signed up (local email/pw, google, or github)
DO $$ BEGIN
    CREATE TYPE auth_provider AS ENUM ('local', 'google', 'github');
EXCEPTION WHEN duplicate_object THEN NULL; END $$
^^

CREATE TABLE IF NOT EXISTS users (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(120)  NOT NULL,
    email           VARCHAR(255)  NOT NULL,
    password_hash   VARCHAR(255),                       -- NULL for OAuth-only accounts
    provider        auth_provider NOT NULL DEFAULT 'local',
    provider_id     VARCHAR(255),                       -- OAuth provider's user ID
    avatar_url      TEXT,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_users_email            UNIQUE (email),
    CONSTRAINT uq_users_provider         UNIQUE (provider, provider_id)
)
^^

-- Upgrade existing local databases created by older schema versions.
ALTER TABLE IF EXISTS users
    ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255),
    ADD COLUMN IF NOT EXISTS provider auth_provider NOT NULL DEFAULT 'local',
    ADD COLUMN IF NOT EXISTS provider_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS avatar_url TEXT
^^

ALTER TABLE IF EXISTS users
    ALTER COLUMN provider SET DEFAULT 'local',
    ALTER COLUMN provider SET NOT NULL
^^

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_users_provider'
          AND conrelid = 'users'::regclass
    ) THEN
        ALTER TABLE users
            ADD CONSTRAINT uq_users_provider UNIQUE (provider, provider_id);
    END IF;
END $$
^^

COMMENT ON TABLE  users                  IS 'Application users'
^^
COMMENT ON COLUMN users.email            IS 'Unique email address used for login'
^^
COMMENT ON COLUMN users.password_hash    IS 'BCrypt-hashed password — NULL for OAuth-only accounts'
^^
COMMENT ON COLUMN users.provider         IS 'How the user authenticates: local email/pw, google, or github'
^^
COMMENT ON COLUMN users.provider_id      IS 'User ID returned by the OAuth provider'
^^
COMMENT ON COLUMN users.avatar_url       IS 'Profile picture URL (from OAuth or user-set)'
^^

-- ─── Indexes ────────────────────────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_users_email       ON users (email)
^^
CREATE INDEX IF NOT EXISTS idx_users_provider_id ON users (provider, provider_id)
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

-- =============================================================================
--  TABLE: commands
-- =============================================================================
CREATE TABLE IF NOT EXISTS commands (
    id              BIGSERIAL    PRIMARY KEY,
    name            VARCHAR(100) NOT NULL UNIQUE,
    description     VARCHAR(500),
    category        VARCHAR(100),
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    requires_auth   BOOLEAN      NOT NULL DEFAULT FALSE,
    icon_url        VARCHAR(1024),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
)
^^

-- =============================================================================
--  TABLE: command_executions
-- =============================================================================
CREATE TABLE IF NOT EXISTS command_executions (
    id              BIGSERIAL    PRIMARY KEY,
    user_id         UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    command_name    VARCHAR(100) NOT NULL,
    raw_input       TEXT         NOT NULL,
    parsed_intent   JSONB,
    status          VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    result          JSONB,
    error_message   TEXT,
    execution_ms    INTEGER,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
)
^^

-- ─── Indexes ────────────────────────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_command_executions_user_id    ON command_executions (user_id)
^^
CREATE INDEX IF NOT EXISTS idx_command_executions_created_at ON command_executions (created_at DESC)
^^
CREATE INDEX IF NOT EXISTS idx_command_executions_status     ON command_executions (status)
^^

-- =============================================================================
--  TABLE: extensions / installed_extensions
-- =============================================================================
CREATE TABLE IF NOT EXISTS extensions (
    id             VARCHAR(120) PRIMARY KEY,
    name           VARCHAR(180) NOT NULL,
    version        VARCHAR(40)  NOT NULL,
    description    VARCHAR(500),
    publisher_id   VARCHAR(120),
    entrypoint     VARCHAR(300) NOT NULL,
    permissions    JSONB,
    manifest       JSONB,
    signature      VARCHAR(512),
    is_verified    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
)
^^

CREATE TABLE IF NOT EXISTS installed_extensions (
    id            BIGSERIAL PRIMARY KEY,
    user_id       UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    extension_id  VARCHAR(120) NOT NULL REFERENCES extensions(id) ON DELETE CASCADE,
    version       VARCHAR(40)  NOT NULL,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    installed_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_installed_extension_user UNIQUE (user_id, extension_id)
)
^^

CREATE INDEX IF NOT EXISTS idx_installed_extensions_user_id ON installed_extensions (user_id)
^^

-- =============================================================================
--  TABLE: oauth_tokens
-- =============================================================================
CREATE TABLE IF NOT EXISTS oauth_tokens (
    id                       BIGSERIAL PRIMARY KEY,
    user_id                  UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider                 VARCHAR(80) NOT NULL,
    access_token_ciphertext  TEXT        NOT NULL,
    refresh_token_ciphertext TEXT,
    scope                    VARCHAR(1000),
    expires_at               TIMESTAMPTZ,
    updated_at               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_oauth_user_provider UNIQUE (user_id, provider)
)
^^

CREATE INDEX IF NOT EXISTS idx_oauth_tokens_user_provider ON oauth_tokens (user_id, provider)
^^

-- =============================================================================
--  TABLE: workspace_folders / file_index_records
-- =============================================================================
CREATE TABLE IF NOT EXISTS workspace_folders (
    id          BIGSERIAL PRIMARY KEY,
    user_id     UUID          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    path        VARCHAR(1024) NOT NULL,
    scope       VARCHAR(80)   NOT NULL DEFAULT 'read',
    granted_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW()
)
^^

CREATE TABLE IF NOT EXISTS file_index_records (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             UUID          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    workspace_folder_id BIGINT        NOT NULL REFERENCES workspace_folders(id) ON DELETE CASCADE,
    path                VARCHAR(1024) NOT NULL,
    relative_path       VARCHAR(1024) NOT NULL,
    extension           VARCHAR(40),
    content_hash        VARCHAR(128),
    size_bytes          BIGINT,
    modified_at         TIMESTAMPTZ,
    indexed_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW()
)
^^

CREATE INDEX IF NOT EXISTS idx_workspace_folders_user_id ON workspace_folders (user_id)
^^
CREATE INDEX IF NOT EXISTS idx_file_index_records_folder ON file_index_records (workspace_folder_id)
^^
CREATE INDEX IF NOT EXISTS idx_file_index_records_user_path ON file_index_records (user_id, path)
^^
