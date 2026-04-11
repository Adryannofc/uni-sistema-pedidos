package com.pedidos.domain.entities;

import com.pedidos.domain.enums.TipoUsuario;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "restaurantes")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class RestauranteEntity extends UsuarioEntity {

    @Column(name = "cnpj", unique = true)
    private String cnpj;

    @Column(name = "status_ativo")
    private boolean statusAtivo;

    @Column(name = "telefone")
    private String telefone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_global_id")
    private CategoriaGlobalEntity categoriaGlobalEntity;

    @OneToMany(mappedBy = "restaurante")
    private List<CategoriaCardapioEntity> categorias;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProdutoEntity> produtoEntities;

    protected RestauranteEntity() {}

    public RestauranteEntity(String nome, String email, String senhaHash, String cnpj) {
        super(nome, email, senhaHash, TipoUsuario.RESTAURANTE);
        this.cnpj = cnpj;
        this.statusAtivo = false;
    }

    public boolean isStatusAtivo() { return statusAtivo; }
    public void setStatusAtivo(boolean statusAtivo) { this.statusAtivo = statusAtivo; }

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

    public CategoriaGlobalEntity getCategoriaGlobal() { return categoriaGlobalEntity; }

    public void setCategoriaGlobal(CategoriaGlobalEntity categoriaGlobalEntity) {
        this.categoriaGlobalEntity = categoriaGlobalEntity;
    }

    // Mantido para não quebrar RestauranteService e camada de apresentação
    public String getCategoriaGlobalId() {
        return categoriaGlobalEntity != null ? categoriaGlobalEntity.getId() : null;
    }

    @Deprecated
    public void setCategoriaGlobalId(String id) {}

    @Override
    public String toString() {
        return "Restaurante{nome=" + getNome() + ", status=" + isStatusAtivo() + "}";
    }
}