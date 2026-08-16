package br.com.MeuPlanner.service;

import java.util.List;

import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.ofx.TransacaoOfx;
import br.com.MeuPlanner.pluggy.PluggyClient;
import br.com.MeuPlanner.pluggy.PluggyConta;

public class PluggyImportService {

    private final PluggyClient pluggyClient = new PluggyClient();
    private final OfxImportService ofxImportService = new OfxImportService();

    public String criarConnectToken(String clientUserId) {
        return pluggyClient.criarConnectToken(clientUserId);
    }

    public List<PluggyConta> listarContasDoItem(String itemId) {
        return pluggyClient.listarContas(itemId);
    }

    public List<OfxImportService.ItemImportacao> lerParaRevisao(Conta conta) {
        List<TransacaoOfx> transacoes = pluggyClient.listarTransacoes(conta.getPluggyAccountId()).stream()
                .map(t -> new TransacaoOfx(t.id(), t.data(), t.valor(), t.descricao()))
                .toList();
        return ofxImportService.revisar(transacoes, conta);
    }
}
