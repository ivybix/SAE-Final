package test;

import modele.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class HeuristiqueGloutonTest {

    private Extraction extraction;
    private HeuristiqueGlouton heuristique;

    @BeforeEach
    void setUp() {
        try {
            extraction = new Extraction();
            if (!extraction.getScenarios().isEmpty()) {
                heuristique = new HeuristiqueGlouton(extraction, 0);
            }
        } catch (Exception e) {
            extraction = null;
            heuristique = null;
        }
    }

    @Test
    void constructeurAvecScenarioValide() {
        assertNotNull(heuristique);
        assertNotNull(heuristique.getSommets());
        assertNotNull(heuristique.getCommandes());
    }

    @Test
    void constructeurAjouteVelizySommets() {
        List<String> sommets = heuristique.getSommets();
        assertTrue(sommets.contains("Velizy"));
        assertEquals("Velizy", sommets.get(0));
    }

    @Test
    void constructeurCreeCommandesValides() {
        List<HeuristiqueGlouton.Commande> commandes = heuristique.getCommandes();

        for (HeuristiqueGlouton.Commande commande : commandes) {
            assertNotNull(commande.vendeur);
            assertNotNull(commande.acheteur);
            assertNotNull(commande.nomVendeur);
            assertNotNull(commande.nomAcheteur);
            assertNotEquals(commande.vendeur, commande.acheteur);
        }
    }

    @Test
    void constructeurAvecScenarioInexistant() {
        assertThrows(Exception.class, () -> {
            new HeuristiqueGlouton(extraction, 999);
        });
    }

    @Test
    void parcoursGloutonCommenceParVelizy() {
        try {
            List<String> parcours = HeuristiqueGlouton.parcoursGlouton();
            assertFalse(parcours.isEmpty());
            assertEquals("Velizy", parcours.get(0));
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

    @Test
    void parcoursGloutonFinitParVelizy() {
        try {
            List<String> parcours = HeuristiqueGlouton.parcoursGlouton();
            assertFalse(parcours.isEmpty());
            assertEquals("Velizy", parcours.get(parcours.size() - 1));
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

    @Test
    void parcoursGloutonRetourneParcoursNonVide() {
        try {
            List<String> parcours = HeuristiqueGlouton.parcoursGlouton();
            assertNotNull(parcours);
            assertFalse(parcours.isEmpty());
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

    @Test
    void calculDistanceAvecParcoursVide() {
        try {
            int distance = HeuristiqueGlouton.calculDistance(new ArrayList<>());
            assertEquals(0, distance);
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

    @Test
    void calculDistanceAvecUneVille() {
        try {
            List<String> parcours = Collections.singletonList("Velizy");
            int distance = HeuristiqueGlouton.calculDistance(parcours);
            assertEquals(0, distance);
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

    @Test
    void calculDistanceAvecDeuxVillesIdentiques() {
        try {
            List<String> parcours = Arrays.asList("Velizy", "Velizy");
            int distance = HeuristiqueGlouton.calculDistance(parcours);
            assertEquals(0, distance);
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

    @Test
    void calculDistanceAvecParcoursValide() {
        try {
            List<String> parcours = HeuristiqueGlouton.parcoursGlouton();
            int distance = HeuristiqueGlouton.calculDistance(parcours);
            assertTrue(distance >= 0);
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

    @Test
    void genererResumeScenarioRetourneObjetValide() {
        try {
            ResumeScenario resume = HeuristiqueGlouton.genererResumeScenario(0);

            assertNotNull(resume);
            assertEquals(0, resume.numeroScenario);
            assertNotNull(resume.ordreVisite);
            assertTrue(resume.distanceTotale >= 0);
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }


    @Test
    void commandeToStringFormatCorrect() {
        HeuristiqueGlouton.Commande commande = new HeuristiqueGlouton.Commande(
                "Paris", "Lyon", "vendeur1", "acheteur1"
        );

        String resultat = commande.toString();
        assertTrue(resultat.contains("vendeur1"));
        assertTrue(resultat.contains("Paris"));
        assertTrue(resultat.contains("acheteur1"));
        assertTrue(resultat.contains("Lyon"));
        assertTrue(resultat.contains("->"));
    }

    @Test
    void commandeAvecValeursNull() {
        HeuristiqueGlouton.Commande commande = new HeuristiqueGlouton.Commande(
                null, null, null, null
        );

        assertNull(commande.vendeur);
        assertNull(commande.acheteur);
        assertNull(commande.nomVendeur);
        assertNull(commande.nomAcheteur);
    }

    @Test
    void commandeAvecValeursVides() {
        HeuristiqueGlouton.Commande commande = new HeuristiqueGlouton.Commande(
                "", "", "", ""
        );

        assertEquals("", commande.vendeur);
        assertEquals("", commande.acheteur);
        assertEquals("", commande.nomVendeur);
        assertEquals("", commande.nomAcheteur);
    }

    @Test
    void getSommetsContientVelizySeulement() {
        try {
            HashMap<String, String> scenarioVide = new HashMap<>();
            extraction.ajouterScenario(998, scenarioVide);
            HeuristiqueGlouton heuristiqueVide = new HeuristiqueGlouton(extraction, 998);

            List<String> sommets = heuristiqueVide.getSommets();
            assertEquals(1, sommets.size());
            assertEquals("Velizy", sommets.get(0));
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

    @Test
    void getCommandesVidePourScenarioVide() {
        try {
            HashMap<String, String> scenarioVide = new HashMap<>();
            extraction.ajouterScenario(997, scenarioVide);
            HeuristiqueGlouton heuristiqueVide = new HeuristiqueGlouton(extraction, 997);

            List<HeuristiqueGlouton.Commande> commandes = heuristiqueVide.getCommandes();
            assertTrue(commandes.isEmpty());
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }
}