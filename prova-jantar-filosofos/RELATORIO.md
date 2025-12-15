# RELATORIO.md — Análise Comparativa (Tarefa 5)

## Introdução
O problema do **Jantar dos Filósofos** modela a coordenação de múltiplas threads competindo por recursos compartilhados (5 filósofos e 5 garfos). O desafio é permitir progresso (comer) sem violar exclusão mútua e sem cair em **deadlock** ou **starvation**.

Neste trabalho, comparamos três soluções implementadas (Tarefas **2**, **3** e **4**) conforme os requisitos da prova, enfatizando métricas de desempenho e justiça.

## Metodologia
- Foram executadas as soluções das **Tarefas 2, 3 e 4 por ~5 minutos cada**, coletando as métricas exigidas (refeições, espera média, utilização dos garfos e CV de refeições).
- As execuções foram feitas via **Windows PowerShell** usando `java -cp out ... --duration 300`, e o tempo real (runtime) foi o reportado pelo próprio programa no final de cada execução.
- Cada solução mantém o mesmo cenário base: 5 threads (filósofos) alternando entre pensar e tentar comer, com tempos aleatórios de pensar/comer (conforme enunciado) e logging/estatísticas ativados.

## Resultados

### Resumo agregado por solução
| Solução | Runtime (s) | Refeições (total) | Throughput (ref./min) | Espera média (ms) | Utilização média dos garfos (%) | CV refeições | Min–Máx (refeições) |
|:-----------------------------------------|--------------:|--------------------:|------------------------:|--------------------:|----------------------------------:|---------------:|:----------------------|
| Tarefa 2 (ordem invertida p/ filósofo 4) | 300.55 | 276 | 55.1 | 1386.6 | 74.04 | 0.04 | 53–58 |
| Tarefa 3 (semáforo: máx. 4 tentando) | 300.43 | 272 | 54.32 | 1458.19 | 71.95 | 0.02 | 53–56 |
| Tarefa 4 (monitor Mesa + fila FIFO) | 300.3 | 246 | 49.15 | 1925.05 | 66.8 | 0.02 | 48–50 |

### Distribuição por filósofo (refeições e espera média)
| Filósofo | T2 refeições | T3 refeições | T4 refeições | T2 espera (ms) | T3 espera (ms) | T4 espera (ms) |
|-----------:|---------------:|---------------:|---------------:|-----------------:|-----------------:|-----------------:|
| 0 | 53 | 56 | 49 | 1543.84 | 1440.01 | 1954.72 |
| 1 | 55 | 53 | 49 | 1400.85 | 1416.73 | 2005.23 |
| 2 | 57 | 54 | 50 | 1145.75 | 1362.68 | 1958.69 |
| 3 | 58 | 54 | 50 | 1193.39 | 1609.33 | 1701.49 |
| 4 | 53 | 55 | 48 | 1649.15 | 1462.21 | 2005.11 |

### Utilização dos garfos
| Garfos | T2 (%) | T3 (%) | T4 (%) |
|:---------|---------:|---------:|---------:|
| G0 | 69.79 | 73.27 | 65.03 |
| G1 | 73.3 | 72.95 | 66.04 |
| G2 | 76.66 | 72.05 | 66.18 |
| G3 | 78.14 | 70.11 | 69.34 |
| G4 | 72.29 | 71.36 | 67.39 |

### Gráficos simples (ASCII)
**Throughput (refeições por minuto, maior é melhor):**
- T2: 55.10 ref./min `████████████████████████████`
- T3: 54.32 ref./min `████████████████████████████`
- T4: 49.15 ref./min `█████████████████████████ `

**Espera média (ms, menor é melhor):**
- T2: 1387 ms `████████████████████ `
- T3: 1458 ms `█████████████████████ `
- T4: 1925 ms `████████████████████████████`

## Análise (comparação crítica)

### Prevenção de deadlock
- **Tarefa 2** previne deadlock ao **quebrar a condição de espera circular**, invertendo a ordem de aquisição dos garfos para um filósofo (ID 4).
- **Tarefa 3** previne deadlock ao limitar o número de filósofos que podem tentar pegar garfos simultaneamente (semáforo).
- **Tarefa 4** previne deadlock ao centralizar o controle de garfos no monitor `Mesa`, liberando acesso apenas quando ambos os garfos podem ser obtidos “de uma vez”.

### Prevenção de starvation
- **Tarefa 2**: apesar de evitar deadlock, **não garante fairness**, então starvation ainda é possível em teoria (depende do escalonamento). Nos testes, o CV foi **0,04**, com 53–58 refeições por filósofo (boa distribuição prática).
- **Tarefa 3**: reduz contenção global com o semáforo e apresentou CV **0,02** (53–56 refeições), mas ainda **não é uma garantia formal** de ausência de starvation (é um controle de concorrência, não uma fila).
- **Tarefa 4**: atende o requisito explícito de **fairness** usando **fila (FIFO) + wait/notifyAll**, o que busca garantir oportunidade de comer para todos e evita starvation por construção.

### Performance / throughput
- **T2** teve o maior throughput: **55.10 ref./min** (276 refeições em ~300,55s), com maior utilização média dos garfos (**74.04%**).
- **T3** ficou muito próximo em throughput (**54.32 ref./min**) e com utilização média **71.95%**.
- **T4** teve throughput menor (**49.15 ref./min**) e menor utilização média (**66.80%**), consistente com um controle mais “centralizado”/justo, porém com mais bloqueios coordenados.

### Complexidade de implementação
- **T2 (ordem invertida)**: a mais simples — altera regra de aquisição de garfos para um filósofo.
- **T3 (semáforo)**: complexidade moderada — adiciona um recurso global (semáforo) e disciplina de entrada.
- **T4 (monitor + fila)**: a mais complexa — exige monitor, fila, regras de elegibilidade e coordenação via `wait()`/`notifyAll()` conforme enunciado.

### Uso de recursos
- **T2/T3**: usam principalmente os próprios locks dos garfos + (em T3) um semáforo global; apresentam maior taxa de ocupação dos garfos e, portanto, maior competição direta.
- **T4**: adiciona estrutura de fila e sincronização centralizada; reduz competição “solta”, melhora fairness, mas pode reduzir ocupação/throughput em alguns cenários.

## Conclusão
- Se o objetivo é **corrigir deadlock com mínima alteração** e manter boa performance, **Tarefa 2** é a escolha mais adequada.
- Se o objetivo é **reduzir contenção** mantendo desempenho muito parecido e boa distribuição observada, **Tarefa 3** é uma boa opção intermediária.
- Se o objetivo principal é **garantir fairness e evitar starvation**, com controle explícito via monitor e fila (como solicitado), **Tarefa 4** é a mais adequada, aceitando o trade-off de menor throughput.