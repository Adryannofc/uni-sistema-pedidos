# Sistema de Delivery

> Projeto universitário — Clean Architecture com JPA/Hibernate e PostgreSQL

Plataforma de delivery completa que cobre o ciclo inteiro de um pedido: cadastro de restaurante, aprovação pelo admin, navegação pelo cliente, finalização e acompanhamento até a entrega. Construída em **Java puro sem Spring**, aplicando Clean Architecture com persistência real em PostgreSQL via Hibernate e Flyway.

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Hibernate](https://img.shields.io/badge/Hibernate-6.4-59666C?logo=hibernate)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-336791?logo=postgresql)
![Flyway](https://img.shields.io/badge/Flyway-12.3-CC0200?logo=flyway)
![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apachemaven)

---

## Sumário

- [Visão Geral](#visão-geral)
- [Funcionalidades por Perfil](#funcionalidades-por-perfil)
- [Tecnologias e Decisões Técnicas](#tecnologias-e-decisões-técnicas)
- [Arquitetura](#arquitetura)
- [Estrutura de Pacotes](#estrutura-de-pacotes)
- [Modelo de Domínio](#modelo-de-domínio)
- [Fluxo de Status do Pedido](#fluxo-de-status-do-pedido)
- [Banco de Dados](#banco-de-dados)
- [Como Executar](#como-executar)
- [Dados de Seed](#dados-de-seed)
- [Regras de Negócio](#regras-de-negócio)
- [Equipe](#equipe)

---

## Visão Geral

O sistema gerencia três perfis de usuário — **Administrador**, **Restaurante** e **Cliente** — com autenticação por e-mail e senha (SHA-256), controle de acesso por tipo e persistência em PostgreSQL via JPA/Hibernate.

A aplicação oferece **dois modos de interface** sobre a mesma lógica de negócio:

- **Terminal (Console):** menus interativos com box-drawing Unicode, entrada validada, fluxo navegável por teclado
- **Swing GUI:** interface gráfica desktop com frames e painéis por perfil, gerenciamento de sessão visual

O banco de dados é versionado com Flyway: as migrations criam o schema, carregam dados de seed e definem triggers — o Hibernate só valida.

---

## Funcionalidades por Perfil

### Administrador

- Listar todos os restaurantes com status de ativação
- Aprovar restaurantes (torná-los visíveis para clientes)
- Bloquear restaurantes (desativação temporária)
- Remover restaurantes permanentemente
- Gerenciar categorias globais do sistema (criar, editar, remover)
- Alterar senha com verificação da senha atual
- Visualizar perfil

### Restaurante

- Gerenciar produtos: criar, editar, ativar/inativar, remover
- Gerenciar categorias de cardápio próprias
- Configurar horários de funcionamento por dia da semana
- Configurar áreas de entrega: bairro, taxa, distância máxima, previsão em minutos
- Gerenciar pedidos recebidos e atualizar status ao longo do ciclo de vida
- Editar perfil: nome, CNPJ, telefone
- Alterar e-mail com verificação de unicidade global
- Atribuir categoria global ao restaurante
- Alterar senha com validação

### Cliente

- Visualizar restaurantes ativos com categoria e status
- Navegar pelo cardápio de um restaurante
- Gerenciar carrinho: adicionar, remover itens e esvaziar
- Gerenciar múltiplos endereços de entrega (um como padrão)
- Finalizar pedido com endereço selecionado e resumo de valores
- Acompanhar histórico de pedidos com status atual e total
- Confirmar entrega com código de confirmação
- Marcar/desmarcar restaurantes como favoritos
- Editar perfil: nome, e-mail, CPF, telefone
- Alterar senha com validação

---

## Tecnologias e Decisões Técnicas

| Componente | Tecnologia | Versão | Motivo |
|---|---|---|---|
| Linguagem | Java | 17 | Requisito da disciplina; LTS moderno com records e sealed classes |
| ORM | Hibernate (JPA 3.0) | 6.4.4 | Mapeamento objeto-relacional sem boilerplate JDBC manual |
| Banco de dados | PostgreSQL | 14+ | Banco relacional maduro; suporte a UUID nativo e triggers |
| Migrations | Flyway | 12.3.0 | Versionamento de schema reproduzível; Hibernate só valida |
| Build | Maven | 3.x | Gerenciamento de dependências e execução padronizada |
| Hash de senha | SHA-256 (`MessageDigest`) | — | Sem dependência externa; hash determinístico por email |
| GUI | Java Swing | built-in | Interface desktop sem libs externas; paralela ao terminal |
| IDs | `UUID.randomUUID()` | — | Unicidade global sem sequência de banco |
| DI | Manual em `Main.java` | — | Composição explícita sem container; facilita leitura do grafo |

---

## Arquitetura

O projeto segue os princípios da **Clean Architecture**, com dependências apontando sempre para dentro (em direção ao domínio):

```
┌─────────────────────────────────────────────┐
│        Presentation                          │
│   Terminal (menus) + Swing GUI (frames)      │
└──────────────────────┬──────────────────────┘
                       │ depende de
┌──────────────────────▼──────────────────────┐
│        Application                           │
│   Services (orquestração de regras)          │
└──────────────────────┬──────────────────────┘
                       │ depende de
┌──────────────────────▼──────────────────────┐
│        Domain                                │
│   Entities · Enums · Repository interfaces  │
└──────────────────────▲──────────────────────┘
                       │ implementa
┌──────────────────────┴──────────────────────┐
│        Infrastructure                        │
│   JPA Repositories · Flyway · JPAUtil        │
└─────────────────────────────────────────────┘
```

- **Domain**: regras de negócio puras, zero dependências externas
- **Application**: serviços que orquestram as regras; dependem apenas de interfaces do Domain
- **Infrastructure**: implementações concretas (JPA/Hibernate); conhece o banco, o Domain não
- **Presentation**: entrada do usuário e formatação de saída; não contém lógica de negócio

Para trocar a persistência (ex.: de JPA para JDBC puro), basta implementar as interfaces de `domain/repository` e atualizar o binding em `Main.java`. Nenhuma regra de negócio muda.

---

## Estrutura de Pacotes

```
src/main/java/com/pedidos/
│
├── Main.java                          # Ponto de entrada — modo terminal
├── MainSwing.java                     # Ponto de entrada — modo Swing GUI
│
├── domain/
│   ├── model/
│   │   ├── Usuario.java               # Entidade base abstrata (JOINED inheritance)
│   │   ├── Admin.java
│   │   ├── Cliente.java               # CPF, telefone, enderecos, favoritos
│   │   ├── Restaurante.java           # CNPJ, statusAtivo, horarios, areas, produtos
│   │   ├── Produto.java               # Preço, statusAtivo, categoriaCardapioId
│   │   ├── Pedido.java                # Status, total, taxaEntrega, codigoConfirmacao
│   │   ├── ItemPedido.java            # Snapshot do produto no momento do pedido
│   │   ├── Carrinho.java              # Sessão do cliente — não persiste
│   │   ├── Endereco.java              # Múltiplos por cliente, um como padrão
│   │   ├── HorarioFuncionamento.java  # Por dia da semana por restaurante
│   │   ├── AreaEntrega.java           # Bairro, taxa, distância, previsão
│   │   ├── CategoriaGlobal.java       # Criada pelo Admin
│   │   └── CategoriaCardapio.java     # Criada pelo Restaurante, vinculada a Produtos
│   ├── repository/                    # Contratos (interfaces)
│   │   ├── AdminRepository.java
│   │   ├── ClienteRepository.java
│   │   ├── RestauranteRepository.java
│   │   ├── ProdutoRepository.java
│   │   ├── PedidoRepository.java
│   │   ├── CategoriaGlobalRepository.java
│   │   ├── CategoriaCardapioRepository.java
│   │   ├── EnderecoRepository.java
│   │   ├── HorarioFuncionamentoRepository.java
│   │   └── AreaEntregaRepository.java
│   └── enums/
│       ├── StatusPedido.java
│       └── TipoUsuario.java
│
├── application/
│   └── service/
│       ├── AutenticacaoService.java   # SHA-256 + login multi-perfil
│       ├── AdminService.java
│       ├── ClienteService.java
│       ├── RestauranteService.java
│       ├── ProdutoService.java
│       ├── CategoriaService.java
│       ├── PedidoService.java         # Máquina de estados + validação de horário
│       ├── CarrinhoService.java       # Carrinho de sessão
│       ├── EnderecoService.java
│       ├── HorarioService.java
│       └── AreaEntregaService.java
│
├── infra/
│   ├── config/
│   │   ├── JPAUtil.java               # Singleton EntityManagerFactory
│   │   └── FlyWayConfig.java          # Executa migrations ao iniciar
│   └── repository/jpa/
│       ├── AdminRepositoryJPA.java
│       ├── ClienteRepositoryJPA.java
│       ├── RestauranteRepositoryJPA.java
│       ├── ProdutoRepositoryJPA.java
│       ├── PedidoRepositoryJPA.java
│       ├── CategoriaGlobalRepositoryJPA.java
│       ├── CategoriaCardapioRepositoryJPA.java
│       ├── EnderecoRepositoryJPA.java
│       ├── HorarioFuncionamentoRepositoryJPA.java
│       └── AreaEntregaRepositoryJPA.java
│
├── presentation/                      # UI Terminal
│   ├── menu/
│   │   └── MenuLogin.java
│   ├── admin/
│   │   ├── MenuAdmin.java
│   │   └── MenuCategorias.java
│   ├── restaurante/
│   │   ├── MenuRestaurante.java
│   │   ├── MenuProdutos.java
│   │   ├── MenuCategoriasCardapio.java
│   │   ├── MenuAreaEntrega.java
│   │   └── MenuHorarios.java
│   ├── cliente/
│   │   └── MenuCliente.java
│   └── util/
│       ├── TerminalUtils.java         # Caixas Unicode, cores, pausas
│       └── EntradaSegura.java         # Leitura validada de int e String
│
└── view/                              # UI Swing GUI
    ├── LoginFrame.java
    ├── CadastroFrame.java
    ├── AdminFrame.java
    ├── RestauranteFrame.java
    ├── ClienteFrame.java
    ├── painel/
    │   ├── PainelProdutos.java
    │   ├── PainelPedidos.java
    │   ├── PainelAreaEntrega.java
    │   ├── PainelHorarios.java
    │   └── PainelCategorias.java
    └── util/
        ├── SessionManager.java        # Usuário logado na sessão Swing
        ├── CarrinhoManager.java       # Carrinho na sessão Swing
        ├── AppColors.java
        └── AppFonts.java

src/main/resources/
├── META-INF/
│   └── persistence.xml               # Configuração JPA (Hibernate + PostgreSQL)
└── db/migration/
    ├── V1__create_database.sql
    ├── V2__create_schema.sql
    ├── V3__crud_dml.sql
    ├── V4__join_tables.sql
    ├── V5__triggers.sql
    ├── V6__multiple_addresses.sql
    ├── V7__seed_restaurants.sql
    ├── V8__seed_products.sql
    ├── V9__seed_clients.sql
    └── V10__seed_delivery_areas.sql
```

---

## Modelo de Domínio

```
Usuario (abstract — estratégia JOINED)
├── Admin
├── Cliente        — cpf (único), telefone
│   ├── enderecos[]           (1:N, cascade delete)
│   └── favoritos[]           (N:N → Restaurante)
└── Restaurante    — cnpj (único), telefone, statusAtivo
    ├── categoriaGlobal       (N:1 → CategoriaGlobal)
    ├── categoriasCardapio[]  (1:N)
    ├── produtos[]            (1:N)
    ├── horarios[]            (1:N → HorarioFuncionamento)
    └── areasEntrega[]        (1:N → AreaEntrega)

CategoriaGlobal     — criada pelo Admin; vinculada ao Restaurante
CategoriaCardapio   — criada pelo Restaurante; vinculada a Produtos

Produto             — preço (BigDecimal), statusAtivo, restauranteId, categoriaCardapioId
Endereco            — rua, número, bairro, cidade, estado, cep, isPadrao
HorarioFuncionamento— diaSemana (DayOfWeek), horaInicio, horaFim
AreaEntrega         — bairro, distanciaKm, taxaEntrega, previsaoMinutos

Carrinho            — sessão do cliente, NÃO persiste
Pedido              — total calculado, taxaEntrega, codigoConfirmacao, status
└── itens[]         (1:N → ItemPedido, cascade + orphan removal)

ItemPedido          — snapshot: nomeProduto + precoUnitario no momento do pedido
```

> **Carrinho é efêmero.** Existe apenas em memória durante a sessão. O `Pedido` é a entidade persistida — por isso `ItemPedido` armazena o nome e o preço no momento da compra, protegendo o histórico contra edições futuras do produto.

---

## Fluxo de Status do Pedido

```
AGUARDANDO_CONFIRMACAO
       │
       ├──► CONFIRMADO
       │         │
       │         ├──► EM_PREPARO
       │         │         │
       │         │         ├──► SAIU_PARA_ENTREGA
       │         │         │             │
       │         │         │             └──► ENTREGUE  (terminal ✓)
       │         │         │
       │         │         └──► CANCELADO (terminal ✓)
       │         │
       │         └──► CANCELADO (terminal ✓)
       │
       └──► CANCELADO (terminal ✓)
```

- Transições inválidas lançam `IllegalStateException`
- Estados `ENTREGUE` e `CANCELADO` são imutáveis
- Confirmação de entrega exige o `codigoConfirmacao` gerado no pedido
- Pedido só é aceito se o restaurante estiver aberto no horário da solicitação (validado em `PedidoService` contra `HorarioFuncionamento`)

---

## Banco de Dados

**Engine:** PostgreSQL 14+  
**Migrations:** Flyway 12.3 (modo `validate` no Hibernate — o schema é gerenciado pelo Flyway)  
**Configuração:** `src/main/resources/META-INF/persistence.xml`

```
URL:      jdbc:postgresql://localhost:5432/deliveryapp
Usuário:  postgres
Senha:    postgres
```

### Migrations

| Versão | Descrição |
|---|---|
| V1 | Criação do banco |
| V2 | Schema principal (todas as tabelas e constraints) |
| V3 | Operações CRUD e DML iniciais |
| V4 | Tabelas de junção (ex.: `cliente_restaurantes_favoritos`) |
| V5 | Triggers (ex.: impede fechamento de restaurante com pedido em trânsito; bloqueia produto inativo em pedido) |
| V6 | Suporte a múltiplos endereços por cliente com flag `padrao` |
| V7 | Seed: restaurantes e categorias |
| V8 | Seed: produtos por restaurante |
| V9 | Seed: clientes e endereços |
| V10 | Seed: áreas de entrega por restaurante |

### Tabelas principais

`usuarios` · `admins` · `clientes` · `restaurantes` · `produtos` · `pedidos` · `itens_pedido` · `enderecos` · `horarios_funcionamento` · `areas_entrega` · `categorias_globais` · `categorias_cardapio` · `cliente_restaurantes_favoritos` · `log_status_pedido`

---

## Como Executar

### Pré-requisitos

- JDK 17 ou superior
- PostgreSQL 14+ rodando localmente
- Maven 3.x

### 1. Criar o banco

```sql
CREATE DATABASE deliveryapp;
```

A URL, usuário e senha podem ser ajustados em `src/main/resources/META-INF/persistence.xml`. O Flyway executa todas as migrations automaticamente na primeira inicialização.

### 2. Executar

**Modo Terminal (Console):**

```bash
mvn compile exec:java -Dexec.mainClass="com.pedidos.Main"
```

**Modo Swing GUI:**

```bash
mvn compile exec:java -Dexec.mainClass="com.pedidos.MainSwing"
```

### 3. Compilar sem Maven (opcional)

```bash
javac -d out -sourcepath src/main/java \
  src/main/java/com/pedidos/Main.java

java -cp "out:lib/*" com.pedidos.Main
```

> Garanta que os JARs de Hibernate, PostgreSQL JDBC e Flyway estejam no classpath.

---

## Dados de Seed

O Flyway popula automaticamente o banco na primeira execução. Use as credenciais abaixo para testar cada perfil:

### Contas de acesso

| Perfil | E-mail | Senha |
|---|---|---|
| Administrador | `admin@delivery.com` | `admin123` |
| Burguer House | `burguer@delivery.com` | `rest123` |
| Pizzaria Bella | `pizza@delivery.com` | `rest123` |
| Sushi Zen | `sushi@delivery.com` | `rest123` |
| Churrascaria do Boi | `churrasco@delivery.com` | `rest123` |
| Marmitaria do Beto | `beto@delivery.com` | `rest123` |
| João Silva | `joao@email.com` | `cliente123` |
| Maria Souza | `maria@email.com` | `cliente123` |
| Adryann (dev) | `adryann@email.com` | `cliente123` |

### Dados pré-carregados

- 5 categorias globais: Lanches, Pizza, Japonesa, Churrasco, Comida Caseira
- 5 restaurantes ativos com produtos, horários e áreas de entrega configurados
- 40+ produtos distribuídos entre os restaurantes
- 30+ áreas de entrega com taxas e previsões variadas
- 3 clientes com endereços cadastrados

---

## Regras de Negócio

1. **E-mail único global** — verificado em Admin, Restaurante e Cliente antes de aceitar cadastro ou edição
2. **Restaurantes precisam de aprovação** — novos restaurantes entram com `statusAtivo = false`; apenas o Admin pode aprovar
3. **Produto inativo bloqueia pedido** — trigger no banco e validação no serviço impedem adição ao carrinho
4. **Um restaurante por carrinho** — tentativa de misturar restaurantes exige esvaziar o carrinho primeiro
5. **Pedido só abre se restaurante estiver aberto** — `PedidoService` consulta `HorarioFuncionamento` antes de criar o pedido
6. **Máquina de estados imutável** — transições fora do fluxo lançam `IllegalStateException`; estados terminais (ENTREGUE/CANCELADO) não aceitam mais atualizações
7. **Entrega requer código de confirmação** — gerado no pedido e verificado pelo cliente na confirmação
8. **Endereço padrão único** — constraint no banco garante apenas um `isPadrao = true` por cliente
9. **Categoria em uso não pode ser removida** — `CategoriaService` bloqueia exclusão se houver restaurante ou produto vinculado
10. **CNPJ normalizado** — aceita entrada formatada (`XX.XXX.XXX/XXXX-XX`) ou crua (14 dígitos); armazena apenas dígitos

---

## Equipe

Projeto desenvolvido em grupo para a disciplina de **Análise e Desenvolvimento de Sistemas** — Uniamerica.

| Integrante | GitHub |
|---|---|
| Adryann | [@Adryannofc](https://github.com/Adryannofc) |
| Luan | — |
| Guilherme | — |
