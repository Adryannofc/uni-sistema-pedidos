-- V11__create_horarios_funcionamento.sql

CREATE TABLE horarios_funcionamento
(
    id             VARCHAR(36) PRIMARY KEY,
    restaurante_id VARCHAR(36)  NOT NULL REFERENCES restaurantes (usuario_id) ON DELETE CASCADE,
    dia_semana     VARCHAR(20) NOT NULL,
    hora_inicio    TIME        NOT NULL,
    hora_fim       TIME        NOT NULL
);

CREATE INDEX idx_horarios_restaurante ON horarios_funcionamento (restaurante_id);
