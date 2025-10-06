import java.util.*;

class SimulacaoCaixaSupermercado {
    private int numeroCaixas = 1;
    private int numeroClientes = 100;
    private double mediaTempo = 5.0;
    private double desvioTempo = 0.5;
    private static final double EPS = 0.1;
    private final Random rng;

    public SimulacaoCaixaSupermercado(long seed) { this.rng = new Random(seed); }

    public void setNumeroCaixas(int k) { this.numeroCaixas = Math.max(1, k); }
    public void setNumeroClientes(int n) { this.numeroClientes = Math.max(1, n); }
    public void setMediaTempo(double mu) { this.mediaTempo = mu; }
    public void setDesvioTempo(double sigma) { this.desvioTempo = Math.max(0.0, sigma); }

    private double tempoServico() {
        double z = rng.nextGaussian();
        double s = mediaTempo + desvioTempo * z;
        return (s < EPS) ? EPS : s;
    }

    public double simularRodada() {

        PriorityQueue<Double> livres = new PriorityQueue<>();
        for (int i = 0; i < numeroCaixas; i++) livres.add(0.0);

        double somaConclusoes = 0.0;
        for (int i = 0; i < numeroClientes; i++) {
            double tLivre = livres.poll();
            double s = tempoServico();
            double tFim = tLivre + s;
            somaConclusoes += tFim;
            livres.add(tFim);
        }
        return somaConclusoes / numeroClientes;
    }

    public double[] executar(int rodadas) {
        double[] xs = new double[rodadas];
        for (int i = 0; i < rodadas; i++) xs[i] = simularRodada();
        double m = media(xs);
        double dp = desvioPadrao(xs, m);
        return new double[]{ m, dp };
    }

    private static double media(double[] xs) {
        double s = 0; for (double x : xs) s += x; return s / xs.length;
    }
    private static double desvioPadrao(double[] xs, double m) {
        double s2 = 0; for (double x : xs) { double d = x - m; s2 += d*d; }
        return Math.sqrt(s2 / (xs.length - 1));
    }
}

public class Main {
    public static void main(String[] args) {
        final int RODADAS = 1000;
        final int N = 100;

//Atividade 1
        {
            SimulacaoCaixaSupermercado sim = new SimulacaoCaixaSupermercado(42L);
            sim.setNumeroClientes(N);
            sim.setNumeroCaixas(1);
            sim.setMediaTempo(5.0);
            sim.setDesvioTempo(0.5);
            double[] r = sim.executar(RODADAS);
            System.out.printf("[A1] k=1  μ=5.0  σ=0.5  N=100  -> média=%.3f min | dp=%.3f min%n", r[0], r[1]);
        }

//Atividade 2
        for (int k : new int[]{1,2,3}) {
            SimulacaoCaixaSupermercado sim = new SimulacaoCaixaSupermercado(4242L + k);
            sim.setNumeroClientes(N);
            sim.setNumeroCaixas(k);
            sim.setMediaTempo(5.0);
            sim.setDesvioTempo(0.5);
            double[] r = sim.executar(RODADAS);
            System.out.printf("[A2] k=%d  μ=5.0  σ=0.5  N=100 -> média=%.3f min | dp=%.3f min%n", k, r[0], r[1]);
        }

// Atividade 3
        int kFix = 2;
        for (double sigma : new double[]{0.25, 1.0, 2.0}) {
            SimulacaoCaixaSupermercado sim = new SimulacaoCaixaSupermercado(2025L + (int)(sigma*100));
            sim.setNumeroClientes(N);
            sim.setNumeroCaixas(kFix);
            sim.setMediaTempo(5.0);
            sim.setDesvioTempo(sigma);
            double[] r = sim.executar(RODADAS);
            System.out.printf("[A3] k=%d  μ=5.0  σ=%.2f  N=100 -> média=%.3f min | dp=%.3f min%n",
                    kFix, sigma, r[0], r[1]);
        }

    }
}

/**
Atividade 4
O simulador é estocástico porque os tempos de atendimento de cada cliente são variáveis aleatórias amostradas 
de uma distribuição (normal truncada). Assim, ainda que os parâmetros médios sejam fixos, cada execução produz 
trajetórias diferentes, causando variação nos tempos médios observados entre rodadas. 
Esse comportamento reflete situações reais, nas quais a duração do atendimento depende de fatores imprevisíveis,
 justificando a análise por simulação para estimar tendências e incertezas.
 */