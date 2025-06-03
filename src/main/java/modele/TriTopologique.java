package modele;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Classe contenant des méthodes pour effectuer un tri topologique
 * sur un graphe orienté représentant les ventes entre villes.
 *
 * Chaque sommet du graphe est une ville avec un suffixe "+" ou "-"
 * pour différencier le rôle de vendeur ("Ville+") et d'acheteur ("Ville-").
 *
 * L'algorithme garantit que l'ordre des visites respecte la contrainte
 * vendeur → acheteur, et calcule la distance totale du parcours en
 * partant et revenant à la ville "Velizy".
 *
 * Méthodes principales :
 * - trierVilles : effectue le tri topologique et renvoie une chaîne descriptive du parcours.
 * - getResumeScenario : effectue le tri et retourne un objet ResumeScenario
 *   contenant le détail du parcours et la distance totale.
 */
public class TriTopologique {
    /**
     * Effectue un tri topologique sur le graphe des ventes pour un scénario donné,
     * en respectant l'ordre vendeur → acheteur, et calcule la distance totale du parcours.
     *
     * Le parcours commence et finit par la ville "Velizy".
     *
     * @param extraction instance permettant d'accéder aux données (villes, ventes, distances)
     * @param monScenario numéro du scénario à traiter
     * @return une chaîne de caractères décrivant l'ordre des visites et la distance totale
     */
    public static String trierVilles(Extraction extraction, int monScenario) throws FileNotFoundException {
        final String villeVelizy = "Velizy";

        // 1. Construire le graphe orienté avec des sommets "Ville+" (vendeur) et "Ville-" (acheteur)
        Map<String, List<String>> graphe = new HashMap<>();
        Set<String> sommets = new HashSet<>();

        // Récupérer la liste des ventes pour le scénario donné
        List<String[]> ventes = extraction.getVentes(monScenario);

        // Pour chaque vente, on crée un arc vendeur+ → acheteur-
        for (String[] vente : ventes) {
            String vendeur = vente[0];
            String acheteur = vente[1];
            String vendeurPlus = vendeur + "+";
            String acheteurMoins = acheteur + "-";
            graphe.putIfAbsent(vendeurPlus, new ArrayList<>());
            graphe.get(vendeurPlus).add(acheteurMoins);

            sommets.add(vendeurPlus);
            sommets.add(acheteurMoins);
        }

        // 2. Pour chaque ville, si on a vendeur+ et acheteur-, on ajoute un arc vendeur+ → acheteur-
        // Cela permet de passer dans la ville une fois pour récupérer puis livrer les cartes (distance 0)
        Set<String> villes = new HashSet<>();
        for (String sommet : sommets) {
            villes.add(sommet.replace("+", "").replace("-", ""));
        }

        for (String ville : villes) {
            String plus = ville + "+";
            String moins = ville + "-";
            if (sommets.contains(plus) && sommets.contains(moins)) {
                graphe.putIfAbsent(plus, new ArrayList<>());
                graphe.get(plus).add(moins);
            }
        }

        // 3. Calculer le degré entrant (nombre d'arcs entrants) pour chaque sommet
        Map<String, Integer> inDegree = new HashMap<>();
        for (String sommet : sommets) {
            inDegree.put(sommet, 0);
        }

        for (List<String> voisins : graphe.values()) {
            for (String voisin : voisins) {
                inDegree.put(voisin, inDegree.getOrDefault(voisin, 0) + 1);
            }
        }

        // 4. Tri topologique avec l’algorithme de Kahn
        // On commence par les sommets sans prédécesseurs (inDegree == 0)
        Queue<String> queue = new LinkedList<>();
        for (String sommet : inDegree.keySet()) {
            if (inDegree.get(sommet) == 0) {
                queue.add(sommet);
            }
        }

        List<String> ordre = new ArrayList<>();
        while (!queue.isEmpty()) {
            String current = queue.poll();
            ordre.add(current);

            // On enlève l’arc courant et on met à jour les degrés entrants
            for (String voisin : graphe.getOrDefault(current, Collections.emptyList())) {
                inDegree.put(voisin, inDegree.get(voisin) - 1);
                if (inDegree.get(voisin) == 0) {
                    queue.add(voisin);
                }
            }
        }

        // 5. Transformer la liste des sommets en noms de villes (enlevant + et -)
        // On évite de répéter une ville deux fois de suite
        List<String> ordreVisite = new ArrayList<>();
        ordreVisite.add(villeVelizy); // On part de Vélizy

        String lastVille = "";
        for (String sommet : ordre) {
            String ville = sommet.replace("+", "").replace("-", "");
            if (!ville.equals(lastVille)) {
                ordreVisite.add(ville);
                lastVille = ville;
            }
        }

        ordreVisite.add(villeVelizy); // On revient à Vélizy à la fin

        // 6. Calculer la distance totale parcourue
        int distanceTotale = 0;
        for (int i = 0; i < ordreVisite.size() - 1; i++) {
            String from = ordreVisite.get(i);
            String to = ordreVisite.get(i + 1);
            try {
                distanceTotale += extraction.distanceVilleToVille(from, to);
            } catch (Exception e) {
                System.err.println("Distance inconnue entre " + from + " et " + to);
            }
        }

        // 7. Construire la chaîne de résultat à afficher
        StringBuilder sb = new StringBuilder();
        sb.append("=====================================================================\n");
        sb.append("Scenario : ").append(monScenario).append("\n");
        sb.append("=====================================================================\n");
        sb.append("Ordre de visite (respecte vendeur → acheteur) :\n");
        for (String ville : ordreVisite) {
            sb.append(ville).append(" -> ");
        }
        sb.setLength(sb.length() - 4); // Supprimer le dernier " -> "
        sb.append("\nDistance totale : ").append(distanceTotale).append(" km");

        return sb.toString();
    }


