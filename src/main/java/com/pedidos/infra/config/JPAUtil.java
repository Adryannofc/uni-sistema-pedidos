package com.pedidos.infra.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utilitário JPA responsável por gerenciar o ciclo de vida do EntityManagerFactory.
 * A factory é criada uma única vez (static final) durante toda a vida da aplicação.
 */
public class JPAUtil {

    private static final EntityManagerFactory factory =
            Persistence.createEntityManagerFactory("deliveryPU");

    private JPAUtil() {
        // Classe utilitária — não instanciar
    }

    /**
     * Retorna um novo EntityManager a partir da factory compartilhada.
     * O chamador é responsável por fechar o EntityManager após o uso.
     *
     * @return EntityManager pronto para uso
     */
    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    /**
     * Fecha a EntityManagerFactory ao encerrar a aplicação.
     * Deve ser chamado explicitamente no Main.java após o retorno do MenuLogin.
     */
    public static void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}