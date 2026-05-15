INSERT INTO usuarios (id, nome, email, senha_hash, tipo_usuario)
VALUES ('a82d53d2-90c3-4923-80e2-0ad2cebb56b9', 'Administrador',  'admin@delivery.com',   '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'ADMIN'),
       ('f856e493-4d87-461d-ad06-f0c34ee5838f', 'João Silva',     'joao@email.com',       '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'CLIENTE'),
       ('80dcde6a-1392-4f68-b096-e26421163750', 'Maria Souza',    'maria@email.com',      '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'CLIENTE'),
       ('6e5a11fb-f343-41ad-9de6-175817abcc06', 'Burguer House',  'burguer@delivery.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'RESTAURANTE'),
       ('f6c3af9b-6c90-498a-bb28-bf824c2dca18', 'Pizzaria Bella', 'pizza@delivery.com',   '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'RESTAURANTE');

SELECT id, nome, email, tipo_usuario FROM usuarios;

UPDATE usuarios
SET nome = 'Administrador Master'
WHERE id = 'a82d53d2-90c3-4923-80e2-0ad2cebb56b9';

-- ADMINS

INSERT INTO admins (usuario_id)
VALUES ('a82d53d2-90c3-4923-80e2-0ad2cebb56b9');

SELECT u.nome, u.email
FROM admins a
         JOIN usuarios u ON u.id = a.usuario_id;

-- CLIENTES

INSERT INTO clientes (usuario_id, cpf, telefone)
VALUES ('f856e493-4d87-461d-ad06-f0c34ee5838f', '12345678901', '44988880001'),
       ('80dcde6a-1392-4f68-b096-e26421163750', '98765432100', '44988880002');

SELECT c.usuario_id, u.nome, c.cpf, c.telefone
FROM clientes c
         JOIN usuarios u ON u.id = c.usuario_id;

UPDATE clientes
SET telefone = '44999990099'
WHERE usuario_id = 'f856e493-4d87-461d-ad06-f0c34ee5838f';

-- CATEGORIAS GLOBAIS
INSERT INTO categorias_globais (id, nome, descricao)
VALUES ('66a49c0f-5616-417d-b714-1afdd2eccff1', 'Lanches',  'Hambúrgueres e sanduíches'),
       ('60d9d3c9-be65-44bb-a21b-de4d9cf976ea', 'Pizza',    'Pizzas variadas'),
       ('820a65c4-8203-4299-987e-70aec93e9b09', 'Japonesa', 'Culinária japonesa');

SELECT id, nome, descricao FROM categorias_globais;

UPDATE categorias_globais
SET descricao = 'Hambúrgueres artesanais e sanduíches naturais'
WHERE id = '66a49c0f-5616-417d-b714-1afdd2eccff1';

-- RESTAURANTES

INSERT INTO restaurantes (usuario_id, cnpj, telefone, status_ativo, categoria_global_id)
VALUES ('6e5a11fb-f343-41ad-9de6-175817abcc06', '12345678000195', '44999990001', TRUE,  '66a49c0f-5616-417d-b714-1afdd2eccff1'),
       ('f6c3af9b-6c90-498a-bb28-bf824c2dca18', '98765432000188', '44999990002', FALSE, '60d9d3c9-be65-44bb-a21b-de4d9cf976ea');

SELECT r.usuario_id, u.nome, r.cnpj, r.status_ativo, cg.nome AS categoria
FROM restaurantes r
         JOIN usuarios           u  ON u.id  = r.usuario_id
         JOIN categorias_globais cg ON cg.id = r.categoria_global_id;

UPDATE restaurantes
SET status_ativo = TRUE
WHERE usuario_id = 'f6c3af9b-6c90-498a-bb28-bf824c2dca18';

-- ENDERECOS

INSERT INTO enderecos (id, rua, numero, bairro, cidade, estado, cep, cliente_id)
VALUES ('547a1b2a-04c5-4752-b3ab-f960740fc457', 'Rua das Flores',       '100', 'Centro', 'Maringá',  'PR', '87010000', 'f856e493-4d87-461d-ad06-f0c34ee5838f'),
       ('e91ffc64-0313-4cbb-8f49-66e4d6032945', 'Rua Sete de Setembro', '42',  'Jardim', 'Londrina', 'PR', '86010000', '80dcde6a-1392-4f68-b096-e26421163750');

SELECT e.id, u.nome AS cliente, e.rua, e.numero, e.cidade
FROM enderecos e
         JOIN clientes c ON c.usuario_id = e.cliente_id
         JOIN usuarios u ON u.id         = c.usuario_id;

UPDATE enderecos
SET numero = '105', bairro = 'Centro Histórico'
WHERE id = '547a1b2a-04c5-4752-b3ab-f960740fc457';

-- CATEGORIAS CARDAPIO

INSERT INTO categorias_cardapio (id, nome, descricao, restaurante_id)
VALUES ('965408f7-86ce-4451-8bfb-d0bf0c961743', 'Burguers Clássicos', 'Linha tradicional',           '6e5a11fb-f343-41ad-9de6-175817abcc06'),
       ('29675af7-947f-4a0b-95c5-bf5681a90b79', 'Burguers Especiais', 'Linha premium',               '6e5a11fb-f343-41ad-9de6-175817abcc06'),
       ('0130dd7b-b378-401a-81be-3ac5fe2032a9', 'Pizzas Salgadas',    'Sabores tradicionais',        'f6c3af9b-6c90-498a-bb28-bf824c2dca18'),
       ('30d30424-7019-4ae5-b710-577afd9372a0', 'Pizzas Doces',       'Sobremesas em formato pizza', 'f6c3af9b-6c90-498a-bb28-bf824c2dca18');

SELECT cc.id, cc.nome, u.nome AS restaurante
FROM categorias_cardapio cc
         JOIN restaurantes r ON r.usuario_id = cc.restaurante_id
         JOIN usuarios     u ON u.id         = r.usuario_id;

UPDATE categorias_cardapio
SET descricao = 'Hambúrgueres clássicos com ingredientes frescos'
WHERE id = '965408f7-86ce-4451-8bfb-d0bf0c961743';

-- PRODUTOS

INSERT INTO produtos (id, nome, descricao, preco, status_ativo, restaurante_id, categoria_cardapio_id)
VALUES ('a9d6ed71-297c-4b30-827c-7657a8383c9e', 'X-Burguer',        'Hambúrguer com queijo e alface', 18.90, TRUE,  '6e5a11fb-f343-41ad-9de6-175817abcc06', '965408f7-86ce-4451-8bfb-d0bf0c961743'),
       ('9687ae1b-05d3-4e5b-ac53-5c67f914539a', 'X-Bacon',          'Hambúrguer com bacon e queijo',  22.90, TRUE,  '6e5a11fb-f343-41ad-9de6-175817abcc06', '965408f7-86ce-4451-8bfb-d0bf0c961743'),
       ('a8155e20-93fc-4327-9050-29300ec53b38', 'Smash Burguer',    'Burguer artesanal smash',        28.90, TRUE,  '6e5a11fb-f343-41ad-9de6-175817abcc06', '29675af7-947f-4a0b-95c5-bf5681a90b79'),
       ('1900e19d-c336-4100-88b6-975f12dfd350', 'Pizza Margherita', 'Tomate e mozzarella',            45.00, TRUE,  'f6c3af9b-6c90-498a-bb28-bf824c2dca18', '0130dd7b-b378-401a-81be-3ac5fe2032a9'),
       ('fd84cd3a-3770-4d04-b363-182f70cd6c68', 'Coca-Cola 350ml',  'Refrigerante gelado',             6.00, FALSE, '6e5a11fb-f343-41ad-9de6-175817abcc06', '965408f7-86ce-4451-8bfb-d0bf0c961743');

SELECT p.nome, p.preco, p.status_ativo, cc.nome AS categoria
FROM produtos p
         LEFT JOIN categorias_cardapio cc ON cc.id = p.categoria_cardapio_id
WHERE p.restaurante_id = '6e5a11fb-f343-41ad-9de6-175817abcc06';

UPDATE produtos
SET preco = 24.90, status_ativo = TRUE
WHERE id = 'fd84cd3a-3770-4d04-b363-182f70cd6c68';

-- PEDIDOS

INSERT INTO pedidos (id, cliente_id, restaurante_id, status, taxa_entrega, total, data_pedido, codigo_confirmacao, endereco_id)
VALUES ('bd2315f4-a19a-4f86-854e-df3d552c37d4', 'f856e493-4d87-461d-ad06-f0c34ee5838f', '6e5a11fb-f343-41ad-9de6-175817abcc06', 'AGUARDANDO_CONFIRMACAO', 5.00, 46.80, NOW(), '1234', '547a1b2a-04c5-4752-b3ab-f960740fc457'),
       ('0d78c441-6629-42cf-9847-37f83b13f78b', '80dcde6a-1392-4f68-b096-e26421163750', 'f6c3af9b-6c90-498a-bb28-bf824c2dca18', 'CONFIRMADO',             5.00, 50.00, NOW(), '9876', 'e91ffc64-0313-4cbb-8f49-66e4d6032945');

SELECT p.id AS pedido, u_c.nome AS cliente, u_r.nome AS restaurante, p.status, p.total
FROM pedidos p
         JOIN clientes     c   ON c.usuario_id = p.cliente_id
         JOIN usuarios     u_c ON u_c.id       = c.usuario_id
         JOIN restaurantes r   ON r.usuario_id = p.restaurante_id
         JOIN usuarios     u_r ON u_r.id       = r.usuario_id;

UPDATE pedidos
SET status = 'CONFIRMADO'
WHERE id = 'bd2315f4-a19a-4f86-854e-df3d552c37d4';

-- ITENS PEDIDO

INSERT INTO itens_pedido (id, pedido_id, produto_id, nome_produto, quantidade, preco_unitario)
VALUES ('f5eca13e-cd9d-429b-8c9e-50645a2f5509', 'bd2315f4-a19a-4f86-854e-df3d552c37d4', 'a9d6ed71-297c-4b30-827c-7657a8383c9e', 'X-Burguer', 2, 18.90),
       ('89695d1e-a8eb-4db2-b33a-83e917772da1', 'bd2315f4-a19a-4f86-854e-df3d552c37d4', '9687ae1b-05d3-4e5b-ac53-5c67f914539a', 'X-Bacon',   1, 22.90);

SELECT ip.nome_produto, ip.quantidade, ip.preco_unitario,
       (ip.quantidade * ip.preco_unitario) AS subtotal
FROM itens_pedido ip
WHERE ip.pedido_id = 'bd2315f4-a19a-4f86-854e-df3d552c37d4';

UPDATE itens_pedido
SET quantidade = 3
WHERE id = 'f5eca13e-cd9d-429b-8c9e-50645a2f5509';
