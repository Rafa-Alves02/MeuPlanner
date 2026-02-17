package br.com.MeuPlanner.view;

import br.com.MeuPlanner.model.Entrada;
import br.com.MeuPlanner.model.Gasto;
import br.com.MeuPlanner.model.TipoGasto;
import br.com.MeuPlanner.model.TipoRecorrencia;
import br.com.MeuPlanner.repository.FinanceiroRepository;
import br.com.MeuPlanner.service.FinanceiroService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class Menu {

    private static final Scanner sc = new Scanner(System.in);
    private static final FinanceiroService service = new FinanceiroService();

    public static void exibir() {

        int opcao;

        do {
            System.out.println("\n=== CONTROLE FINANCEIRO ===");
            System.out.println("1 - Adicionar Entrada");
            System.out.println("2 - Adicionar Gasto");
            System.out.println("3 - Ver Resumo do Mês");
            System.out.println("4 - Sair");
            System.out.print("Escolha: ");

            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1 -> adicionarEntrada();
                case 2 -> adicionarGasto();
                case 3 -> mostrarResumo();
                case 4 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 4);
    }

    private static void adicionarEntrada() {

        service.verificarFechamentoAutomatico();

        System.out.print("Descrição da entrada: ");
        String descricao = sc.nextLine();

        System.out.print("Valor: ");
        double valor = sc.nextDouble();
        sc.nextLine();

        Entrada entrada = new Entrada(
                descricao,
                BigDecimal.valueOf(valor),
                LocalDate.now(),
                TipoRecorrencia.UNICA,
                null,
                null
        );
        FinanceiroRepository.adicionarEntrada(entrada);

        System.out.println("Entrada adicionada!");
    }

    private static void adicionarGasto() {

        service.verificarFechamentoAutomatico();

        System.out.print("Descrição do gasto: ");
        String descricao = sc.nextLine();

        System.out.print("Valor: ");
        double valor = sc.nextDouble();
        sc.nextLine();

        System.out.println("Tipo do gasto:");
        for (TipoGasto tipo : TipoGasto.values()) {
            System.out.println("- " + tipo);
        }

        System.out.print("Escolha o tipo: ");
        TipoGasto tipoGasto = TipoGasto.valueOf(sc.nextLine().toUpperCase());


        Gasto gasto = new Gasto(
                descricao,
                BigDecimal.valueOf(valor),
                tipoGasto,
                LocalDate.now(),
                TipoRecorrencia.UNICA,
                null,
                null
        );

        FinanceiroRepository.adicionarGasto(gasto);

        System.out.println("Gasto adicionado!");
    }

    private static void mostrarResumo() {

        service.verificarFechamentoAutomatico();

        BigDecimal totalEntradas = service.totalEntradas();
        BigDecimal totalGastos = service.totalGastos();
        BigDecimal saldo = totalEntradas.subtract(totalGastos);

        System.out.println("\n=== RESUMO FINANCEIRO ===");
        System.out.println("Entradas: R$ " + totalEntradas);
        System.out.println("Gastos:   R$ " + totalGastos);
        System.out.println("Saldo:    R$ " + saldo);

        if (saldo.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Mês negativo!");
        } else if (saldo.compareTo(
                totalEntradas.multiply(BigDecimal.valueOf(0.1))
        ) < 0) {
            System.out.println("Mês apertado!");
        } else {
            System.out.println("Mês tranquilo!");
        }
    }
}
