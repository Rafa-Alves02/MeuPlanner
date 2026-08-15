package br.com.MeuPlanner.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    void salvar(T entidade);
    void deletar(ID id);
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
}