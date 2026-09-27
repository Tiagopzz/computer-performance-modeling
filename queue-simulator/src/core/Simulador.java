package core;

import java.util.List;

import model.Evento;
import model.TipoEvento;
import model.Rota;
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
        int indiceFila = evento.getIndiceDestino();
        Fila fila = filas.get(indiceFila);

        avancarTempo(evento);

        if (fila.temEspaco()) {
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
        int indiceFila = evento.getIndiceOrigem();
        Fila fila = filas.get(indiceFila);

        avancarTempo(evento);

        fila.saida();
        int indiceProximaFila = evento.getIndiceDestino();

        if (fila.getStatus() >= fila.getServidores()) {
            agendarTerminoAtendimento(fila, indiceFila);
        }

        Fila proximaFila = filas.get(indiceProximaFila);

        if (proximaFila.temEspaco()) {
            proximaFila.entrada();

            if (proximaFila.getStatus() <= proximaFila.getServidores()) {
                agendarTerminoAtendimento(proximaFila, indiceProximaFila);
            }
        } else {
            proximaFila.registrarPerda();
        }
    }

    private void agendarTerminoAtendimento(Fila fila, int indiceFila) {
        if (!gerador.temAleatoriosDisponiveis()) {
            return;
        }

        List<Rota> rotas = fila.getRotas();
        int indiceDestino = rotas.get(0).getIndiceDestino();

        if (rotas.size() > 1) {
            double aleatorio = gerador.proximo();
            double acumulada = 0.0;
            indiceDestino = -2;
            for (Rota rota : rotas) {
                acumulada += rota.getProbabilidade();
                if (aleatorio < acumulada) {
                    indiceDestino = rota.getIndiceDestino();
                    break;
                }
            }
            if (indiceDestino == -2) {
                throw new IllegalStateException("Nenhuma rota corresponde ao número sorteado");
            }
        }

        if (!gerador.temAleatoriosDisponiveis()) {
            return;
        }

        if (indiceDestino == -1) {
            escalonador.agendarSaida(fila, tempoGlobal, indiceFila);
        } else {
            escalonador.agendarPassagem(fila, tempoGlobal, indiceFila, indiceDestino);
        }
    }

    public void SAIDA(Evento evento) {
        int indiceFila = evento.getIndiceOrigem();
        Fila fila = filas.get(indiceFila);

        avancarTempo(evento);

        fila.saida();

        if (fila.getStatus() >= fila.getServidores()) {
            agendarTerminoAtendimento(fila, indiceFila);
        }
    }

    public void simular() {
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

            for (int estado = 0; estado <= fila.getMaiorEstado(); estado++) {
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
