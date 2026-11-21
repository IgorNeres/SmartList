package model;

import exception.ValorInvalidoException;

public class ItemPorPeso extends ItemCompra {

    private double pesoKg;

    public ItemPorPeso(String nome, String categoria, double precoUnitario, double pesoKg)
            throws ValorInvalidoException {

        super(nome, categoria, precoUnitario);

        if (pesoKg <= 0 || !Double.isFinite(pesoKg)) {
            throw new ValorInvalidoException("O peso deve ser maior que zero.");
        }

        this.pesoKg = pesoKg;
    }

    @Override
    public double calcularSubtotal() {
        return getPrecoUnitario() * pesoKg;
    }

    public void setPesoKg(double pesoKg) throws ValorInvalidoException {

        if (pesoKg <= 0 || !Double.isFinite(pesoKg)) {
            throw new ValorInvalidoException("O peso deve ser maior que zero.");
        }

        this.pesoKg = pesoKg;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    @Override
    public String toString() {

        String status = isColetado() ? "[Coletado]" : "[Pendente]";

        return status + " " + getNome()
                + " | " + String.format("%.2f", pesoKg) + " kg"
                + " | Preço/kg: R$ " + String.format("%.2f", getPrecoUnitario())
                + " | Subtotal: R$ " + String.format("%.2f", calcularSubtotal());
    }

}