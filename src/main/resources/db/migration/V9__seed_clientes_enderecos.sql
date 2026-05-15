-- ================================================================
-- 1. DEV USER: adryann@email.com (necessário para MainCliente.java)
-- ================================================================
INSERT INTO usuarios (id, nome, email, senha_hash, tipo_usuario)
VALUES ('d1a2b3c4-0000-0000-0000-000000000001',
        'Adryann',
        'adryann@email.com',
        '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
        'CLIENTE')
    ON CONFLICT (email) DO NOTHING;

INSERT INTO clientes (usuario_id, cpf, telefone)
VALUES ('d1a2b3c4-0000-0000-0000-000000000001', '11122233344', '44991110001')
    ON CONFLICT (cpf) DO NOTHING;  -- ← era (usuario_id)

INSERT INTO enderecos (id, rua, numero, bairro, cidade, estado, cep, cliente_id, padrao)
VALUES ('aaaa0001-0000-0000-0000-000000000001',
        'Rua do Desenvolvedor', '1', 'Vila Tech', 'Cascavel', 'PR', '85800000',
        'd1a2b3c4-0000-0000-0000-000000000001', TRUE)
    ON CONFLICT (id) DO NOTHING;

-- ================================================================
-- 2. ENDEREÇOS ADICIONAIS - João Silva
-- ================================================================
INSERT INTO enderecos (id, rua, numero, bairro, cidade, estado, cep, cliente_id, padrao)
VALUES ('bbbb0001-0000-0000-0000-000000000001',
        'Av. Brasil', '500', 'Zona 01', 'Maringá', 'PR', '87013001',
        'f856e493-4d87-461d-ad06-f0c34ee5838f', FALSE),
       ('bbbb0002-0000-0000-0000-000000000002',
        'Rua Pioneiro', '220', 'Zona Industrial', 'Maringá', 'PR', '87065000',
        'f856e493-4d87-461d-ad06-f0c34ee5838f', FALSE)
    ON CONFLICT (id) DO NOTHING;

-- ================================================================
-- 3. ENDEREÇOS ADICIONAIS - Maria Souza
-- ================================================================
INSERT INTO enderecos (id, rua, numero, bairro, cidade, estado, cep, cliente_id, padrao)
VALUES ('cccc0001-0000-0000-0000-000000000001',
        'Rua Mato Grosso', '80', 'Centro', 'Londrina', 'PR', '86010090',
        '80dcde6a-1392-4f68-b096-e26421163750', FALSE),
       ('cccc0002-0000-0000-0000-000000000002',
        'Av. Higienópolis', '999', 'Higienópolis', 'Londrina', 'PR', '86020080',
        '80dcde6a-1392-4f68-b096-e26421163750', FALSE)
    ON CONFLICT (id) DO NOTHING;