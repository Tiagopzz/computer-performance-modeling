package model;

public class Rota {
    private final int indiceDestino;
    private final double probabilidade;

    public Rota(int indiceDestino, double probabilidade) {
        this.indiceDestino = indiceDestino;
        this.probabilidade = probabilidade;
    }

    public int getIndiceDestino() {
        return indiceDestino;
    }

    public double getProbabilidade() {
        return probabilidade;
    }
}
