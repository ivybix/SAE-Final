package test;

import modele.Extraction;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testng.AssertJUnit.*;

class ExtractionTest {

    private Extraction extraction;

    @BeforeEach
    void setUp() {
        try {
            extraction = new Extraction();
        } catch (Exception e) {
            extraction = null;
        }
    }

    @Test
    void constructeurChargeLesDonnees() {
        assertNotNull(extraction);
        assertNotNull(extraction.getScenarios());
        assertNotNull(extraction.getMembresVilles());
        assertNotNull(extraction.getDistances());
    }

    @Test
    void getScenariosRetourneMapNonVide() {
        HashMap<Integer, HashMap<String, String>> scenarios = extraction.getScenarios();

        assertNotNull(scenarios);
        assertFalse(scenarios.isEmpty());
    }

    @Test
    void getMembresVillesRetourneMapNonVide() {
        HashMap<String, String> membresVilles = extraction.getMembresVilles();

        assertNotNull(membresVilles);
        assertFalse(membresVilles.isEmpty());
    }

    @Test
    void getDistancesRetourneMapNonVide() {
        TreeMap<String, ArrayList<Integer>> distances = extraction.getDistances();

        assertNotNull(distances);
        assertFalse(distances.isEmpty());
    }

    @Test
    void getVillesAvecScenarioValide() {
        Map<String, List<String>> villes = extraction.getVilles(0);

        assertNotNull(villes);
    }

    @Test
    void getVillesAvecScenarioInexistant() {
        Map<String, List<String>> villes = extraction.getVilles(999);

        assertNotNull(villes);
        assertTrue(villes.isEmpty());
    }

