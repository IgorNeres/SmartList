package app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import model.ListaCompras;
import model.ItemCompra;
import model.ItemUnidade;
import model.ItemPorPeso;

import persistence.GerenciadorArquivos;

import exception.ValorInvalidoException;
import exception.ItemDuplicadoException;
import exception.SaldoInsuficienteException;
import exception.FormatoArquivoException;

public class Exec {

    public static void main(String[] args) {

        try {

            System.out.println("===== SMARTLIST =====");

            ListaCompras lista = new ListaCompras(100.00);

            ItemCompra arroz = new ItemUnidade(
                    "Arroz", "Alimentação", 20.00, 2
            );

            ItemCompra feijao = new ItemUnidade(
                    "Feijão", "Alimentação", 8.00, 2
            );

            ItemCompra tomate = new ItemPorPeso(
                    "Tomate", "Hortifruti", 15.00, 3.0
            );

            lista.adicionarItem(arroz);
            lista.adicionarItem(feijao);
            lista.adicionarItem(tomate);

            System.out.println("\n===== LISTA DE COMPRAS =====");

            imprimirLista(lista);

            System.out.printf(
                    "\nTotal planejado: R$ %.2f%n",
                    lista.calcularTotalPlanejado()
            );


            arroz.alternarColetado();
            tomate.alternarColetado();

            System.out.println("\n===== CONTROLE DO ORÇAMENTO =====");

            System.out.printf(
                    "Total coletado: R$ %.2f%n",
                    lista.calcularTotalColetado()
            );

            System.out.printf(
                    "Saldo restante: R$ %.2f%n",
                    lista.calcularSaldoRestante()
            );

            System.out.println(
                    "Saldo disponível: " + lista.temSaldoDisponivel()
            );


            System.out.println("\n===== TESTANDO EXCEÇÕES =====");

            try {

                ItemCompra invalido = new ItemUnidade(
                        "Produto inválido", "Teste", -10.00, 1
                );

            } catch (ValorInvalidoException e) {

                System.out.println(
                        "ValorInvalidoException: " + e.getMessage()
                );
            }


            try {

                ItemCompra duplicado = new ItemUnidade(
                        "ARROZ", "Alimentação", 20.00, 1
                );

                lista.adicionarItem(duplicado);

            } catch (ValorInvalidoException | ItemDuplicadoException e) {

                System.out.println(
                        e.getClass().getSimpleName() + ": " + e.getMessage()
                );
            }


            try {

                ItemCompra leite = new ItemUnidade(
                        "Leite", "Laticínios", 10.00, 2
                );

                lista.adicionarItem(leite);
                leite.alternarColetado();

                lista.verificarOrcamento();

            } catch (ValorInvalidoException | ItemDuplicadoException
                    | SaldoInsuficienteException e) {

                System.out.println(
                        e.getClass().getSimpleName() + ": " + e.getMessage()
                );
            }


            GerenciadorArquivos gerenciador = new GerenciadorArquivos();

            File arquivoInvalido = new File("arquivo_invalido.txt");

            try (FileWriter escritor = new FileWriter(arquivoInvalido)) {

                escritor.write("CONTEUDO_INVALIDO");

            }

            try {

                gerenciador.importarListaTxt(arquivoInvalido);

            } catch (FormatoArquivoException e) {

                System.out.println(
                        "FormatoArquivoException: " + e.getMessage()
                );
            }


            System.out.println("\n===== TESTANDO ARQUIVOS =====");

            File arquivo = new File("lista_compras.txt");

            gerenciador.exportarListaTxt(lista, arquivo);

            System.out.println("Lista exportada com sucesso!");

            gerenciador.salvarModeloPreFabricado(
                    "MinhaLista", lista
            );

            System.out.println("Modelo salvo com sucesso!");


            ListaCompras listaImportada =
                    gerenciador.importarListaTxt(arquivo);

            System.out.println("\n===== LISTA IMPORTADA =====");

            imprimirLista(listaImportada);

            System.out.printf(
                    "\nTotal planejado original: R$ %.2f%n",
                    lista.calcularTotalPlanejado()
            );

            System.out.printf(
                    "Total planejado importado: R$ %.2f%n",
                    listaImportada.calcularTotalPlanejado()
            );

            System.out.printf(
                    "Total coletado original: R$ %.2f%n",
                    lista.calcularTotalColetado()
            );

            System.out.printf(
                    "Total coletado importado: R$ %.2f%n",
                    listaImportada.calcularTotalColetado()
            );

            System.out.println("\n===== MODELOS DISPONÍVEIS =====");

            List<String> modelos = gerenciador.listarModelosDisponiveis();

            for (String modelo : modelos) {
                System.out.println(modelo);
            }

            System.out.println("\n===== TESTES FINALIZADOS =====");

        } catch (ValorInvalidoException | ItemDuplicadoException
                | FormatoArquivoException | IOException e) {

            System.out.println("Erro durante a execução: " + e.getMessage());
        }
    }


    public static void imprimirLista(ListaCompras lista) {

        String[] categorias = {
                "Alimentação",
                "Hortifruti",
                "Laticínios"
        };

        for (String categoria : categorias) {

            List<ItemCompra> itens = lista.listarPorCategoria(categoria);

            if (!itens.isEmpty()) {

                System.out.println("\nCategoria: " + categoria);

                for (ItemCompra item : itens) {
                    System.out.println(item);
                }
            }
        }
    }

}