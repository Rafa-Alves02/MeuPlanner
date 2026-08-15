package br.com.MeuPlanner.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import br.com.MeuPlanner.config.TransactionManager;
import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.model.Gasto;
import br.com.MeuPlanner.model.TipoGasto;
import br.com.MeuPlanner.model.TipoRecorrencia;

public class GastoRepository extends BaseRepository {

    private final ContaRepository contaRepo = new ContaRepository();
    private final CategoriaRepository categoriaRepo = new CategoriaRepository();

    @Override
    protected String nomeEntidade() {
        return "gasto";
    }

    public void salvar(Gasto gasto) {
        TransactionManager.executeInTransaction(() -> {
            String sql = """
                    INSERT INTO gastos
                    (descricao, valor, data_lancamento, mes_referencia, tipo_gasto,
                     tipo_recorrencia, parcela_atual, total_parcelas, conta_id, categoria_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            Long id = executarInsert(sql, stmt -> {
                stmt.setString(1, gasto.getDescricao());
                stmt.setBigDecimal(2, gasto.getValor());
                stmt.setDate(3, Date.valueOf(gasto.getDataLancamento()));
                stmt.setString(4, gasto.getMesReferencia().toString());
                stmt.setString(5, gasto.getTipoGasto().name());
                stmt.setString(6, gasto.getTipoRecorrencia().name());
                stmt.setObject(7, gasto.getParcelaAtual());
                stmt.setObject(8, gasto.getTotalParcelas());
                stmt.setLong(9, gasto.getConta().getId());
                stmt.setObject(10, gasto.getCategoria() != null ? gasto.getCategoria().getId() : null);
            });
            if (id != null) gasto.setId(id);

            Conta conta = gasto.getConta();
            conta.setSaldoAtual(conta.getSaldoAtual().subtract(gasto.getValor()));
            contaRepo.atualizar(conta);
        });
    }

    public void deletar(Long id) {
        TransactionManager.executeInTransaction(() -> {
            buscarPorId(id).ifPresent(gasto -> {
                Conta conta = gasto.getConta();
                conta.setSaldoAtual(conta.getSaldoAtual().add(gasto.getValor()));
                contaRepo.atualizar(conta);
            });

            String sql = "DELETE FROM gastos WHERE id = ?";
            executarUpdate(sql, stmt -> stmt.setLong(1, id));
        });
    }

    public Optional<Gasto> buscarPorId(Long id) {
        String sql = "SELECT * FROM gastos WHERE id = ?";
        return consultarUm(sql, stmt -> stmt.setLong(1, id), this::mapear);
    }

    public List<Gasto> listarPorMes(YearMonth mes) {
        String sql = "SELECT * FROM gastos WHERE mes_referencia = ? ORDER BY data_lancamento DESC";
        return consultarLista(sql, stmt -> stmt.setString(1, mes.toString()), this::mapear);
    }

    public List<Gasto> listarRecorrentes() {
        String sql = "SELECT * FROM gastos WHERE tipo_recorrencia = 'RECORRENTE'";
        return consultarLista(sql, stmt -> {}, this::mapear);
    }

    public List<Gasto> listarPorCategoria(Long categoriaId, YearMonth mes) {
        String sql = "SELECT * FROM gastos WHERE categoria_id = ? AND mes_referencia = ?";
        return consultarLista(sql, stmt -> {
            stmt.setLong(1, categoriaId);
            stmt.setString(2, mes.toString());
        }, this::mapear);
    }

    private Gasto mapear(ResultSet rs) throws SQLException {
        Gasto gasto = new Gasto();
        gasto.setId(rs.getLong("id"));
        gasto.setDescricao(rs.getString("descricao"));
        gasto.setValor(rs.getBigDecimal("valor"));
        gasto.setDataLancamento(rs.getDate("data_lancamento").toLocalDate());
        gasto.setMesReferencia(YearMonth.parse(rs.getString("mes_referencia")));
        gasto.setTipoGasto(TipoGasto.valueOf(rs.getString("tipo_gasto")));
        gasto.setTipoRecorrencia(TipoRecorrencia.valueOf(rs.getString("tipo_recorrencia")));
        gasto.setParcelaAtual(rs.getObject("parcela_atual", Integer.class));
        gasto.setTotalParcelas(rs.getObject("total_parcelas", Integer.class));

        // TODO: N+1 — considerar JOIN
        contaRepo.buscarPorId(rs.getLong("conta_id")).ifPresent(gasto::setConta);

        long catId = rs.getLong("categoria_id");
        if (!rs.wasNull()) categoriaRepo.buscarPorId(catId).ifPresent(gasto::setCategoria);

        return gasto;
    }
}
