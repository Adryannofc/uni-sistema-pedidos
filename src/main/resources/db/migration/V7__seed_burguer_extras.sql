-- V7__seed_burguer_extras.sql
-- Enriquece o cardápio do Burguer House com novas categorias,
-- produtos e áreas de entrega.

-- CATEGORIAS DE CARDÁPIO
INSERT INTO categorias_cardapio (id, nome, descricao, restaurante_id)
VALUES ('95ff73f9-d582-41c8-8eb6-4c0b0ad52f55', 'Bebidas',         'Refrigerantes, sucos e água', '6e5a11fb-f343-41ad-9de6-175817abcc06'),
       ('4396955d-014d-4935-aaa5-0c31339ff338', 'Acompanhamentos', 'Fritas, onion rings e mais',  '6e5a11fb-f343-41ad-9de6-175817abcc06'),
       ('99c22eab-ff7a-4921-a12c-0a9c9546a430', 'Sobremesas',      'Milk-shakes e doces',         '6e5a11fb-f343-41ad-9de6-175817abcc06');

-- PRODUTOS
INSERT INTO produtos (id, nome, descricao, preco, status_ativo, restaurante_id, categoria_cardapio_id)
VALUES ('8f1c187d-15f1-4713-a932-265dd4734e8c', 'Burguer Duplo',         'Dois hambúrgueres com queijo e alface',   29.90, TRUE, '6e5a11fb-f343-41ad-9de6-175817abcc06', '965408f7-86ce-4451-8bfb-d0bf0c961743'),
       ('9b4c0e8e-4cf8-4e36-a751-9728d34eaa33', 'X-Tudo',               'Completo com ovo, bacon, queijo e alface', 32.90, TRUE, '6e5a11fb-f343-41ad-9de6-175817abcc06', '965408f7-86ce-4451-8bfb-d0bf0c961743'),
       ('2e9d8479-e837-4f6d-b1c0-32c05614192b', 'Truffle Burguer',       'Burguer gourmet com maionese trufada',     42.90, TRUE, '6e5a11fb-f343-41ad-9de6-175817abcc06', '29675af7-947f-4a0b-95c5-bf5681a90b79'),
       ('3a9bc42a-959b-40ee-ae95-9cf12aa058a6', 'Burguer de Frango',     'Frango grelhado com molho especial',       26.90, TRUE, '6e5a11fb-f343-41ad-9de6-175817abcc06', '29675af7-947f-4a0b-95c5-bf5681a90b79'),
       ('0dd383b1-7896-442f-95d8-e794743c6a9f', 'Água Mineral 500ml',    'Água sem gás gelada',                       4.00, TRUE, '6e5a11fb-f343-41ad-9de6-175817abcc06', '95ff73f9-d582-41c8-8eb6-4c0b0ad52f55'),
       ('464596dd-e537-486f-b013-94e46f7bab7e', 'Suco de Laranja 400ml', 'Suco natural de laranja',                   9.90, TRUE, '6e5a11fb-f343-41ad-9de6-175817abcc06', '95ff73f9-d582-41c8-8eb6-4c0b0ad52f55'),
       ('ae5ecc68-ba9c-4d8b-af8a-589fca7de5f9', 'Guaraná Antarctica',    'Refrigerante gelado 350ml',                 6.00, TRUE, '6e5a11fb-f343-41ad-9de6-175817abcc06', '95ff73f9-d582-41c8-8eb6-4c0b0ad52f55'),
       ('c2ff6446-693b-4ac8-afb9-aa0113ca7357', 'Batata Frita P',        'Porção pequena de batata frita crocante',  12.90, TRUE, '6e5a11fb-f343-41ad-9de6-175817abcc06', '4396955d-014d-4935-aaa5-0c31339ff338'),
       ('fa3f3acd-ba45-48fa-8e1b-e674babd2e6c', 'Batata Frita G',        'Porção grande de batata frita crocante',   18.90, TRUE, '6e5a11fb-f343-41ad-9de6-175817abcc06', '4396955d-014d-4935-aaa5-0c31339ff338'),
       ('1f862468-0b33-4533-926f-6bafb195474b', 'Onion Rings',           'Anéis de cebola empanados e fritos',       16.90, TRUE, '6e5a11fb-f343-41ad-9de6-175817abcc06', '4396955d-014d-4935-aaa5-0c31339ff338'),
       ('e054bb81-6808-4d28-b3b8-ac3c2f3a20fb', 'Milk-shake Chocolate',  'Milk-shake cremoso sabor chocolate',       19.90, TRUE, '6e5a11fb-f343-41ad-9de6-175817abcc06', '99c22eab-ff7a-4921-a12c-0a9c9546a430'),
       ('57c829ca-3ea4-45fe-9542-a99c273bcc0c', 'Milk-shake Morango',    'Milk-shake cremoso sabor morango',         19.90, TRUE, '6e5a11fb-f343-41ad-9de6-175817abcc06', '99c22eab-ff7a-4921-a12c-0a9c9546a430');

-- ÁREAS DE ENTREGA
INSERT INTO areas_entrega (id, restaurante_id, bairro, distancia_maxima_km, taxa_entrega, previsao_entrega_minutos)
VALUES ('6c8b8a58-c764-43e4-9419-e89f6097517b', '6e5a11fb-f343-41ad-9de6-175817abcc06', 'Centro',              2.0,  3.00, 25),
       ('7cf5d4dc-ff42-495f-a11d-1294bb6affce', '6e5a11fb-f343-41ad-9de6-175817abcc06', 'Jardim Alvorada',     4.5,  5.00, 35),
       ('2c878495-b686-4d0c-8ff7-0f2991e24bd7', '6e5a11fb-f343-41ad-9de6-175817abcc06', 'Vila Nova',           6.0,  7.00, 45),
       ('8c30acc8-316d-4f95-9280-dd1afff174ad', '6e5a11fb-f343-41ad-9de6-175817abcc06', 'Zona Sul',            8.0,  9.00, 55),
       ('e5aac01c-5ca8-40ed-8cc6-f58e19c9ca00', '6e5a11fb-f343-41ad-9de6-175817abcc06', 'Distrito Industrial', 10.0, 12.00, 65);
