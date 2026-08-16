package br.com.MeuPlanner.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import br.com.MeuPlanner.model.Usuario;

public class UsuarioRepository extends BaseRepository {

    @Override
    protected String nomeEntidade() {
        return "usuário";
    }

    public void salvar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (username, senha_hash) VALUES (?, ?)";
        Long id = executarInsert(sql, stmt -> {
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getSenhaHash());
        });
        if (id != null) usuario.setId(id);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        return consultarUm(sql, stmt -> stmt.setString(1, username), this::mapear);
    }

    public boolean existeUsername(String username) {
        return buscarPorUsername(username).isPresent();
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setUsername(rs.getString("username"));
        usuario.setSenhaHash(rs.getString("senha_hash"));
        return usuario;
    }
}
