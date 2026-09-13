import core.Escalonador;
import core.Fila;
import core.Simulador;
import java.util.List;
import model.Intervalo;
import util.GeradorNumerosAleatorios;

public class Main {
    public static void main(String[] args) {
        int quantidadeAleatorios = 100000;
        double tempoPrimeiroEvento = 2.5;

        Fila fila1 = new Fila(2, 3, new Intervalo(1, 5), new Intervalo(4, 5));
        Fila fila2 = new Fila(1, 5, null, new Intervalo(1, 3));
        List<Fila> filas = List.of(fila1, fila2);

        GeradorNumerosAleatorios gerador = new GeradorNumerosAleatorios(quantidadeAleatorios);
        Escalonador escalonador = new Escalonador(gerador);
        Simulador simulador = new Simulador(filas, escalonador, gerador);

        simulador.simular(tempoPrimeiroEvento);
    }
}
