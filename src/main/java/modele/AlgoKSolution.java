package modele;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Version optimisée de l'algorithme K meilleures solutions.
 *
 * Optimisations principales :
 * 1. Branch and bound : élague les branches qui dépassent déjà la meilleure distance
 * 2. Ordre topologique : respecte les contraintes dès la construction
 * 3. Limite dynamique du nombre de solutions gardées
 * 4. Calcul incrémental des distances
 * 5. Heuristiques de tri des sommets
 * 6. Détection efficace des doublons basée sur le parcours final
 */
public class AlgoKSolution {
    private int kLimite;
    private final Extraction extraction;
    private final int numeroScenario;
    private final List<String[]> ventes;
    private final Set<String> villes;
    private final PriorityQueue<ResultatSolution> topKSolutions;
    private int meilleureDistanceActuelle = Integer.MAX_VALUE;

    // Changement : utiliser le parcours final comme clé unique au lieu de l'ordre des sommets
    private final Set<String> parcoursUniques = new HashSet<>();
    private final Map<String, Integer> distancesParParcours = new HashMap<>();

    private static final long LIMITE_TEMPS_MS = 10_000; // 10 secondes
    private static final int LIMITE_CALCULS = 500_000; // Nombre max de branches explorées
    private long debutExecution;
    private int compteurCalculs;

    // Cache des distances pour éviter les recalculs
    private final Map<String, Integer> cacheDistances = new HashMap<>();

    // Graphe des dépendances pour l'ordre topologique
    private final Map<String, Set<String>> dependances = new HashMap<>();
    private final Map<String, Integer> compteurPredecesseurs = new HashMap<>();

    /**
     * Génère un résumé du meilleur scénario parmi les K meilleures solutions possibles
     * pour un scénario donné.
     *
     * @param numeroScenario Le numéro du scénario à résoudre.
     * @param k Le nombre de meilleures solutions à générer.
     * @param extraction L'objet d'extraction contenant les données du scénario.
     * @return Un objet ResumeScenario représentant la meilleure solution trouvée.
     * @throws Exception Si aucune solution n'est trouvée ou en cas d'erreur.
     */
    public static ResumeScenario genererResumeScenario(int numeroScenario, int k, Extraction extraction) throws Exception {
        AlgoKSolution algo = new AlgoKSolution(extraction, numeroScenario);
        List<ResultatSolution> topSolutions = algo.genererKSolutionsResume(k);

        if (topSolutions.isEmpty()) {
            throw new Exception("Aucune solution trouvée.");
        }

        return topSolutions.get(0).resume;
    }

    /**
     * Génère les K meilleures solutions optimisées pour le scénario courant.
     *
     * @param k Le nombre de solutions à générer.
     * @return Une liste triée des meilleures solutions par distance croissante.
     * @throws Exception Si une erreur se produit pendant le calcul.
     */
    public List<ResultatSolution> genererKSolutionsResume(int k) throws Exception {
        this.kLimite = k;
        this.debutExecution = System.currentTimeMillis();
        this.compteurCalculs = 0;
        afficherContraintes();

        construireGrapheDependances();
        List<String> sommets = new ArrayList<>();
        for (String ville : villes) {
            sommets.add(ville + "+");
            sommets.add(ville + "-");
        }

        // Tri heuristique
        sommets.sort((a, b) -> {
            int depA = dependances.getOrDefault(a, Collections.emptySet()).size();
            int depB = dependances.getOrDefault(b, Collections.emptySet()).size();
            return Integer.compare(depB, depA);
        });

        genererSolutionsOptimisees(new ArrayList<>(), new HashSet<>(), sommets, 0);

        List<ResultatSolution> resultats = new ArrayList<>(topKSolutions);
        resultats.sort(Comparator.comparingInt(s -> s.resume.distanceTotale));

        return resultats.subList(0, Math.min(k, resultats.size()));
    }

    private static class ResultatSolution {
        final List<String> ordreSommets;
        final ResumeScenario resume;

