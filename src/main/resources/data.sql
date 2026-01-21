INSERT INTO users_type (user_type_id, user_type_name) VALUES (1, 'Recruiter') ON CONFLICT (user_type_id) DO NOTHING;
INSERT INTO users_type (user_type_id, user_type_name) VALUES (2, 'Job Seeker') ON CONFLICT (user_type_id) DO NOTHING;
INSERT INTO users_type (user_type_id, user_type_name) VALUES (3, 'Admin') ON CONFLICT (user_type_id) DO NOTHING;
