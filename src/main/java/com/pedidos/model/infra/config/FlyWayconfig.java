package com.pedidos.model.infra.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

/**
 * Configuração do Flyway para migrations de banco de dados.
 *
 * Sequência de migrations suportadas:
 * - V1: Criar banco de dados e schema principal
 * - V2: Criar tabelas (incluindo areas_entrega e horarios_funcionamento)
 * - V3: CRUD e DML (dados de exemplo)
 * - V4: JOINs e queries complexas
 * - V5: Triggers e validações
 *
 * Nota: Tabelas de horarios_funcionamento foram consolidadas em V2 (antes era V6).
 * Se receber erro de V6, execute: DROP DATABASE deliveryapp; CREATE DATABASE deliveryapp;
 */
public class FlyWayconfig {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/deliveryapp";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";
    private static final String MIGRATION_LOCATION = "classpath:db/migration";

    public static void migrate() {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(DB_URL, DB_USER, DB_PASSWORD)
                    .locations(MIGRATION_LOCATION)
                    .baselineOnMigrate(true)
                    .validateOnMigrate(true)
                    .load();

            // Executar migrations sem repair automático
            // repair() é operação manual de DBA — chamá-la automaticamente mascara erros reais
            int migrationsExecuted = flyway.migrate().migrationsExecuted;

            if (migrationsExecuted > 0) {
                System.out.println("✓ Flyway: " + migrationsExecuted + " migration(s) executada(s) com sucesso.");
            } else {
                System.out.println("✓ Flyway: Banco de dados já está atualizado.");
            }

        } catch (FlywayException e) {
            System.err.println("✗ Flyway falhou: " + e.getMessage());
            System.err.println("  IMPORTANTE: Se houver conflito de versão, execute:");
            System.err.println("  - Deletar banco: DROP DATABASE deliveryapp;");
            System.err.println("  - Recriar: CREATE DATABASE deliveryapp;");
            System.err.println("  - Reiniciar aplicação");
            throw new RuntimeException("Falha crítica nas migrations do Flyway", e);
        } catch (Exception e) {
            System.err.println("✗ Erro ao executar Flyway: " + e.getMessage());
            throw new RuntimeException("Erro desconhecido no Flyway", e);
        }
    }

}