package com.pedidos.domain.entities;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.persistence.*;


@Entity
@Table (name = "produtos")
public class Produto {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column (name = "nome")
    private String nome;

    @Column (name = "descricao")
    private String descricao;

    @Column (name = "preco", precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "categoria_cardapio_id")
    private String categoriaCardapioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @Column (name = "status_Ativo")
    private boolean statusAtivo;

    protected Produto() {

    }

    public Produto(String nome, String descricao, BigDecimal preco, String categoriaCardapioId) {
        this.id = UUID.randomUUID().toString();
        setNome(nome);
        setDescricao(descricao);
        setPreco(preco);
        this.categoriaCardapioId = categoriaCardapioId;
        this.statusAtivo = true;
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
        return restaurante != null ? restaurante.getId() : null;
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
    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }
}