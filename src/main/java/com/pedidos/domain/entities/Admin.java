package com.pedidos.domain.entities;

import com.pedidos.domain.enums.TipoUsuario;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;


@Entity
@Table (name = "admins")
@PrimaryKeyJoinColumn(name = "usuario_id")

public class Admin extends Usuario {

    public Admin() {

    }

    public Admin(String nome, String email, String senhaHash) {
        super(nome, email, senhaHash, TipoUsuario.ADMIN);
    }

    @Override
    public String toString() {
        return "Admin{nome=" + getNome() + ", email=" + getEmail() + "}";
    }


}