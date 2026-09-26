package model;

public class Evento implements Comparable<Evento> {
    private double tempo;
    private TipoEvento tipo;
    private int indiceOrigem;
    private int indiceDestino;

    public Evento(double tempo, TipoEvento tipo, int indiceOrigem, int indiceDestino) {
        this.tempo = tempo;
        this.tipo = tipo;
        this.indiceOrigem = indiceOrigem;
        this.indiceDestino = indiceDestino;
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

    public int getIndiceOrigem() {
        return indiceOrigem;
    }

    public int getIndiceDestino() {
        return indiceDestino;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public void setIndiceOrigem(int indiceOrigem) {
        this.indiceOrigem = indiceOrigem;
    }

    public void setIndiceDestino(int indiceDestino) {
        this.indiceDestino = indiceDestino;
    }
}
