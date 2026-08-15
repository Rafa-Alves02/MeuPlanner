package br.com.MeuPlanner.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import br.com.MeuPlanner.config.TransactionManager;
import br.com.MeuPlanner.model.Categoria;
import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.model.Gasto;
import br.com.MeuPlanner.model.TipoGasto;
import br.com.MeuPlanner.model.TipoRecorrencia;

public class GastoRepository extends BaseRepository {

    private static final String SELECT_COM_JOINS = """
            SELECT g.*,
                   c.nome AS c_nome, c.tipo AS c_tipo, c.saldo_inicial AS c_saldo_inicial,
                   c.saldo_atual AS c_saldo_atual,
                   cat.id AS cat_id, cat.nome AS cat_nome, cat.tipo AS cat_tipo, cat.cor AS cat_cor
            FROM gastos g
            JOIN contas c ON c.id = g.conta_id
            LEFT JOIN categorias cat ON cat.id = g.categoria_id
            """;

    private final ContaRepository contaRepo = new ContaRepository();

    @Override
    protected String nomeEntidade() {
        return "gasto";
    }

    public void salvar(Gasto gasto) {
        TransactionManager.executeInTransaction(() -> {
            String sql = """
                    INSERT INTO gastos
                    (descricao, valor, data_lancamento, mes_referencia, tipo_gasto,
                     tipo_recorrencia, parcela_atual, total_parcelas, conta_id, categoria_id, fitid_ofx)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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
                stmt.setString(11, gasto.getFitidOfx());
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
        String sql = SELECT_COM_JOINS + " WHERE g.id = ?";
        return consultarUm(sql, stmt -> stmt.setLong(1, id), this::mapear);
    }

    public List<Gasto> listarPorMes(YearMonth mes) {
        String sql = SELECT_COM_JOINS + " WHERE g.mes_referencia = ? ORDER BY g.data_lancamento DESC";
        return consultarLista(sql, stmt -> stmt.setString(1, mes.toString()), this::mapear);
    }

    public List<Gasto> listarRecorrentes() {
        String sql = SELECT_COM_JOINS + " WHERE g.tipo_recorrencia = 'RECORRENTE'";
        return consultarLista(sql, stmt -> {}, this::mapear);
    }

    public List<Gasto> listarPorCategoria(Long categoriaId, YearMonth mes) {
        String sql = SELECT_COM_JOINS + " WHERE g.categoria_id = ? AND g.mes_referencia = ?";
        return consultarLista(sql, stmt -> {
            stmt.setLong(1, categoriaId);
            stmt.setString(2, mes.toString());
        }, this::mapear);
    }

    public boolean existeFitid(Long contaId, String fitidOfx) {
        String sql = "SELECT 1 FROM gastos WHERE conta_id = ? AND fitid_ofx = ?";
        return consultarUm(sql, stmt -> {
            stmt.setLong(1, contaId);
            stmt.setString(2, fitidOfx);
        }, rs -> rs.getInt(1)).isPresent();
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
        gasto.setFitidOfx(rs.getString("fitid_ofx"));

        gasto.setConta(mapearConta(rs));

        long catId = rs.getLong("cat_id");
        if (!rs.wasNull()) gasto.setCategoria(mapearCategoria(rs));

        return gasto;
    }

    private Conta mapearConta(ResultSet rs) throws SQLException {
        Conta conta = new Conta();
        conta.setId(rs.getLong("conta_id"));
        conta.setNome(rs.getString("c_nome"));
        conta.setTipo(Conta.TipoConta.valueOf(rs.getString("c_tipo")));
        conta.setSaldoInicial(rs.getBigDecimal("c_saldo_inicial"));
        conta.setSaldoAtual(rs.getBigDecimal("c_saldo_atual"));
        return conta;
    }

    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getLong("cat_id"));
        categoria.setNome(rs.getString("cat_nome"));
        categoria.setTipo(Categoria.TipoCategoria.valueOf(rs.getString("cat_tipo")));
        categoria.setCor(rs.getString("cat_cor"));
        return categoria;
    }
}
