package util;

import model.Intervalo;
import java.util.List;

public class GeradorNumerosAleatorios {
    private static final long A = 31109;
    private static final long C = 14321;
    private static final long M = 2147483648L;
    private static final long SEMENTE_INICIAL = 22;

    private final int quantidadeMaxima;
    private int quantidadeUtilizada;
    private long valorAnterior;
    private final List<Double> numeros;

    public GeradorNumerosAleatorios(int quantidadeMaxima) {
        this(quantidadeMaxima, SEMENTE_INICIAL);
    }

    public GeradorNumerosAleatorios(int quantidadeMaxima, long semente) {
        if (quantidadeMaxima < 0) {
            throw new IllegalArgumentException("A quantidade de aleatórios não pode ser negativa");
        }
        this.quantidadeMaxima = quantidadeMaxima;
        this.quantidadeUtilizada = 0;
        this.valorAnterior = Math.floorMod(semente, M);
        this.numeros = null;
    }

    public GeradorNumerosAleatorios(List<Double> numeros) {
        for (double numero : numeros) {
            if (!Double.isFinite(numero) || numero < 0 || numero >= 1) {
                throw new IllegalArgumentException("rndnumbers deve conter valores em [0, 1)");
            }
        }
        this.numeros = List.copyOf(numeros);
        this.quantidadeMaxima = numeros.size();
        this.quantidadeUtilizada = 0;
        this.valorAnterior = SEMENTE_INICIAL;
    }

    public double proximo() {
        if (!temAleatoriosDisponiveis()) {
            throw new IllegalStateException("Não há números aleatórios disponíveis");
        }

        if (numeros != null) {
            return numeros.get(quantidadeUtilizada++);
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
