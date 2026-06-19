# Diagrama de Casos de Uso — Sistema de Pedidos

**Foco (comportamental):** identificar os atores e as principais funcionalidades que o
software permite, incluindo os relacionamentos `«include»` (obrigatório) e `«extend»`
(opcional/condicional).

**Atores:**
- **Cliente** — consome o sistema: navega, monta carrinho, faz e acompanha pedidos.
- **Restaurante** — gerencia cardápio, logística (horários/área de entrega) e pedidos recebidos.
- **Admin** — modera a plataforma: aprova/bloqueia restaurantes e mantém categorias globais.
- **Sistema** — comportamento automático (validações e cálculos disparados por outros casos).

> **Importar no Lucidchart / Lucidapp:** painel **Diagrama em código → Novo diagrama em
> Mermaid** e colar o bloco abaixo. (Mesmo fluxo dos demais diagramas em `docs/`.)

```mermaid
graph LR
    %% ===== ATORES =====
    CLIENTE["👤 Cliente"]:::ator
    RESTAURANTE["👤 Restaurante"]:::ator
    ADMIN["👤 Admin"]:::ator
    SISTEMA["⚙️ Sistema"]:::ator

    subgraph SP["SISTEMA DE PEDIDOS"]

        %% ----- Acesso (compartilhado) -----
        LOGIN(["Fazer Login"]):::shared
        CADASTRO(["Cadastrar-se"]):::shared

        %% ----- Cliente -----
        subgraph G_CLI["Área do Cliente"]
            VIS_REST(["Visualizar Restaurantes"]):::cli
            VIS_CARD(["Visualizar Cardápio"]):::cli
            GER_CARR(["Gerenciar Carrinho"]):::cli
            REALIZAR(["Realizar Pedido"]):::cli
            ACOMP(["Acompanhar Pedido"]):::cli
            CONFIRMA(["Confirmar Entrega"]):::cli
            HIST_CLI(["Ver Histórico de Pedidos"]):::cli
            PERFIL_CLI(["Gerenciar Perfil"]):::cli
            ENDERECOS(["Gerenciar Endereços"]):::cli
        end

        %% ----- Restaurante -----
        subgraph G_RES["Área do Restaurante"]
            GER_PROD(["Gerenciar Produtos"]):::res
            GER_CATC(["Gerenciar Categorias do Cardápio"]):::res
            CFG_HOR(["Configurar Horários"]):::res
            CFG_AREA(["Configurar Área de Entrega / Taxas"]):::res
            GER_PED(["Gerenciar Pedidos"]):::res
            ATU_STATUS(["Atualizar Status do Pedido"]):::res
            PERFIL_RES(["Gerenciar Perfil / Dados da Empresa"]):::res
        end

        %% ----- Admin -----
        subgraph G_ADM["Área do Admin"]
            GER_REST(["Gerenciar Restaurantes"]):::adm
            GER_CATG(["Gerenciar Categorias Globais"]):::adm
            PERFIL_ADM(["Gerenciar Perfil (Admin)"]):::adm
        end

        %% ----- Sistema (validações automáticas) -----
        subgraph G_SIS["Validações Automáticas"]
            VAL_HOR(["Validar Horário de Funcionamento"]):::sis
            CALC_TAXA(["Calcular Taxa de Entrega"]):::sis
            VAL_TRANS(["Validar Transição de Status"]):::sis
            VAL_PROD(["Validar Produto Ativo"]):::sis
        end
    end

    %% ===== ASSOCIAÇÕES ATOR -> CASO (linha sólida) =====
    CLIENTE --- LOGIN
    CLIENTE --- CADASTRO
    CLIENTE --- VIS_REST
    CLIENTE --- VIS_CARD
    CLIENTE --- GER_CARR
    CLIENTE --- REALIZAR
    CLIENTE --- ACOMP
    CLIENTE --- HIST_CLI
    CLIENTE --- PERFIL_CLI
    CLIENTE --- ENDERECOS

    RESTAURANTE --- LOGIN
    RESTAURANTE --- CADASTRO
    RESTAURANTE --- GER_PROD
    RESTAURANTE --- GER_CATC
    RESTAURANTE --- CFG_HOR
    RESTAURANTE --- CFG_AREA
    RESTAURANTE --- GER_PED
    RESTAURANTE --- PERFIL_RES

    ADMIN --- LOGIN
    ADMIN --- GER_REST
    ADMIN --- GER_CATG
    ADMIN --- PERFIL_ADM

    SISTEMA --- VAL_HOR
    SISTEMA --- CALC_TAXA
    SISTEMA --- VAL_TRANS
    SISTEMA --- VAL_PROD

    %% ===== «include» (tracejado) =====
    REALIZAR -.->|"«include»"| VAL_HOR
    REALIZAR -.->|"«include»"| CALC_TAXA
    GER_CARR -.->|"«include»"| VAL_PROD
    ATU_STATUS -.->|"«include»"| VAL_TRANS
    GER_PROD -.->|"«include»"| GER_CATC

    %% ===== «extend» (tracejado) =====
    CONFIRMA -.->|"«extend»"| ACOMP
    ATU_STATUS -.->|"«extend»"| GER_PED

    %% ===== ESTILOS (cores por ator) =====
    classDef ator fill:#2D3142,stroke:#1A1C28,color:#FFFFFF,font-weight:bold;
    classDef shared fill:#BFD7FF,stroke:#3A6EA5,color:#0B2545;
    classDef cli fill:#E4D9FF,stroke:#6C4AB6,color:#2A1A5E;
    classDef res fill:#FFE9A8,stroke:#C9962B,color:#5A4205;
    classDef adm fill:#BBDFFF,stroke:#2F6FB0,color:#0B2545;
    classDef sis fill:#CDEFD8,stroke:#3FA76A,color:#10401F;
```

