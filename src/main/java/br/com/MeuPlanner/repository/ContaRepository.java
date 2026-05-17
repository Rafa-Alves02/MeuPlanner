package br.com.MeuPlanner.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.MeuPlanner.config.ConnectionFactory;
import br.com.MeuPlanner.model.Conta;

public class ContaRepository {

    public void salvar(Conta conta) {
        String sql = "INSERT INTO contas (nome, tipo, saldo_inicial, saldo_atual) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, conta.getNome());
            stmt.setString(2, conta.getTipo().name());
            stmt.setBigDecimal(3, conta.getSaldoInicial());
            stmt.setBigDecimal(4, conta.getSaldoAtual());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) conta.setId(keys.getLong(1));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar conta", e);
        }
    }

    public void atualizar(Conta conta) {
        String sql = "UPDATE contas SET nome = ?, tipo = ?, saldo_atual = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, conta.getNome());
            stmt.setString(2, conta.getTipo().name());
            stmt.setBigDecimal(3, conta.getSaldoAtual());
            stmt.setLong(4, conta.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar conta", e);
        }
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM contas WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar conta", e);
        }
    }

    public Optional<Conta> buscarPorId(Long id) {
        String sql = "SELECT * FROM contas WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar conta", e);
        }
        return Optional.empty();
    }

    public List<Conta> listarTodas() {
        String sql = "SELECT * FROM contas ORDER BY nome";
        List<Conta> contas = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) contas.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar contas", e);
        }
        return contas;
    }

    private Conta mapear(ResultSet rs) throws SQLException {
        Conta conta = new Conta();
        conta.setId(rs.getLong("id"));
        conta.setNome(rs.getString("nome"));
        conta.setTipo(Conta.TipoConta.valueOf(rs.getString("tipo")));
        conta.setSaldoInicial(rs.getBigDecimal("saldo_inicial"));
        conta.setSaldoAtual(rs.getBigDecimal("saldo_atual"));
        return conta;
    }
}