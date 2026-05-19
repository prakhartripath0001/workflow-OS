-- =============================================================================
-- V3 — Create OAuth integration tokens table
-- Author: Prakhar Tripathi
-- Description: Store OAuth access/refresh tokens for third-party integrations
-- =============================================================================

CREATE TABLE IF NOT EXISTS integration_tokens (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth_users(id) ON DELETE CASCADE,
    provider        VARCHAR(50) NOT NULL,            -- 'google' | 'github' | 'slack'
    access_token    TEXT NOT NULL,                   -- Encrypted in production
    refresh_token   TEXT,                            -- Encrypted in production
    token_type      VARCHAR(50) DEFAULT 'Bearer',
    scopes          TEXT[],                          -- e.g. ARRAY['gmail.readonly', 'gmail.send']
    expires_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (user_id, provider)
);

CREATE INDEX IF NOT EXISTS idx_integration_tokens_user_provider ON integration_tokens (user_id, provider);

CREATE TRIGGER trg_integration_tokens_updated_at
    BEFORE UPDATE ON integration_tokens
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- User preferences table
CREATE TABLE IF NOT EXISTS user_preferences (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth_users(id) ON DELETE CASCADE UNIQUE,
    theme           VARCHAR(50) NOT NULL DEFAULT 'dark',
    command_history_limit INTEGER NOT NULL DEFAULT 100,
    preferred_ai_model    VARCHAR(100) DEFAULT 'gpt-4o-mini',
    settings        JSONB NOT NULL DEFAULT '{}',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER trg_user_preferences_updated_at
    BEFORE UPDATE ON user_preferences
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
