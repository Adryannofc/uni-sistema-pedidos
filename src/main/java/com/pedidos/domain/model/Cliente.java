package com.pedidos.domain.model;

import com.pedidos.domain.enums.TipoUsuario;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Cliente extends Usuario {

    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "telefone")
    private String telefone;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Endereco enderecoEntrega;

    protected Cliente() {}

    public Cliente(String nome, String email, String senhaHash, String cpf, String telefone) {
        super(nome, email, senhaHash, TipoUsuario.CLIENTE);
        this.cpf = cpf;
        this.telefone = telefone;
    }

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

    public Endereco getEnderecoEntrega() { return enderecoEntrega; }

    public void setEnderecoEntrega(Endereco endereco) {
        this.enderecoEntrega = endereco;
        if (endereco != null) {
            endereco.setCliente(this);
        }
    }

    @Override
    public String toString() {
        return "Cliente{nome=" + getNome() + ", email=" + getEmail() + "}";
    }
}