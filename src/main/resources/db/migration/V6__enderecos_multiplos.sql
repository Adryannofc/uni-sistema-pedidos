ALTER TABLE enderecos
    ADD COLUMN padrao BOOLEAN DEFAULT FALSE;

UPDATE enderecos
SET padrao = TRUE;

ALTER TABLE enderecos
DROP CONSTRAINT enderecos_cliente_id_key;

CREATE UNIQUE INDEX uq_endereco_padrao_cliente
    ON enderecos(cliente_id)
    WHERE padrao = TRUE;