        ResultatSolution(List<String> ordreSommets, ResumeScenario resume) {
            this.ordreSommets = new ArrayList<>(ordreSommets);
            this.resume = resume;
        }
    }

    public AlgoKSolution(Extraction extraction, int numeroScenario) throws FileNotFoundException {
        this.extraction = extraction;
        this.numeroScenario = numeroScenario;
        this.ventes = extraction.getVentes(numeroScenario);
        this.villes = new HashSet<>();
        this.topKSolutions = new PriorityQueue<>((a, b) ->
                Integer.compare(b.resume.distanceTotale, a.resume.distanceTotale)); // Max heap

        for (String[] vente : ventes) {
            String villeVendeur = vente[0].replace("+", "");
            String villeAcheteur = vente[1].replace("-", "");
            villes.add(villeVendeur);
            villes.add(villeAcheteur);
        }
    }

    /**
     * Construit le graphe des dépendances pour optimiser l'ordre de génération
     */
    private void construireGrapheDependances() {
        // Initialiser les structures
        for (String ville : villes) {
            String plus = ville + "+";
            String minus = ville + "-";
            dependances.put(plus, new HashSet<>());
            dependances.put(minus, new HashSet<>());
            compteurPredecesseurs.put(plus, 0);
            compteurPredecesseurs.put(minus, 0);
        }

        // Ajouter les dépendances basées sur les ventes
        for (String[] vente : ventes) {
            String vendeur = vente[0];
            String acheteur = vente[1];

            dependances.get(vendeur).add(acheteur);
            compteurPredecesseurs.put(acheteur, compteurPredecesseurs.get(acheteur) + 1);
        }
    }

    /**
     * Génère récursivement les solutions valides en respectant l'ordre topologique
     * et applique des optimisations comme branch and bound et tri heuristique.
     *
     * @param ordreCourant L'ordre partiel actuel des sommets.
     * @param utilises Les sommets déjà utilisés.
     * @param sommetsRestants Tous les sommets disponibles pour l'exploration.
     * @param distancePartielle Distance actuelle calculée jusqu'à ce point.
     * @throws Exception Si une erreur se produit lors du calcul de distance.
     */
    private void genererSolutionsOptimisees(List<String> ordreCourant, Set<String> utilises,
                                            List<String> sommetsRestants, int distancePartielle) throws Exception {
        // Condition d'arrêt temps ou calculs
        if (System.currentTimeMillis() - debutExecution > LIMITE_TEMPS_MS || compteurCalculs++ > LIMITE_CALCULS) {
            return;
        }

        if (distancePartielle >= meilleureDistanceActuelle) {
            return;
        }

        if (ordreCourant.size() == sommetsRestants.size()) {
            if (estSolutionValide(ordreCourant)) {
                List<String> parcours = convertirEnParcours(ordreCourant);

                // Créer une clé basée sur le parcours final (sans Velizy de début et fin)
                String cleParcours = creerCleParcours(parcours);

                // Vérifier si ce parcours existe déjà
                if (parcoursUniques.contains(cleParcours)) {
                    return; // Doublon détecté, ignorer
                }

                int distanceTotale = calculerDistanceComplete(parcours);

                // Vérifier aussi si une distance identique existe pour ce parcours
                if (distancesParParcours.containsKey(cleParcours) &&
                        distancesParParcours.get(cleParcours) <= distanceTotale) {
                    return; // Une solution identique ou meilleure existe déjà
                }

                // Enregistrer ce parcours comme unique
                parcoursUniques.add(cleParcours);
                distancesParParcours.put(cleParcours, distanceTotale);

                if (distanceTotale < meilleureDistanceActuelle) {
                    meilleureDistanceActuelle = distanceTotale;
                }

                ResumeScenario resume = new ResumeScenario(numeroScenario, parcours, distanceTotale);
                ResultatSolution solution = new ResultatSolution(ordreCourant, resume);

                if (topKSolutions.size() < kLimite) {
                    topKSolutions.offer(solution);
                } else if (distanceTotale < topKSolutions.peek().resume.distanceTotale) {
                    topKSolutions.poll();
                    topKSolutions.offer(solution);
                    meilleureDistanceActuelle = Math.min(meilleureDistanceActuelle,
                            topKSolutions.peek().resume.distanceTotale);
                }
            }
            return;
        }

        List<String> candidatsValides = new ArrayList<>();
        for (String sommet : sommetsRestants) {
            if (!utilises.contains(sommet) && peutPlacerSommetOptimise(sommet, utilises)) {
                candidatsValides.add(sommet);
            }
        }

        candidatsValides.sort((a, b) -> {
            int contraintesA = compterContraintesImposees(a, utilises);
            int contraintesB = compterContraintesImposees(b, utilises);
            return Integer.compare(contraintesB, contraintesA);
        });

        for (String sommet : candidatsValides) {
            ordreCourant.add(sommet);
            utilises.add(sommet);

            int nouvelleDistance = distancePartielle + calculerDistanceIncrementale(ordreCourant);

            genererSolutionsOptimisees(ordreCourant, utilises, sommetsRestants, nouvelleDistance);

            ordreCourant.remove(ordreCourant.size() - 1);
            utilises.remove(sommet);
        }
    }

