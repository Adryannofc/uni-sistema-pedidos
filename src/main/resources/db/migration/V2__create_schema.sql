CREATE TABLE usuarios
(
    id                  VARCHAR(36) PRIMARY KEY,
    nome                VARCHAR(100) NOT NULL,
    email               VARCHAR(150) NOT NULL UNIQUE,
    senha_hash          VARCHAR(64)  NOT NULL,
    tipo_usuario        VARCHAR(20)  NOT NULL,
    cpf                 VARCHAR(14),
    telefone            VARCHAR(20),
    rua                 VARCHAR(150),
    numero              VARCHAR(10),
    bairro              VARCHAR(100),
    cidade              VARCHAR(100),
    estado              VARCHAR(2),
    cep                 VARCHAR(9),
    cnpj                VARCHAR(18),
    status_ativo        BOOLEAN,
    categoria_global_id VARCHAR(36),
    telefone_rest       VARCHAR(20)
);

CREATE TABLE categorias_globais
(
    id        VARCHAR(36) PRIMARY KEY,
    nome      VARCHAR(100) NOT NULL,
    descricao VARCHAR(255)
);

CREATE TABLE categorias_cardapio
(
    id             VARCHAR(36) PRIMARY KEY,
    nome           VARCHAR(100) NOT NULL,
    descricao      VARCHAR(255),
    restaurante_id VARCHAR(36)  NOT NULL REFERENCES usuarios (id)
);

CREATE TABLE produtos
(
    id                    VARCHAR(36) PRIMARY KEY,
    nome                  VARCHAR(100)   NOT NULL,
    descricao             VARCHAR(255),
    preco                 NUMERIC(10, 2) NOT NULL,
    categoria_cardapio_id VARCHAR(36) REFERENCES categorias_cardapio (id),
    restaurante_id        VARCHAR(36)    NOT NULL REFERENCES usuarios (id),
    status_ativo          BOOLEAN        NOT NULL DEFAULT TRUE
);

CREATE TABLE pedidos
(
    id                 VARCHAR(36) PRIMARY KEY,
    cliente_id         VARCHAR(36)    NOT NULL REFERENCES usuarios (id),
    restaurante_id     VARCHAR(36)    NOT NULL REFERENCES usuarios (id),
    status_pedido      VARCHAR(30)    NOT NULL,
    taxa_entrega       NUMERIC(10, 2) NOT NULL,
    total              NUMERIC(10, 2) NOT NULL DEFAULT 0,
    data_pedido        TIMESTAMP      NOT NULL,
    codigo_confirmacao VARCHAR(20),
    rua                VARCHAR(150),
    numero             VARCHAR(10),
    bairro             VARCHAR(100),
    cidade             VARCHAR(100),
    estado             VARCHAR(2),
    cep                VARCHAR(9)
);

CREATE TABLE itens_pedido
(
    id             VARCHAR(36) PRIMARY KEY,
    pedido_id      VARCHAR(36)    NOT NULL REFERENCES pedidos (id),
    produto_id     VARCHAR(36)    NOT NULL,
    nome_produto   VARCHAR(100)   NOT NULL,
    quantidade     INT            NOT NULL,
    preco_unitario NUMERIC(10, 2) NOT NULL
);