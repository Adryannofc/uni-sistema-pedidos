-- =============================================================================
-- V7__fulljoin.sql
-- =============================================================================



-- FULL JOIN: Todos os restaurantes e pedidos, incluindo aqueles sem correspondência
SELECT
    r.usuario_id AS restaurante_id,
    u.nome       AS restaurante_nome,
    p.id         AS pedido_id,
    p.status
FROM restaurantes r
         FULL OUTER JOIN usuarios u ON u.id         = r.usuario_id
         FULL OUTER JOIN pedidos  p ON p.restaurante_id = r.usuario_id;

-- FULL JOIN: Todos os produtos e categorias do cardápio
SELECT
    c.id   AS categoria_id,
    c.nome AS categoria_nome,
    p.id   AS produto_id,
    p.nome AS produto_nome
FROM categorias_cardapio c
         FULL OUTER JOIN produtos p ON p.categoria_cardapio_id = c.id;
