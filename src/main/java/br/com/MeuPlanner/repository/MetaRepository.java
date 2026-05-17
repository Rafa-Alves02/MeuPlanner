package br.com.MeuPlanner.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.MeuPlanner.config.ConnectionFactory;
import br.com.MeuPlanner.model.Meta;

public class MetaRepository {

    private final ContaRepository contaRepo = new ContaRepository();

    public void salvar(Meta meta) {
        String sql = """
                INSERT INTO metas (descricao, valor_alvo, valor_atual, data_limite, conta_id, concluida)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, meta.getDescricao());
            stmt.setBigDecimal(2, meta.getValorAlvo());
            stmt.setBigDecimal(3, meta.getValorAtual());
            stmt.setDate(4, Date.valueOf(meta.getDataLimite()));
            stmt.setObject(5, meta.getConta() != null ? meta.getConta().getId() : null);
            stmt.setBoolean(6, meta.isConcluida());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) meta.setId(keys.getLong(1));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar meta", e);
        }
    }

    public void atualizar(Meta meta) {
        String sql = "UPDATE metas SET valor_atual = ?, concluida = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, meta.getValorAtual());
            stmt.setBoolean(2, meta.isConcluida());
            stmt.setLong(3, meta.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar meta", e);
        }
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM metas WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar meta", e);
        }
    }

    public Optional<Meta> buscarPorId(Long id) {
        String sql = "SELECT * FROM metas WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar meta", e);
        }
        return Optional.empty();
    }

    public List<Meta> listarAtivas() {
        String sql = "SELECT * FROM metas WHERE concluida = false ORDER BY data_limite";
        List<Meta> metas = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) metas.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar metas", e);
        }
        return metas;
    }

    private Meta mapear(ResultSet rs) throws SQLException {
        Meta meta = new Meta();
        meta.setId(rs.getLong("id"));
        meta.setDescricao(rs.getString("descricao"));
        meta.setValorAlvo(rs.getBigDecimal("valor_alvo"));
        meta.setValorAtual(rs.getBigDecimal("valor_atual"));
        meta.setDataLimite(rs.getDate("data_limite").toLocalDate());
        meta.setConcluida(rs.getBoolean("concluida"));
        long contaId = rs.getLong("conta_id");
        if (!rs.wasNull()) contaRepo.buscarPorId(contaId).ifPresent(meta::setConta);
        return meta;
    }
}