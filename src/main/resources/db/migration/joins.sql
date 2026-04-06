-- INNER JOIN: Retorna pedidos com clientes e usuarios vinculados
SELECT
    p.id AS pedido_id,
    c.nome AS cliente_nome
    u.nome AS usuario_nome
FROM pedidos p
INNER JOIN clientes c ON p.cliente_id = c.id
INNER JOIN usuarios u ON p.usuario_id = u.id;

-- LEFT JOIN: Retorna todos os restaurantes mesmo sem pedidos
SELECT
    r.id AS restaurante_id,
    r.nome AS restaurante_nome,
    p.id AS pedido_id
FROM restaurante r
LEFT JOIN pedidos p ON p.restaurante_id = r.id;

-- RIGHT JOIN: Retorna todas as categorias_produto mesmo sem produtos vinculados
SELECT
    c.id AS categoria_id,
    c.nome AS categoria_nome,
    p.nome AS produto_nome
FROM produtos p
RIGHT JOIN categorias_cardapio c ON p.categoria_id = c.id;

-- INNER JOIN: Detalhamento dos itens do pedido
SELECT
    ip.id AS item_id,
    ped.id AS pedido_id,
    prod.nome AS produto_nome,
    ip.quantidade,
    ip.preco
FROM itens_pedidos ip
INNER JOIN pedidos ped ON ip_pedido_id = ped.id
INNER JOIN produtos prod ON ip.produto_id = prod.id;

-- INNER JOIN: Retornando total de pedidos por restaurante
SELECT
    r.nome AS restaurante_nome,
    COUNT(p.id) AS total_pedidos
FROM restaurantes r
INNER JOIN pedidos p ON p.restaurante_id = r.id
GROUP BY r.nome;

-- INNER JOIN: Retorna pedidos ativos por restaurante
SELECT
    r.nome AS restaurante_nome,
    p.id AS pedido_id,
    p.status
FROM restaurante r
INNER JOIN pedidos p ON p.restaurante_id = r.id
WHERE p.status = 'ATIVO';