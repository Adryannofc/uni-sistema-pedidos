INSERT INTO usuarios (id, nome, email, senha_hash, tipo_usuario)
VALUES ('u-admin-01', 'Administrador',  'admin@delivery.com',   '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',  'ADMIN'),
       ('u-cli-01',   'João Silva',     'joao@email.com',       '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',   'CLIENTE'),
       ('u-cli-02',   'Maria Souza',    'maria@email.com',      '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',  'CLIENTE'),
       ('u-rest-01',  'Burguer House',  'burguer@delivery.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',  'RESTAURANTE'),
       ('u-rest-02',  'Pizzaria Bella', 'pizza@delivery.com',   '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',  'RESTAURANTE');

SELECT id, nome, email, tipo_usuario FROM usuarios;

UPDATE usuarios
SET nome = 'Administrador Master'
WHERE id = 'u-admin-01';

-- ADMINS

INSERT INTO admins (usuario_id)
VALUES ('u-admin-01');

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

INSERT INTO restaurantes (usuario_id, cnpj, telefone, status_ativo, categoria_global_id)
VALUES ('u-rest-01', '12345678000195', '44999990001', TRUE,  'cg-01'),
       ('u-rest-02', '98765432000188', '44999990002', FALSE, 'cg-02');

SELECT r.usuario_id, u.nome, r.cnpj, r.status_ativo, cg.nome AS categoria
FROM restaurantes r
         JOIN usuarios           u  ON u.id  = r.usuario_id
         JOIN categorias_globais cg ON cg.id = r.categoria_global_id;

UPDATE restaurantes
SET status_ativo = TRUE
WHERE usuario_id = 'u-rest-02';

-- ENDERECOS

INSERT INTO enderecos (id, rua, numero, bairro, cidade, estado, cep, cliente_id)
VALUES ('end-01', 'Rua das Flores',       '100', 'Centro',   'Maringá',  'PR', '87010000', 'u-cli-01'),
       ('end-02', 'Rua Sete de Setembro', '42',  'Jardim',   'Londrina', 'PR', '86010000', 'u-cli-02');

SELECT e.id, u.nome AS cliente, e.rua, e.numero, e.cidade
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

SELECT cc.id, cc.nome, u.nome AS restaurante
FROM categorias_cardapio cc
         JOIN restaurantes r ON r.usuario_id = cc.restaurante_id
         JOIN usuarios     u ON u.id         = r.usuario_id;

UPDATE categorias_cardapio
SET descricao = 'Hambúrgueres clássicos com ingredientes frescos'
WHERE id = 'cc-01';

-- PRODUTOS

INSERT INTO produtos (id, nome, descricao, preco, status_ativo, restaurante_id, categoria_cardapio_id)
VALUES ('p-01', 'X-Burguer',       'Hambúrguer com queijo e alface', 18.90, TRUE,  'u-rest-01', 'cc-01'),
       ('p-02', 'X-Bacon',         'Hambúrguer com bacon e queijo',  22.90, TRUE,  'u-rest-01', 'cc-01'),
       ('p-03', 'Smash Burguer',   'Burguer artesanal smash',        28.90, TRUE,  'u-rest-01', 'cc-02'),
       ('p-04', 'Pizza Margherita','Tomate e mozzarella',            45.00, TRUE,  'u-rest-02', 'cc-03'),
       ('p-05', 'Coca-Cola 350ml', 'Refrigerante gelado',             6.00, FALSE, 'u-rest-01', 'cc-01');

SELECT p.nome, p.preco, p.status_ativo, cc.nome AS categoria
FROM produtos p
         LEFT JOIN categorias_cardapio cc ON cc.id = p.categoria_cardapio_id
WHERE p.restaurante_id = 'u-rest-01';

UPDATE produtos
SET preco = 24.90, status_ativo = TRUE
WHERE id = 'p-05';

-- PEDIDOS

INSERT INTO pedidos (id, cliente_id, restaurante_id, status, taxa_entrega, total, data_pedido, codigo_confirmacao, endereco_id)
VALUES ('ped-01', 'u-cli-01', 'u-rest-01', 'AGUARDANDO_CONFIRMACAO', 5.00, 46.80, NOW(), '1234', 'end-01'),
       ('ped-02', 'u-cli-02', 'u-rest-02', 'CONFIRMADO',             5.00, 50.00, NOW(), '9876', 'end-02');

SELECT p.id AS pedido, u_c.nome AS cliente, u_r.nome AS restaurante, p.status, p.total
FROM pedidos p
         JOIN clientes     c   ON c.usuario_id = p.cliente_id
         JOIN usuarios     u_c ON u_c.id       = c.usuario_id
         JOIN restaurantes r   ON r.usuario_id = p.restaurante_id
         JOIN usuarios     u_r ON u_r.id       = r.usuario_id;

UPDATE pedidos
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
