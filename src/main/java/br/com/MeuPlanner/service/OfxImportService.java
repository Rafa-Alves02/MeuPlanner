package br.com.MeuPlanner.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.MeuPlanner.model.Categoria;
import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.model.Entrada;
import br.com.MeuPlanner.model.Gasto;
import br.com.MeuPlanner.model.TipoGasto;
import br.com.MeuPlanner.model.TipoRecorrencia;
import br.com.MeuPlanner.ofx.OfxParser;
import br.com.MeuPlanner.ofx.TransacaoOfx;
import br.com.MeuPlanner.repository.EntradaRepository;
import br.com.MeuPlanner.repository.GastoRepository;

public class OfxImportService {

    private final EntradaRepository entradaRepo = new EntradaRepository();
    private final GastoRepository gastoRepo = new GastoRepository();

    public record ItemImportacao(TransacaoOfx transacao, boolean jaImportado) {}

    public List<ItemImportacao> lerParaRevisao(File arquivo, Conta conta) {
        List<TransacaoOfx> transacoes = OfxParser.parse(arquivo);
        List<ItemImportacao> itens = new ArrayList<>(transacoes.size());
        for (TransacaoOfx transacao : transacoes) {
            itens.add(new ItemImportacao(transacao, jaFoiImportada(conta, transacao)));
        }
        return itens;
    }

    public int importar(Conta conta, Map<TransacaoOfx, Categoria> selecionadas) {
        int importados = 0;
        for (var entry : selecionadas.entrySet()) {
            TransacaoOfx transacao = entry.getKey();
            Categoria categoria = entry.getValue();
            if (jaFoiImportada(conta, transacao)) continue;

            if (transacao.isEntrada()) {
                Entrada entrada = new Entrada(transacao.descricao(), transacao.valor(), transacao.data(),
                        TipoRecorrencia.UNICA, conta, categoria);
                entrada.setFitidOfx(transacao.fitid());
                entradaRepo.salvar(entrada);
            } else {
                Gasto gasto = new Gasto(transacao.descricao(), transacao.valor().abs(), transacao.data(),
                        TipoGasto.VARIAVEL, TipoRecorrencia.UNICA, conta, categoria);
                gasto.setFitidOfx(transacao.fitid());
                gastoRepo.salvar(gasto);
            }
            importados++;
        }
        return importados;
    }

    private boolean jaFoiImportada(Conta conta, TransacaoOfx transacao) {
        return transacao.isEntrada()
                ? entradaRepo.existeFitid(conta.getId(), transacao.fitid())
                : gastoRepo.existeFitid(conta.getId(), transacao.fitid());
    }
}
