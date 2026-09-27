package util;

import core.Escalonador;
import core.Fila;
import model.Evento;
import model.Intervalo;
import model.Rota;
import model.TipoEvento;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Tag;

public class LeitorModelo {
    private final ArrayList<Fila> filas = new ArrayList<>();
    private final ArrayList<Evento> chegadasIniciais = new ArrayList<>();
    private final GeradorNumerosAleatorios gerador;

    public LeitorModelo(String caminho) throws IOException {
        try {
            LoaderOptions opcoes = new LoaderOptions();
            opcoes.setAllowDuplicateKeys(false);
            SafeConstructor construtor = new SafeConstructor(opcoes) {
                {
                    yamlConstructors.put(new Tag("!PARAMETERS"), yamlConstructors.get(Tag.MAP));
                }
            };
            Map<?, ?> dados;
            try (Reader arquivo = Files.newBufferedReader(Path.of(caminho), StandardCharsets.UTF_8)) {
                dados = (Map<?, ?>) new Yaml(construtor).load(arquivo);
            }

            Map<?, ?> filasDeclaradas = (Map<?, ?>) dados.get("queues");
            if (filasDeclaradas.isEmpty()) {
                throw new IllegalArgumentException("queues deve declarar pelo menos uma fila");
            }
            Map<String, Integer> indices = new LinkedHashMap<>();
            ArrayList<ArrayList<Rota>> rotas = new ArrayList<>();
            for (Object nome : filasDeclaradas.keySet()) {
                if (!(nome instanceof String) || ((String) nome).isEmpty()) {
                    throw new IllegalArgumentException("O nome da fila deve ser um texto não vazio");
                }
                indices.put((String) nome, indices.size());
                rotas.add(new ArrayList<>());
            }

            List<?> ligacoes = dados.containsKey("network") ? (List<?>) dados.get("network") : List.of();
            for (Object item : ligacoes) {
                Map<?, ?> ligacao = (Map<?, ?>) item;
                int origem = indice(indices, ligacao.get("source"));
                int destino = indice(indices, ligacao.get("target"));
                double probabilidade = numero(ligacao.get("probability"), "probability");
                rotas.get(origem).add(new Rota(destino, probabilidade));
            }

            for (Map.Entry<?, ?> entrada : filasDeclaradas.entrySet()) {
                String nome = (String) entrada.getKey();
                Map<?, ?> parametros = (Map<?, ?>) entrada.getValue();
                int servidores = inteiro(parametros.get("servers"), "servers");
                int capacidade = parametros.containsKey("capacity") ? inteiro(parametros.get("capacity"), "capacity") : -1;
                if (servidores <= 0 || (capacidade != -1 && capacidade < servidores)) {
                    throw new IllegalArgumentException("Servidores/capacidade inválidos na fila " + nome + ".");
                }
                Intervalo chegada = null;
                if (parametros.containsKey("minArrival") || parametros.containsKey("maxArrival")) {
                    chegada = intervalo(parametros, "minArrival", "maxArrival");
                }
                Intervalo atendimento = intervalo(parametros, "minService", "maxService");
                ArrayList<Rota> rotasFila = rotas.get(indices.get(nome));
                double soma = 0.0;
                for (Rota rota : rotasFila) {
                    soma += rota.getProbabilidade();
                }
                if (soma > 1.0 + 1e-12) {
                    throw new IllegalArgumentException("As probabilidades de saída da fila " + nome + " excedem 1.");
                }
                if (soma < 1.0) {
                    rotasFila.add(new Rota(-1, 1.0 - soma));
                }

                filas.add(new Fila(servidores, capacidade, chegada, atendimento, rotasFila));
            }

            Map<?, ?> chegadas = (Map<?, ?>) dados.get("arrivals");
            for (Map.Entry<?, ?> entrada : chegadas.entrySet()) {
                int destino = indice(indices, entrada.getKey());
                double tempo = numero(entrada.getValue(), "instante em arrivals");
                if (tempo < 0) {
                    throw new IllegalArgumentException("O instante da primeira chegada não pode ser negativo");
                }
                chegadasIniciais.add(new Evento(tempo, TipoEvento.CHEGADA, -1, destino));
            }

            if (dados.containsKey("seeds")) {
                List<?> sementes = (List<?>) dados.get("seeds");
                if (sementes.size() != 1) {
                    throw new IllegalArgumentException("Esta versão executa uma semente por vez; seeds deve conter exatamente uma semente");
                }
                long semente = inteiroLongo(sementes.get(0), "seeds");
                int quantidade = inteiro(dados.get("rndnumbersPerSeed"), "rndnumbersPerSeed");
                gerador = new GeradorNumerosAleatorios(quantidade, semente);
            } else {
                ArrayList<Double> numeros = new ArrayList<>();
                for (Object valor : (List<?>) dados.get("rndnumbers")) {
                    numeros.add(numero(valor, "rndnumbers"));
                }
                gerador = new GeradorNumerosAleatorios(numeros);
            }
        } catch (RuntimeException erro) {
            throw new IllegalArgumentException("Modelo inválido: " + erro.getMessage(), erro);
        }
    }

    public List<Fila> getFilas() {
        return filas;
    }

    public GeradorNumerosAleatorios getGerador() {
        return gerador;
    }

    public void agendarChegadasIniciais(Escalonador escalonador) {
        for (Evento chegada : chegadasIniciais) {
            escalonador.agendarPrimeiroEvento(chegada.getTempo(), chegada.getIndiceDestino());
        }
    }

    private static double numero(Object valor, String campo) {
        if (!(valor instanceof Number) || !Double.isFinite(((Number) valor).doubleValue())) {
            throw new IllegalArgumentException(campo + " deve ser um número finito.");
        }
        return ((Number) valor).doubleValue();
    }

    private static long inteiroLongo(Object valor, String campo) {
        if (!(valor instanceof Integer) && !(valor instanceof Long)) {
            throw new IllegalArgumentException(campo + " deve ser um inteiro de 64 bits.");
        }
        return ((Number) valor).longValue();
    }

    private static int inteiro(Object valor, String campo) {
        long numero = inteiroLongo(valor, campo);
        if (numero < Integer.MIN_VALUE || numero > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(campo + " deve ser um inteiro de 32 bits.");
        }
        return (int) numero;
    }

    private static int indice(Map<String, Integer> indices, Object nome) {
        Integer indice = indices.get(nome);
        if (indice == null) {
            throw new IllegalArgumentException("Fila inexistente: " + nome);
        }
        return indice;
    }

    private static Intervalo intervalo(Map<?, ?> parametros, String minimo, String maximo) {
        double inicio = numero(parametros.get(minimo), minimo);
        double fim = numero(parametros.get(maximo), maximo);
        if (inicio <= 0 || fim < inicio) {
            throw new IllegalArgumentException("Intervalo inválido: " + minimo + "/" + maximo);
        }
        return new Intervalo(inicio, fim);
    }
}
