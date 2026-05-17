package br.com.MeuPlanner.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.model.Meta;
import br.com.MeuPlanner.repository.MetaRepository;

public class MetaService {

    private final MetaRepository metaRepo = new MetaRepository();

    public Meta criarMeta(String descricao, BigDecimal valorAlvo,
                          LocalDate dataLimite, Conta conta) {
        if (valorAlvo == null || valorAlvo.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Valor alvo deve ser maior que zero!");
        if (dataLimite == null || dataLimite.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Data limite deve ser futura!");

        Meta meta = new Meta(descricao, valorAlvo, dataLimite, conta);
        metaRepo.salvar(meta);
        return meta;
    }

    public void adicionarProgresso(Long metaId, BigDecimal valor) {
        Meta meta = metaRepo.buscarPorId(metaId)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada!"));

        meta.setValorAtual(meta.getValorAtual().add(valor));

        if (meta.getValorAtual().compareTo(meta.getValorAlvo()) >= 0) {
            meta.setValorAtual(meta.getValorAlvo());
            meta.setConcluida(true);
        }

        metaRepo.atualizar(meta);
    }

    public void deletar(Long id) {
        metaRepo.deletar(id);
    }

    public List<Meta> listarAtivas() {
        return metaRepo.listarAtivas();
    }
}