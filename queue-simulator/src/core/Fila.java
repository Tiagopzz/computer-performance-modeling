package core;

import model.Intervalo;
import model.Rota;
import java.util.List;
import java.util.ArrayList;

public class Fila {
    private final int servidores;
    private final int capacidade;
    private int clientes;
    private int perdas;
    private final ArrayList<Double> tempos;
    private final List<Rota> rotas;

    Intervalo intervaloChegada;
    Intervalo intervaloAtendimento;

    public Fila(int servidores, int capacidade, Intervalo intervaloChegada, Intervalo intervaloAtendimento,  List<Rota> rotas) {
        double soma = 0.0;
        for (Rota rota : rotas) {
            double probabilidade = rota.getProbabilidade();
            if (!Double.isFinite(probabilidade) || probabilidade < 0 || probabilidade > 1 || rota.getIndiceDestino() < -1) {
                throw new IllegalArgumentException("Rota inválida");
            }
            soma += probabilidade;
        }
        if (rotas.isEmpty() || Math.abs(soma - 1.0) > 1e-12) {
            throw new IllegalArgumentException("As probabilidades das rotas devem somar 1");
        }
        this.rotas = List.copyOf(rotas);
        this.servidores = servidores;
        this.capacidade = capacidade;
        this.intervaloChegada = intervaloChegada;
        this.intervaloAtendimento = intervaloAtendimento;
        this.clientes = 0;
        this.perdas = 0;
        this.tempos = new ArrayList<>();
        this.tempos.add(0.0);
        for (int estado = 1; estado <= capacidade; estado++) {
            this.tempos.add(0.0);
        }
    }

    public int getStatus() {
        return clientes;
    }

    public List<Rota> getRotas() {
        return rotas;
    }

    public int getServidores() {
        return servidores;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public boolean temEspaco() {
        return capacidade == -1 || clientes < capacidade;
    }

    public int getMaiorEstado() {
        return tempos.size() - 1;
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
        if (capacidade == -1 && clientes >= tempos.size()) {
            tempos.add(0.0);
        }
    }

    public void saida() {
        clientes--;
    }

    public void registrarPerda() {
        perdas++;
    }

    public void acumularTempo(double intervalo) {
        tempos.set(clientes, tempos.get(clientes) + intervalo);
    }

    public double getTempoAcumulado(int estado) {
        return tempos.get(estado);
    }
}
