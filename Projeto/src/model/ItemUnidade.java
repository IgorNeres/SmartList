package model;

import exception.ValorInvalidoException;

public class ItemUnidade extends ItemCompra {

    private int quantidade;

    public ItemUnidade(String nome, String categoria, double precoUnitario, int quantidade)
            throws ValorInvalidoException {

        super(nome, categoria, precoUnitario);

        if (quantidade <= 0) {
            throw new ValorInvalidoException("A quantidade deve ser maior que zero.");
        }

        this.quantidade = quantidade;
    }

    @Override
    public double calcularSubtotal() {
        return getPrecoUnitario() * quantidade;
    }

    public void setQuantidade(int quantidade) throws ValorInvalidoException {

        if (quantidade <= 0) {
            throw new ValorInvalidoException("A quantidade deve ser maior que zero.");
        }

        this.quantidade = quantidade;
    }

    public int getQuantidade() {
        return quantidade;
    }

    @Override
    public String toString() {

        String status = isColetado() ? "[Coletado]" : "[Pendente]";

        return status + " " + getNome()
                + " | " + quantidade + " un."
                + " | Preço unitário: R$ " + String.format("%.2f", getPrecoUnitario())
                + " | Subtotal: R$ " + String.format("%.2f", calcularSubtotal());
    }

}