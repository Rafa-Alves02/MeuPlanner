package br.com.MeuPlanner.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.MeuPlanner.config.ConnectionFactory;
import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.model.Transferencia;

public class TransferenciaRepository {

    private final ContaRepository contaRepo = new ContaRepository();

    public void salvar(Transferencia transferencia) {
        // Valida saldo suficiente
        Conta origem = transferencia.getContaOrigem();
        if (origem.getSaldoAtual().compareTo(transferencia.getValor()) < 0) {
            throw new RuntimeException("Saldo insuficiente na conta de origem!");
        }

        String sql = """
                INSERT INTO transferencias
                (conta_origem_id, conta_destino_id, valor, data_transferencia, descricao)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, origem.getId());
            stmt.setLong(2, transferencia.getContaDestino().getId());
            stmt.setBigDecimal(3, transferencia.getValor());
            stmt.setDate(4, Date.valueOf(transferencia.getDataTransferencia()));
            stmt.setString(5, transferencia.getDescricao());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) transferencia.setId(keys.getLong(1));

            // Atualiza saldos das duas contas
            origem.setSaldoAtual(origem.getSaldoAtual().subtract(transferencia.getValor()));
            contaRepo.atualizar(origem);

            Conta destino = transferencia.getContaDestino();
            destino.setSaldoAtual(destino.getSaldoAtual().add(transferencia.getValor()));
            contaRepo.atualizar(destino);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar transferência", e);
        }
    }

    public List<Transferencia> listarPorConta(Long contaId) {
        String sql = """
                SELECT * FROM transferencias
                WHERE conta_origem_id = ? OR conta_destino_id = ?
                ORDER BY data_transferencia DESC
                """;
        List<Transferencia> transferencias = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, contaId);
            stmt.setLong(2, contaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) transferencias.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar transferências", e);
        }
        return transferencias;
    }

    private Transferencia mapear(ResultSet rs) throws SQLException {
        Transferencia t = new Transferencia();
        t.setId(rs.getLong("id"));
        t.setValor(rs.getBigDecimal("valor"));
        t.setDataTransferencia(rs.getDate("data_transferencia").toLocalDate());
        t.setDescricao(rs.getString("descricao"));
        contaRepo.buscarPorId(rs.getLong("conta_origem_id")).ifPresent(t::setContaOrigem);
        contaRepo.buscarPorId(rs.getLong("conta_destino_id")).ifPresent(t::setContaDestino);
        return t;
    }
}