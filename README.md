# Simulador de filas

Requisitos: JDK 11 ou superior e SnakeYAML 2.6, única dependência, usada para ler YAML.

Execute os comandos na raiz do projeto, onde está o arquivo `model.yml` carregado pelo Main (Linux/macOS):

```sh
mkdir -p lib build
curl -fL https://repo.maven.apache.org/maven2/org/yaml/snakeyaml/2.6/snakeyaml-2.6.jar -o lib/snakeyaml-2.6.jar
javac -cp 'lib/*' -d build src/Main.java src/core/*.java src/model/*.java src/util/*.java
java -cp 'build:lib/*' Main
```

No Windows, substitua `:` por `;` no classpath de execução. O Main carrega `model.yml` sem argumentos; execute a partir da raiz do projeto.

`model.yml` contém o modelo M8 com a semente 22. O formato aceita a marca `!PARAMETERS` como um mapa de dados, sem construção de classes Java. A ordem das filas em `queues` determina os índices; a ordem das ligações em `network` determina as faixas de roteamento. A probabilidade restante de cada origem representa saída para o exterior. Sem ligações, a saída tem probabilidade 1.

Omitir `capacity` representa capacidade ilimitada (`-1` internamente). Omitir `minArrival` e `maxArrival` representa ausência de chegadas recorrentes. `arrivals` associa cada fila ao instante da primeira chegada externa, sem sorteio desse instante. Intervalos devem ser positivos e ter mínimo menor ou igual ao máximo; a capacidade finita deve comportar os servidores.

Esta versão executa uma semente por vez: `seeds` deve conter exatamente um inteiro, e `rndnumbersPerSeed` define o orçamento total. Para outra execução, altere essa semente no arquivo. Várias sementes são rejeitadas com uma mensagem explícita; não há agregação de execuções.

Como alternativa, omita `seeds` e declare `rndnumbers: [0.1, 0.8, 0.4]`. Os valores devem estar em `[0, 1)` e são consumidos na ordem declarada. Quando `seeds` está presente, ele tem precedência e `rndnumbers` não é utilizado. Chegadas, atendimentos e roteamento compartilham o mesmo orçamento; a simulação encerra quando os aleatórios acabam.
