package test;



import modele.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestAlgoKSolution {

    private AlgoKSolution algo;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        // Rediriger la sortie standard pour capturer les affichages
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        // Restaurer la sortie standard
        System.setOut(originalOut);
    }

    /**
     * Test du constructeur avec un scénario valide
     */
    @Test
    @DisplayName("Test constructeur avec scénario valide")
    void testConstructeurScenarioValide() {
        assertDoesNotThrow(() -> {
            algo = new AlgoKSolution(1);
            assertNotNull(algo);
        });
    }

    /**
     * Test du constructeur avec un scénario inexistant
     */
    @Test
    @DisplayName("Test constructeur avec scénario inexistant")
    void testConstructeurScenarioInexistant() {
        assertThrows(Exception.class, () -> {
            algo = new AlgoKSolution(999);
        });
    }

    /**
     * Test de génération de solutions avec K = 1
     */
    @Test
    @DisplayName("Test génération K=1 solutions")
    void testGenererUneSolution() throws Exception {
        algo = new AlgoKSolution(1);

        assertDoesNotThrow(() -> {
            algo.genererKSolutions(1);
        });

        // Vérifier qu'au moins une solution a été trouvée
        assertTrue(algo.getNombreSolutions() >= 1,
                "Au moins une solution devrait être trouvée");

        // Vérifier que l'affichage contient les éléments attendus
        String output = outputStream.toString();
        assertTrue(output.contains("RECHERCHE DES K MEILLEURES SOLUTIONS"));
        assertTrue(output.contains("CONTRAINTES DU SCENARIO"));
        assertTrue(output.contains("SOLUTION #1"));
    }

    /**
     * Test de génération de solutions avec K = 3
     */
    @Test
    @DisplayName("Test génération K=3 solutions")
    void testGenererTroisSolutions() throws Exception {
        algo = new AlgoKSolution(1);

        assertDoesNotThrow(() -> {
            algo.genererKSolutions(3);
        });

        // Vérifier l'affichage des solutions
        String output = outputStream.toString();

        if (algo.getNombreSolutions() >= 3) {
            assertTrue(output.contains("SOLUTION #1"));
            assertTrue(output.contains("SOLUTION #2"));
            assertTrue(output.contains("SOLUTION #3"));
        }
    }

    /**
     * Test avec K supérieur au nombre de solutions disponibles
     */
    @Test
    @DisplayName("Test K supérieur au nombre de solutions")
    void testKSuperieureNombreSolutions() throws Exception {
        algo = new AlgoKSolution(1);

        assertDoesNotThrow(() -> {
            algo.genererKSolutions(1000);
        });

        String output = outputStream.toString();
        if (algo.getNombreSolutions() < 1000) {
            assertTrue(output.contains("seulement " + algo.getNombreSolutions() +
                    " solutions valides existent"));
        }
    }

    /**
     * Test de getKMeilleuresSolutions
     */
    @Test
    @DisplayName("Test getKMeilleuresSolutions")
    void testGetKMeilleuresSolutions() throws Exception {
        algo = new AlgoKSolution(1);
        algo.genererKSolutions(5); // Générer d'abord les solutions

        List<ResumeScenario> solutions = algo.getKMeilleuresSolutions(3);

        assertNotNull(solutions);
        assertTrue(solutions.size() <= 3);
        assertTrue(solutions.size() <= algo.getNombreSolutions());

        // Vérifier que les solutions sont triées par distance croissante
        for (int i = 1; i < solutions.size(); i++) {
            assertTrue(solutions.get(i-1).distanceTotale <= solutions.get(i).distanceTotale,
                    "Les solutions devraient être triées par distance croissante");
        }
    }

    /**
     * Test de getNombreSolutions avant génération
     */
    @Test
    @DisplayName("Test getNombreSolutions avant génération")
    void testGetNombreSolutionsAvantGeneration() throws Exception {
        algo = new AlgoKSolution(1);
        assertEquals(0, algo.getNombreSolutions(),
                "Le nombre de solutions devrait être 0 avant génération");
    }

    /**
     * Test de getNombreSolutions après génération
     */
    @Test
    @DisplayName("Test getNombreSolutions après génération")
    void testGetNombreSolutionsApresGeneration() throws Exception {
        algo = new AlgoKSolution(1);
        algo.genererKSolutions(1);

        int nombreSolutions = algo.getNombreSolutions();
        assertTrue(nombreSolutions >= 0,
                "Le nombre de solutions devrait être positif ou nul");
    }

    /**
     * Test avec différents scénarios
     */
    @Test
    @DisplayName("Test avec différents scénarios")
    void testDifferentsScenarios() {
        // Tester plusieurs scénarios (en supposant qu'ils existent)
        for (int scenario = 1; scenario <= 3; scenario++) {
            try {
                algo = new AlgoKSolution(scenario);
                assertDoesNotThrow(() -> {
                    algo.genererKSolutions(2);
                });

                // Vérifier que chaque scénario génère des résultats cohérents
                List<ResumeScenario> solutions = algo.getKMeilleuresSolutions(2);
                for (ResumeScenario solution : solutions) {
                    assertEquals(scenario, solution.numeroScenario,
                            "Le numéro de scénario devrait correspondre");
                    assertNotNull(solution.ordreVisite,
                            "L'ordre de visite ne devrait pas être null");
                    assertTrue(solution.distanceTotale >= 0,
                            "La distance totale devrait être positive");
                }
            } catch (Exception e) {
                // Si le scénario n'existe pas, c'est normal
                System.out.println("Scénario " + scenario + " non disponible: " + e.getMessage());
            }
        }
    }

    /**
     * Test de validation des solutions retournées
     */
    @Test
    @DisplayName("Test validation des solutions")
    void testValidationSolutions() throws Exception {
        algo = new AlgoKSolution(1);
        algo.genererKSolutions(5);

        List<ResumeScenario> solutions = algo.getKMeilleuresSolutions(3);

        for (ResumeScenario solution : solutions) {
            // Vérifier que chaque solution commence et finit par Velizy
            assertNotNull(solution.ordreVisite);
            assertTrue(solution.ordreVisite.size() >= 2);
            assertEquals("Velizy", solution.ordreVisite.get(0),
                    "La solution devrait commencer par Velizy");
            assertEquals("Velizy", solution.ordreVisite.get(solution.ordreVisite.size() - 1),
                    "La solution devrait finir par Velizy");

            // Vérifier que la distance est cohérente
            assertTrue(solution.distanceTotale > 0,
                    "La distance totale devrait être positive");
        }
    }

    /**
     * Test de performance avec K = 0
     */
    @Test
    @DisplayName("Test avec K = 0")
    void testAvecKZero() throws Exception {
        algo = new AlgoKSolution(1);

        assertDoesNotThrow(() -> {
            algo.genererKSolutions(0);
        });

        List<ResumeScenario> solutions = algo.getKMeilleuresSolutions(0);
        assertEquals(0, solutions.size(),
                "Aucune solution ne devrait être retournée avec K=0");
    }

    /**
     * Test de robustesse avec K négatif
     */
    @Test
    @DisplayName("Test avec K négatif")
    void testAvecKNegatif() throws Exception {
        algo = new AlgoKSolution(1);

        assertDoesNotThrow(() -> {
            algo.genererKSolutions(-1);
        });

        List<ResumeScenario> solutions = algo.getKMeilleuresSolutions(-1);
        assertEquals(0, solutions.size(),
                "Aucune solution ne devrait être retournée avec K négatif");
    }

    /**
     * Test de l'unicité des solutions
     */
    @Test
    @DisplayName("Test unicité des solutions")
    void testUniciteSolutions() throws Exception {
        algo = new AlgoKSolution(1);
        algo.genererKSolutions(10);

        List<ResumeScenario> solutions = algo.getKMeilleuresSolutions(10);

        // Vérifier qu'il n'y a pas de doublons dans les parcours
        for (int i = 0; i < solutions.size(); i++) {
            for (int j = i + 1; j < solutions.size(); j++) {
                String parcours1 = String.join("->", solutions.get(i).ordreVisite);
                String parcours2 = String.join("->", solutions.get(j).ordreVisite);
                assertNotEquals(parcours1, parcours2,
                        "Les solutions devraient être uniques");
            }
        }
    }

    /**
     * Test d'intégration complet
     */
    @Test
    @DisplayName("Test d'intégration complet")
    void testIntegrationComplet() throws Exception {
        // Test du cycle complet
        algo = new AlgoKSolution(1);

        // 1. Génération des solutions
        algo.genererKSolutions(5);

        // 2. Vérification du nombre de solutions
        int nbSolutions = algo.getNombreSolutions();
        assertTrue(nbSolutions >= 0);

        // 3. Récupération des meilleures solutions
        List<ResumeScenario> meilleures = algo.getKMeilleuresSolutions(3);
        assertNotNull(meilleures);
        assertTrue(meilleures.size() <= Math.min(3, nbSolutions));

        // 4. Vérification de la cohérence
        if (!meilleures.isEmpty()) {
            ResumeScenario premiere = meilleures.get(0);
            assertEquals(1, premiere.numeroScenario);
            assertNotNull(premiere.ordreVisite);
            assertTrue(premiere.distanceTotale >= 0);
        }

        // 5. Vérification de l'affichage
        String output = outputStream.toString();
        assertTrue(output.length() > 0, "Il devrait y avoir un affichage");
    }
}
