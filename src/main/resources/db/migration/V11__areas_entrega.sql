CREATE TABLE areas_entrega (
                               id VARCHAR(36) PRIMARY KEY,
                               restaurante_id VARCHAR(36) NOT NULL REFERENCES restaurantes(usuario_id),
                               bairro VARCHAR(100) NOT NULL,
                               distancia_maxima_km DECIMAL(6,2),
                               taxa_entrega DECIMAL(10,2) NOT NULL,
                               previsao_entrega_minutos INT
);