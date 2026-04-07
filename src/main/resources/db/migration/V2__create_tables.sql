--V2__create_tables.sql

CREATE TABLE categorias_globais (
                                    id        VARCHAR(36)  PRIMARY KEY,
                                    nome      VARCHAR(100) NOT NULL UNIQUE,
                                    descricao TEXT
);

CREATE TABLE usuarios (
                          id           VARCHAR(36)  PRIMARY KEY,
                          tipo_usuario VARCHAR(20)  NOT NULL,
                          nome         VARCHAR(150) NOT NULL,
                          email        VARCHAR(255) NOT NULL UNIQUE,
                          senha_hash   VARCHAR(255) NOT NULL
);

CREATE TABLE admins (
                        id VARCHAR(36) PRIMARY KEY,
                        FOREIGN KEY (id) REFERENCES usuarios(id)
);

CREATE TABLE clientes (
                          id                   VARCHAR(36)  PRIMARY KEY,
                          cpf                  VARCHAR(14)  NOT NULL UNIQUE,
                          telefone             VARCHAR(20),
                          endereco_cep         VARCHAR(10),
                          endereco_logradouro  VARCHAR(255),
                          endereco_numero      VARCHAR(20),
                          endereco_complemento VARCHAR(100),
                          endereco_bairro      VARCHAR(100),
                          endereco_cidade      VARCHAR(100),
                          endereco_estado      CHAR(2),
                          FOREIGN KEY (id) REFERENCES usuarios(id)
);

CREATE TABLE restaurantes (
                              id                  VARCHAR(36)  PRIMARY KEY,
                              cnpj                VARCHAR(18)  NOT NULL UNIQUE,
                              telefone            VARCHAR(20),
                              status_ativo        BOOLEAN      NOT NULL DEFAULT TRUE,
                              categoria_global_id VARCHAR(36),
                              FOREIGN KEY (categoria_global_id) REFERENCES categorias_globais(id)
);

CREATE TABLE categorias_cardapio (
                                     id             VARCHAR(36)  PRIMARY KEY,
                                     nome           VARCHAR(100) NOT NULL,
                                     descricao      TEXT,
                                     restaurante_id VARCHAR(36)  NOT NULL,
                                     FOREIGN KEY (restaurante_id) REFERENCES restaurantes(id)
);

CREATE TABLE produtos (
                          id                    VARCHAR(36)    PRIMARY KEY,
                          nome                  VARCHAR(150)   NOT NULL,
                          descricao             TEXT,
                          preco                 DECIMAL(10, 2) NOT NULL,
                          status_ativo          BOOLEAN        NOT NULL DEFAULT TRUE,
                          restaurante_id        VARCHAR(36)    NOT NULL,
                          categoria_cardapio_id VARCHAR(36),
                          FOREIGN KEY (restaurante_id) REFERENCES restaurantes(id),
                          FOREIGN KEY (categoria_cardapio_id) REFERENCES categorias_cardapio(id)
);

CREATE TABLE pedidos (
                         id                  VARCHAR(36)    PRIMARY KEY,
                         cliente_id          VARCHAR(36)    NOT NULL,
                         restaurante_id      VARCHAR(36)    NOT NULL,
                         status              VARCHAR(30)    NOT NULL,
                         taxa_entrega        DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
                         total               DECIMAL(10, 2) NOT NULL,
                         data_pedido         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         codigo_confirmacao  VARCHAR(20),
                         entrega_cep         VARCHAR(10),
                         entrega_logradouro  VARCHAR(255),
                         entrega_numero      VARCHAR(20),
                         entrega_complemento VARCHAR(100),
                         entrega_bairro      VARCHAR(100),
                         entrega_cidade      VARCHAR(100),
                         entrega_estado      CHAR(2),
                         FOREIGN KEY (cliente_id) REFERENCES clientes(id),
                         FOREIGN KEY (restaurante_id) REFERENCES restaurantes(id)
);

CREATE TABLE itens_pedido (
                              id             VARCHAR(36)    PRIMARY KEY,
                              pedido_id      VARCHAR(36)    NOT NULL,
                              produto_id     VARCHAR(36),
                              nome_produto   VARCHAR(150)   NOT NULL,
                              quantidade     INT            NOT NULL,
                              preco_unitario DECIMAL(10, 2) NOT NULL,
                              FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
                              FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE TABLE clientes_favoritos (
                                    cliente_id     VARCHAR(36) NOT NULL,
                                    restaurante_id VARCHAR(36) NOT NULL,
                                    PRIMARY KEY (cliente_id, restaurante_id),
                                    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
                                    FOREIGN KEY (restaurante_id) REFERENCES restaurantes(id)
);