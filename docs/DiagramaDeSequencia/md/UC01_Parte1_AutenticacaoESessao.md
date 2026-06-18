sequenceDiagram
    actor CLI as Cliente
    participant LF as LoginFrame
    participant AUTH_C as AutenticacaoController
    participant AUTH_S as AutenticacaoService
    participant SM as SessionManager
    participant CFR as ClienteFrame
    participant DB as PostgreSQL

    rect rgb(220, 235, 255)
        Note right of CLI: FASE 1 - AUTENTICACAO E SESSAO
        CLI->>+LF: abre aplicacao
        LF-->>-CLI: exibe formulario de login
        CLI->>+LF: informa email + senha
        LF->>+AUTH_C: autenticar(email, senha)
        AUTH_C->>+AUTH_S: autenticar(email, senha)
        AUTH_S->>AUTH_S: hashSenha SHA-256
        AUTH_S->>+DB: SELECT usuario WHERE email e hash
        DB-->>-AUTH_S: Usuario tipo CLIENTE
        AUTH_S-->>-AUTH_C: Cliente
        AUTH_C-->>-LF: Cliente
        LF->>+SM: criarSessao(UsuarioSessaoDTO)
        SM-->>-LF: sessao armazenada em memoria
        LF->>CFR: abrir ClienteFrame
        deactivate LF
    end
