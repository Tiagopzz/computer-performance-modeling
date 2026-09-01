import java.util.PriorityQueue;

public class SimuladorFila {

    private static int CHEGADA_MINIMA = 2;
    private static int CHEGADA_MAXIMA = 5;
    private static int ATENDIMENTO_MINIMO = 3;
    private static int ATENDIMENTO_MAXIMO = 5;

    private static long a = 2493;
    private static long c = 1098;
    private static long M = 23123128321345L;
    private static long previous = 22;

    private static int K = 5;
    private static int servidores = 2;

    private static PriorityQueue<Evento> eventos = new PriorityQueue<>();
    private static double[] tempos = new double[K + 1];

    private static double TempoGlobal = 0.0;
    private static int fila = 0;

    private static int perdas = 0;
    private static int count = 100000;

    public static double NextRandom() {
        previous = ((a * previous) + c) % M;
        return (double) previous / M;
    }

    public static double rnd(int a, int b) {
        count--;
        return a + ((b - a) * NextRandom());
    }

    private static Evento NextEvent() {
        return eventos.poll();
    }

    private static void CHEGADA(Evento evento) {
        tempos[fila] += evento.getTempo() - TempoGlobal;
        TempoGlobal = evento.getTempo();

        if (fila < K) {
            fila++;

            if (fila <= servidores && count > 0) {
                double atendimento = rnd(ATENDIMENTO_MINIMO, ATENDIMENTO_MAXIMO);
                eventos.add(new Evento(TempoGlobal + atendimento, TipoEvento.SAIDA));
            }
        } else {
            perdas++;
        }

        if (count > 0) {
            double intervaloChegada = rnd(CHEGADA_MINIMA, CHEGADA_MAXIMA);
            eventos.add(new Evento(TempoGlobal + intervaloChegada, TipoEvento.CHEGADA));
        }
    }

    private static void SAIDA(Evento evento) {
        tempos[fila] += evento.getTempo() - TempoGlobal;
        TempoGlobal = evento.getTempo();

        fila--;

        if (fila >= servidores && count > 0) {
            double atendimento = rnd(ATENDIMENTO_MINIMO, ATENDIMENTO_MAXIMO);
            eventos.add(new Evento(TempoGlobal + atendimento, TipoEvento.SAIDA));
        }
    }

    private static void imprimirResultados() {
        for (int i = 0; i < K + 1; i++) {
            System.out.println(i + ": " + tempos[i] + " (" + tempos[i] / TempoGlobal + "%)");
        }

        System.out.println("Clientes perdidos: " + perdas);
        System.out.println("Tempo global: " + TempoGlobal);
    }

    public static void main(String[] args) {
        eventos.add(new Evento(3, TipoEvento.CHEGADA));

        while (count > 0) {
            Evento evento = NextEvent();

            if (evento.getTipo() == TipoEvento.CHEGADA) {
                CHEGADA(evento);
            } else {
                SAIDA(evento);
            }
        }

        imprimirResultados();
    }
}
