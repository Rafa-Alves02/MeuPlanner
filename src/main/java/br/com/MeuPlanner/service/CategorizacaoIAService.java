package br.com.MeuPlanner.service;

import java.util.List;
import java.util.stream.Collectors;

import br.com.MeuPlanner.model.Categoria;

public class CategorizacaoIAService {

    private final OllamaClient ollamaClient = new OllamaClient();
    private final CategoriaService categoriaService = new CategoriaService();

    public Categoria sugerirCategoria(String descricaoTransacao, Categoria.TipoCategoria tipo) {
        List<Categoria> categorias = tipo == Categoria.TipoCategoria.ENTRADA
                ? categoriaService.listarEntradas()
                : categoriaService.listarSaidas();

        if (categorias.isEmpty()) return null;

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

        return categorias.stream()
                .filter(c -> c.getNome().equalsIgnoreCase(resposta))
                .findFirst()
                .orElse(null);
    }
}
