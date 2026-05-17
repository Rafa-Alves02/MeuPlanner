package br.com.MeuPlanner.service;

import java.util.List;

import br.com.MeuPlanner.model.Categoria;
import br.com.MeuPlanner.repository.CategoriaRepository;

public class CategoriaService {

    private final CategoriaRepository categoriaRepo = new CategoriaRepository();

    public Categoria criarCategoria(String nome, Categoria.TipoCategoria tipo, String cor) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome da categoria não pode ser vazio!");

        Categoria categoria = new Categoria(nome, tipo, cor);
        categoriaRepo.salvar(categoria);
        return categoria;
    }

    public void atualizar(Categoria categoria) {
        categoriaRepo.atualizar(categoria);
    }

    public void deletar(Long id) {
        categoriaRepo.deletar(id);
    }

    public List<Categoria> listarTodas() {
        return categoriaRepo.listarTodas();
    }

    public List<Categoria> listarEntradas() {
        return categoriaRepo.listarPorTipo(Categoria.TipoCategoria.ENTRADA);
    }

    public List<Categoria> listarSaidas() {
        return categoriaRepo.listarPorTipo(Categoria.TipoCategoria.SAIDA);
    }
}