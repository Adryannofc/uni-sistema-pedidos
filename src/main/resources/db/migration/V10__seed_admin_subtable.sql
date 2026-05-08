INSERT INTO admins (usuario_id)
VALUES ('u-admin-01')
ON CONFLICT  (usuario_id) DO NOTHING;