-- =============================================================================
-- V4__joins.sql
-- =============================================================================

-- INNER JOIN: Retorna pedidos com clientes e usuários vinculados
SELECT
    p.id       AS pedido_id,
    u_c.nome   AS cliente_nome,
    u_r.nome   AS restaurante_nome
FROM pedidos p
         INNER JOIN clientes     c   ON c.usuario_id = p.cliente_id
         INNER JOIN usuarios     u_c ON u_c.id       = c.usuario_id
         INNER JOIN restaurantes r   ON r.usuario_id = p.restaurante_id
         INNER JOIN usuarios     u_r ON u_r.id       = r.usuario_id;

-- LEFT JOIN: Retorna todos os restaurantes mesmo sem pedidos
SELECT
    r.usuario_id AS restaurante_id,
    u.nome       AS restaurante_nome,
    p.id         AS pedido_id
FROM restaurantes r
         LEFT JOIN usuarios u ON u.id         = r.usuario_id
         LEFT JOIN pedidos  p ON p.restaurante_id = r.usuario_id;

-- RIGHT JOIN: Retorna todas as categorias do cardápio mesmo sem produtos vinculados
SELECT
    c.id   AS categoria_id,
    c.nome AS categoria_nome,
    p.nome AS produto_nome
FROM produtos p
         RIGHT JOIN categorias_cardapio c ON p.categoria_cardapio_id = c.id;

-- INNER JOIN: Detalhamento dos itens do pedido
SELECT
    ip.id          AS item_id,
    ped.id         AS pedido_id,
    prod.nome      AS produto_nome,
    ip.quantidade,
    ip.preco_unitario
FROM itens_pedido ip
         INNER JOIN pedidos  ped  ON ip.pedido_id  = ped.id
         INNER JOIN produtos prod ON ip.produto_id = prod.id;

-- INNER JOIN: Total de pedidos por restaurante
SELECT
    u.nome         AS restaurante_nome,
    COUNT(p.id)    AS total_pedidos
FROM restaurantes r
         INNER JOIN usuarios u ON u.id          = r.usuario_id
         INNER JOIN pedidos  p ON p.restaurante_id = r.usuario_id
GROUP BY u.nome;

-- INNER JOIN: Pedidos por status
SELECT
    u.nome AS restaurante_nome,
    p.id   AS pedido_id,
    p.status
FROM restaurantes r
         INNER JOIN usuarios u ON u.id             = r.usuario_id
         INNER JOIN pedidos  p ON p.restaurante_id = r.usuario_id
WHERE p.status = 'AGUARDANDO_CONFIRMACAO';