    /**
     * Effectue un tri topologique similaire à trierVilles mais retourne
     * un objet ResumeScenario contenant le scénario, la liste ordonnée des villes
     * du parcours et la distance totale.
     *
     * @param extraction instance permettant d'accéder aux données (villes, ventes, distances)
     * @param monScenario numéro du scénario à traiter
     * @return un objet ResumeScenario contenant le détail du parcours et la distance totale
     */
    public static ResumeScenario getResumeScenario(Extraction extraction, int monScenario) throws FileNotFoundException {
        final String villeVelizy = "Velizy";

        // 1. Construire le graphe orienté avec des sommets "Ville+" (vendeur) et "Ville-" (acheteur)
        Map<String, List<String>> graphe = new HashMap<>();
        Set<String> sommets = new HashSet<>();

        List<String[]> ventes = extraction.getVentes(monScenario);

        for (String[] vente : ventes) {
            String vendeur = vente[0];
            String acheteur = vente[1];
            String vendeurPlus = vendeur + "+";
            String acheteurMoins = acheteur + "-";

            graphe.putIfAbsent(vendeurPlus, new ArrayList<>());
            graphe.get(vendeurPlus).add(acheteurMoins);

            sommets.add(vendeurPlus);
            sommets.add(acheteurMoins);
        }

        Set<String> villes = new HashSet<>();
        for (String sommet : sommets) {
            villes.add(sommet.replace("+", "").replace("-", ""));
        }

        for (String ville : villes) {
            String plus = ville + "+";
            String moins = ville + "-";
            if (sommets.contains(plus) && sommets.contains(moins)) {
                graphe.putIfAbsent(plus, new ArrayList<>());
                graphe.get(plus).add(moins);
            }
        }

        Map<String, Integer> inDegree = new HashMap<>();
        for (String sommet : sommets) {
            inDegree.put(sommet, 0);
        }

        for (List<String> voisins : graphe.values()) {
            for (String voisin : voisins) {
                inDegree.put(voisin, inDegree.getOrDefault(voisin, 0) + 1);
            }
        }

        Queue<String> queue = new LinkedList<>();
        for (String sommet : inDegree.keySet()) {
            if (inDegree.get(sommet) == 0) {
                queue.add(sommet);
            }
        }

        List<String> ordre = new ArrayList<>();
        while (!queue.isEmpty()) {

            String current = queue.poll();
            ordre.add(current);

            for (String voisin : graphe.getOrDefault(current, Collections.emptyList())) {
                inDegree.put(voisin, inDegree.get(voisin) - 1);
                if (inDegree.get(voisin) == 0) {
                    queue.add(voisin);
                }
            }
        }

        List<String> ordreVisite = new ArrayList<>();
        ordreVisite.add(villeVelizy);

        String lastVille = "";
        for (String sommet : ordre) {
            String ville = sommet.replace("+", "").replace("-", "");
            if (!ville.equals(lastVille)) {
                ordreVisite.add(ville);
                lastVille = ville;
            }
        }

        ordreVisite.add(villeVelizy);

        int distanceTotale = 0;
        for (int i = 0; i < ordreVisite.size() - 1; i++) {
            String from = ordreVisite.get(i);
            String to = ordreVisite.get(i + 1);
            try {
                distanceTotale += extraction.distanceVilleToVille(from, to);
            } catch (Exception e) {
                System.err.println("Distance inconnue entre " + from + " et " + to);
            }
        }

        // On crée et retourne l'objet ResumeScenario
        return new ResumeScenario(monScenario, ordreVisite, distanceTotale);
    }
}
