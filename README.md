# Simulador de Filas

Simulador de filas baseado em eventos discretos, desenvolvido para a disciplina de **Simulação e Métodos Analíticos** do curso de Engenharia de Software da PUCRS.

O projeto simula sistemas de uma única fila e de filas em tandem, utilizando a notação de Kendall **A/B/c/K** (distribuição das chegadas / distribuição dos atendimentos / número de servidores / capacidade do sistema).

## Como funciona

**Componentes principais:**

- `Simulador`: coordena o processo de simulação orientado a eventos;
- `Escalonador`: agenda e recupera eventos utilizando uma fila de prioridade;
- `Fila`: gerencia clientes, estados, tempos acumulados e perdas;
- `Evento` e `TipoEvento`: representam os eventos `CHEGADA`, `PASSAGEM` e `SAIDA`;
- `GeradorNumerosAleatorios`: gerador congruencial linear (`Xₙ₊₁ = (a·Xₙ + c) mod M`) responsável por produzir sequências reproduzíveis;
- `Intervalo`: representa os valores mínimo e máximo dos intervalos de chegada e atendimento.

**Fluxo da simulação:**

1. `Main.java` cria as filas, o gerador de números aleatórios, o escalonador e o simulador;
2. Os eventos são processados em ordem cronológica por meio da fila de prioridade do escalonador;
3. O tempo de permanência em cada estado, de `0` até `K` clientes, é acumulado separadamente para cada fila;
4. **Chegada (`CHEGADA`)**: o cliente entra se houver capacidade disponível. Caso contrário, é contabilizado como perdido;
5. Se houver um servidor disponível, é agendada uma `PASSAGEM` para uma fila intermediária ou uma `SAIDA` para a última fila;
6. **Passagem (`PASSAGEM`)**: o cliente sai da fila atual e tenta entrar na próxima fila da sequência em tandem;
7. **Saída (`SAIDA`)**: o cliente deixa a última fila e um novo término de atendimento é agendado quando necessário;
8. A simulação termina quando o 100.000º número aleatório é utilizado;
9. Ao final, são apresentados o tempo acumulado e o percentual de cada estado, as perdas de clientes e o tempo global da simulação.

## Estrutura do projeto

```text
queue-simulator/
└── src/
    ├── Main.java
    ├── core/
    │   ├── Escalonador.java
    │   ├── Fila.java
    │   └── Simulador.java
    ├── model/
    │   ├── Evento.java
    │   ├── Intervalo.java
    │   └── TipoEvento.java
    └── util/
        └── GeradorNumerosAleatorios.java
```

## Configuração

A configuração atual representa duas filas em tandem:

```java
int quantidadeAleatorios = 100000;
double tempoPrimeiroEvento = 2.5;

Fila fila1 = new Fila(
        2,
        3,
        new Intervalo(1, 5),
        new Intervalo(4, 5));

Fila fila2 = new Fila(
        1,
        5,
        null,
        new Intervalo(1, 3));

List<Fila> filas = List.of(fila1, fila2);

GeradorNumerosAleatorios gerador =
        new GeradorNumerosAleatorios(quantidadeAleatorios);

Escalonador escalonador = new Escalonador(gerador);
Simulador simulador = new Simulador(filas, escalonador, gerador);

simulador.simular(tempoPrimeiroEvento);
```

A primeira fila representa um sistema **G/G/2/3**, enquanto a segunda representa um sistema **G/G/1/5**.

**Parâmetros:**

- `quantidadeAleatorios`: quantidade máxima de números aleatórios utilizados;
- `tempoPrimeiroEvento`: instante da primeira chegada externa;
- `servidores`: número de servidores paralelos (`c`);
- `capacidade`: quantidade máxima de clientes no sistema (`K`);
- `intervaloChegada`: intervalo uniforme entre chegadas externas;
- `intervaloAtendimento`: intervalo uniforme de duração dos atendimentos;
- `null` no intervalo de chegada indica que a fila não recebe chegadas externas.

## Compilação e execução

Execute os comandos a partir da raiz do repositório.

**Compilar:**

```bash
javac -d queue-simulator/bin \
  queue-simulator/src/Main.java \
  queue-simulator/src/core/*.java \
  queue-simulator/src/model/*.java \
  queue-simulator/src/util/*.java
```

**Executar:**

```bash
java -cp queue-simulator/bin Main
```

## Saída

```text
Fila 1
0: 1154.6582822646014 (1.1427014034112737%)
1: 50217.02177923871 (49.69700745551025%)
2: 43355.23339841841 (42.906275224935314%)
3: 6319.456030623522 (6.254015916143156%)
Clientes perdidos: 361

Fila 2
0: 34550.394929408096 (34.19261385006111%)
1: 60299.52095538797 (59.6750989267656%)
2: 6184.97897631675 (6.120931417427589%)
3: 11.474629432428628 (0.01135580574570004%)
4: 0.0 (0.0%)
5: 0.0 (0.0%)
Clientes perdidos: 0

Tempo global: 101046.36949054524
```

## Contexto acadêmico

**Disciplina:** Simulação e Métodos Analíticos  
**Curso:** Engenharia de Software  
**Instituição:** PUCRS — Pontifícia Universidade Católica do Rio Grande do Sul

Este projeto aplica simulação de eventos discretos para modelar e avaliar sistemas de filas únicas e filas em tandem com capacidade finita.
