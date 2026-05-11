package com.pedidos.infra.config;

import org.flywaydb.core.Flyway;

public class FlyWayconfig {
    public static void migrate() {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(
                            "jdbc:postgresql://localhost:5432/deliveryapp",
                            "postgres",
                            "postgres"
                    )
                    .locations("classpath:db/migration")
                    .load();

            flyway.repair();
            flyway.migrate();

        } catch (Exception e) {
            System.out.println("Flyway não executado: " + e.getMessage());
        }
    }
}