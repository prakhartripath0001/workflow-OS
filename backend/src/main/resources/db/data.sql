-- =============================================================================
--  WORKFLOW OS — SEED DATA
--  File   : data.sql
--  Purpose: Insert initial demo users, workflows, and tasks
--  Note   : Uses fixed UUIDs so re-runs are idempotent (ON CONFLICT DO NOTHING)
--
--  NOTE: Statements are delimited by ^^ (not ;) — matches spring.sql.init.separator
-- =============================================================================

-- ─── Users ────────────────────────────────────────────────────────────────────
-- password_hash is BCrypt of "password123" (cost factor 10)
INSERT INTO users (id, name, email, password_hash, avatar_url) VALUES
(
    'a1b2c3d4-0001-0001-0001-000000000001',
    'Alice Johnson',
    'alice@workflowos.dev',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'https://api.dicebear.com/8.x/avataaars/svg?seed=alice'
),
(
    'a1b2c3d4-0002-0002-0002-000000000002',
    'Bob Martinez',
    'bob@workflowos.dev',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'https://api.dicebear.com/8.x/avataaars/svg?seed=bob'
)
ON CONFLICT (id) DO NOTHING
^^

-- ─── Workflows ────────────────────────────────────────────────────────────────
INSERT INTO workflows (id, name, description, status, owner_id) VALUES
(
    'b1000000-0001-0001-0001-000000000001',
    'Onboarding Pipeline',
    'End-to-end employee onboarding automation — from offer letter to first check-in.',
    'ACTIVE',
    'a1b2c3d4-0001-0001-0001-000000000001'
),
(
    'b1000000-0002-0002-0002-000000000002',
    'Release Checklist v2.0',
    'Pre-release quality gate tasks for the 2.0 product milestone.',
    'ACTIVE',
    'a1b2c3d4-0001-0001-0001-000000000001'
),
(
    'b1000000-0003-0003-0003-000000000003',
    'Infrastructure Audit',
    'Quarterly review of cloud resources, costs, and security posture.',
    'DRAFT',
    'a1b2c3d4-0002-0002-0002-000000000002'
)
ON CONFLICT (id) DO NOTHING
^^

-- ─── Workflow Tasks ────────────────────────────────────────────────────────────
INSERT INTO workflow_tasks (id, workflow_id, title, description, status, position) VALUES

-- Onboarding Pipeline tasks
(
    'c1000000-0001-0001-0001-000000000001',
    'b1000000-0001-0001-0001-000000000001',
    'Send offer letter',
    'Draft and send the signed offer letter via DocuSign.',
    'DONE', 0
),
(
    'c1000000-0002-0002-0002-000000000002',
    'b1000000-0001-0001-0001-000000000001',
    'Provision laptop & accounts',
    'Set up MacBook, GitHub access, Slack, and GSuite account.',
    'IN_PROGRESS', 1
),
(
    'c1000000-0003-0003-0003-000000000003',
    'b1000000-0001-0001-0001-000000000001',
    'Schedule onboarding sessions',
    'Book intro calls with team leads and stakeholders for Day 1.',
    'TODO', 2
),

-- Release Checklist tasks
(
    'c1000000-0004-0004-0004-000000000004',
    'b1000000-0002-0002-0002-000000000002',
    'Run full regression suite',
    'Execute the automated test suite in CI and resolve any failures.',
    'DONE', 0
),
(
    'c1000000-0005-0005-0005-000000000005',
    'b1000000-0002-0002-0002-000000000002',
    'Update CHANGELOG & version bump',
    'Update CHANGELOG.md, bump pom.xml and package.json versions.',
    'IN_PROGRESS', 1
),
(
    'c1000000-0006-0006-0006-000000000006',
    'b1000000-0002-0002-0002-000000000002',
    'Publish Docker image',
    'Build and push the tagged Docker image to the container registry.',
    'TODO', 2
),

-- Infrastructure Audit tasks
(
    'c1000000-0007-0007-0007-000000000007',
    'b1000000-0003-0003-0003-000000000003',
    'Review AWS cost explorer',
    'Identify over-provisioned resources and right-sizing opportunities.',
    'TODO', 0
),
(
    'c1000000-0008-0008-0008-000000000008',
    'b1000000-0003-0003-0003-000000000003',
    'Rotate IAM credentials',
    'Rotate all long-lived IAM access keys and update Secrets Manager.',
    'TODO', 1
)
ON CONFLICT (id) DO NOTHING
^^
