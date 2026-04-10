package com.pedidos.domain.model;

import java.util.List;
import java.util.UUID;
import jakarta.persistence.*;


@Entity
@Table(name = "categorias_cardapio")
public class CategoriaCardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final String id;

    @Column (name = "nome")
    private String nome;

    @Column (name = "descricao")
    private String descricao;

    @ManyToOne
    @JoinColumn (name = "restaurante_Id")
    private Restaurante restauranteId;

    @OneToMany(mappedBy = "categoria")
    private List<Produto> produtos;

    public CategoriaCardapio(String nome, String descricao, String restauranteId) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.restauranteId = restauranteId;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(String restauranteId) {
        this.restauranteId = restauranteId;
    }
}
