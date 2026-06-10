
-- V2__create_schema.sql

CREATE TABLE usuarios
(
    id           VARCHAR(36) PRIMARY KEY,
    nome         VARCHAR(100) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    senha_hash   VARCHAR(64)  NOT NULL,
    tipo_usuario VARCHAR(20)  NOT NULL
);

CREATE TABLE admins
(
    usuario_id VARCHAR(36) PRIMARY KEY REFERENCES usuarios (id)
);

CREATE TABLE clientes
(
    usuario_id VARCHAR(36) PRIMARY KEY REFERENCES usuarios (id),
    cpf        VARCHAR(14) UNIQUE,
    telefone   VARCHAR(20)
);

CREATE TABLE categorias_globais
(
    id        VARCHAR(36) PRIMARY KEY,
    nome      VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255)
);

CREATE TABLE restaurantes
(
    usuario_id          VARCHAR(36) PRIMARY KEY REFERENCES usuarios (id),
    cnpj                VARCHAR(18) UNIQUE,
    telefone            VARCHAR(20),
    status_ativo        BOOLEAN NOT NULL DEFAULT FALSE,
    categoria_global_id VARCHAR(36) REFERENCES categorias_globais (id)
);

CREATE TABLE enderecos
(
    id         VARCHAR(36) PRIMARY KEY,
    rua        VARCHAR(150),
    numero     VARCHAR(10),
    bairro     VARCHAR(100),
    cidade     VARCHAR(100),
    estado     VARCHAR(2),
    cep        VARCHAR(9),
    cliente_id VARCHAR(36) REFERENCES clientes (usuario_id)
);

CREATE TABLE categorias_cardapio
(
    id             VARCHAR(36) PRIMARY KEY,
    nome           VARCHAR(100) NOT NULL,
    descricao      VARCHAR(255),
    restaurante_id VARCHAR(36)  NOT NULL REFERENCES restaurantes (usuario_id)
);

CREATE TABLE produtos
(
    id                    VARCHAR(36) PRIMARY KEY,
    nome                  VARCHAR(100)   NOT NULL,
    descricao             VARCHAR(255),
    preco                 DECIMAL(10, 2) NOT NULL,
    status_ativo          BOOLEAN        NOT NULL DEFAULT TRUE,
    restaurante_id        VARCHAR(36)    NOT NULL REFERENCES restaurantes (usuario_id),
    categoria_cardapio_id VARCHAR(36) REFERENCES categorias_cardapio (id)
);

CREATE TABLE pedidos
(
    id                 VARCHAR(36) PRIMARY KEY,
    cliente_id         VARCHAR(36)    NOT NULL REFERENCES clientes (usuario_id),
    restaurante_id     VARCHAR(36)    NOT NULL REFERENCES restaurantes (usuario_id),
    status             VARCHAR(30)    NOT NULL,
    taxa_entrega       DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total              DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    data_pedido        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    codigo_confirmacao VARCHAR(20),
    endereco_id        VARCHAR(36)    NOT NULL REFERENCES enderecos (id)
);

CREATE TABLE itens_pedido
(
    id             VARCHAR(36) PRIMARY KEY,
    pedido_id      VARCHAR(36)    NOT NULL REFERENCES pedidos (id),
    produto_id     VARCHAR(36),
    nome_produto   VARCHAR(100)   NOT NULL,
    quantidade     INT            NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL
);

CREATE TABLE areas_entrega (
    id VARCHAR(36) PRIMARY KEY,
    restaurante_id VARCHAR(36) NOT NULL REFERENCES restaurantes(usuario_id),
    bairro VARCHAR(100) NOT NULL,
    distancia_maxima_km DECIMAL(6,2),
    taxa_entrega DECIMAL(10,2) NOT NULL,
    previsao_entrega_minutos INT
);

CREATE TABLE horarios_funcionamento (
    id VARCHAR(36) PRIMARY KEY,
    restaurante_id VARCHAR(36) NOT NULL REFERENCES restaurantes(usuario_id),
    dia_semana VARCHAR(15) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    CONSTRAINT chk_horario CHECK (hora_fim > hora_inicio)
);

CREATE INDEX idx_horarios_restaurante ON horarios_funcionamento (restaurante_id);
