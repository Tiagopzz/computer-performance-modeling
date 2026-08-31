public class Evento implements Comparable<Evento> {
    private double tempo;
    private TipoEvento tipo;

    public Evento(double tempo, TipoEvento tipo) {
        this.tempo = tempo;
        this.tipo = tipo;
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

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }
}
