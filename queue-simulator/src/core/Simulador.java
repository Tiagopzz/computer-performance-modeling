package core;

import java.util.List;

import model.Evento;
import model.TipoEvento;
import util.GeradorNumerosAleatorios;

public class Simulador {
    private double tempoGlobal;
    private final List<Fila> filas;
    private final Escalonador escalonador;
    private final GeradorNumerosAleatorios gerador;

    public Simulador(List<Fila> filas, Escalonador escalonador, GeradorNumerosAleatorios gerador) {
        this.tempoGlobal = 0.0;
        this.filas = filas;
        this.escalonador = escalonador;
        this.gerador = gerador;
    }

    public double getTempoGlobal() {
        return tempoGlobal;
    }

    public void CHEGADA(Evento evento) {
        int indiceFila = evento.getIndiceFila();
        Fila fila = filas.get(indiceFila);

        avancarTempo(evento);

        if (fila.getStatus() < fila.getCapacidade()) {
            fila.entrada();

            if (fila.getStatus() <= fila.getServidores()) {
                agendarTerminoAtendimento(fila, indiceFila);
            }
        } else {
            fila.registrarPerda();
        }

        escalonador.agendarChegada(fila, tempoGlobal, indiceFila);
    }

    public void PASSAGEM(Evento evento) {
        int indiceFila = evento.getIndiceFila();
        Fila fila = filas.get(indiceFila);

        avancarTempo(evento);

        fila.saida();
        int indiceProximaFila = indiceFila + 1;

        if (fila.getStatus() >= fila.getServidores()) {
            agendarTerminoAtendimento(fila, indiceFila);
        }

        Fila proximaFila = filas.get(indiceProximaFila);

        if (proximaFila.getStatus() < proximaFila.getCapacidade()) {
            proximaFila.entrada();

            if (proximaFila.getStatus() <= proximaFila.getServidores()) {
                agendarTerminoAtendimento(proximaFila, indiceProximaFila);
            }
        } else {
            proximaFila.registrarPerda();
        }
    }

    private void agendarTerminoAtendimento(Fila fila, int indiceFila) {
        if (indiceFila == filas.size() - 1) {
            escalonador.agendarSaida(fila, tempoGlobal, indiceFila);
        } else {
            escalonador.agendarPassagem(fila, tempoGlobal, indiceFila);
        }
    }

    public void SAIDA(Evento evento) {
        int indiceFila = evento.getIndiceFila();
        Fila fila = filas.get(indiceFila);

        avancarTempo(evento);

        fila.saida();

        if (fila.getStatus() >= fila.getServidores()) {
            escalonador.agendarSaida(fila, tempoGlobal, indiceFila);
        }
    }

    public void simular(double tempoPrimeiroEvento) {
        escalonador.agendarPrimeiroEvento(tempoPrimeiroEvento);

        while (gerador.temAleatoriosDisponiveis() && escalonador.temEventos()) {
            Evento evento = escalonador.proximoEvento();

            if (evento.getTipo() == TipoEvento.CHEGADA) {
                CHEGADA(evento);
            } else if (evento.getTipo() == TipoEvento.SAIDA) {
                SAIDA(evento);
            } else if (evento.getTipo() == TipoEvento.PASSAGEM) {
                PASSAGEM(evento);
            }
        }

        imprimirResultados();
    }

    private void imprimirResultados() {
        for (int indiceFila = 0; indiceFila < filas.size(); indiceFila++) {
            Fila fila = filas.get(indiceFila);

            System.out.println("Fila " + (indiceFila + 1));

            for (int estado = 0; estado <= fila.getCapacidade(); estado++) {
                double tempoAcumulado = fila.getTempoAcumulado(estado);
                double percentual = (tempoAcumulado / tempoGlobal) * 100;

                System.out.println(estado + ": " + tempoAcumulado + " (" + percentual + "%)");
            }

            System.out.println("Clientes perdidos: " + fila.getPerdas());
            System.out.println();
        }

        System.out.println("Tempo global: " + tempoGlobal);
    }

    private void avancarTempo(Evento evento) {
        double intervalo = evento.getTempo() - tempoGlobal;

        for (Fila fila : filas) {
            fila.acumularTempo(intervalo);
        }

        tempoGlobal = evento.getTempo();
    }
}
