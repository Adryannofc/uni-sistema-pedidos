-- V8__seed_novos_restaurantes.sql
-- Adiciona 3 novos restaurantes com categorias, produtos e áreas de entrega.

-- ============================================================
-- CATEGORIAS GLOBAIS EXTRAS
-- ============================================================
INSERT INTO categorias_globais (id, nome, descricao)
VALUES ('d37cf5ce-1536-4f76-a112-19c3a9c97440', 'Churrasco',      'Carnes grelhadas e churrascarias'),
       ('8bb557eb-9de6-4138-a7c0-8860b0812d8c', 'Comida Caseira', 'Pratos caseiros e marmitas');

-- ============================================================
-- RESTAURANTE 3 — Sushi Zen (Japonesa)
-- ============================================================
INSERT INTO usuarios (id, nome, email, senha_hash, tipo_usuario)
VALUES ('06dd7459-aef5-47dc-8941-cb0fc69163ab', 'Sushi Zen', 'sushi@delivery.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'RESTAURANTE');

INSERT INTO restaurantes (usuario_id, cnpj, telefone, status_ativo, categoria_global_id)
VALUES ('06dd7459-aef5-47dc-8941-cb0fc69163ab', '11223344000155', '44999990003', TRUE, '820a65c4-8203-4299-987e-70aec93e9b09');

INSERT INTO categorias_cardapio (id, nome, descricao, restaurante_id)
VALUES ('7fd0f892-bcc8-4209-b03c-2d99a99a1377', 'Entradas',           'Sopas e petiscos japoneses',    '06dd7459-aef5-47dc-8941-cb0fc69163ab'),
       ('bf49456b-e002-4539-ad28-84495fc7bb83', 'Sushis e Sashimis',  'Peças frescas de sushi',        '06dd7459-aef5-47dc-8941-cb0fc69163ab'),
       ('3d2c22a1-8a3a-432e-9c6e-6928d4072d37', 'Pratos Quentes',     'Pratos servidos quentes',       '06dd7459-aef5-47dc-8941-cb0fc69163ab');

INSERT INTO produtos (id, nome, descricao, preco, status_ativo, restaurante_id, categoria_cardapio_id)
VALUES ('d64148ff-6d6a-4898-bf6d-6cd6fd569d74', 'Missoshiru',           'Sopa de missô com tofu e cebolinha',     8.90,  TRUE, '06dd7459-aef5-47dc-8941-cb0fc69163ab', '7fd0f892-bcc8-4209-b03c-2d99a99a1377'),
       ('c407df51-e097-41a4-b0a8-5546289ed9d6', 'Gyoza (6 un.)',        'Pastel japonês grelhado recheado de carne', 22.90, TRUE, '06dd7459-aef5-47dc-8941-cb0fc69163ab', '7fd0f892-bcc8-4209-b03c-2d99a99a1377'),
       ('18893feb-8158-4169-ab8e-c5f847902529', 'Combo Sushi 10 peças', 'Seleção de 10 peças do chef',            55.90, TRUE, '06dd7459-aef5-47dc-8941-cb0fc69163ab', 'bf49456b-e002-4539-ad28-84495fc7bb83'),
       ('c2f3220b-d25f-4068-b4eb-caa2dd1b57b9', 'Temaki Salmão',        'Cone de alga com salmão e cream cheese', 29.90, TRUE, '06dd7459-aef5-47dc-8941-cb0fc69163ab', 'bf49456b-e002-4539-ad28-84495fc7bb83'),
       ('a3fc3e0b-8236-49d7-894a-f55b1630b878', 'Uramaki California',   'Roll invertido com kani e pepino',       32.90, TRUE, '06dd7459-aef5-47dc-8941-cb0fc69163ab', 'bf49456b-e002-4539-ad28-84495fc7bb83'),
       ('7969e5ea-c9d7-471b-aba8-6a6428d32a29', 'Yakissoba de Frango',  'Macarrão japonês salteado com frango',   39.90, TRUE, '06dd7459-aef5-47dc-8941-cb0fc69163ab', '3d2c22a1-8a3a-432e-9c6e-6928d4072d37'),
       ('5122bca6-191c-4a44-95a7-e252e2762b9e', 'Katsu Curry',          'Filé empanado com curry japonês e arroz', 44.90, TRUE, '06dd7459-aef5-47dc-8941-cb0fc69163ab', '3d2c22a1-8a3a-432e-9c6e-6928d4072d37');

INSERT INTO areas_entrega (id, restaurante_id, bairro, distancia_maxima_km, taxa_entrega, previsao_entrega_minutos)
VALUES ('00f4c1eb-c399-4d26-ade4-a0a0e8237dc6', '06dd7459-aef5-47dc-8941-cb0fc69163ab', 'Centro',         3.0,  4.00, 30),
       ('1d26f905-5098-4ea4-b653-0e49b60e645d', '06dd7459-aef5-47dc-8941-cb0fc69163ab', 'Zona Norte',     5.5,  6.00, 40),
       ('bc96e26f-b3fd-4bc6-95f8-c46e4fb6f20e', '06dd7459-aef5-47dc-8941-cb0fc69163ab', 'Jardim América', 7.0,  8.00, 50);

-- ============================================================
-- RESTAURANTE 4 — Churrascaria Gaúcha (Churrasco)
-- ============================================================
INSERT INTO usuarios (id, nome, email, senha_hash, tipo_usuario)
VALUES ('393fb0c2-ffd4-4a60-a15f-1744806a00b0', 'Churrascaria Gaúcha', 'churrasco@delivery.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'RESTAURANTE');

INSERT INTO restaurantes (usuario_id, cnpj, telefone, status_ativo, categoria_global_id)
VALUES ('393fb0c2-ffd4-4a60-a15f-1744806a00b0', '55667788000122', '44999990004', TRUE, 'd37cf5ce-1536-4f76-a112-19c3a9c97440');

INSERT INTO categorias_cardapio (id, nome, descricao, restaurante_id)
VALUES ('1ad03492-1c14-4d44-a8c2-6ee31d077bcc', 'Cortes Bovinos', 'Carnes bovinas selecionadas',  '393fb0c2-ffd4-4a60-a15f-1744806a00b0'),
       ('6e600cb3-5acc-4b96-bc1d-74fc6609c97e', 'Cortes Suínos',  'Suínos e embutidos',           '393fb0c2-ffd4-4a60-a15f-1744806a00b0'),
       ('7f104980-93f9-42b5-90c6-ef2d380b501a', 'Acompanhamentos','Guarnições do churrasco',      '393fb0c2-ffd4-4a60-a15f-1744806a00b0');

INSERT INTO produtos (id, nome, descricao, preco, status_ativo, restaurante_id, categoria_cardapio_id)
VALUES ('4de9e2b5-cc3f-4a20-babc-7888d19821ce', 'Picanha (300g)',       'Picanha grelhada no carvão com sal grosso',       69.90, TRUE, '393fb0c2-ffd4-4a60-a15f-1744806a00b0', '1ad03492-1c14-4d44-a8c2-6ee31d077bcc'),
       ('e509041f-0030-4d5a-9c1f-4a510d604c07', 'Fraldinha (300g)',     'Fraldinha suculenta ao ponto',                    55.90, TRUE, '393fb0c2-ffd4-4a60-a15f-1744806a00b0', '1ad03492-1c14-4d44-a8c2-6ee31d077bcc'),
       ('5da17060-3e04-4b67-8008-a76d6bc51926', 'Contrafilé (300g)',    'Contrafilé grelhado com manteiga e alho',         49.90, TRUE, '393fb0c2-ffd4-4a60-a15f-1744806a00b0', '1ad03492-1c14-4d44-a8c2-6ee31d077bcc'),
       ('a8e20987-367c-44e5-8c06-aa1fe1c30a01', 'Costela Suína (400g)', 'Costela suína defumada lentamente',               52.90, TRUE, '393fb0c2-ffd4-4a60-a15f-1744806a00b0', '6e600cb3-5acc-4b96-bc1d-74fc6609c97e'),
       ('687881cb-ba5a-4b9f-8cab-c37ab32ebea2', 'Linguiça Toscana',     'Par de linguiças toscanas grelhadas',             28.90, TRUE, '393fb0c2-ffd4-4a60-a15f-1744806a00b0', '6e600cb3-5acc-4b96-bc1d-74fc6609c97e'),
       ('ea1c1aa6-c8ae-4c36-bae8-92790f1118b4', 'Arroz Carreteiro',     'Arroz com charque e temperos da casa',            24.90, TRUE, '393fb0c2-ffd4-4a60-a15f-1744806a00b0', '7f104980-93f9-42b5-90c6-ef2d380b501a'),
       ('6484916d-ea57-4f9d-935b-c6de3156d1a1', 'Farofa da Casa',       'Farofa especial com bacon e ovos',                18.90, TRUE, '393fb0c2-ffd4-4a60-a15f-1744806a00b0', '7f104980-93f9-42b5-90c6-ef2d380b501a');

INSERT INTO areas_entrega (id, restaurante_id, bairro, distancia_maxima_km, taxa_entrega, previsao_entrega_minutos)
VALUES ('79013e7f-a24a-43b5-8827-d73e6af8e898', '393fb0c2-ffd4-4a60-a15f-1744806a00b0', 'Centro',   3.0,  5.00, 35),
       ('82296f68-8b34-4313-be5d-ac1aa209518e', '393fb0c2-ffd4-4a60-a15f-1744806a00b0', 'Vila Nova', 5.0,  7.00, 45),
       ('dc470c6f-1941-47b7-8ebe-281f96d8eaea', '393fb0c2-ffd4-4a60-a15f-1744806a00b0', 'Zona Sul',  7.5, 10.00, 55),
       ('c3270c5b-a41d-4642-a590-7d8479d41b6f', '393fb0c2-ffd4-4a60-a15f-1744806a00b0', 'Colônia',   9.0, 12.00, 65);

-- ============================================================
-- RESTAURANTE 5 — Marmitaria do Beto (Comida Caseira)
-- ============================================================
INSERT INTO usuarios (id, nome, email, senha_hash, tipo_usuario)
VALUES ('eb9c7e09-efec-4b2f-a072-46a6d2f2bc9c', 'Marmitaria do Beto', 'beto@delivery.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'RESTAURANTE');

INSERT INTO restaurantes (usuario_id, cnpj, telefone, status_ativo, categoria_global_id)
VALUES ('eb9c7e09-efec-4b2f-a072-46a6d2f2bc9c', '99887766000133', '44999990005', TRUE, '8bb557eb-9de6-4138-a7c0-8860b0812d8c');

INSERT INTO categorias_cardapio (id, nome, descricao, restaurante_id)
VALUES ('d0a6470c-53ec-442a-a73a-c851035ad32c', 'Marmitas',          'Marmitas individuais e família',    'eb9c7e09-efec-4b2f-a072-46a6d2f2bc9c'),
       ('a51a5969-44d6-401b-bfac-fc9092433b4d', 'Pratos Executivos', 'Pratos completos para o almoço',   'eb9c7e09-efec-4b2f-a072-46a6d2f2bc9c');

INSERT INTO produtos (id, nome, descricao, preco, status_ativo, restaurante_id, categoria_cardapio_id)
VALUES ('29b23a0e-a1d7-4a82-8adc-8c56beddf5bd', 'Marmita Pequena',             'Arroz, feijão, salada e 1 proteína',              18.90, TRUE, 'eb9c7e09-efec-4b2f-a072-46a6d2f2bc9c', 'd0a6470c-53ec-442a-a73a-c851035ad32c'),
       ('bc2d776f-7a55-43f3-9cf2-ed521e5e6ffb', 'Marmita Grande',              'Arroz, feijão, salada e 2 proteínas',             24.90, TRUE, 'eb9c7e09-efec-4b2f-a072-46a6d2f2bc9c', 'd0a6470c-53ec-442a-a73a-c851035ad32c'),
       ('99738cf7-a3fd-494f-9f9d-2a1d5159d1c1', 'Marmita Família (4 pessoas)', 'Porção para 4 pessoas com 3 proteínas',           69.90, TRUE, 'eb9c7e09-efec-4b2f-a072-46a6d2f2bc9c', 'd0a6470c-53ec-442a-a73a-c851035ad32c'),
       ('914d325c-46bd-48a1-9cac-a410db02c854', 'Executivo Frango Grelhado',   'Frango grelhado, arroz, feijão e salada',         22.90, TRUE, 'eb9c7e09-efec-4b2f-a072-46a6d2f2bc9c', 'a51a5969-44d6-401b-bfac-fc9092433b4d'),
       ('6487438d-2cff-4f7c-9ec6-b04ec7b68902', 'Executivo Bife Acebolado',    'Bife acebolado, arroz, feijão e salada',          25.90, TRUE, 'eb9c7e09-efec-4b2f-a072-46a6d2f2bc9c', 'a51a5969-44d6-401b-bfac-fc9092433b4d'),
       ('2910a26b-7067-446d-9bd6-4d99b17b03fd', 'Executivo Peixe Grelhado',    'Tilápia grelhada, arroz, feijão e salada',        26.90, TRUE, 'eb9c7e09-efec-4b2f-a072-46a6d2f2bc9c', 'a51a5969-44d6-401b-bfac-fc9092433b4d');

INSERT INTO areas_entrega (id, restaurante_id, bairro, distancia_maxima_km, taxa_entrega, previsao_entrega_minutos)
VALUES ('6ee8854d-53b4-45f1-ad19-7ebe5eb2b82d', 'eb9c7e09-efec-4b2f-a072-46a6d2f2bc9c', 'Centro',          2.5, 3.00, 25),
       ('339378af-c9fe-4cf1-b606-d62066213841', 'eb9c7e09-efec-4b2f-a072-46a6d2f2bc9c', 'Zona Norte',      4.0, 5.00, 35),
       ('bb3ef6a3-b232-4f4e-b2d5-5f8fe0ddddab', 'eb9c7e09-efec-4b2f-a072-46a6d2f2bc9c', 'Jardim Alvorada', 6.0, 7.00, 45);
