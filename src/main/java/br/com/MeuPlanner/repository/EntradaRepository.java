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
import br.com.MeuPlanner.model.Entrada;
import br.com.MeuPlanner.model.TipoRecorrencia;

public class EntradaRepository extends BaseRepository {

    private static final String SELECT_COM_JOINS = """
            SELECT e.*,
                   c.nome AS c_nome, c.tipo AS c_tipo, c.saldo_inicial AS c_saldo_inicial,
                   c.saldo_atual AS c_saldo_atual,
                   cat.id AS cat_id, cat.nome AS cat_nome, cat.tipo AS cat_tipo, cat.cor AS cat_cor
            FROM entradas e
            JOIN contas c ON c.id = e.conta_id
            LEFT JOIN categorias cat ON cat.id = e.categoria_id
            """;

    private final ContaRepository contaRepo = new ContaRepository();

    @Override
    protected String nomeEntidade() {
        return "entrada";
    }

    public void salvar(Entrada entrada) {
        TransactionManager.executeInTransaction(() -> {
            String sql = """
                    INSERT INTO entradas
                    (descricao, valor, data_lancamento, mes_referencia, tipo_recorrencia,
                     parcela_atual, total_parcelas, conta_id, categoria_id, fitid_ofx)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            Long id = executarInsert(sql, stmt -> {
                stmt.setString(1, entrada.getDescricao());
                stmt.setBigDecimal(2, entrada.getValor());
                stmt.setDate(3, Date.valueOf(entrada.getDataLancamento()));
                stmt.setString(4, entrada.getMesReferencia().toString());
                stmt.setString(5, entrada.getTipoRecorrencia().name());
                stmt.setObject(6, entrada.getParcelaAtual());
                stmt.setObject(7, entrada.getTotalParcelas());
                stmt.setLong(8, entrada.getConta().getId());
                stmt.setObject(9, entrada.getCategoria() != null ? entrada.getCategoria().getId() : null);
                stmt.setString(10, entrada.getFitidOfx());
            });
            if (id != null) entrada.setId(id);

            Conta conta = entrada.getConta();
            conta.setSaldoAtual(conta.getSaldoAtual().add(entrada.getValor()));
            contaRepo.atualizar(conta);
        });
    }

    public void deletar(Long id) {
        TransactionManager.executeInTransaction(() -> {
            buscarPorId(id).ifPresent(entrada -> {
                Conta conta = entrada.getConta();
                conta.setSaldoAtual(conta.getSaldoAtual().subtract(entrada.getValor()));
                contaRepo.atualizar(conta);
            });

            String sql = "DELETE FROM entradas WHERE id = ?";
            executarUpdate(sql, stmt -> stmt.setLong(1, id));
        });
    }

    public Optional<Entrada> buscarPorId(Long id) {
        String sql = SELECT_COM_JOINS + " WHERE e.id = ?";
        return consultarUm(sql, stmt -> stmt.setLong(1, id), this::mapear);
    }

    public List<Entrada> listarPorMes(YearMonth mes) {
        String sql = SELECT_COM_JOINS + " WHERE e.mes_referencia = ? ORDER BY e.data_lancamento DESC";
        return consultarLista(sql, stmt -> stmt.setString(1, mes.toString()), this::mapear);
    }

    public List<Entrada> listarRecorrentes() {
        String sql = SELECT_COM_JOINS + " WHERE e.tipo_recorrencia = 'RECORRENTE'";
        return consultarLista(sql, stmt -> {}, this::mapear);
    }

    public boolean existeFitid(Long contaId, String fitidOfx) {
        String sql = "SELECT 1 FROM entradas WHERE conta_id = ? AND fitid_ofx = ?";
        return consultarUm(sql, stmt -> {
            stmt.setLong(1, contaId);
            stmt.setString(2, fitidOfx);
        }, rs -> rs.getInt(1)).isPresent();
    }

    private Entrada mapear(ResultSet rs) throws SQLException {
        Entrada entrada = new Entrada();
        entrada.setId(rs.getLong("id"));
        entrada.setDescricao(rs.getString("descricao"));
        entrada.setValor(rs.getBigDecimal("valor"));
        entrada.setDataLancamento(rs.getDate("data_lancamento").toLocalDate());
        entrada.setMesReferencia(YearMonth.parse(rs.getString("mes_referencia")));
        entrada.setTipoRecorrencia(TipoRecorrencia.valueOf(rs.getString("tipo_recorrencia")));
        entrada.setParcelaAtual(rs.getObject("parcela_atual", Integer.class));
        entrada.setTotalParcelas(rs.getObject("total_parcelas", Integer.class));
        entrada.setFitidOfx(rs.getString("fitid_ofx"));

        entrada.setConta(mapearConta(rs));

        long catId = rs.getLong("cat_id");
        if (!rs.wasNull()) entrada.setCategoria(mapearCategoria(rs));

        return entrada;
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
