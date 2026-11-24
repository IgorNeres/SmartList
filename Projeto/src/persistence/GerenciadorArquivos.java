package persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.ListaCompras;
import model.ItemCompra;
import model.ItemUnidade;
import model.ItemPorPeso;

import exception.FormatoArquivoException;
import exception.ValorInvalidoException;
import exception.ItemDuplicadoException;

public class GerenciadorArquivos {

    private static final String PASTA_MODELOS = "modelos";

    public GerenciadorArquivos() {

        File pasta = new File(PASTA_MODELOS);

        if (!pasta.exists()) {
            pasta.mkdirs();
        }
    }

    public void exportarListaTxt(ListaCompras lista, File arquivo)
            throws IOException {

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo))) {

            escritor.write("ORCAMENTO;" +
                    String.format(Locale.US, "%.2f", lista.getOrcamento()));

            escritor.newLine();

            for (ItemCompra item : lista.listarPorCategoria("Todas")) {

                String linha;

                if (item instanceof ItemUnidade) {

                    ItemUnidade unidade = (ItemUnidade) item;

                    linha = item.getCategoria() + ";UNIDADE;"
                            + item.getNome() + ";"
                            + unidade.getQuantidade() + ";"
                            + String.format(Locale.US, "%.2f", item.getPrecoUnitario()) + ";"
                            + item.isColetado();

                } else if (item instanceof ItemPorPeso) {

                    ItemPorPeso peso = (ItemPorPeso) item;

                    linha = item.getCategoria() + ";PESO;"
                            + item.getNome() + ";"
                            + String.format(Locale.US, "%.2f", peso.getPesoKg()) + ";"
                            + String.format(Locale.US, "%.2f", item.getPrecoUnitario()) + ";"
                            + item.isColetado();

                } else {
                    throw new IOException("Tipo de item desconhecido.");
                }

                escritor.write(linha);
                escritor.newLine();
            }
        }
    }

    public ListaCompras importarListaTxt(File arquivo)
            throws FormatoArquivoException {

        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {

            String cabecalho = leitor.readLine();

            if (cabecalho == null || !cabecalho.startsWith("ORCAMENTO;")) {
                throw new FormatoArquivoException(
                        "Arquivo vazio ou sem cabeçalho de orçamento."
                );
            }

            String[] dadosOrcamento = cabecalho.split(";", -1);

            if (dadosOrcamento.length != 2) {
                throw new FormatoArquivoException("Cabeçalho inválido.");
            }

            double orcamento = Double.parseDouble(dadosOrcamento[1]);

            ListaCompras lista = new ListaCompras(orcamento);

            String linha;

            while ((linha = leitor.readLine()) != null) {

                if (linha.trim().isEmpty()) {
                    throw new FormatoArquivoException("Linha vazia no arquivo.");
                }

                String[] dados = linha.split(";", -1);

                if (dados.length != 6) {
                    throw new FormatoArquivoException("Quantidade de colunas inválida.");
                }

                String categoria = dados[0];
                String tipo = dados[1];
                String nome = dados[2];
                double quantidadeOuPeso = Double.parseDouble(dados[3]);
                double preco = Double.parseDouble(dados[4]);

                if (!dados[5].equalsIgnoreCase("true")
                        && !dados[5].equalsIgnoreCase("false")) {
                    throw new FormatoArquivoException("Estado de coleta inválido.");
                }

                boolean coletado = Boolean.parseBoolean(dados[5]);

                ItemCompra item;

                if (tipo.equalsIgnoreCase("UNIDADE")) {

                    int quantidade = Integer.parseInt(dados[3]);

                    item = new ItemUnidade(
                            nome, categoria, preco, quantidade
                    );

                } else if (tipo.equalsIgnoreCase("PESO")) {

                    item = new ItemPorPeso(
                            nome, categoria, preco, quantidadeOuPeso
                    );

                } else {
                    throw new FormatoArquivoException("Tipo de item inválido.");
                }

                item.setColetado(coletado);
                lista.adicionarItem(item);
            }

            return lista;

        } catch (IOException | NumberFormatException
                | ValorInvalidoException | ItemDuplicadoException e) {

            throw new FormatoArquivoException(
                    "Erro ao importar arquivo: " + e.getMessage()
            );
        }
    }

    
    public void salvarModeloPreFabricado(String nomeModelo, ListaCompras lista)
            throws IOException {

        validarNomeModelo(nomeModelo);

        File arquivo = new File(PASTA_MODELOS, nomeModelo + ".txt");

        ListaCompras copia;

        try {
            copia = new ListaCompras(lista.getOrcamento());

            for (ItemCompra item : lista.listarPorCategoria("Todas")) {

                ItemCompra novoItem;

                if (item instanceof ItemUnidade) {

                    ItemUnidade unidade = (ItemUnidade) item;

                    novoItem = new ItemUnidade(
                            item.getNome(),
                            item.getCategoria(),
                            item.getPrecoUnitario(),
                            unidade.getQuantidade()
                    );

                } else if (item instanceof ItemPorPeso) {

                    ItemPorPeso peso = (ItemPorPeso) item;

                    novoItem = new ItemPorPeso(
                            item.getNome(),
                            item.getCategoria(),
                            item.getPrecoUnitario(),
                            peso.getPesoKg()
                    );

                } else {
                    throw new IOException("Tipo de item desconhecido.");
                }

                novoItem.setColetado(false);

                copia.adicionarItem(novoItem);
            }

        } catch (ValorInvalidoException | ItemDuplicadoException e) {
            throw new IOException("Erro ao criar modelo: " + e.getMessage());
        }

        exportarListaTxt(copia, arquivo);
    }

    public ListaCompras carregarModeloPreFabricado(String nomeModelo)
            throws FormatoArquivoException {

        validarNomeModelo(nomeModelo);

        File arquivo = new File(PASTA_MODELOS, nomeModelo + ".txt");

        if (!arquivo.exists()) {
            throw new FormatoArquivoException("Modelo não encontrado.");
        }

        return importarListaTxt(arquivo);
    }

    public List<String> listarModelosDisponiveis() {

        List<String> modelos = new ArrayList<>();

        File pasta = new File(PASTA_MODELOS);

        File[] arquivos = pasta.listFiles();

        if (arquivos == null) {
            return modelos;
        }

        for (File arquivo : arquivos) {

            if (arquivo.isFile() && arquivo.getName().endsWith(".txt")) {

                String nome = arquivo.getName();

                modelos.add(nome.substring(0, nome.length() - 4));
            }
        }

        return modelos;
    }

    private void validarNomeModelo(String nomeModelo) {

        if (nomeModelo == null || nomeModelo.trim().isEmpty()
                || nomeModelo.contains("/")
                || nomeModelo.contains("\\")
                || nomeModelo.contains("..")) {

            throw new IllegalArgumentException("Nome de modelo inválido.");
        }
    }

}