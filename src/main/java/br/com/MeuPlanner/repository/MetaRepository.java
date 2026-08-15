package br.com.MeuPlanner.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import br.com.MeuPlanner.model.Meta;

public class MetaRepository extends BaseRepository {

    private final ContaRepository contaRepo = new ContaRepository();

    @Override
    protected String nomeEntidade() {
        return "meta";
    }

    public void salvar(Meta meta) {
        String sql = """
                INSERT INTO metas (descricao, valor_alvo, valor_atual, data_limite, conta_id, concluida)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        Long id = executarInsert(sql, stmt -> {
            stmt.setString(1, meta.getDescricao());
            stmt.setBigDecimal(2, meta.getValorAlvo());
            stmt.setBigDecimal(3, meta.getValorAtual());
            stmt.setDate(4, Date.valueOf(meta.getDataLimite()));
            stmt.setObject(5, meta.getConta() != null ? meta.getConta().getId() : null);
            stmt.setBoolean(6, meta.isConcluida());
        });
        if (id != null) meta.setId(id);
    }

    public void atualizar(Meta meta) {
        String sql = "UPDATE metas SET valor_atual = ?, concluida = ? WHERE id = ?";
        executarUpdate(sql, stmt -> {
            stmt.setBigDecimal(1, meta.getValorAtual());
            stmt.setBoolean(2, meta.isConcluida());
            stmt.setLong(3, meta.getId());
        });
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM metas WHERE id = ?";
        executarUpdate(sql, stmt -> stmt.setLong(1, id));
    }

    public Optional<Meta> buscarPorId(Long id) {
        String sql = "SELECT * FROM metas WHERE id = ?";
        return consultarUm(sql, stmt -> stmt.setLong(1, id), this::mapear);
    }

    public List<Meta> listarAtivas() {
        String sql = "SELECT * FROM metas WHERE concluida = false ORDER BY data_limite";
        return consultarLista(sql, stmt -> {}, this::mapear);
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
