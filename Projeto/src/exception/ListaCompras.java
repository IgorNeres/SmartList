package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import exception.ValorInvalidoException;
import exception.ItemDuplicadoException;
import exception.SaldoInsuficienteException;

public class ListaCompras {

    private double orcamento;
    private HashMap<String, ArrayList<ItemCompra>> itensPorCategoria;

    																																						public ListaCompras(double orcamento) throws ValorInvalidoException {

        if (orcamento <= 0 || !Double.isFinite(orcamento)) {
            throw new ValorInvalidoException("O orçamento deve ser maior que zero.");
        }

        this.orcamento = orcamento;
        this.itensPorCategoria = new HashMap<>();
    }

    public void definirOrcamento(double valor) throws ValorInvalidoException {

        if (valor <= 0 || !Double.isFinite(valor)) {
            throw new ValorInvalidoException("O orçamento deve ser maior que zero.");
        }

        this.orcamento = valor;
    }

    public void adicionarItem(ItemCompra item) throws ItemDuplicadoException {

        String categoria = item.getCategoria();

        if (!itensPorCategoria.containsKey(categoria)) {
            itensPorCategoria.put(categoria, new ArrayList<>());
        }

        ArrayList<ItemCompra> lista = itensPorCategoria.get(categoria);

        for (ItemCompra existente : lista) {

            if (existente.getNome().equalsIgnoreCase(item.getNome())) {
                throw new ItemDuplicadoException(
                    "Esse item já existe nesta categoria."
                );
            }
        }

        lista.add(item);
    }

    public void removerItem(String categoria, String nomeItem) {

        ArrayList<ItemCompra> lista = itensPorCategoria.get(categoria);

        if (lista == null) {
            return;
        }

        lista.removeIf(item ->
            item.getNome().equalsIgnoreCase(nomeItem)
        );

        if (lista.isEmpty()) {
            itensPorCategoria.remove(categoria);
        }
    }

    public List<ItemCompra> listarPorCategoria(String categoria) {

        ArrayList<ItemCompra> resultado = new ArrayList<>();

        if (categoria.equalsIgnoreCase("Todas")) {

            for (ArrayList<ItemCompra> lista : itensPorCategoria.values()) {
                resultado.addAll(lista);
            }

        } else {

            ArrayList<ItemCompra> lista = itensPorCategoria.get(categoria);

            if (lista != null) {
                resultado.addAll(lista);
            }
        }

        return resultado;
    }

    public double calcularTotalPlanejado() {

        double total = 0;

        for (ArrayList<ItemCompra> lista : itensPorCategoria.values()) {

            for (ItemCompra item : lista) {
                total += item.calcularSubtotal();
            }
        }

        return total;
    }

    public double calcularTotalColetado() {

        double total = 0;

        for (ArrayList<ItemCompra> lista : itensPorCategoria.values()) {

            for (ItemCompra item : lista) {

                if (item.isColetado()) {
                    total += item.calcularSubtotal();
                }
            }
        }

        return total;
    }

    public double calcularSaldoRestante() {
        return orcamento - calcularTotalColetado();
    }

    public boolean temSaldoDisponivel() {
        return calcularSaldoRestante() >= 0;
    }


    public void verificarOrcamento() throws SaldoInsuficienteException {

        if (!temSaldoDisponivel()) {
            throw new SaldoInsuficienteException(
                "O orçamento foi ultrapassado!"
            );
        }
    }

    public double getOrcamento() {
        return orcamento;
    }
}