package com.pedidos.model.entity;

import com.pedidos.model.enums.StatusRestaurante;
import com.pedidos.model.enums.TipoUsuario;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurantes")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Restaurante extends Usuario {

    @Column(name = "cnpj", unique = true)
    private String cnpj;

    @Column(name = "status_ativo")
    private boolean statusAtivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusRestaurante status;

    @Column(name = "telefone")
    private String telefone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_global_id")
    private CategoriaGlobal categoriaGlobal;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CategoriaCardapio> categorias;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Produto> produtos;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HorarioFuncionamento> horarios;


    protected Restaurante() {}

    public Restaurante(String nome, String email, String senhaHash, String cnpj, String telefone) {
        super(nome, email, senhaHash, TipoUsuario.RESTAURANTE);
        this.cnpj = cnpj;
        this.statusAtivo = false;
        this.status = StatusRestaurante.PENDENTE;
        this.telefone = telefone;
    }

    public StatusRestaurante getStatus() { return status; }
    public void setStatus(StatusRestaurante status) {
        this.status = status;
        this.statusAtivo = (status == StatusRestaurante.ATIVO);
    }

    public boolean isStatusAtivo() { return status == StatusRestaurante.ATIVO; }
    public void setStatusAtivo(boolean statusAtivo) {
        this.statusAtivo = statusAtivo;
        this.status = statusAtivo ? StatusRestaurante.ATIVO : StatusRestaurante.PENDENTE;
    }

    public String getCnpj() { return cnpj; }

    public void setCnpj(String cnpj) {
        if (cnpj == null || !cnpj.matches("^(\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}|\\d{14})$")) {
            throw new IllegalArgumentException("CNPJ Inválido");
        }
        this.cnpj = cnpj;
    }

    public String getTelefone() { return telefone; }

    public void setTelefone(String telefone) {
        if (telefone == null || !telefone.matches("^(55)?(?:([1-9]{2})?)(\\d{4,5})(\\d{4})$")) {
            throw new IllegalArgumentException("Telefone inválido!");
        }
        this.telefone = telefone;
    }

    public CategoriaGlobal getCategoriaGlobal() { return categoriaGlobal; }

    public void setCategoriaGlobal(CategoriaGlobal categoriaGlobal) {
        this.categoriaGlobal = categoriaGlobal;
    }

    public List<HorarioFuncionamento> getHorarios() { return horarios; }
    public void setHorarios(List<HorarioFuncionamento> horarios) { this.horarios = horarios; }

    // Mantido para não quebrar RestauranteService e camada de apresentação
    public String getCategoriaGlobalId() {
        return categoriaGlobal != null ? categoriaGlobal.getId() : null;
    }

    @Deprecated
    public void setCategoriaGlobalId(String id) {}

    @Override
    public String toString() {
        return "Restaurante{nome=" + getNome() + ", status=" + status + "}";
    }

    public List<CategoriaCardapio> getCategorias() {
        return categorias;
    }
}