---

## Legenda

| Elemento | Notação | Significado |
|---|---|---|
| **Ator** | retângulo escuro (`👤` / `⚙️`) | Usuário ou sistema externo que interage com os casos de uso. |
| **Caso de uso** | elipse-estádio `([ ... ])` | Funcionalidade observável que o sistema oferece. |
| **Fronteira do sistema** | caixa `SISTEMA DE PEDIDOS` | Delimita o que pertence ao software. |
| **Associação** | linha sólida `———` | O ator participa do caso de uso. |
| **«include»** | seta tracejada `-.->` | A base **sempre** executa o caso incluído (comportamento obrigatório). |
| **«extend»** | seta tracejada `-.->` | O caso estende a base **apenas sob condição** (comportamento opcional). |

**Cores por ator:** roxo = Cliente · amarelo = Restaurante · azul = Admin ·
verde = Sistema · azul-claro = casos de acesso compartilhados (Login/Cadastro).

---

## Notas de modelagem (rastreabilidade com o código)

### Relacionamentos «include» (obrigatórios)
- **Realizar Pedido → Validar Horário de Funcionamento** — `PedidoService.criarPedido()`
  chama `isRestauranteAberto()`, que rejeita o pedido fora do horário cadastrado.
- **Realizar Pedido → Calcular Taxa de Entrega** — o pedido sempre define a taxa
  (`setTaxaEntrega()`) e consolida o total (`calcularTotal()`).
- **Gerenciar Carrinho → Validar Produto Ativo** — `CarrinhoService` não permite adicionar
  produto inativo ao carrinho.
- **Atualizar Status do Pedido → Validar Transição de Status** — `PedidoService.atualizarStatus()`
  chama `validarTransicao()`, que impõe a máquina de estados do `StatusPedido`.
- **Gerenciar Produtos → Gerenciar Categorias do Cardápio** — não é possível cadastrar um
  produto sem uma categoria de cardápio previamente existente.

### Relacionamentos «extend» (condicionais)
- **Confirmar Entrega ⇢ Acompanhar Pedido** — a confirmação por código só é possível quando o
  pedido está em `SAIU_PARA_ENTREGA` (`PedidoService.confirmarEntrega()`).
- **Atualizar Status do Pedido ⇢ Gerenciar Pedidos** — ao gerenciar os pedidos recebidos, o
  restaurante **pode** avançar o status de um pedido ativo (ação opcional do fluxo).

### Observações
- **Login/Cadastro** aparecem como casos compartilhados associados diretamente aos atores
  (não como `«include»` em cada caso), para refletir o controle de acesso sem poluir o diagrama.
  O login do **Restaurante** ainda é condicionado ao status `ATIVO` (aprovação do Admin).
- O ator **Sistema** concentra os comportamentos automáticos (validações e cálculos) que são
  disparados por outros casos via `«include»`, e não diretamente por um usuário humano.
- O diagrama foi modelado como um grafo (`graph LR`) do Mermaid (que não possui tipo nativo
  de *use case*), emulando a notação UML — mesma técnica usada no diagrama de componentes
  deste repositório.
  Após importar no Lucidchart, é possível substituir os nós de ator pela forma nativa de
  "ator" (boneco-palito), se desejar maior fidelidade visual.
