-- Smart Productivity Assistant - Database Initialization Script
-- PostgreSQL

-- Create database (run as superuser)
-- CREATE DATABASE productivity_db;

-- Connect to database
\c productivity_db;

-- Enable UUID extension if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Sample data for testing

-- Insert test user
INSERT INTO users (email, name, timezone, created_at, updated_at)
VALUES ('demo@productivity.com', 'Demo User', 'America/New_York', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Get user ID for subsequent inserts
DO $$
DECLARE
    demo_user_id bigint;
BEGIN
    SELECT id INTO demo_user_id FROM users WHERE email = 'demo@productivity.com';

    -- Insert sample tasks
    INSERT INTO tasks (user_id, title, description, priority, status, due_date, estimated_duration, recommendation_score, created_at, updated_at)
    VALUES
        (demo_user_id, 'Review project proposal', 'Go through the Q1 project proposal and provide feedback', 'HIGH', 'TODO', NOW() + INTERVAL '2 hours', 30, 85.5, NOW(), NOW()),
        (demo_user_id, 'Team standup meeting', 'Daily standup with development team', 'MEDIUM', 'TODO', NOW() + INTERVAL '1 hour', 15, 75.0, NOW(), NOW()),
        (demo_user_id, 'Update documentation', 'Update API documentation with new endpoints', 'MEDIUM', 'TODO', NOW() + INTERVAL '1 day', 60, 45.0, NOW(), NOW()),
        (demo_user_id, 'Fix login bug', 'Critical bug in authentication flow', 'URGENT', 'TODO', NOW() + INTERVAL '3 hours', 45, 95.0, NOW(), NOW()),
        (demo_user_id, 'Code review for PR #123', 'Review pull request from team member', 'HIGH', 'TODO', NOW() + INTERVAL '4 hours', 20, 70.0, NOW(), NOW()),
        (demo_user_id, 'Research new framework', 'Evaluate Spring Boot 3.3 features', 'LOW', 'TODO', NOW() + INTERVAL '3 days', 120, 25.0, NOW(), NOW()),
        (demo_user_id, 'Write unit tests', 'Add tests for new feature module', 'MEDIUM', 'IN_PROGRESS', NOW() + INTERVAL '2 days', 90, 50.0, NOW(), NOW()),
        (demo_user_id, 'Deploy to staging', 'Deploy latest changes to staging environment', 'HIGH', 'TODO', NOW() + INTERVAL '5 hours', 25, 65.0, NOW(), NOW())
    ON CONFLICT DO NOTHING;

    -- Insert sample notes
    INSERT INTO notes (user_id, title, content, pinned, created_at, updated_at)
    VALUES
        (demo_user_id, 'Meeting Notes - Sprint Planning', 'Sprint goals:\n- Complete authentication module\n- Fix critical bugs\n- Update documentation\n\nAction items:\n- Review backlog\n- Assign tasks\n- Set sprint deadline', true, NOW(), NOW()),
        (demo_user_id, 'API Design Ideas', 'Considerations for new API:\n- RESTful design\n- JWT authentication\n- Rate limiting\n- Versioning strategy\n- Documentation with Swagger', false, NOW(), NOW()),
        (demo_user_id, 'Learning Resources', 'Spring Boot:\n- Official documentation\n- Baeldung tutorials\n- YouTube channel: Spring Developer\n\nPostgreSQL:\n- PostgreSQL tutorial\n- Performance optimization guide', true, NOW(), NOW()),
        (demo_user_id, 'Project Checklist', '- [x] Setup development environment\n- [x] Create database schema\n- [ ] Implement core features\n- [ ] Write tests\n- [ ] Deploy to production', false, NOW(), NOW())
    ON CONFLICT DO NOTHING;

    -- Insert sample note tags
    INSERT INTO note_tags (note_id, tags)
    SELECT n.id, 'meeting'
    FROM notes n
    WHERE n.user_id = demo_user_id AND n.title LIKE '%Meeting Notes%'
    ON CONFLICT DO NOTHING;

    INSERT INTO note_tags (note_id, tags)
    SELECT n.id, 'planning'
    FROM notes n
    WHERE n.user_id = demo_user_id AND n.title LIKE '%Meeting Notes%'
    ON CONFLICT DO NOTHING;

    INSERT INTO note_tags (note_id, tags)
    SELECT n.id, 'development'
    FROM notes n
    WHERE n.user_id = demo_user_id AND n.title LIKE '%API Design%'
    ON CONFLICT DO NOTHING;

    INSERT INTO note_tags (note_id, tags)
    SELECT n.id, 'learning'
    FROM notes n
    WHERE n.user_id = demo_user_id AND n.title LIKE '%Learning Resources%'
    ON CONFLICT DO NOTHING;

    -- Insert sample completion history for ML training
    INSERT INTO task_completion_history (user_id, task_id, task_title, priority, estimated_duration, actual_duration, completed_at, day_of_week, hour_of_day, was_overdue, time_before_due, created_at)
    VALUES
        (demo_user_id, 1, 'Database optimization', 'HIGH', 60, 55, NOW() - INTERVAL '1 day', EXTRACT(DOW FROM NOW() - INTERVAL '1 day')::integer, EXTRACT(HOUR FROM NOW() - INTERVAL '1 day')::integer, false, 120, NOW() - INTERVAL '1 day'),
        (demo_user_id, 2, 'Code refactoring', 'MEDIUM', 90, 95, NOW() - INTERVAL '2 days', EXTRACT(DOW FROM NOW() - INTERVAL '2 days')::integer, EXTRACT(HOUR FROM NOW() - INTERVAL '2 days')::integer, false, 240, NOW() - INTERVAL '2 days'),
        (demo_user_id, 3, 'Bug fix - login', 'URGENT', 30, 25, NOW() - INTERVAL '3 days', EXTRACT(DOW FROM NOW() - INTERVAL '3 days')::integer, EXTRACT(HOUR FROM NOW() - INTERVAL '3 days')::integer, true, -60, NOW() - INTERVAL '3 days'),
        (demo_user_id, 4, 'Update dependencies', 'LOW', 45, 50, NOW() - INTERVAL '4 days', EXTRACT(DOW FROM NOW() - INTERVAL '4 days')::integer, EXTRACT(HOUR FROM NOW() - INTERVAL '4 days')::integer, false, 480, NOW() - INTERVAL '4 days'),
        (demo_user_id, 5, 'Write API docs', 'MEDIUM', 120, 110, NOW() - INTERVAL '5 days', EXTRACT(DOW FROM NOW() - INTERVAL '5 days')::integer, EXTRACT(HOUR FROM NOW() - INTERVAL '5 days')::integer, false, 180, NOW() - INTERVAL '5 days'),
        (demo_user_id, 6, 'Performance testing', 'HIGH', 75, 80, NOW() - INTERVAL '6 days', EXTRACT(DOW FROM NOW() - INTERVAL '6 days')::integer, EXTRACT(HOUR FROM NOW() - INTERVAL '6 days')::integer, false, 300, NOW() - INTERVAL '6 days'),
        (demo_user_id, 7, 'Security audit', 'URGENT', 60, 70, NOW() - INTERVAL '7 days', EXTRACT(DOW FROM NOW() - INTERVAL '7 days')::integer, EXTRACT(HOUR FROM NOW() - INTERVAL '7 days')::integer, false, 90, NOW() - INTERVAL '7 days'),
        (demo_user_id, 8, 'Team training', 'MEDIUM', 120, 115, NOW() - INTERVAL '8 days', EXTRACT(DOW FROM NOW() - INTERVAL '8 days')::integer, EXTRACT(HOUR FROM NOW() - INTERVAL '8 days')::integer, false, 360, NOW() - INTERVAL '8 days')
    ON CONFLICT DO NOTHING;

END $$;

-- Create useful views

-- View for active tasks with user information
CREATE OR REPLACE VIEW active_tasks_view AS
SELECT
    t.id,
    t.title,
    t.description,
    t.priority,
    t.status,
    t.due_date,
    t.estimated_duration,
    t.recommendation_score,
    u.name as user_name,
    u.email as user_email,
    CASE
        WHEN t.due_date < NOW() THEN true
        ELSE false
    END as is_overdue,
    t.created_at,
    t.updated_at
FROM tasks t
JOIN users u ON t.user_id = u.id
WHERE t.status NOT IN ('COMPLETED', 'CANCELLED')
ORDER BY t.recommendation_score DESC;

-- View for productivity statistics
CREATE OR REPLACE VIEW productivity_stats_view AS
SELECT
    u.id as user_id,
    u.name as user_name,
    COUNT(DISTINCT t.id) as total_tasks,
    COUNT(DISTINCT CASE WHEN t.status = 'COMPLETED' THEN t.id END) as completed_tasks,
    COUNT(DISTINCT CASE WHEN t.status = 'TODO' THEN t.id END) as todo_tasks,
    COUNT(DISTINCT CASE WHEN t.status = 'IN_PROGRESS' THEN t.id END) as in_progress_tasks,
    COUNT(DISTINCT CASE WHEN t.due_date < NOW() AND t.status NOT IN ('COMPLETED', 'CANCELLED') THEN t.id END) as overdue_tasks,
    COUNT(DISTINCT n.id) as total_notes,
    COUNT(DISTINCT tch.id) as completed_task_history_count,
    ROUND(AVG(tch.actual_duration), 2) as avg_completion_time
FROM users u
LEFT JOIN tasks t ON u.id = t.user_id
LEFT JOIN notes n ON u.id = n.user_id
LEFT JOIN task_completion_history tch ON u.id = tch.user_id
GROUP BY u.id, u.name;

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_tasks_user_status ON tasks(user_id, status);
CREATE INDEX IF NOT EXISTS idx_tasks_user_score ON tasks(user_id, recommendation_score DESC);
CREATE INDEX IF NOT EXISTS idx_tasks_due_date ON tasks(due_date);
CREATE INDEX IF NOT EXISTS idx_notes_user ON notes(user_id);
CREATE INDEX IF NOT EXISTS idx_history_user ON task_completion_history(user_id);
CREATE INDEX IF NOT EXISTS idx_history_completed_at ON task_completion_history(completed_at);

-- Grant permissions (adjust as needed)
-- GRANT ALL PRIVILEGES ON DATABASE productivity_db TO your_app_user;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO your_app_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO your_app_user;

COMMIT;
