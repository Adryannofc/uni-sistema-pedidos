package com.pedidos.domain.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table (name = "categorias_globais")
public class CategoriaGlobal {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    protected CategoriaGlobal() {}

    public CategoriaGlobal(String nome, String descricao) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
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
}