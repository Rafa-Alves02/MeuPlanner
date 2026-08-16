package br.com.MeuPlanner.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.com.MeuPlanner.model.Categoria;
import br.com.MeuPlanner.repository.CategorizacaoAprendidaRepository;

public class CategorizacaoIAService {

    public record Sugestao(Categoria categoria, boolean aprendida) {}

    private final OllamaClient ollamaClient = new OllamaClient();
    private final CategoriaService categoriaService = new CategoriaService();
    private final CategorizacaoAprendidaRepository aprendidaRepo = new CategorizacaoAprendidaRepository();

    public Sugestao sugerirCategoria(String descricaoTransacao, Categoria.TipoCategoria tipo) {
        String padrao = normalizarPadrao(descricaoTransacao);

        Optional<Categoria> aprendida = aprendidaRepo.buscarCategoriaPorPadrao(padrao);
        if (aprendida.isPresent()) {
            return new Sugestao(aprendida.get(), true);
        }

        List<Categoria> categorias = tipo == Categoria.TipoCategoria.ENTRADA
                ? categoriaService.listarEntradas()
                : categoriaService.listarSaidas();

        if (categorias.isEmpty()) return new Sugestao(null, false);

        String nomesDisponiveis = categorias.stream()
                .map(Categoria::getNome)
                .collect(Collectors.joining(", "));

        String prompt = """
                Você categoriza transações financeiras de um app de controle financeiro
                pessoal brasileiro. Dada a descrição de uma transação, responda APENAS
                com o nome exato de uma destas categorias, sem explicação nenhuma:
                %s

                Descrição da transação: "%s"

                Categoria:
                """.formatted(nomesDisponiveis, descricaoTransacao);

        String resposta = ollamaClient.gerar(prompt).trim();

        Categoria categoria = categorias.stream()
                .filter(c -> c.getNome().equalsIgnoreCase(resposta))
                .findFirst()
                .orElse(null);

        return new Sugestao(categoria, false);
    }

    public void corrigir(String descricaoTransacao, Categoria categoriaCorreta) {
        String padrao = normalizarPadrao(descricaoTransacao);
        aprendidaRepo.aprender(padrao, categoriaCorreta.getId());
    }

    private String normalizarPadrao(String descricao) {
        return descricao.toUpperCase().replaceAll("[0-9]", "").trim();
    }
}
