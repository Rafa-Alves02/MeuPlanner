package br.com.MeuPlanner.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import br.com.MeuPlanner.model.Categoria;

public class CategorizacaoAprendidaRepository extends BaseRepository {

    @Override
    protected String nomeEntidade() {
        return "categorização aprendida";
    }

    public Optional<Categoria> buscarCategoriaPorPadrao(String padrao) {
        String sql = """
                SELECT c.* FROM categorizacao_aprendida ca
                JOIN categorias c ON c.id = ca.categoria_id
                WHERE ca.padrao = ?
                """;
        return consultarUm(sql, stmt -> stmt.setString(1, padrao), this::mapearCategoria);
    }

    public void aprender(String padrao, Long categoriaId) {
        String sql = """
                INSERT INTO categorizacao_aprendida (padrao, categoria_id) VALUES (?, ?)
                ON DUPLICATE KEY UPDATE categoria_id = VALUES(categoria_id), atualizado_em = CURRENT_TIMESTAMP
                """;
        executarUpdate(sql, stmt -> {
            stmt.setString(1, padrao);
            stmt.setLong(2, categoriaId);
        });
    }

    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getLong("id"));
        categoria.setNome(rs.getString("nome"));
        categoria.setTipo(Categoria.TipoCategoria.valueOf(rs.getString("tipo")));
        categoria.setCor(rs.getString("cor"));
        return categoria;
    }
}
