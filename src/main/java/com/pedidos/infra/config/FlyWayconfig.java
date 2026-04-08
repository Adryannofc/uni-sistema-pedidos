package com.pedidos.infra.config;

import org.flywaydb.core.Flyway;

public class FlyWayconfig {
    public static void migrate() {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:postgresql://localhost:5432/delivery_db",
                        "postgres",
                        "postgres"
                )
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();
    }
}