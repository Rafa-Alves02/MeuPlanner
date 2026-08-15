package br.com.MeuPlanner.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

import br.com.MeuPlanner.exception.RepositoryException;

public final class TransactionManager {

    private static final ThreadLocal<Connection> CURRENT_CONNECTION = new ThreadLocal<>();

    private TransactionManager() {}

    public static <T> T executeInTransaction(Supplier<T> action) {
        boolean isOuterTransaction = CURRENT_CONNECTION.get() == null;
        Connection conn = null;
        try {
            if (isOuterTransaction) {
                conn = ConnectionFactory.getConnection();
                conn.setAutoCommit(false);
                CURRENT_CONNECTION.set(conn);
            } else {
                conn = CURRENT_CONNECTION.get();
            }

            T result = action.get();

            if (isOuterTransaction) {
                conn.commit();
            }
            return result;

        } catch (RuntimeException e) {
            rollbackQuietly(conn, isOuterTransaction);
            throw e;
        } catch (Exception e) {
            rollbackQuietly(conn, isOuterTransaction);
            throw new RepositoryException("Erro ao executar transação", e);
        } finally {
            if (isOuterTransaction) {
                closeQuietly(conn);
                CURRENT_CONNECTION.remove();
            }
        }
    }

    public static void executeInTransaction(Runnable action) {
        executeInTransaction(() -> {
            action.run();
            return null;
        });
    }

    public static Connection currentConnection() throws SQLException {
        Connection tx = CURRENT_CONNECTION.get();
        return tx != null ? tx : ConnectionFactory.getConnection();
    }

    public static boolean isInTransaction() {
        return CURRENT_CONNECTION.get() != null;
    }

    private static void rollbackQuietly(Connection conn, boolean isOuterTransaction) {
        if (conn != null && isOuterTransaction) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    private static void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}