    @Test
    void distanceVilleToVilleAvecVillesValides() {
        try {
            TreeMap<String, ArrayList<Integer>> distances = extraction.getDistances();
            if (!distances.isEmpty()) {
                String premierVille = distances.firstKey();
                int distance = extraction.distanceVilleToVille(premierVille, premierVille);
                assertEquals(0, distance);
            }
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

    @Test
    void distanceVilleToVilleAvecVilleDepartInconnue() {
        Exception exception = assertThrows(Exception.class, () ->
                extraction.distanceVilleToVille("VilleInconnue", "Velizy")
        );
        assertTrue(exception.getMessage().contains("Ville départ inconnue"));
    }

    @Test
    void distanceVilleToVilleAvecVilleArriveeInconnue() {
        TreeMap<String, ArrayList<Integer>> distances = extraction.getDistances();
        if (!distances.isEmpty()) {
            String premierVille = distances.firstKey();
            Exception exception = assertThrows(Exception.class, () ->
                    extraction.distanceVilleToVille(premierVille, "VilleInconnue")
            );
            assertTrue(exception.getMessage().contains("Ville arrivée inconnue"));
        }
    }

    @Test
    void distanceVilleToVilleAvecEspacesSuperflu() {
        try {
            TreeMap<String, ArrayList<Integer>> distances = extraction.getDistances();
            if (!distances.isEmpty()) {
                String premierVille = distances.firstKey();
                int distance = extraction.distanceVilleToVille("  " + premierVille + "  ", "  " + premierVille + "  ");
                assertEquals(0, distance);
            }
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

    @Test
    void getVentesAvecScenarioValide() {
        List<String[]> ventes = extraction.getVentes(0);

        assertNotNull(ventes);
        for (String[] vente : ventes) {
            assertEquals(2, vente.length);
            assertTrue(vente[0].endsWith("+"));
            assertTrue(vente[1].endsWith("-"));
        }
    }

    @Test
    void getVentesAvecScenarioInexistant() {
        List<String[]> ventes = extraction.getVentes(999);

        assertNotNull(ventes);
        assertTrue(ventes.isEmpty());
    }

    @Test
    void ajouterScenarioAvecContenuValide() {
        HashMap<String, String> nouveauScenario = new HashMap<>();
        nouveauScenario.put("vendeur1", "acheteur1");
        int tailleInitiale = extraction.getScenarios().size();

        extraction.ajouterScenario(100, nouveauScenario);

        assertEquals(tailleInitiale + 1, extraction.getScenarios().size());
        assertTrue(extraction.getScenarios().containsKey(100));
        assertEquals(nouveauScenario, extraction.getScenarios().get(100));
    }

    @Test
    void ajouterScenarioAvecContenuVide() {
        HashMap<String, String> scenarioVide = new HashMap<>();
        int tailleInitiale = extraction.getScenarios().size();

        extraction.ajouterScenario(101, scenarioVide);

        assertEquals(tailleInitiale + 1, extraction.getScenarios().size());
        assertTrue(extraction.getScenarios().containsKey(101));
        assertTrue(extraction.getScenarios().get(101).isEmpty());
    }

    @Test
    void ajouterScenarioEcraseExistant() {
        HashMap<String, String> nouveauScenario = new HashMap<>();
        nouveauScenario.put("vendeur", "acheteur");

        extraction.ajouterScenario(0, nouveauScenario);

        assertEquals(nouveauScenario, extraction.getScenarios().get(0));
    }

    @Test
    void validerScenarioVide() {
        HashMap<String, String> scenarioVide = new HashMap<>();

        assertFalse(extraction.validerScenario(scenarioVide));
    }

    @Test
    void validerScenarioAvecMembresInexistants() {
        HashMap<String, String> scenarioInvalide = new HashMap<>();
        scenarioInvalide.put("membreInexistant1", "membreInexistant2");

        assertFalse(extraction.validerScenario(scenarioInvalide));
    }

    @Test
    void validerScenarioAvecVendeurEgalAcheteur() {
        HashMap<String, String> scenarioInvalide = new HashMap<>();
        HashMap<String, String> membresVilles = extraction.getMembresVilles();
        if (!membresVilles.isEmpty()) {
            String premierMembre = membresVilles.keySet().iterator().next();
            scenarioInvalide.put(premierMembre, premierMembre);

            assertFalse(extraction.validerScenario(scenarioInvalide));
        }
    }

    @Test
    void validerScenarioValide() {
        HashMap<String, String> membresVilles = extraction.getMembresVilles();
        if (membresVilles.size() >= 2) {
            Iterator<String> iter = membresVilles.keySet().iterator();
            String membre1 = iter.next();
            String membre2 = iter.next();

            HashMap<String, String> scenarioValide = new HashMap<>();
            scenarioValide.put(membre1, membre2);

            assertTrue(extraction.validerScenario(scenarioValide));
        }
    }

    @Test
    void validerScenarioAvecPlusieursPaires() {
        HashMap<String, String> membresVilles = extraction.getMembresVilles();
        if (membresVilles.size() >= 4) {
            Iterator<String> iter = membresVilles.keySet().iterator();
            String membre1 = iter.next();
            String membre2 = iter.next();
            String membre3 = iter.next();
            String membre4 = iter.next();

            HashMap<String, String> scenarioValide = new HashMap<>();
            scenarioValide.put(membre1, membre2);
            scenarioValide.put(membre3, membre4);

            assertTrue(extraction.validerScenario(scenarioValide));
        }
    }

    @Test
    void validerScenarioAvecUnePaireInvalide() {
        HashMap<String, String> membresVilles = extraction.getMembresVilles();
        if (membresVilles.size() >= 2) {
            Iterator<String> iter = membresVilles.keySet().iterator();
            String membre1 = iter.next();
            String membre2 = iter.next();

            HashMap<String, String> scenarioInvalide = new HashMap<>();
            scenarioInvalide.put(membre1, membre2);
            scenarioInvalide.put("membreInexistant", membre1);

            assertFalse(extraction.validerScenario(scenarioInvalide));
        }
    }
}