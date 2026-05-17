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
import br.com.MeuPlanner.model.Entrada;
import br.com.MeuPlanner.model.TipoRecorrencia;

public class EntradaRepository {

    private final ContaRepository contaRepo = new ContaRepository();
    private final CategoriaRepository categoriaRepo = new CategoriaRepository();

    public void salvar(Entrada entrada) {
        String sql = """
                INSERT INTO entradas
                (descricao, valor, data_lancamento, mes_referencia, tipo_recorrencia,
                 parcela_atual, total_parcelas, conta_id, categoria_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, entrada.getDescricao());
            stmt.setBigDecimal(2, entrada.getValor());
            stmt.setDate(3, Date.valueOf(entrada.getDataLancamento()));
            stmt.setString(4, entrada.getMesReferencia().toString());
            stmt.setString(5, entrada.getTipoRecorrencia().name());
            stmt.setObject(6, entrada.getParcelaAtual());
            stmt.setObject(7, entrada.getTotalParcelas());
            stmt.setLong(8, entrada.getConta().getId());
            stmt.setObject(9, entrada.getCategoria() != null ? entrada.getCategoria().getId() : null);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) entrada.setId(keys.getLong(1));

            // Atualiza saldo da conta
            Conta conta = entrada.getConta();
            conta.setSaldoAtual(conta.getSaldoAtual().add(entrada.getValor()));
            contaRepo.atualizar(conta);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar entrada", e);
        }
    }

    public void deletar(Long id) {
        // Reverte o saldo antes de deletar
        buscarPorId(id).ifPresent(entrada -> {
            Conta conta = entrada.getConta();
            conta.setSaldoAtual(conta.getSaldoAtual().subtract(entrada.getValor()));
            contaRepo.atualizar(conta);
        });

        String sql = "DELETE FROM entradas WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar entrada", e);
        }
    }

    public java.util.Optional<Entrada> buscarPorId(Long id) {
        String sql = "SELECT * FROM entradas WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return java.util.Optional.of(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar entrada", e);
        }
        return java.util.Optional.empty();
    }

    public List<Entrada> listarPorMes(YearMonth mes) {
        String sql = "SELECT * FROM entradas WHERE mes_referencia = ? ORDER BY data_lancamento DESC";
        List<Entrada> entradas = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mes.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) entradas.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar entradas", e);
        }
        return entradas;
    }

    public List<Entrada> listarRecorrentes() {
        String sql = "SELECT * FROM entradas WHERE tipo_recorrencia = 'RECORRENTE'";
        List<Entrada> entradas = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) entradas.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar entradas recorrentes", e);
        }
        return entradas;
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

        contaRepo.buscarPorId(rs.getLong("conta_id")).ifPresent(entrada::setConta);

        long catId = rs.getLong("categoria_id");
        if (!rs.wasNull()) categoriaRepo.buscarPorId(catId).ifPresent(entrada::setCategoria);

        return entrada;
    }
}