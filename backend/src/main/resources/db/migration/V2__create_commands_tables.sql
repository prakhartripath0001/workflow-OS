-- =============================================================================
-- V2 — Create slash commands and command history tables
-- Author: Prakhar Tripathi
-- Description: Command registry and execution history for slash command system
-- =============================================================================

-- Registry of available slash commands
CREATE TABLE IF NOT EXISTS commands (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL UNIQUE,    -- e.g. 'gmail', 'github', 'summarize'
    description     VARCHAR(500),
    category        VARCHAR(100),                    -- 'communication' | 'dev' | 'productivity'
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    requires_auth   BOOLEAN NOT NULL DEFAULT FALSE,
    icon_url        VARCHAR(1024),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Command execution history per user
CREATE TABLE IF NOT EXISTS command_executions (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth_users(id) ON DELETE CASCADE,
    command_name    VARCHAR(100) NOT NULL,
    raw_input       TEXT NOT NULL,                   -- Full user input: '/gmail compose to: ...'
    parsed_intent   JSONB,                           -- AI-parsed intent structure
    status          VARCHAR(50) NOT NULL DEFAULT 'PENDING',  -- PENDING | SUCCESS | FAILED
    result          JSONB,                           -- Execution result payload
    error_message   TEXT,
    execution_ms    INTEGER,                         -- Execution time in milliseconds
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for fast user history queries
CREATE INDEX IF NOT EXISTS idx_command_executions_user_id    ON command_executions (user_id);
CREATE INDEX IF NOT EXISTS idx_command_executions_created_at ON command_executions (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_command_executions_status     ON command_executions (status);

-- Seed default commands
INSERT INTO commands (name, description, category, requires_auth, is_active) VALUES
    ('gmail',     'Manage Gmail — compose, read, search emails',   'communication', TRUE,  TRUE),
    ('github',    'Interact with GitHub — PRs, issues, repos',     'dev',           TRUE,  TRUE),
    ('summarize', 'AI-powered text summarization',                  'productivity',  FALSE, TRUE),
    ('remind',    'Set smart reminders and follow-ups',             'productivity',  FALSE, TRUE),
    ('search',    'Search across all connected integrations',       'productivity',  FALSE, TRUE)
ON CONFLICT (name) DO NOTHING;
