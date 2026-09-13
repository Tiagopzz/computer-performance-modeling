package model;

public class Evento implements Comparable<Evento> {
    private double tempo;
    private TipoEvento tipo;
    private int indiceFila;

    public Evento(double tempo, TipoEvento tipo, int indiceFila) {
        this.tempo = tempo;
        this.tipo = tipo;
        this.indiceFila = indiceFila;
    }

    @Override
    public int compareTo(Evento outro) {
        return Double.compare(this.tempo, outro.tempo);
    }

    public double getTempo() {
        return tempo;
    }
    
    public TipoEvento getTipo() {
        return tipo;
    }

    public int getIndiceFila() {
        return indiceFila;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public void setIndiceFila(int indiceFila) {
        this.indiceFila = indiceFila;
    }
}
