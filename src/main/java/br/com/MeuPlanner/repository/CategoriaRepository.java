package br.com.MeuPlanner.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import br.com.MeuPlanner.model.Categoria;

public class CategoriaRepository extends BaseRepository {

    @Override
    protected String nomeEntidade() {
        return "categoria";
    }

    public void salvar(Categoria categoria) {
        String sql = "INSERT INTO categorias (nome, tipo, cor) VALUES (?, ?, ?)";
        Long id = executarInsert(sql, stmt -> {
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getTipo().name());
            stmt.setString(3, categoria.getCor());
        });
        if (id != null) categoria.setId(id);
    }

    public void atualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nome = ?, tipo = ?, cor = ? WHERE id = ?";
        executarUpdate(sql, stmt -> {
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getTipo().name());
            stmt.setString(3, categoria.getCor());
            stmt.setLong(4, categoria.getId());
        });
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM categorias WHERE id = ?";
        executarUpdate(sql, stmt -> stmt.setLong(1, id));
    }

    public Optional<Categoria> buscarPorId(Long id) {
        String sql = "SELECT * FROM categorias WHERE id = ?";
        return consultarUm(sql, stmt -> stmt.setLong(1, id), this::mapear);
    }

    public List<Categoria> listarTodas() {
        String sql = "SELECT * FROM categorias ORDER BY tipo, nome";
        return consultarLista(sql, stmt -> {}, this::mapear);
    }

    public List<Categoria> listarPorTipo(Categoria.TipoCategoria tipo) {
        String sql = "SELECT * FROM categorias WHERE tipo = ? ORDER BY nome";
        return consultarLista(sql, stmt -> stmt.setString(1, tipo.name()), this::mapear);
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getLong("id"));
        categoria.setNome(rs.getString("nome"));
        categoria.setTipo(Categoria.TipoCategoria.valueOf(rs.getString("tipo")));
        categoria.setCor(rs.getString("cor"));
        return categoria;
    }
}
