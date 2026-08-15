package br.com.MeuPlanner.repository;

import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico do Repository Pattern.
 * Todo repositório concreto trabalha em cima de uma entidade T identificada por ID.
 */
public interface Repository<T, ID> {
    void salvar(T entidade);
    void deletar(ID id);
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
}