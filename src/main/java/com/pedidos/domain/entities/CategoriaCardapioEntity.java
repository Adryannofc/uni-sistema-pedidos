package com.pedidos.domain.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "categorias_cardapio")
public class CategoriaCardapioEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private RestauranteEntity restauranteEntity;

    protected CategoriaCardapioEntity() {}

    public CategoriaCardapioEntity(String nome, String descricao, String restauranteId) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getId()        { return id; }
    public String getNome()      { return nome; }
    public String getDescricao() { return descricao; }

    public RestauranteEntity getRestaurante() { return restauranteEntity; }
    public void setRestaurante(RestauranteEntity restauranteEntity) { this.restauranteEntity = restauranteEntity; }

    // Mantido para compatibilidade com CategoriaService e apresentação
    public String getRestauranteId() {
        return restauranteEntity != null ? restauranteEntity.getId() : null;
    }

    @Deprecated
    public void setRestauranteId(String id) {}

    public void setNome(String nome)           { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}