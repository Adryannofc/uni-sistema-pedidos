-- =============================================================================
-- V4__update_tables.sql
-- =============================================================================

-- Remover colunas extras de usuarios
ALTER TABLE usuarios
DROP COLUMN IF EXISTS cpf,
    DROP COLUMN IF EXISTS telefone,
    DROP COLUMN IF EXISTS rua,
    DROP COLUMN IF EXISTS numero,
    DROP COLUMN IF EXISTS bairro,
    DROP COLUMN IF EXISTS cidade,
    DROP COLUMN IF EXISTS estado,
    DROP COLUMN IF EXISTS cep,
    DROP COLUMN IF EXISTS cnpj,
    DROP COLUMN IF EXISTS status_ativo,
    DROP COLUMN IF EXISTS categoria_global_id,
    DROP COLUMN IF EXISTS telefone_rest;

-- Criar tabelas filhas
CREATE TABLE admins (
                        usuario_id VARCHAR PRIMARY KEY REFERENCES usuarios(id)
);

CREATE TABLE clientes (
                          usuario_id VARCHAR PRIMARY KEY REFERENCES usuarios(id),
                          cpf        VARCHAR UNIQUE,
                          telefone   VARCHAR
);

CREATE TABLE restaurantes (
                              usuario_id          VARCHAR PRIMARY KEY REFERENCES usuarios(id),
                              cnpj                VARCHAR UNIQUE,
                              telefone            VARCHAR,
                              status_ativo        BOOLEAN,
                              categoria_global_id VARCHAR REFERENCES categorias_globais(id)
);

-- Criar tabela de enderecos
CREATE TABLE enderecos (
                           id         VARCHAR PRIMARY KEY,
                           rua        VARCHAR,
                           numero     VARCHAR,
                           bairro     VARCHAR,
                           cidade     VARCHAR,
                           estado     VARCHAR,
                           cep        VARCHAR,
                           cliente_id VARCHAR REFERENCES clientes(usuario_id)
);

-- Atualizar FK de categorias_cardapio
ALTER TABLE categorias_cardapio
DROP CONSTRAINT IF EXISTS fk_cat_cardapio_restaurante,
    ADD FOREIGN KEY (restaurante_id) REFERENCES restaurantes(usuario_id);

-- Atualizar FK de produtos
ALTER TABLE produtos
DROP CONSTRAINT IF EXISTS fk_produtos_restaurante,
    ADD FOREIGN KEY (restaurante_id) REFERENCES restaurantes(usuario_id);

-- Atualizar pedidos
ALTER TABLE pedidos
DROP COLUMN IF EXISTS rua,
    DROP COLUMN IF EXISTS numero,
    DROP COLUMN IF EXISTS bairro,
    DROP COLUMN IF EXISTS cidade,
    DROP COLUMN IF EXISTS estado,
    DROP COLUMN IF EXISTS cep;

ALTER TABLE pedidos
    RENAME COLUMN status_pedido TO status;

ALTER TABLE pedidos
    ADD COLUMN endereco_id VARCHAR REFERENCES enderecos(id),
DROP CONSTRAINT IF EXISTS pedidos_cliente_id_fkey,
    DROP CONSTRAINT IF EXISTS pedidos_restaurante_id_fkey,
    ADD FOREIGN KEY (cliente_id)     REFERENCES clientes(usuario_id),
    ADD FOREIGN KEY (restaurante_id) REFERENCES restaurantes(usuario_id);