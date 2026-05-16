ALTER TABLE restaurantes ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE';

UPDATE restaurantes
SET status = CASE
    WHEN status_ativo = true THEN 'ATIVO'
    ELSE 'PENDENTE'
END;
