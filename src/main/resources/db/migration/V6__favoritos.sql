CREATE TABLE cliente_restaurantes_favoritos
(
    cliente_id     VARCHAR(36) REFERENCES clientes (usuario_id) ON DELETE CASCADE,
    restaurante_id VARCHAR(36) REFERENCES restaurantes (usuario_id) ON DELETE CASCADE,
    PRIMARY KEY (cliente_id, restaurante_id)
);