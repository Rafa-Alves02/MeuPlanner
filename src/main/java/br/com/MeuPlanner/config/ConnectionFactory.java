package br.com.MeuPlanner.config;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import br.com.MeuPlanner.exception.RepositoryException;

public final class ConnectionFactory {

    private static final HikariDataSource dataSource;

    static {
        AppConfig appConfig = AppConfig.getInstance();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(appConfig.get("db.url", "jdbc:mysql://localhost:3306/meuplanner?useSSL=false&serverTimezone=UTC"));
        config.setUsername(appConfig.get("db.user", "root"));
        config.setPassword(appConfig.get("db.password", ""));
        config.setMaximumPoolSize(appConfig.getInt("db.pool.maxSize", 10));
        config.setMinimumIdle(appConfig.getInt("db.pool.minIdle", 2));
        config.setConnectionTimeout(appConfig.getInt("db.pool.connectionTimeoutMs", 30000));
        dataSource = new HikariDataSource(config);
    }

    private ConnectionFactory() {}

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao obter conexão", e);
        }
    }
}

