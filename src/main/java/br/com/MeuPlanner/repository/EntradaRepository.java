package br.com.MeuPlanner.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import br.com.MeuPlanner.config.TransactionManager;
import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.model.Entrada;
import br.com.MeuPlanner.model.TipoRecorrencia;

public class EntradaRepository extends BaseRepository {

    private final ContaRepository contaRepo = new ContaRepository();
    private final CategoriaRepository categoriaRepo = new CategoriaRepository();

    @Override
    protected String nomeEntidade() {
        return "entrada";
    }

    public void salvar(Entrada entrada) {
        TransactionManager.executeInTransaction(() -> {
            String sql = """
                    INSERT INTO entradas
                    (descricao, valor, data_lancamento, mes_referencia, tipo_recorrencia,
                     parcela_atual, total_parcelas, conta_id, categoria_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
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
        String sql = "SELECT * FROM entradas WHERE id = ?";
        return consultarUm(sql, stmt -> stmt.setLong(1, id), this::mapear);
    }

    public List<Entrada> listarPorMes(YearMonth mes) {
        String sql = "SELECT * FROM entradas WHERE mes_referencia = ? ORDER BY data_lancamento DESC";
        return consultarLista(sql, stmt -> stmt.setString(1, mes.toString()), this::mapear);
    }

    public List<Entrada> listarRecorrentes() {
        String sql = "SELECT * FROM entradas WHERE tipo_recorrencia = 'RECORRENTE'";
        return consultarLista(sql, stmt -> {}, this::mapear);
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

        // TODO: N+1 — considerar JOIN
        contaRepo.buscarPorId(rs.getLong("conta_id")).ifPresent(entrada::setConta);

        long catId = rs.getLong("categoria_id");
        if (!rs.wasNull()) categoriaRepo.buscarPorId(catId).ifPresent(entrada::setCategoria);

        return entrada;
    }
}
