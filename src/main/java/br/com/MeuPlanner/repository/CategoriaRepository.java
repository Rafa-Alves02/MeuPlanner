package br.com.MeuPlanner.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.MeuPlanner.config.ConnectionFactory;
import br.com.MeuPlanner.model.Categoria;

public class CategoriaRepository {

    public void salvar(Categoria categoria) {
        String sql = "INSERT INTO categorias (nome, tipo, cor) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getTipo().name());
            stmt.setString(3, categoria.getCor());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) categoria.setId(keys.getLong(1));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar categoria", e);
        }
    }

    public void atualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nome = ?, tipo = ?, cor = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getTipo().name());
            stmt.setString(3, categoria.getCor());
            stmt.setLong(4, categoria.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar categoria", e);
        }
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM categorias WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar categoria", e);
        }
    }

    public Optional<Categoria> buscarPorId(Long id) {
        String sql = "SELECT * FROM categorias WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar categoria", e);
        }
        return Optional.empty();
    }

    public List<Categoria> listarTodas() {
        String sql = "SELECT * FROM categorias ORDER BY tipo, nome";
        List<Categoria> categorias = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) categorias.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar categorias", e);
        }
        return categorias;
    }

    public List<Categoria> listarPorTipo(Categoria.TipoCategoria tipo) {
        String sql = "SELECT * FROM categorias WHERE tipo = ? ORDER BY nome";
        List<Categoria> categorias = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipo.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) categorias.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar categorias por tipo", e);
        }
        return categorias;
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