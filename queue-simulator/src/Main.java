import core.Escalonador;
import core.Simulador;
import util.LeitorModelo;

public class Main {
    public static void main(String[] args) throws Exception {
        LeitorModelo modelo = new LeitorModelo("model.yml");
        Escalonador escalonador = new Escalonador(modelo.getGerador());
        modelo.agendarChegadasIniciais(escalonador);
        Simulador simulador = new Simulador(modelo.getFilas(), escalonador, modelo.getGerador());
        simulador.simular();
    }
}
