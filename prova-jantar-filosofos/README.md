# Prova – Jantar dos Filósofos (Programação Paralela e Distribuída)

Este repositório contém as implementações das **Tarefas 1 a 4** do problema do *Jantar dos Filósofos* e um **RELATORIO.md** (Tarefa 5) já no formato comparativo.

## Requisitos

- **JDK (Java) instalado** (recomendado: 11+)
- Windows: PowerShell / Terminal
- Linux/macOS: bash/zsh

Verifique:
```bash
java -version
javac -version
```

## Estrutura

```
prova-jantar-filosofos/
  README.md
  RELATORIO.md
  src/
    common/
    tarefa1/
    tarefa2/
    tarefa3/
    tarefa4/
  logs/
    tarefa2/
    tarefa3/
    tarefa4/
```

## Compilar

### Linux/macOS (bash)
```bash
mkdir -p out
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d out @sources.txt
```

### Windows (PowerShell)

> Dica (para acentos/UTF‑8 no terminal):
```powershell
chcp 65001 | Out-Null
$OutputEncoding = [Console]::OutputEncoding = [System.Text.UTF8Encoding]::new()
```

**Opção A (com arquivo `sources.txt`):**
```powershell
mkdir out -ea 0
Get-ChildItem -Recurse -Filter *.java src | % FullName | Out-File -Encoding ascii sources.txt
javac -encoding UTF-8 -d out @sources.txt
```

**Opção B (se o seu `javac` não aceitar `@sources.txt`):**
```powershell
mkdir out -ea 0
$files = Get-ChildItem -Recurse -Filter *.java src | % FullName
javac -encoding UTF-8 -d out $files
```

## Executar

### Rodar rápido (para testar)
```powershell
java -cp out tarefa1.MainTarefa1 --duration 30
java -cp out tarefa2.MainTarefa2 --duration 60
java -cp out tarefa3.MainTarefa3 --duration 60
java -cp out tarefa4.MainTarefa4 --duration 60
```

### Rodar “modo relatório” (recomendado: 5 min cada nas tarefas 2–4)
```powershell
java -cp out tarefa1.MainTarefa1 --duration 30
java -cp out tarefa2.MainTarefa2 --duration 300
java -cp out tarefa3.MainTarefa3 --duration 300
java -cp out tarefa4.MainTarefa4 --duration 300
```

### Salvar logs (PowerShell)
```powershell
mkdir logs -ea 0
java -cp out tarefa2.MainTarefa2 --duration 300 | Tee-Object -FilePath logs/tarefa2.log
java -cp out tarefa3.MainTarefa3 --duration 300 | Tee-Object -FilePath logs/tarefa3.log
java -cp out tarefa4.MainTarefa4 --duration 300 | Tee-Object -FilePath logs/tarefa4.log
```

## O que cada tarefa faz (resumo)

- **Tarefa 1**: `synchronized` em cada garfo (monitor do objeto). Todos tentam pegar primeiro o garfo esquerdo e depois o direito → pode ocorrer **deadlock**. O programa registra logs e tenta **detectar deadlock** (ThreadMXBean) para evidenciar a ocorrência.
- **Tarefa 2**: elimina deadlock invertendo a ordem de aquisição dos garfos para o filósofo **ID 4**. Ao final, imprime **estatísticas** (refeições, espera média, etc.).
- **Tarefa 3**: usa **Semaphore** (limite 4) para impedir que os 5 tentem competir ao mesmo tempo; imprime estatísticas ao final.
- **Tarefa 4**: usa um **monitor Mesa** com `wait()`/`notifyAll()` e **fila FIFO** para fairness (reduz starvation); imprime estatísticas ao final.
## Tarefa 5 – Relatório comparativo