package model;

public class Intervalo {
    private final double minimo;
    private final double maximo;

    public Intervalo(double minimo, double maximo) {
        this.minimo = minimo;
        this.maximo = maximo;
    }

    public double getMinimo() {
        return minimo;
    }

    public double getMaximo() {
        return maximo;
    }
}
