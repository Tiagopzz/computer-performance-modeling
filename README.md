# Simulador de filas

Simulador de eventos discretos desenvolvido para a disciplina de **Simulação e Métodos Analíticos** do curso de Engenharia de Software da PUCRS.

O programa simula redes de filas com roteamento probabilístico. Cada fila pode ter seu próprio número de servidores, capacidade e intervalos de chegada e atendimento. Quando a capacidade não é informada, a fila é ilimitada.

## Componentes

- `Main`: carrega `model.yml` e inicia a simulação.
- `LeitorModelo`: lê o YAML e monta as filas, as rotas e as chegadas iniciais.
- `Simulador`: processa eventos, avança o tempo e imprime os resultados.
- `Escalonador`: mantém os eventos em ordem cronológica e agenda chegadas, passagens e saídas.
- `Fila`: armazena clientes, servidores, capacidade, perdas e tempos acumulados por estado.
- `Evento` e `TipoEvento`: representam `CHEGADA`, `PASSAGEM` e `SAIDA`, com origem e destino.
- `Rota`: armazena destino e probabilidade de roteamento.
- `Intervalo`: armazena os limites de chegada ou atendimento.
- `GeradorNumerosAleatorios`: gera números pseudoaleatórios ou consome uma sequência informada no YAML.

## Funcionamento

1. `Main` lê `model.yml` e agenda as chegadas iniciais declaradas em `arrivals`.
2. O `Escalonador` entrega o próximo evento em ordem de tempo. O tempo decorrido é acumulado no estado atual de todas as filas.
3. Na `CHEGADA`, o cliente entra se houver espaço. Caso possa iniciar atendimento, sua rota é escolhida e o término é agendado. Uma nova chegada externa é agendada quando a fila tem intervalo de chegada.
4. Na `PASSAGEM`, o cliente sai da origem e tenta entrar no destino. Se o destino estiver cheio, a perda é registrada nessa fila. Clientes à espera podem iniciar atendimento.
5. Na `SAIDA`, o cliente deixa o sistema e outro cliente à espera pode iniciar atendimento.
6. A simulação termina quando acabam os números aleatórios disponíveis ou os eventos agendados. O relatório apresenta tempos e percentuais por estado, perdas e tempo global.

Se as probabilidades das ligações de uma fila somarem menos que `1`, a diferença representa saída para o exterior. Uma fila sem ligações envia todos os clientes atendidos para o exterior.

## Estrutura do projeto

```text
computer-performance-modeling/
├── README.md
└── queue-simulator/
    ├── model.yml
    └── src/
        ├── Main.java
        ├── core/
        │   ├── Escalonador.java
        │   ├── Fila.java
        │   └── Simulador.java
        ├── model/
        │   ├── Evento.java
        │   ├── Intervalo.java
        │   ├── Rota.java
        │   └── TipoEvento.java
        └── util/
            ├── GeradorNumerosAleatorios.java
            └── LeitorModelo.java
```

## Configuração

O arquivo `model.yml` usa o formato do simulador fornecido na disciplina. O exemplo abaixo descreve as três filas do M8, com a primeira chegada no tempo `2.0` e `100000` números aleatórios:

```yaml
!PARAMETERS
arrivals:
  Q1: 2.0

queues:
  Q1:
    servers: 1
    minArrival: 2.0
    maxArrival: 4.0
    minService: 1.0
    maxService: 2.0
  Q2:
    servers: 2
    capacity: 5
    minService: 4.0
    maxService: 6.0
  Q3:
    servers: 2
    capacity: 10
    minService: 5.0
    maxService: 15.0

network:
- source: Q1
  target: Q2
  probability: 0.2
- source: Q1
  target: Q3
  probability: 0.8
- source: Q2
  target: Q1
  probability: 0.3
- source: Q2
  target: Q3
  probability: 0.5
- source: Q3
  target: Q2
  probability: 0.7

rndnumbersPerSeed: 100000
seeds:
- 22
```

- `arrivals`: fila e instante de cada primeira chegada externa.
- `queues`: servidores, capacidade opcional e intervalos de chegada e atendimento.
- `network`: ligações e probabilidades entre filas. A ordem declarada determina as faixas do sorteio.
- `seeds` e `rndnumbersPerSeed`: uma semente e a quantidade de aleatórios disponíveis. Esta versão executa uma semente por vez.
- `rndnumbers`: alternativa a `seeds` para fornecer diretamente uma lista de números em `[0, 1)`. Se ambos forem informados, `seeds` tem precedência.

Requisitos: JDK 11 ou superior e SnakeYAML 2.6. A partir da raiz do repositório, execute (macOS/Linux):

```sh
cd queue-simulator
mkdir -p lib build
curl -fL https://repo.maven.apache.org/maven2/org/yaml/snakeyaml/2.6/snakeyaml-2.6.jar -o lib/snakeyaml-2.6.jar
javac -cp 'lib/*' -d build src/Main.java src/core/*.java src/model/*.java src/util/*.java
java -cp 'build:lib/*' Main
```

O `Main` carrega `model.yml` da pasta atual, sem argumentos. `build/` contém as classes compiladas; `lib/` guarda a dependência baixada. No Windows, use `;` no lugar de `:` no classpath de execução.

## Exemplo de saída

Resultado obtido com o `model.yml` acima:

```text
Fila 1
0: 20716.068250174634 (40.46775321422595%)
1: 26903.858457652386 (52.55527696794321%)
2: 3429.3426340818405 (6.699041040371047%)
3: 138.79019313305616 (0.27111936572307477%)
4: 3.4858430991880596 (0.006809411736721122%)
Clientes perdidos: 0

Fila 2
0: 13081.975168051198 (25.55495262238602%)
1: 21232.70924714906 (41.476984315100346%)
2: 12726.168108034879 (24.8599021850764%)
3: 3598.3768217172474 (7.029240463707044%)
4: 508.54094774555415 (0.9934080793792605%)
5: 43.77508544316515 (0.08551233435093211%)
Clientes perdidos: 1

Fila 3
0: 7.950742342974991 (0.015531358321466055%)
1: 7.449918381404132 (0.014553024969989012%)
2: 4.630147045478225 (0.00904474950165366%)
3: 7.055178079288453 (0.013781920485449984%)
4: 2.2594407270662487 (0.004413699001224203%)
5: 1.2609305526129901 (0.0024631617258255504%)
6: 17.377477741334587 (0.03394599169251649%)
7: 53.927460787352175 (0.1053444673119388%)
8: 2953.693114652764 (5.769884641759608%)
9: 16319.296307807788 (31.878889740992584%)
10: 31816.64466002304 (62.152147244237746%)
Clientes perdidos: 11634

Tempo global: 51191.545378141105
```

## Contexto da disciplina

**Disciplina:** Simulação e Métodos Analíticos  
**Curso:** Engenharia de Software  
**Instituição:** PUCRS (Pontifícia Universidade Católica do Rio Grande do Sul)
