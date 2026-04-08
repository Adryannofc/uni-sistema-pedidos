package com.pedidos.domain.model;

import com.pedidos.domain.enums.TipoUsuario;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;


@Entity
@Table(name = "restaurantes")
@PrimaryKeyJoinColumn(name = "usuario_id")

public class Restaurante extends Usuario {

    private String cnpj;
    private boolean statusAtivo;
    private String categoriaGlobalId;
    private String telefone;

    public Restaurante(String nome, String email, String senhaHash,String cnpj) {
        super(nome, email, senhaHash, TipoUsuario.RESTAURANTE);
        this.cnpj = cnpj;
        this.statusAtivo = false;
    }

    public boolean isStatusAtivo() {
        return statusAtivo;
    }

    public void setStatusAtivo(boolean statusAtivo) {
        this.statusAtivo = statusAtivo;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        String regexCnpj = "^(\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}|\\d{14})$";
        if (cnpj != null && cnpj.matches(regexCnpj)) {
            this.cnpj = cnpj;
        }
        else
        {
            throw new IllegalArgumentException("CNPJ Inválido");
        }
    }


    public String getCategoriaGlobalId() {
        return categoriaGlobalId;
    }

    public void setCategoriaGlobalId(String categoriaGlobalId) {
        this.categoriaGlobalId = categoriaGlobalId;
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
        return "Restaurante{nome=" + getNome() + ", status=" + isStatusAtivo() + "}";
    }
}