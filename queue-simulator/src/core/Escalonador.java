package core;

import java.util.PriorityQueue;

import model.Evento;
import model.TipoEvento;
import model.Intervalo;
import util.GeradorNumerosAleatorios;

public class Escalonador {
    private final PriorityQueue<Evento> eventos;
    private final GeradorNumerosAleatorios gerador;

    public Escalonador(GeradorNumerosAleatorios gerador) {
        this.gerador = gerador;
        this.eventos = new PriorityQueue<>();
    }

    public Evento proximoEvento() {
        return eventos.poll();
    }

    public boolean temEventos() {
        return !eventos.isEmpty();
    }

    public void agendarPrimeiroEvento(double tempo) {
        eventos.add(new Evento(tempo, TipoEvento.CHEGADA, 0));
    }

    public void agendarChegada(Fila fila, double tempoGlobal, int indiceFila) {
        Intervalo intervalo = fila.getIntervaloChegada();

        if (intervalo == null || !gerador.temAleatoriosDisponiveis()) {
            return;
        }

        double valorGerado = gerador.gerarNoIntervalo(intervalo);
        eventos.add(new Evento(tempoGlobal + valorGerado, TipoEvento.CHEGADA, indiceFila));
    }

    public void agendarPassagem(Fila fila, double tempoGlobal, int indiceFila) {
        if (!gerador.temAleatoriosDisponiveis()) {
            return;
        }

        Intervalo intervalo = fila.getIntervaloAtendimento();
        double valorGerado = gerador.gerarNoIntervalo(intervalo);
        eventos.add(new Evento(tempoGlobal + valorGerado, TipoEvento.PASSAGEM, indiceFila));
    }

    public void agendarSaida(Fila fila, double tempoGlobal, int indiceFila) {
        if (!gerador.temAleatoriosDisponiveis()) {
            return;
        }

        Intervalo intervalo = fila.getIntervaloAtendimento();
        double valorGerado = gerador.gerarNoIntervalo(intervalo);
        eventos.add(new Evento(tempoGlobal + valorGerado, TipoEvento.SAIDA, indiceFila));
    }
}
