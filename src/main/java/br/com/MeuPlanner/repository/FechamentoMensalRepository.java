package br.com.MeuPlanner.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import br.com.MeuPlanner.model.FechamentoMensal;
import br.com.MeuPlanner.model.StatusMes;

public class FechamentoMensalRepository extends BaseRepository {

    @Override
    protected String nomeEntidade() {
        return "fechamento mensal";
    }

    public void salvar(FechamentoMensal fechamento) {
        String sql = """
                INSERT INTO fechamento_mensal
                (mes_referencia, total_entradas, total_gastos, saldo_final, status, data_fechamento)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        Long id = executarInsert(sql, stmt -> {
            stmt.setString(1, fechamento.getMesReferencia().toString());
            stmt.setBigDecimal(2, fechamento.getTotalEntradas());
            stmt.setBigDecimal(3, fechamento.getTotalGastos());
            stmt.setBigDecimal(4, fechamento.getSaldoFinal());
            stmt.setString(5, fechamento.getStatus().name());
            stmt.setDate(6, Date.valueOf(fechamento.getDataFechamento()));
        });
        if (id != null) fechamento.setId(id);
    }

    public Optional<FechamentoMensal> buscarPorMes(YearMonth mes) {
        String sql = "SELECT * FROM fechamento_mensal WHERE mes_referencia = ?";
        return consultarUm(sql, stmt -> stmt.setString(1, mes.toString()), this::mapear);
    }

    public List<FechamentoMensal> listarTodos() {
        String sql = "SELECT * FROM fechamento_mensal ORDER BY mes_referencia DESC";
        return consultarLista(sql, stmt -> {}, this::mapear);
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
