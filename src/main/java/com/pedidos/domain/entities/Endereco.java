package com.pedidos.domain.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "Enderecos")
public class Endereco {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column (name = "rua")
    private String rua;

    @Column (name = "numero")
    private String numero;

    @Column (name = "bairro")
    private String bairro;

    @Column (name = "cidade")
    private String cidade;

    @Column (name = "estado")
    private String estado;

    @Column (name = "cep")
    private String cep;

    @Column (name = "padrao")
    private boolean isPadrao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    protected Endereco() {}

    public Endereco(String rua, String numero, String bairro, String cidade, String estado, String cep, Boolean isPadrao) {
        this.id = UUID.randomUUID().toString();
        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.isPadrao = isPadrao;
    }

    public String getId() { return id; }
    public String getRua() { return rua; }
    public String getNumero() { return numero; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getCep() { return cep; }
    public boolean isPadrao() { return isPadrao; }

    public void setRua(String rua) { this.rua = rua; }
    public void setNumero(String numero) { this.numero = numero; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCep(String cep) { this.cep = cep; }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    public void setIsPadrao(Boolean isPadrao) {this.isPadrao = isPadrao;}

    @Override
    public String toString() {
        return rua + ", " + numero + " - " + bairro + ", " + cidade + " - " + estado + " | CEP: " + cep;
    }
}