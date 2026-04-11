package com.pedidos.domain.entities;

import com.pedidos.domain.enums.TipoUsuario;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class ClienteEntity extends UsuarioEntity {

    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "telefone")
    private String telefone;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EnderecoEntity enderecoEntityEntrega;

    @ManyToMany
    @JoinTable(
            name = "cliente_restaurantes_favoritos",
            joinColumns = @JoinColumn(name = "cliente_id"),
            inverseJoinColumns = @JoinColumn(name = "restaurante_id")
    )
    private List<RestauranteEntity> favoritos = new ArrayList<>();

    public void adicionarFavorito(RestauranteEntity restauranteEntity) {
        this.favoritos.add(restauranteEntity);
    }

    public void removerFavorito(RestauranteEntity restauranteEntity) {
        this.favoritos.remove(restauranteEntity);
    }

    protected ClienteEntity() {}

    public ClienteEntity(String nome, String email, String senhaHash, String cpf, String telefone) {
        super(nome, email, senhaHash, TipoUsuario.CLIENTE);
        this.cpf = cpf;
        this.telefone = telefone;
    }

    public List<RestauranteEntity> getFavoritos() { return favoritos; }

    public String getCpf() { return cpf; }

    public void setCpf(String cpf) {
        if (cpf == null || !cpf.matches("(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{11})$")) {
            throw new IllegalArgumentException("CPF inválido!");
        }
        this.cpf = cpf;
    }

    public String getTelefone() { return telefone; }

    public void setTelefone(String telefone) {
        if (telefone == null || !telefone.matches("^(55)?(?:([1-9]{2})?)(\\d{4,5})(\\d{4})$")) {
            throw new IllegalArgumentException("Telefone inválido!");
        }
        this.telefone = telefone;
    }

    public EnderecoEntity getEnderecoEntrega() { return enderecoEntityEntrega; }

    public void setEnderecoEntrega(EnderecoEntity enderecoEntity) {
        this.enderecoEntityEntrega = enderecoEntity;
        if (enderecoEntity != null) {
            enderecoEntity.setCliente(this);
        }
    }

    public List<RestauranteEntity> listarFavoritos(ClienteEntity clienteEntity) {
        return clienteEntity.getFavoritos();
    }

    @Override
    public String toString() {
        return "Cliente{nome=" + getNome() + ", email=" + getEmail() + "}";
    }
}