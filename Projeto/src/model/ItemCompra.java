package model;

import exception.ValorInvalidoException;

public abstract class ItemCompra implements Orcavel {

    private String nome;
    private String categoria;
    private double precoUnitario;
    private boolean coletado;

    public ItemCompra(String nome, String categoria, double precoUnitario)
            throws ValorInvalidoException {

        if (nome == null || nome.trim().isEmpty()) {
            throw new ValorInvalidoException("O nome não pode ser vazio.");
        }

        if (categoria == null || categoria.trim().isEmpty()) {
            throw new ValorInvalidoException("A categoria não pode ser vazia.");
        }

        if (precoUnitario <= 0 || !Double.isFinite(precoUnitario)) {
            throw new ValorInvalidoException("O preço deve ser maior que zero.");
        }

        this.nome = nome;
        this.categoria = categoria;
        this.precoUnitario = precoUnitario;
        this.coletado = false;
    }

    public void alternarColetado() {
        this.coletado = !this.coletado;
    }

    public void setPrecoUnitario(double preco) throws ValorInvalidoException {

        if (preco <= 0 || !Double.isFinite(preco)) {
            throw new ValorInvalidoException("O preço deve ser maior que zero.");
        }

        this.precoUnitario = preco;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public boolean isColetado() {
        return coletado;
    }

    public void setColetado(boolean coletado) {
        this.coletado = coletado;
    }

}