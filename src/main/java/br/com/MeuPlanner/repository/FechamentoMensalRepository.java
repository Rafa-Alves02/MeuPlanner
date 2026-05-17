package br.com.MeuPlanner.repository;

import br.com.MeuPlanner.config.ConnectionFactory;
import br.com.MeuPlanner.model.FechamentoMensal;
import br.com.MeuPlanner.model.StatusMes;

import java.sql.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FechamentoMensalRepository {

    public void salvar(FechamentoMensal fechamento) {
        String sql = """
                INSERT INTO fechamento_mensal
                (mes_referencia, total_entradas, total_gastos, saldo_final, status, data_fechamento)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, fechamento.getMesReferencia().toString());
            stmt.setBigDecimal(2, fechamento.getTotalEntradas());
            stmt.setBigDecimal(3, fechamento.getTotalGastos());
            stmt.setBigDecimal(4, fechamento.getSaldoFinal());
            stmt.setString(5, fechamento.getStatus().name());
            stmt.setDate(6, Date.valueOf(fechamento.getDataFechamento()));
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) fechamento.setId(keys.getLong(1));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar fechamento", e);
        }
    }

    public Optional<FechamentoMensal> buscarPorMes(YearMonth mes) {
        String sql = "SELECT * FROM fechamento_mensal WHERE mes_referencia = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mes.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar fechamento", e);
        }
        return Optional.empty();
    }

    public List<FechamentoMensal> listarTodos() {
        String sql = "SELECT * FROM fechamento_mensal ORDER BY mes_referencia DESC";
        List<FechamentoMensal> fechamentos = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) fechamentos.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar fechamentos", e);
        }
        return fechamentos;
    }

    private FechamentoMensal mapear(ResultSet rs) throws SQLException {
        FechamentoMensal fechamento = new FechamentoMensal();
        fechamento.setId(rs.getLong("id"));
        fechamento.setMesReferencia(YearMonth.parse(rs.getString("mes_referencia")));
        fechamento.setTotalEntradas(rs.getBigDecimal("total_entradas"));
        fechamento.setTotalGastos(rs.getBigDecimal("total_gastos"));
        fechamento.setSaldoFinal(rs.getBigDecimal("saldo_final"));
        fechamento.setStatus(StatusMes.valueOf(rs.getString("status")));
        fechamento.setDataFechamento(rs.getDate("data_fechamento").toLocalDate());
        return fechamento;
    }
}