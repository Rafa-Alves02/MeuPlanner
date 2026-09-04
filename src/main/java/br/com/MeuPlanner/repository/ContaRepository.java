package br.com.MeuPlanner.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import br.com.MeuPlanner.model.Conta;

public class ContaRepository extends BaseRepository {

    @Override
    protected String nomeEntidade() {
        return "conta";
    }

    public void salvar(Conta conta) {
        String sql = "INSERT INTO contas (nome, tipo, banco, saldo_inicial, saldo_atual) VALUES (?, ?, ?, ?, ?)";
        Long id = executarInsert(sql, stmt -> {
            stmt.setString(1, conta.getNome());
            stmt.setString(2, conta.getTipo().name());
            stmt.setString(3, conta.getBanco());
            stmt.setBigDecimal(4, conta.getSaldoInicial());
            stmt.setBigDecimal(5, conta.getSaldoAtual());
        });
        if (id != null) conta.setId(id);
    }

    public void atualizar(Conta conta) {
        String sql = "UPDATE contas SET nome = ?, tipo = ?, banco = ?, saldo_atual = ?, "
                + "pluggy_item_id = ?, pluggy_account_id = ? WHERE id = ?";
        executarUpdate(sql, stmt -> {
            stmt.setString(1, conta.getNome());
            stmt.setString(2, conta.getTipo().name());
            stmt.setString(3, conta.getBanco());
            stmt.setBigDecimal(4, conta.getSaldoAtual());
            stmt.setString(5, conta.getPluggyItemId());
            stmt.setString(6, conta.getPluggyAccountId());
            stmt.setLong(7, conta.getId());
        });
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM contas WHERE id = ?";
        executarUpdate(sql, stmt -> stmt.setLong(1, id));
    }

    public Optional<Conta> buscarPorId(Long id) {
        String sql = "SELECT * FROM contas WHERE id = ?";
        return consultarUm(sql, stmt -> stmt.setLong(1, id), this::mapear);
    }

    public List<Conta> listarTodas() {
        String sql = "SELECT * FROM contas ORDER BY nome";
        return consultarLista(sql, stmt -> {}, this::mapear);
    }

    private Conta mapear(ResultSet rs) throws SQLException {
        Conta conta = new Conta();
        conta.setId(rs.getLong("id"));
        conta.setNome(rs.getString("nome"));
        conta.setTipo(Conta.TipoConta.valueOf(rs.getString("tipo")));
        conta.setBanco(rs.getString("banco"));
        conta.setSaldoInicial(rs.getBigDecimal("saldo_inicial"));
        conta.setSaldoAtual(rs.getBigDecimal("saldo_atual"));
        conta.setPluggyItemId(rs.getString("pluggy_item_id"));
        conta.setPluggyAccountId(rs.getString("pluggy_account_id"));
        return conta;
    }
}
