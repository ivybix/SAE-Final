package test;

import modele.Commande;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

class CommandeTest {

    @Test
    void constructeurAvecParametresValides() {
        Commande commande = new Commande("Paris", "Lyon");

        assertEquals("Paris", commande.vendeur);
        assertEquals("Lyon", commande.acheteur);
    }

    @Test
    void constructeurAvecParametresVides() {
        Commande commande = new Commande("", "");

        assertEquals("", commande.vendeur);
        assertEquals("", commande.acheteur);
    }

    @Test
    void constructeurAvecParametresNull() {
        Commande commande = new Commande(null, null);

        assertNull(commande.vendeur);
        assertNull(commande.acheteur);
    }

    @Test
    void toStringAvecValeursNormales() {
        Commande commande = new Commande("Marseille", "Toulouse");

        assertEquals("Marseille -> Toulouse", commande.toString());
    }

    @Test
    void toStringAvecValeursVides() {
        Commande commande = new Commande("", "");

        assertEquals(" -> ", commande.toString());
    }

    @Test
    void toStringAvecValeursNull() {
        Commande commande = new Commande(null, null);

        assertEquals("null -> null", commande.toString());
    }

    @Test
    void toStringAvecMemeVille() {
        Commande commande = new Commande("Nice", "Nice");

        assertEquals("Nice -> Nice", commande.toString());
    }
}