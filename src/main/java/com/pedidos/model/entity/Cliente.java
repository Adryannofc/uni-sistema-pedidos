package com.pedidos.model.entity;

import com.pedidos.model.enums.TipoUsuario;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Cliente extends Usuario {

    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "telefone")
    private String telefone;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Endereco> enderecos = new ArrayList<>();

    protected Cliente() {
    }

    public Cliente(String nome, String email, String senhaHash, String cpf, String telefone) {
        super(nome, email, senhaHash, TipoUsuario.CLIENTE);
        this.cpf = cpf;
        this.telefone = telefone;
    }


    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (cpf == null || !cpf.matches("(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{11})$")) {
            throw new IllegalArgumentException("CPF inválido!");
        }
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        if (telefone == null || !telefone.matches("^(55)?(?:([1-9]{2})?)(\\d{4,5})(\\d{4})$")) {
            throw new IllegalArgumentException("Telefone inválido!");
        }
        this.telefone = telefone;
    }


    public List<Endereco> getEnderecos() {
        return enderecos;
    }

    public void setEndereco(Endereco endereco) {
        this.enderecos.add(endereco);
    }

    public Optional<Endereco> getEnderecoPadrao() {
        if (enderecos == null) {
            return Optional.empty();
        }
        return enderecos.stream().filter(Endereco::isPadrao).findFirst();
    }

    public void setClienteAoEndereco(Endereco endereco) {
        endereco.setCliente(this);
    }

    
    @Override
    public String toString() {
        return "Cliente{nome=" + getNome() + ", email=" + getEmail() + "}";
    }
}
