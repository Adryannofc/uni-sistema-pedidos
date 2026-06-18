sequenceDiagram
    actor CLI as Cliente
    participant PCD as PainelCardapio
    participant PCH as PainelCheckout
    participant CARR_C as CarrinhoController
    participant CARR_S as CarrinhoService
    participant END_C as EnderecoController
    participant PED_C as PedidoController
    participant PED_S as PedidoService
    participant HOR_R as HorarioFuncionamentoRepo
    participant PED_R as PedidoRepository
    participant CFR as ClienteFrame
    participant DB as PostgreSQL

    rect rgb(240, 225, 255)
        Note right of CLI: FASE 5 - CHECKOUT
        CLI->>+PCD: clica Finalizar Pedido
        PCD->>PCH: abrir PainelCheckout
        deactivate PCD
        activate PCH
        PCH->>+CARR_C: getItensComoDTO()
        CARR_C->>+CARR_S: getCarrinho()
        CARR_S-->>-CARR_C: Carrinho
        CARR_C-->>-PCH: List ItemCarrinhoDTO
        PCH->>+CARR_C: calcularTotal()
        CARR_C-->>-PCH: R$ 136,70
        PCH->>+END_C: listar(clienteId)
        END_C->>+DB: SELECT endereco WHERE padrao true
        DB-->>-END_C: Endereco Rua das Flores
        END_C-->>-PCH: List Endereco
        PCH->>CLI: exibe resumo itens + endereco + R$ 136,70
        CLI->>PCH: clica Confirmar Pedido
    end

    rect rgb(255, 240, 210)
        Note right of CLI: FASE 6 - CRIAR PEDIDO
        PCH->>+CARR_C: estaVazio()
        CARR_C-->>-PCH: false
        PCH->>+PED_C: criarPedido(cliente, restaurante, carrinho, endereco, taxa)
        PED_C->>+PED_S: criarPedido(...)
        PED_S->>PED_S: validar enderecoEntrega nao nulo
        PED_S->>+HOR_R: buscarPorRestaurante(restauranteId)
        HOR_R->>+DB: SELECT horarios WHERE restaurante_id
        DB-->>-HOR_R: HorarioFuncionamento TUESDAY 18h-23h
        HOR_R-->>-PED_S: List HorarioFuncionamento
        PED_S->>PED_S: isAberto 19h30 em TUESDAY OK
        PED_S->>PED_S: new Pedido cod K7X9 AGUARDANDO
        loop Para cada item do carrinho
            PED_S->>PED_S: adicionarItem snapshot nome+preco
        end
        PED_S->>PED_S: calcularTotal = R$ 136,70
        PED_S->>+PED_R: salvar(pedido)
        PED_R->>+DB: INSERT pedidos e itens cascade ALL
        DB-->>-PED_R: COMMIT OK
        PED_R-->>-PED_S: Pedido persistido
        PED_S-->>-PED_C: Pedido AGUARDANDO_CONFIRMACAO K7X9
        PED_C-->>-PCH: Pedido confirmado
    end

    rect rgb(210, 255, 245)
        Note right of CLI: FASE 7 - POS-PEDIDO
        PCH->>+CARR_C: esvaziar()
        CARR_C->>+CARR_S: encerrarCarrinho()
        CARR_S->>CARR_S: carrinhosAtivos remove clienteId
        CARR_S-->>-CARR_C: ok
        CARR_C-->>-PCH: ok
        PCH->>CLI: Pedido K7X9 confirmado R$ 136,70
        CLI->>PCH: clica OK
        PCH->>CFR: navegar para Meus Pedidos
        deactivate PCH
    end
