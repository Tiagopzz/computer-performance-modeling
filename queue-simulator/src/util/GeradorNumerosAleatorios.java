package util;

import model.Intervalo;

public class GeradorNumerosAleatorios {
    private static final long A = 31109;
    private static final long C = 14321;
    private static final long M = 2147483648L;
    private static final long SEMENTE_INICIAL = 22;

    private final int quantidadeMaxima;
    private int quantidadeUtilizada;
    private long valorAnterior;

    public GeradorNumerosAleatorios(int quantidadeMaxima) {
        this.quantidadeMaxima = quantidadeMaxima;
        this.quantidadeUtilizada = 0;
        this.valorAnterior = SEMENTE_INICIAL;
    }

    public double proximo() {
        if (!temAleatoriosDisponiveis()) {
            throw new IllegalStateException("Não há números aleatórios disponíveis.");
        }

        valorAnterior = ((A * valorAnterior) + C) % M;
        quantidadeUtilizada++;
        return (double) valorAnterior / M;
    }

    public double gerarNoIntervalo(Intervalo intervalo) {
        double aleatorio = proximo();
        return intervalo.getMinimo()
                + ((intervalo.getMaximo() - intervalo.getMinimo()) * aleatorio);
    }

    public boolean temAleatoriosDisponiveis() {
        return quantidadeUtilizada < quantidadeMaxima;
    }

    public int getQuantidadeUtilizada() {
        return quantidadeUtilizada;
    }

    public int getQuantidadeRestante() {
        return quantidadeMaxima - quantidadeUtilizada;
    }
}
