package com.pedidos.domain.model;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.persistence.*;


@Entity
@Table (name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final String id;

    @Column (name = "nome")
    private String nome;

    @Column (name = "descricao")
    private String descricao;

    @Column (name = "preco", precision = 10, scale = 2)
    private BigDecimal preco;

    @ManyToOne
    @JoinColumn (name = "cateoriaCardapio_Id")
    private String categoriaCardapioId;

    @ManyToOne
    @JoinColumn (name = "restaurante_Id")
    private String restauranteId;

    @Column (name = "status_Ativo")
    private boolean statusAtivo;

    // Construtor completo — carregado do banco (UUID já existente)
    public Produto(String nome, String descricao, BigDecimal preco, String categoriaCardapioId, String restauranteId) {
        this.id = UUID.randomUUID().toString();
        setNome(nome);
        setDescricao(descricao);
        setPreco(preco);
        this.categoriaCardapioId = categoriaCardapioId;
        this.restauranteId = restauranteId;
        this.statusAtivo = true; // começa ativo por padrão
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do produto não pode ser nulo ou vazio.");
        }
        this.nome = nome.trim();
    }

    public String getId() {
        return id;
    }

    public String getCategoriaCardapioId() {
        return categoriaCardapioId;
    }

    public void setCategoriaCardapioId(String categoriaCardapioId) {
        this.categoriaCardapioId = categoriaCardapioId;
    }

    public String getRestauranteId() {
        return restauranteId;
    }

    public boolean isStatusAtivo() {
        return statusAtivo;
    }

    public void setStatusAtivo(boolean statusAtivo) {
        this.statusAtivo = statusAtivo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero.");
        }
        this.preco = preco;
    }
}

