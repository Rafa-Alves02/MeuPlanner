package br.com.MeuPlanner.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import br.com.MeuPlanner.config.ConnectionFactory;
import br.com.MeuPlanner.model.Alerta;

public class AlertaRepository {

    private final CategoriaRepository categoriaRepo = new CategoriaRepository();

    public void salvar(Alerta alerta) {
        String sql = """
                INSERT INTO alertas (descricao, categoria_id, valor_limite, mes_referencia, disparado)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, alerta.getDescricao());
            stmt.setObject(2, alerta.getCategoria() != null ? alerta.getCategoria().getId() : null);
            stmt.setBigDecimal(3, alerta.getValorLimite());
            stmt.setString(4, alerta.getMesReferencia().toString());
            stmt.setBoolean(5, alerta.isDisparado());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) alerta.setId(keys.getLong(1));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar alerta", e);
        }
    }

    public void marcarDisparado(Long id) {
        String sql = "UPDATE alertas SET disparado = true WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao disparar alerta", e);
        }
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM alertas WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar alerta", e);
        }
    }

    public List<Alerta> listarPorMes(YearMonth mes) {
        String sql = "SELECT * FROM alertas WHERE mes_referencia = ?";
        List<Alerta> alertas = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mes.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) alertas.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar alertas", e);
        }
        return alertas;
    }

    public List<Alerta> listarNaoDisparados(YearMonth mes) {
        String sql = "SELECT * FROM alertas WHERE mes_referencia = ? AND disparado = false";
        List<Alerta> alertas = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mes.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) alertas.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar alertas não disparados", e);
        }
        return alertas;
    }

    private Alerta mapear(ResultSet rs) throws SQLException {
        Alerta alerta = new Alerta();
        alerta.setId(rs.getLong("id"));
        alerta.setDescricao(rs.getString("descricao"));
        alerta.setValorLimite(rs.getBigDecimal("valor_limite"));
        alerta.setMesReferencia(YearMonth.parse(rs.getString("mes_referencia")));
        alerta.setDisparado(rs.getBoolean("disparado"));
        long catId = rs.getLong("categoria_id");
        if (!rs.wasNull()) categoriaRepo.buscarPorId(catId).ifPresent(alerta::setCategoria);
        return alerta;
    }
}