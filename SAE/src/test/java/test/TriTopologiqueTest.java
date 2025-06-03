package test;


import modele.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TriTopologiqueTest {

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
    void trierVillesAvecScenarioValide() {
        try {
            String resultat = TriTopologique.trierVilles(extraction, 0);

            assertNotNull(resultat);
            assertFalse(resultat.isEmpty());
            assertTrue(resultat.contains("Scenario : 0"));
            assertTrue(resultat.contains("Distance totale"));
            assertTrue(resultat.contains("km"));
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

    @Test
    void trierVillesAvecScenarioInexistant() {
        assertThrows(Exception.class, () -> {
            TriTopologique.trierVilles(extraction, 999);
        });
    }

    @Test
    void trierVillesContientOrdreVisite() {
        try {
            String resultat = TriTopologique.trierVilles(extraction, 0);

            assertTrue(resultat.contains("Ordre de visite"));
            assertTrue(resultat.contains("respecte vendeur → acheteur"));
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

    @Test
    void trierVillesContientSeparateurs() {
        try {
            String resultat = TriTopologique.trierVilles(extraction, 0);

            int separateurs = resultat.split("=====================================================================").length - 1;
            assertEquals(2, separateurs);
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }


    @Test
    void getResumeScenarioAvecScenarioValide() {
        try {
            ResumeScenario resume = TriTopologique.getResumeScenario(extraction, 0);

            assertNotNull(resume);
            assertEquals(0, resume.numeroScenario);
            assertNotNull(resume.ordreVisite);
            assertTrue(resume.distanceTotale >= 0);
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

    @Test
    void getResumeScenarioAvecScenarioInexistant() {
        assertThrows(Exception.class, () -> {
            TriTopologique.getResumeScenario(extraction, 999);
        });
    }

    @Test
    void getResumeScenarioCommenceEtFinitParVelizy() {
        try {
            ResumeScenario resume = TriTopologique.getResumeScenario(extraction, 0);

            assertFalse(resume.ordreVisite.isEmpty());
            assertEquals("Velizy", resume.ordreVisite.get(0));
            assertEquals("Velizy", resume.ordreVisite.get(resume.ordreVisite.size() - 1));
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

    @Test
    void getResumeScenarioDistancePositive() {
        try {
            ResumeScenario resume = TriTopologique.getResumeScenario(extraction, 0);

            assertTrue(resume.distanceTotale >= 0);
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

    @Test
    void trierVillesEtGetResumeScenarioCoherents() {
        try {
            String resultatTexte = TriTopologique.trierVilles(extraction, 0);
            ResumeScenario resume = TriTopologique.getResumeScenario(extraction, 0);

            assertTrue(resultatTexte.contains("Scenario : " + resume.numeroScenario));
            assertTrue(resultatTexte.contains("Distance totale : " + resume.distanceTotale + " km"));
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }

}

