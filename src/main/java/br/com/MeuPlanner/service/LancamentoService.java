package br.com.MeuPlanner.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import br.com.MeuPlanner.model.Categoria;
import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.model.Entrada;
import br.com.MeuPlanner.model.Gasto;
import br.com.MeuPlanner.model.TipoGasto;
import br.com.MeuPlanner.model.TipoRecorrencia;
import br.com.MeuPlanner.repository.EntradaRepository;
import br.com.MeuPlanner.repository.GastoRepository;

public class LancamentoService {

    private final EntradaRepository entradaRepo = new EntradaRepository();
    private final GastoRepository gastoRepo = new GastoRepository();

    public Entrada adicionarEntrada(String descricao, BigDecimal valor, LocalDate data,
                                    TipoRecorrencia recorrencia, Conta conta, Categoria categoria) {
        validarValor(valor);
        if (conta == null) throw new IllegalArgumentException("Conta é obrigatória!");

        Entrada entrada = new Entrada(descricao, valor, data, recorrencia, conta, categoria);
        entradaRepo.salvar(entrada);
        return entrada;
    }

    public Gasto adicionarGasto(String descricao, BigDecimal valor, LocalDate data,
                                TipoGasto tipoGasto, TipoRecorrencia recorrencia,
                                Conta conta, Categoria categoria) {
        validarValor(valor);
        if (conta == null) throw new IllegalArgumentException("Conta é obrigatória!");

        Gasto gasto = new Gasto(descricao, valor, data, tipoGasto, recorrencia, conta, categoria);
        gastoRepo.salvar(gasto);
        return gasto;
    }

    public Gasto adicionarGastoParcelado(String descricao, BigDecimal valorTotal, LocalDate dataInicio,
                                          TipoGasto tipoGasto, int totalParcelas,
                                          Conta conta, Categoria categoria) {
        validarValor(valorTotal);
        if (totalParcelas <= 0) throw new IllegalArgumentException("Total de parcelas deve ser maior que zero!");

        BigDecimal valorParcela = valorTotal.divide(BigDecimal.valueOf(totalParcelas), 2, java.math.RoundingMode.HALF_UP);
        Gasto primeiraParcelaGasto = null;

        for (int i = 1; i <= totalParcelas; i++) {
            LocalDate dataParcela = dataInicio.plusMonths(i - 1);
            Gasto gasto = new Gasto(descricao, valorParcela, dataParcela, tipoGasto, TipoRecorrencia.PARCELAMENTO, conta, categoria);
            gasto.setParcelaAtual(i);
            gasto.setTotalParcelas(totalParcelas);
            gastoRepo.salvar(gasto);
            if (i == 1) primeiraParcelaGasto = gasto;
        }

        return primeiraParcelaGasto;
    }

    public void deletarEntrada(Long id) {
        entradaRepo.deletar(id);
    }

    public void deletarGasto(Long id) {
        gastoRepo.deletar(id);
    }

    public List<Entrada> listarEntradasDoMes(YearMonth mes) {
        return entradaRepo.listarPorMes(mes);
    }

    public List<Gasto> listarGastosDoMes(YearMonth mes) {
        return gastoRepo.listarPorMes(mes);
    }

    public BigDecimal totalEntradasDoMes(YearMonth mes) {
        return entradaRepo.listarPorMes(mes).stream()
                .map(Entrada::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalGastosDoMes(YearMonth mes) {
        return gastoRepo.listarPorMes(mes).stream()
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal saldoDoMes(YearMonth mes) {
        return totalEntradasDoMes(mes).subtract(totalGastosDoMes(mes));
    }

    private void validarValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Valor deve ser maior que zero!");
    }
}