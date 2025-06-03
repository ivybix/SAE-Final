package modele;

import java.util.List;
/**
 * Classe représentant un résumé d'un scénario de parcours.
 *
 * Contient le numéro du scénario, l'ordre des visites de villes
 * (respectant l'ordre vendeur → acheteur) et la distance totale du parcours.
 */
public class ResumeScenario {
    public final int numeroScenario;
    public final List<String> ordreVisite;
    public final int distanceTotale;

    /**
     * Constructeur d'un résumé de scénario.
     *
     * @param numeroScenario numéro du scénario concerné.
     * @param ordreVisite liste ordonnée des villes visitées dans le parcours.
     * @param distanceTotale distance totale en kilomètres parcourue.
     */
    public ResumeScenario(int numeroScenario, List<String> ordreVisite, int distanceTotale) {
        this.numeroScenario = numeroScenario;
        this.ordreVisite = ordreVisite;
        this.distanceTotale = distanceTotale;
    }

    /**
     * Retourne une représentation textuelle détaillée du résumé du scénario,
     * incluant le numéro, l'ordre de visite des villes, et la distance totale.
     *
     * @return chaîne de caractères formatée du résumé.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=====================================================================\n");
        sb.append("Scenario : ").append(numeroScenario).append("\n");
        sb.append("=====================================================================\n");
        sb.append("Ordre de visite (respecte vendeur → acheteur) :\n");
        for (String ville : ordreVisite) {
            sb.append(ville).append(" -> ");
        }
        sb.setLength(sb.length() - 4); // Enlever la dernière flèche
        sb.append("\nDistance totale : ").append(distanceTotale).append(" km");
        return sb.toString();
    }
}
