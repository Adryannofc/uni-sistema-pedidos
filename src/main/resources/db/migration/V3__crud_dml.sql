INSERT INTO usuarios (id, nome, email, senha_hash, tipo_usuario)
VALUES ('u-adminEntity-01', 'Administrador',  'adminEntity@delivery.com',   'hash_admin',  'ADMIN'),
       ('u-cli-01',   'João Silva',     'joao@email.com',       'hash_joao',   'CLIENTE'),
       ('u-cli-02',   'Maria Souza',    'maria@email.com',      'hash_maria',  'CLIENTE'),
       ('u-rest-01',  'Burguer House',  'burguer@delivery.com', 'hash_rest1',  'RESTAURANTE'),
       ('u-rest-02',  'Pizzaria Bella', 'pizza@delivery.com',   'hash_rest2',  'RESTAURANTE');

SELECT id, nome, email, tipo_usuario FROM usuarios;

UPDATE usuarios
SET nome = 'Administrador Master'
WHERE id = 'u-adminEntity-01';

-- ADMINS

INSERT INTO admins (usuario_id)
VALUES ('u-adminEntity-01');

SELECT u.nome, u.email
FROM admins a
         JOIN usuarios u ON u.id = a.usuario_id;

-- CLIENTES

INSERT INTO clientes (usuario_id, cpf, telefone)
VALUES ('u-cli-01', '12345678901', '44988880001'),
       ('u-cli-02', '98765432100', '44988880002');

SELECT c.usuario_id, u.nome, c.cpf, c.telefone
FROM clientes c
         JOIN usuarios u ON u.id = c.usuario_id;

UPDATE clientes
SET telefone = '44999990099'
WHERE usuario_id = 'u-cli-01';

-- CATEGORIAS GLOBAIS
INSERT INTO categorias_globais (id, nome, descricao)
VALUES ('cg-01', 'Lanches',  'Hambúrgueres e sanduíches'),
       ('cg-02', 'Pizza',    'Pizzas variadas'),
       ('cg-03', 'Japonesa', 'Culinária japonesa');

SELECT id, nome, descricao FROM categorias_globais;

UPDATE categorias_globais
SET descricao = 'Hambúrgueres artesanais e sanduíches naturais'
WHERE id = 'cg-01';

-- RESTAURANTES

INSERT INTO restauranteEntities (usuario_id, cnpj, telefone, status_ativo, categoria_global_id)
VALUES ('u-rest-01', '12345678000195', '44999990001', TRUE,  'cg-01'),
       ('u-rest-02', '98765432000188', '44999990002', FALSE, 'cg-02');

SELECT r.usuario_id, u.nome, r.cnpj, r.status_ativo, cg.nome AS categoria
FROM restauranteEntities r
         JOIN usuarios           u  ON u.id  = r.usuario_id
         JOIN categorias_globais cg ON cg.id = r.categoria_global_id;

UPDATE restauranteEntities
SET status_ativo = TRUE
WHERE usuario_id = 'u-rest-02';

-- ENDERECOS

INSERT INTO enderecos (id, rua, numero, bairro, cidade, estado, cep, cliente_id)
VALUES ('end-01', 'Rua das Flores',       '100', 'Centro',   'Maringá',  'PR', '87010000', 'u-cli-01'),
       ('end-02', 'Rua Sete de Setembro', '42',  'Jardim',   'Londrina', 'PR', '86010000', 'u-cli-02');

SELECT e.id, u.nome AS clienteEntity, e.rua, e.numero, e.cidade
FROM enderecos e
         JOIN clientes c ON c.usuario_id = e.cliente_id
         JOIN usuarios u ON u.id         = c.usuario_id;

UPDATE enderecos
SET numero = '105', bairro = 'Centro Histórico'
WHERE id = 'end-01';

-- CATEGORIAS CARDAPIO

INSERT INTO categorias_cardapio (id, nome, descricao, restaurante_id)
VALUES ('cc-01', 'Burguers Clássicos', 'Linha tradicional',           'u-rest-01'),
       ('cc-02', 'Burguers Especiais', 'Linha premium',               'u-rest-01'),
       ('cc-03', 'Pizzas Salgadas',    'Sabores tradicionais',        'u-rest-02'),
       ('cc-04', 'Pizzas Doces',       'Sobremesas em formato pizza', 'u-rest-02');

