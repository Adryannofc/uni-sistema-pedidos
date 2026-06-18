sequenceDiagram
    actor CLI as Cliente
    participant CFR as ClienteFrame
    participant PFP as PainelFazerPedido
    participant REST_C as RestauranteController
    participant PCD as PainelCardapio
    participant CARR_C as CarrinhoController
    participant CARR_S as CarrinhoService
    participant DB as PostgreSQL

    rect rgb(255, 245, 200)
        Note right of CLI: FASE 2 - BUSCAR RESTAURANTES
        CLI->>+PFP: acessa aba Fazer Pedido
        PFP->>+REST_C: listarComoDTO()
        REST_C->>+DB: SELECT restaurantes WHERE status ATIVO
        DB-->>-REST_C: List Restaurante
        REST_C->>REST_C: mapear para RestauranteResumoDTO
        REST_C-->>-PFP: List RestauranteResumoDTO
        PFP-->>-CLI: exibe cards dos restaurantes ativos
    end

    rect rgb(220, 255, 220)
        Note right of CLI: FASE 3 - INICIAR CARRINHO
        CLI->>+PFP: clica Ver Cardapio Bella Napoli
        PFP->>+CARR_C: iniciar(clienteId, restauranteId, taxa)
        CARR_C->>+CARR_S: setClienteLogado(clienteId)
        CARR_S-->>-CARR_C: ok
        CARR_C->>+CARR_S: iniciarCarrinho(clienteId, restauranteId)
        CARR_S->>CARR_S: new Carrinho em carrinhosAtivos
        CARR_S-->>-CARR_C: Carrinho criado
        CARR_C->>+CARR_S: setTaxaEntrega(5.00)
        CARR_S-->>-CARR_C: ok
        CARR_C-->>-PFP: ok
        PFP->>PCD: exibir cardapio do restaurante
        deactivate PFP
    end

    rect rgb(255, 225, 225)
        Note right of CLI: FASE 4 - ADICIONAR ITENS AO CARRINHO
        CLI->>+PCD: seleciona Pizza Margherita qty 1
        PCD->>+CARR_C: adicionarItem(produto, 1)
        CARR_C->>+CARR_S: adicionarItem(produto, 1)
        CARR_S->>CARR_S: validar ativo e qty e restaurante
        CARR_S->>CARR_S: adicionarItem subtotal R$ 45,90
        CARR_S-->>-CARR_C: ok
        CARR_C-->>-PCD: carrinho atualizado
        PCD-->>-CLI: exibe subtotal atualizado
        CLI->>+PCD: seleciona Pizza Calabresa qty 2
        PCD->>+CARR_C: adicionarItem(produto, 2)
        CARR_C->>+CARR_S: adicionarItem(produto, 2)
        CARR_S->>CARR_S: validar e adicionar subtotal R$ 131,70
        CARR_S-->>-CARR_C: ok
        CARR_C-->>-PCD: carrinho atualizado
        PCD-->>-CLI: exibe subtotal atualizado
    end
