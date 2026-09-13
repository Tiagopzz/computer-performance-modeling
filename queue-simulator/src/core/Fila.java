package core;

import model.Intervalo;

public class Fila {
    private final int servidores;
    private final int capacidade;
    private int clientes;
    private int perdas;
    private final double[] tempos;

    Intervalo intervaloChegada;
    Intervalo intervaloAtendimento;

    public Fila(int servidores, int capacidade, Intervalo intervaloChegada, Intervalo intervaloAtendimento) {
        this.servidores = servidores;
        this.capacidade = capacidade;
        this.intervaloChegada = intervaloChegada;
        this.intervaloAtendimento = intervaloAtendimento;
        this.clientes = 0;
        this.perdas = 0;
        this.tempos = new double[capacidade + 1];
    }

    public int getStatus() {
        return clientes;
    }

    public int getServidores() {
        return servidores;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public Intervalo getIntervaloChegada() {
        return intervaloChegada;
    }

    public Intervalo getIntervaloAtendimento() {
        return intervaloAtendimento;
    }

    public int getPerdas() {
        return perdas;
    }

    public void entrada() {
        clientes++;
    }

    public void saida() {
        clientes--;
    }

    public void registrarPerda() {
        perdas++;
    }

    public void acumularTempo(double intervalo) {
        tempos[clientes] += intervalo;
    }

    public double getTempoAcumulado(int estado) {
        return tempos[estado];
    }
}