SELECT cc.id, cc.nome, u.nome AS restauranteEntity
FROM categorias_cardapio cc
         JOIN restauranteEntities r ON r.usuario_id = cc.restaurante_id
         JOIN usuarios     u ON u.id         = r.usuario_id;

UPDATE categorias_cardapio
SET descricao = 'Hambúrgueres clássicos com ingredientes frescos'
WHERE id = 'cc-01';

-- PRODUTOS

INSERT INTO produtoEntities (id, nome, descricao, preco, status_ativo, restaurante_id, categoria_cardapio_id)
VALUES ('p-01', 'X-Burguer',       'Hambúrguer com queijo e alface', 18.90, TRUE,  'u-rest-01', 'cc-01'),
       ('p-02', 'X-Bacon',         'Hambúrguer com bacon e queijo',  22.90, TRUE,  'u-rest-01', 'cc-01'),
       ('p-03', 'Smash Burguer',   'Burguer artesanal smash',        28.90, TRUE,  'u-rest-01', 'cc-02'),
       ('p-04', 'Pizza Margherita','Tomate e mozzarella',            45.00, TRUE,  'u-rest-02', 'cc-03'),
       ('p-05', 'Coca-Cola 350ml', 'Refrigerante gelado',             6.00, FALSE, 'u-rest-01', 'cc-01');

SELECT p.nome, p.preco, p.status_ativo, cc.nome AS categoria
FROM produtoEntities p
         LEFT JOIN categorias_cardapio cc ON cc.id = p.categoria_cardapio_id
WHERE p.restaurante_id = 'u-rest-01';

UPDATE produtoEntities
SET preco = 24.90, status_ativo = TRUE
WHERE id = 'p-05';

-- PEDIDOS

INSERT INTO pedidoEntities (id, cliente_id, restaurante_id, status, taxa_entrega, total, data_pedido, codigo_confirmacao, endereco_id)
VALUES ('ped-01', 'u-cli-01', 'u-rest-01', 'AGUARDANDO_CONFIRMACAO', 5.00, 46.80, NOW(), '1234', 'end-01'),
       ('ped-02', 'u-cli-02', 'u-rest-02', 'CONFIRMADO',             5.00, 50.00, NOW(), '9876', 'end-02');

SELECT p.id AS pedidoEntity, u_c.nome AS clienteEntity, u_r.nome AS restauranteEntity, p.status, p.total
FROM pedidoEntities p
         JOIN clientes     c   ON c.usuario_id = p.cliente_id
         JOIN usuarios     u_c ON u_c.id       = c.usuario_id
         JOIN restauranteEntities r   ON r.usuario_id = p.restaurante_id
         JOIN usuarios     u_r ON u_r.id       = r.usuario_id;

UPDATE pedidoEntities
SET status = 'CONFIRMADO'
WHERE id = 'ped-01';

-- ITENS PEDIDO

INSERT INTO itens_pedido (id, pedido_id, produto_id, nome_produto, quantidade, preco_unitario)
VALUES ('ip-01', 'ped-01', 'p-01', 'X-Burguer', 2, 18.90),
       ('ip-02', 'ped-01', 'p-02', 'X-Bacon',   1, 22.90);

SELECT ip.nome_produto, ip.quantidade, ip.preco_unitario,
       (ip.quantidade * ip.preco_unitario) AS subtotal
FROM itens_pedido ip
WHERE ip.pedido_id = 'ped-01';

UPDATE itens_pedido
SET quantidade = 3
WHERE id = 'ip-01';

-- DELETE (filhos antes dos pais para respeitar FKs)

DELETE FROM itens_pedido        WHERE id         = 'ip-02';
DELETE FROM pedidoEntities             WHERE id         = 'ped-02';
DELETE FROM enderecos           WHERE id         = 'end-02';
DELETE FROM produtoEntities            WHERE id         = 'p-04';
DELETE FROM produtoEntities            WHERE id         = 'p-05';
DELETE FROM categorias_cardapio WHERE id         = 'cc-03';
DELETE FROM categorias_cardapio WHERE id         = 'cc-04';
DELETE FROM restauranteEntities        WHERE usuario_id = 'u-rest-02';
DELETE FROM categorias_globais  WHERE id         = 'cg-03';
DELETE FROM clientes            WHERE usuario_id = 'u-cli-02';
DELETE FROM admins              WHERE usuario_id = 'u-adminEntity-01';
DELETE FROM usuarios            WHERE id         = 'u-adminEntity-01';