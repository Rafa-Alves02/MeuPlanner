package br.com.MeuPlanner.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.MeuPlanner.config.TransactionManager;
import br.com.MeuPlanner.exception.RepositoryException;


public abstract class BaseRepository {

    protected abstract String nomeEntidade();

    protected Long executarInsert(String sql, StatementSetter setter) {
        Connection conn = null;
        try {
            conn = TransactionManager.currentConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setter.set(stmt);
                stmt.executeUpdate();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    return keys.next() ? keys.getLong(1) : null;
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao salvar " + nomeEntidade(), e);
        } finally {
            fecharSeForaDeTransacao(conn);
        }
    }

    protected int executarUpdate(String sql, StatementSetter setter) {
        Connection conn = null;
        try {
            conn = TransactionManager.currentConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.set(stmt);
                return stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao atualizar " + nomeEntidade(), e);
        } finally {
            fecharSeForaDeTransacao(conn);
        }
    }

    protected <T> Optional<T> consultarUm(String sql, StatementSetter setter, RowMapper<T> mapper) {
        Connection conn = null;
        try {
            conn = TransactionManager.currentConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.set(stmt);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? Optional.of(mapper.map(rs)) : Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao buscar " + nomeEntidade(), e);
        } finally {
            fecharSeForaDeTransacao(conn);
        }
    }

    protected <T> List<T> consultarLista(String sql, StatementSetter setter, RowMapper<T> mapper) {
        Connection conn = null;
        try {
            conn = TransactionManager.currentConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.set(stmt);
                try (ResultSet rs = stmt.executeQuery()) {
                    List<T> resultado = new ArrayList<>();
                    while (rs.next()) resultado.add(mapper.map(rs));
                    return resultado;
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao listar " + nomeEntidade(), e);
        } finally {
            fecharSeForaDeTransacao(conn);
        }
    }

    private void fecharSeForaDeTransacao(Connection conn) {
        if (conn != null && !TransactionManager.isInTransaction()) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}