    /**
     * Crée une clé unique basée sur le parcours effectif des villes (sans les Velizy de début/fin)
     * Cette clé permet de détecter les vrais doublons de parcours
     */
    private String creerCleParcours(List<String> parcours) {
        if (parcours.size() <= 2) {
            return ""; // Parcours vide ou minimal
        }

        // Extraire le parcours sans les Velizy de début et fin
        List<String> parcoursEffectif = parcours.subList(1, parcours.size() - 1);
        return String.join("->", parcoursEffectif);
    }

    /**
     * Version optimisée de la vérification de placement
     */
    private boolean peutPlacerSommetOptimise(String sommet, Set<String> utilises) {
        if (sommet.endsWith("-")) {
            String villeAcheteur = sommet.replace("-", "");

            for (String[] vente : ventes) {
                String villeVendeur = vente[0].replace("+", "");
                String villeAcheteurVente = vente[1].replace("-", "");

                if (villeAcheteurVente.equals(villeAcheteur)) {
                    String sommetVendeur = villeVendeur + "+";
                    if (!utilises.contains(sommetVendeur)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Compte combien de contraintes ce sommet impose aux choix futurs
     */
    private int compterContraintesImposees(String sommet, Set<String> utilises) {
        return dependances.getOrDefault(sommet, Collections.emptySet()).size();
    }

    /**
     * Calcule la distance incrémentale lors de l'ajout d'un sommet
     */
    private int calculerDistanceIncrementale(List<String> ordre) throws Exception {
        if (ordre.size() < 2) return 0;

        String dernierSommet = ordre.get(ordre.size() - 1);
        String avantDernierSommet = ordre.get(ordre.size() - 2);

        String dernierVille = dernierSommet.replace("+", "").replace("-", "");
        String avantDerniereVille = avantDernierSommet.replace("+", "").replace("-", "");

        if (dernierVille.equals(avantDerniereVille)) {
            return 0; // Pas de déplacement
        }

        String cle = avantDerniereVille + "->" + dernierVille;
        if (cacheDistances.containsKey(cle)) {
            return cacheDistances.get(cle);
        }

        int distance = extraction.distanceVilleToVille(avantDerniereVille, dernierVille);
        cacheDistances.put(cle, distance);
        return distance;
    }

    private boolean estSolutionValide(List<String> ordre) {
        for (String[] vente : ventes) {
            String vendeur = vente[0];
            String acheteur = vente[1];

            int indexVendeur = ordre.indexOf(vendeur);
            int indexAcheteur = ordre.indexOf(acheteur);

            if (indexVendeur == -1 || indexAcheteur == -1 || indexVendeur >= indexAcheteur) {
                return false;
            }
        }
        return true;
    }

    private List<String> convertirEnParcours(List<String> ordre) {
        List<String> parcours = new ArrayList<>();
        parcours.add("Velizy");

        String derniereVille = "Velizy";
        for (String sommet : ordre) {
            String ville = sommet.replace("+", "").replace("-", "");
            if (!ville.equals(derniereVille)) {
                parcours.add(ville);
                derniereVille = ville;
            }
        }

        parcours.add("Velizy");
        return parcours;
    }

    private int calculerDistanceComplete(List<String> parcours) throws Exception {
        int distance = 0;
        for (int i = 0; i < parcours.size() - 1; i++) {
            String from = parcours.get(i);
            String to = parcours.get(i + 1);
            if (!from.equals(to)) {
                String cle = from + "->" + to;
                if (cacheDistances.containsKey(cle)) {
                    distance += cacheDistances.get(cle);
                } else {
                    int dist = extraction.distanceVilleToVille(from, to);
                    cacheDistances.put(cle, dist);
                    distance += dist;
                }
            }
        }
        return distance;
    }

    private void afficherContraintes() {
        System.out.println("CONTRAINTES DU SCENARIO " + numeroScenario + " :");
        System.out.println("   (Chaque vente impose : Vendeur+ doit etre visite AVANT Acheteur-)");
        System.out.println();

        for (int i = 0; i < ventes.size(); i++) {
            String[] vente = ventes.get(i);
            String vendeur = vente[0];
            String acheteur = vente[1];
            System.out.printf("   %2d. %s -> %s%n", i + 1, vendeur, acheteur);
        }
        System.out.println();
    }

    // Méthodes de compatibilité avec l'interface existante
    public void genererKSolutions(int k) throws Exception {
        if (numeroScenario >=  2 ) {
            System.out.println("Erreur : Scénario sélectionné trop complexe");
            return;
        }
        List<ResultatSolution> solutions = genererKSolutionsResume(k);

        System.out.println("Nombre total de solutions uniques trouvees : " + solutions.size());
        System.out.println();

        if (solutions.isEmpty()) {
            System.out.println("Aucune solution valide trouvee pour ce scenario.");
            return;
        }

        System.out.println("LES " + Math.min(k, solutions.size()) + " MEILLEURES SOLUTIONS :");
        System.out.println();

        for (int i = 0; i < Math.min(k, solutions.size()); i++) {
            afficherSolutionDetaillee(i + 1, solutions.get(i));
        }
    }

    private void afficherSolutionDetaillee(int rang, ResultatSolution solution) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf(" SOLUTION #%-2d                                    Distance: %-6d km %n",
                rang, solution.resume.distanceTotale);
        System.out.println("--------------------------------------------------------------------------------");

        System.out.println(" Parcours des villes :");
        System.out.print(" ");
        StringBuilder parcours = new StringBuilder();
        for (int i = 0; i < solution.resume.ordreVisite.size(); i++) {
            parcours.append(solution.resume.ordreVisite.get(i));
            if (i < solution.resume.ordreVisite.size() - 1) {
                parcours.append(" -> ");
            }
        }

        String[] mots = parcours.toString().split(" -> ");
        StringBuilder ligneActuelle = new StringBuilder();

        for (int i = 0; i < mots.length; i++) {
            String prochainMot = mots[i] + (i < mots.length - 1 ? " -> " : "");
            if (ligneActuelle.length() + prochainMot.length() > 72) {
                System.out.println(" " + ligneActuelle);
                ligneActuelle = new StringBuilder(prochainMot);
            } else {
                ligneActuelle.append(prochainMot);
            }
        }

        if (ligneActuelle.length() > 0) {
            System.out.println(" " + ligneActuelle);
        }

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println();
    }

    public List<ResumeScenario> getKMeilleuresSolutions(int k) throws Exception {
        List<ResultatSolution> solutions = genererKSolutionsResume(k);
        List<ResumeScenario> resultats = new ArrayList<>();
        for (ResultatSolution sol : solutions) {
            resultats.add(sol.resume);
        }
        return resultats;
    }

    public int getNombreSolutions() {
        return topKSolutions.size();
    }
}
