
-- Bloquear cancelamento de pedido SAIU_PARA_ENTREGA

CREATE OR REPLACE FUNCTION fn_bloquear_cancelamento()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.status = 'SAIU_PARA_ENTREGA' AND NEW.status = 'CANCELADO' THEN
        RAISE EXCEPTION 'Pedido não pode ser cancelado após sair para entrega';
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_bloquear_cancelamento
    BEFORE UPDATE ON pedidos
    FOR EACH ROW
    EXECUTE FUNCTION fn_bloquear_cancelamento();



-- Impedir item com produto inativo

CREATE OR REPLACE FUNCTION fn_impedir_produto_inativo()
RETURNS TRIGGER AS $$
DECLARE
ativo BOOLEAN;
BEGIN
SELECT status_ativo INTO ativo
FROM produtos
WHERE id = NEW.produto_id;

IF ativo IS FALSE THEN
        RAISE EXCEPTION 'Não é permitido adicionar produto inativo ao pedido';
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_impedir_produto_inativo
    BEFORE INSERT ON itens_pedido
    FOR EACH ROW
    EXECUTE FUNCTION fn_impedir_produto_inativo();



-- Auditoria de status

CREATE TABLE IF NOT EXISTS log_status_pedido (
     id SERIAL PRIMARY KEY,
     pedido_id INT NOT NULL,
     status_anterior VARCHAR(50),
     status_novo VARCHAR(50),
     alterado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE OR REPLACE FUNCTION fn_log_status_pedido()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.status IS DISTINCT FROM NEW.status THEN
        INSERT INTO log_status_pedido (
            pedido_id,
            status_anterior,
            status_novo,
            alterado_em
        ) VALUES (
            OLD.id,
            OLD.status,
            NEW.status,
            CURRENT_TIMESTAMP
        );
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_log_status_pedido
    AFTER UPDATE ON pedidos
    FOR EACH ROW
    EXECUTE FUNCTION fn_log_status_pedido();