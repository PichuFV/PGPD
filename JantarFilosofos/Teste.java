import static org.junit.jupiter.api.Assertions.*;

import java.util.IdentityHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class JantarDosFilosofosConfiguracaoTest {

    @Test
    void deveConfigurarAmbienteCorretamenteAoIniciarJantar() {
        int numeroFilosofos = 5;

        // Supondo um construtor que recebe a quantidade de filósofos
        Jantar jantar = new Jantar(numeroFilosofos);

        // Ação: inicializar o ambiente do jantar
        jantar.iniciar();

        // Recupera os artefatos criados
        Fisolofo[] filosofos = jantar.getFilosofos();
        Garfo[] garfos = jantar.getGarfos();

        // Verificações básicas de existência e tamanho
        assertNotNull(filosofos, "O array de filósofos não deve ser nulo após iniciar().");
        assertNotNull(garfos, "O array de garfos não deve ser nulo após iniciar().");

        assertEquals(numeroFilosofos, filosofos.length,
                "A quantidade de filósofos deve ser igual ao número configurado.");
        assertEquals(numeroFilosofos, garfos.length,
                "A quantidade de garfos deve ser igual ao número de filósofos.");

        // Verifica se cada filósofo foi criado corretamente e se tem dois garfos associados
        for (int i = 0; i < numeroFilosofos; i++) {
            Fisolofo f = filosofos[i];
            assertNotNull(f, "Filósofo na posição " + i + " não deve ser nulo.");

            Garfo garfoEsquerdo = f.getGarfoEsquerdo();
            Garfo garfoDireito = f.getGarfoDireito();

            assertNotNull(garfoEsquerdo, "Filósofo " + i + " deve ter um garfo esquerdo.");
            assertNotNull(garfoDireito, "Filósofo " + i + " deve ter um garfo direito.");
            assertNotSame(garfoEsquerdo, garfoDireito,
                    "Filósofo " + i + " não deve ter o mesmo objeto para os dois garfos.");

            // Verifica se o mapeamento segue o arranjo circular clássico:
            // garfoEsquerdo = garfos[i]
            // garfoDireito  = garfos[(i + 1) % numeroFilosofos]
            assertSame(garfos[i], garfoEsquerdo,
                    "Garfo esquerdo do filósofo " + i + " deve ser o garfo de índice " + i + ".");
            assertSame(garfos[(i + 1) % numeroFilosofos], garfoDireito,
                    "Garfo direito do filósofo " + i + " deve ser o garfo de índice " + ((i + 1) % numeroFilosofos) + ".");
        }

        // Verifica se cada garfo é compartilhado por exatamente dois filósofos
        Map<Garfo, Integer> usoPorGarfo = new IdentityHashMap<>();

        for (Fisolofo f : filosofos) {
            usoPorGarfo.merge(f.getGarfoEsquerdo(), 1, Integer::sum);
            usoPorGarfo.merge(f.getGarfoDireito(), 1, Integer::sum);
        }

        for (int i = 0; i < garfos.length; i++) {
            Garfo g = garfos[i];
            Integer usos = usoPorGarfo.get(g);
            assertNotNull(usos, "Garfo " + i + " não foi associado a nenhum filósofo.");
            assertEquals(2, usos.intValue(),
                    "Garfo " + i + " deve ser compartilhado por exatamente dois filósofos.");
        }
    }
}