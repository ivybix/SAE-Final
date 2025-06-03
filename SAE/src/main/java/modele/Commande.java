package modele;

/**
 * Représente une commande entre un vendeur et un acheteur.
 * Utilisé pour modéliser une livraison de carte d'une ville à une autre.
 *
 * Attributs :
 * - vendeur : ville du vendeur
 * - acheteur : ville de l'acheteur
 */
public class Commande {
    public final String vendeur;
    public final String acheteur;

    public Commande(String vendeur, String acheteur) {
        this.vendeur = vendeur;
        this.acheteur = acheteur;
    }

    @Override
    public String toString() {
        return vendeur + " -> " + acheteur;
    }
}