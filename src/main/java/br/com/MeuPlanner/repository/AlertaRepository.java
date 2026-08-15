package br.com.MeuPlanner.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;

import br.com.MeuPlanner.model.Alerta;

public class AlertaRepository extends BaseRepository {

    private final CategoriaRepository categoriaRepo = new CategoriaRepository();

    @Override
    protected String nomeEntidade() {
        return "alerta";
    }

    public void salvar(Alerta alerta) {
        String sql = """
                INSERT INTO alertas (descricao, categoria_id, valor_limite, mes_referencia, disparado)
                VALUES (?, ?, ?, ?, ?)
                """;
        Long id = executarInsert(sql, stmt -> {
            stmt.setString(1, alerta.getDescricao());
            stmt.setObject(2, alerta.getCategoria() != null ? alerta.getCategoria().getId() : null);
            stmt.setBigDecimal(3, alerta.getValorLimite());
            stmt.setString(4, alerta.getMesReferencia().toString());
            stmt.setBoolean(5, alerta.isDisparado());
        });
        if (id != null) alerta.setId(id);
    }

    public void marcarDisparado(Long id) {
        String sql = "UPDATE alertas SET disparado = true WHERE id = ?";
        executarUpdate(sql, stmt -> stmt.setLong(1, id));
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM alertas WHERE id = ?";
        executarUpdate(sql, stmt -> stmt.setLong(1, id));
    }

    public List<Alerta> listarPorMes(YearMonth mes) {
        String sql = "SELECT * FROM alertas WHERE mes_referencia = ?";
        return consultarLista(sql, stmt -> stmt.setString(1, mes.toString()), this::mapear);
    }

    public List<Alerta> listarNaoDisparados(YearMonth mes) {
        String sql = "SELECT * FROM alertas WHERE mes_referencia = ? AND disparado = false";
        return consultarLista(sql, stmt -> stmt.setString(1, mes.toString()), this::mapear);
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
