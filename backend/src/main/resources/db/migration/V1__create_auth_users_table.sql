-- =============================================================================
-- V1 — Create core users table
-- Author: Prakhar Tripathi
-- Description: Initial schema - auth users with OAuth provider support
-- =============================================================================

CREATE TABLE IF NOT EXISTS auth_users (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    display_name    VARCHAR(255),
    avatar_url      VARCHAR(1024),
    password_hash   VARCHAR(255),                -- NULL for pure OAuth users
    provider        VARCHAR(50) NOT NULL DEFAULT 'local',  -- local | google | github
    provider_id     VARCHAR(255),                -- OAuth provider's user ID
    role            VARCHAR(50) NOT NULL DEFAULT 'USER',   -- USER | ADMIN
    email_verified  BOOLEAN NOT NULL DEFAULT FALSE,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Index for fast email + provider lookups
CREATE INDEX IF NOT EXISTS idx_auth_users_email    ON auth_users (email);
CREATE INDEX IF NOT EXISTS idx_auth_users_provider ON auth_users (provider, provider_id);

-- Auto-update updated_at on every row modification
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_auth_users_updated_at
    BEFORE UPDATE ON auth_users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
