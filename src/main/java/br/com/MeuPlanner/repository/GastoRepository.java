package br.com.MeuPlanner.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import br.com.MeuPlanner.config.ConnectionFactory;
import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.model.Gasto;
import br.com.MeuPlanner.model.TipoGasto;
import br.com.MeuPlanner.model.TipoRecorrencia;

public class GastoRepository {

    private final ContaRepository contaRepo = new ContaRepository();
    private final CategoriaRepository categoriaRepo = new CategoriaRepository();

    public void salvar(Gasto gasto) {
        String sql = """
                INSERT INTO gastos
                (descricao, valor, data_lancamento, mes_referencia, tipo_gasto,
                 tipo_recorrencia, parcela_atual, total_parcelas, conta_id, categoria_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) gasto.setId(keys.getLong(1));

            // Atualiza saldo da conta
            Conta conta = gasto.getConta();
            conta.setSaldoAtual(conta.getSaldoAtual().subtract(gasto.getValor()));
            contaRepo.atualizar(conta);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar gasto", e);
        }
    }

    public void deletar(Long id) {
        // Reverte o saldo antes de deletar
        buscarPorId(id).ifPresent(gasto -> {
            Conta conta = gasto.getConta();
            conta.setSaldoAtual(conta.getSaldoAtual().add(gasto.getValor()));
            contaRepo.atualizar(conta);
        });

        String sql = "DELETE FROM gastos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar gasto", e);
        }
    }

    public java.util.Optional<Gasto> buscarPorId(Long id) {
        String sql = "SELECT * FROM gastos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return java.util.Optional.of(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar gasto", e);
        }
        return java.util.Optional.empty();
    }

    public List<Gasto> listarPorMes(YearMonth mes) {
        String sql = "SELECT * FROM gastos WHERE mes_referencia = ? ORDER BY data_lancamento DESC";
        List<Gasto> gastos = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mes.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) gastos.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar gastos", e);
        }
        return gastos;
    }

    public List<Gasto> listarRecorrentes() {
        String sql = "SELECT * FROM gastos WHERE tipo_recorrencia = 'RECORRENTE'";
        List<Gasto> gastos = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) gastos.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar gastos recorrentes", e);
        }
        return gastos;
    }

    public List<Gasto> listarPorCategoria(Long categoriaId, YearMonth mes) {
        String sql = "SELECT * FROM gastos WHERE categoria_id = ? AND mes_referencia = ?";
        List<Gasto> gastos = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, categoriaId);
            stmt.setString(2, mes.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) gastos.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar gastos por categoria", e);
        }
        return gastos;
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

        contaRepo.buscarPorId(rs.getLong("conta_id")).ifPresent(gasto::setConta);

        long catId = rs.getLong("categoria_id");
        if (!rs.wasNull()) categoriaRepo.buscarPorId(catId).ifPresent(gasto::setCategoria);

        return gasto;
    }
}