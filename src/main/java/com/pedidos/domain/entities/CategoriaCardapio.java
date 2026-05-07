package com.pedidos.domain.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "categorias_cardapio")
public class CategoriaCardapio {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    protected CategoriaCardapio() {}

    public CategoriaCardapio(String nome, String descricao, Restaurante restaurante) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.restaurante = restaurante;
    }

    public String getId()        { return id; }
    public String getNome()      { return nome; }
    public String getDescricao() { return descricao; }

    public Restaurante getRestaurante() { return restaurante; }
    public void setRestaurante(Restaurante restaurante) { this.restaurante = restaurante; }

    // Mantido para compatibilidade com CategoriaService e apresentação
    public String getRestauranteId() {
        return restaurante != null ? restaurante.getId() : null;
    }

    @Deprecated
    public void setRestauranteId(String id) {}

    public void setNome(String nome)           { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}