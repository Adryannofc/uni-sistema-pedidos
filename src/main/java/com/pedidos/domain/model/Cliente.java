package com.pedidos.domain.model;

import com.pedidos.domain.enums.TipoUsuario;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "usuario_id")

public class Cliente extends Usuario {

    private String cpf;
    private String telefone;
    private List<String> favoritos;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Endereco enderecoEntrega;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Pedido pedido;

    public Cliente(String nome, String email, String senhaHash, String cpf, String telefone) {
        super(nome, email, senhaHash, TipoUsuario.CLIENTE);
        this.cpf = cpf;
        this.telefone = telefone;
        this.favoritos = new ArrayList<>();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        String regexCpf = "(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{11})$";
        if (cpf != null && cpf.matches(regexCpf))
        {
            this.cpf = cpf;
        }
        else {
            throw new IllegalArgumentException("CPF inválido!");
        };

    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        String regexTelefone = "^(55)?(?:([1-9]{2})?)(\\d{4,5})(\\d{4})$";
        if(telefone != null && telefone.matches(regexTelefone))
        {
            this.telefone = telefone;
        }
        else
        {
            throw new IllegalArgumentException("Telefone inválido!");
        }
    }

    @Override
    public String toString() {
        return "Cliente{nome=" + getNome() + ", email=" + getEmail() + "}";
    }

    public void adicionarFavorito(String restauranteId) {
        if (!this.favoritos.contains(restauranteId)) {
            this.favoritos.add(restauranteId);
        }
    }

    public void removerFavorito(String restauranteId) {
        this.favoritos.remove(restauranteId);
    }

    public List<String> getFavoritos() {
        return new ArrayList<>(this.favoritos);
    }

    public Endereco getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(Endereco enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }
}