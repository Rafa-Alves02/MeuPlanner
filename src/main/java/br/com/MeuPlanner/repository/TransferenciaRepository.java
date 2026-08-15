package br.com.MeuPlanner.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import br.com.MeuPlanner.config.TransactionManager;
import br.com.MeuPlanner.exception.SaldoInsuficienteException;
import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.model.Transferencia;

public class TransferenciaRepository extends BaseRepository {

    private final ContaRepository contaRepo = new ContaRepository();

    @Override
    protected String nomeEntidade() {
        return "transferência";
    }

    public void salvar(Transferencia transferencia) {
        Conta origem = transferencia.getContaOrigem();
        if (origem.getSaldoAtual().compareTo(transferencia.getValor()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente na conta de origem!");
        }

        TransactionManager.executeInTransaction(() -> {
            String sql = """
                    INSERT INTO transferencias
                    (conta_origem_id, conta_destino_id, valor, data_transferencia, descricao)
                    VALUES (?, ?, ?, ?, ?)
                    """;
            Long id = executarInsert(sql, stmt -> {
                stmt.setLong(1, origem.getId());
                stmt.setLong(2, transferencia.getContaDestino().getId());
                stmt.setBigDecimal(3, transferencia.getValor());
                stmt.setDate(4, Date.valueOf(transferencia.getDataTransferencia()));
                stmt.setString(5, transferencia.getDescricao());
            });
            if (id != null) transferencia.setId(id);

            origem.setSaldoAtual(origem.getSaldoAtual().subtract(transferencia.getValor()));
            contaRepo.atualizar(origem);

            Conta destino = transferencia.getContaDestino();
            destino.setSaldoAtual(destino.getSaldoAtual().add(transferencia.getValor()));
            contaRepo.atualizar(destino);
        });
    }

    public List<Transferencia> listarPorConta(Long contaId) {
        String sql = """
                SELECT * FROM transferencias
                WHERE conta_origem_id = ? OR conta_destino_id = ?
                ORDER BY data_transferencia DESC
                """;
        return consultarLista(sql, stmt -> {
            stmt.setLong(1, contaId);
            stmt.setLong(2, contaId);
        }, this::mapear);
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
