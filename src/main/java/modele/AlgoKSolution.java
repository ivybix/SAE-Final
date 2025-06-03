package modele;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Classe implémentant l'algorithme de recherche des K meilleures solutions
 * pour le problème de tournée avec contraintes vendeur → acheteur.
 *
 * L'algorithme énumère toutes les solutions valides respectant :
 * - Contraintes d'ordre : pour chaque vente A→B, passer par A+ avant B-
 * - Départ et retour à Vélizy
 * - Maximum 2 passages par ville
 *
 * Les solutions sont triées par distance croissante.
 */
public class AlgoKSolution {
    private int kLimite;  // nombre max de solutions à générer
    private final Extraction extraction;
    private final int numeroScenario;
    private final List<String[]> ventes;
    private final Set<String> villes;
    private final List<ResultatSolution> solutions;

    public static ResumeScenario genererResumeScenario(int numeroScenario, int k, Extraction extraction) throws Exception {



        AlgoKSolution algo = new AlgoKSolution(extraction, numeroScenario);
        List<ResultatSolution> topSolutions = algo.genererKSolutionsResume(k);

        if (topSolutions.isEmpty()) {
            throw new Exception("Aucune solution trouvée.");
        }

        return topSolutions.get(0).resume;
    }




    /**
     * Classe interne pour stocker une solution avec son ordre de sommets et son résumé
     */
    private static class ResultatSolution {
        final List<String> ordreSommets;
        final ResumeScenario resume;

        ResultatSolution(List<String> ordreSommets, ResumeScenario resume) {
            this.ordreSommets = new ArrayList<>(ordreSommets);
            this.resume = resume;
        }
    }

    /**
     * Constructeur de l'algorithme K meilleures solutions.
     *
     * @param numeroScenario Le numéro du scénario à traiter.
     * @throws FileNotFoundException Si les fichiers de données ne sont pas trouvés.
     */
    public AlgoKSolution(Extraction extraction,int numeroScenario) throws FileNotFoundException {
        this.extraction = extraction;
        this.numeroScenario = numeroScenario;
        this.ventes = this.extraction.getVentes(numeroScenario);
        this.villes = new HashSet<>();
        this.solutions = new ArrayList<>();

        // Extraire toutes les villes impliquées dans les ventes
        for (String[] vente : ventes) {
            String villeVendeur = vente[0].replace("+", "");
            String villeAcheteur = vente[1].replace("-", "");
            villes.add(villeVendeur);
            villes.add(villeAcheteur);
        }
    }

    /**
     * Génère les K meilleures solutions pour le scénario.
     * Énumère toutes les solutions valides et affiche les K meilleures.
     *
     * @param k Le nombre de solutions à afficher.
     * @throws Exception En cas d'erreur lors du calcul des distances.
     */
    public void genererKSolutions(int k) throws Exception {
        this.kLimite = k;  // Limite le nombre de solutions
        afficherContraintes();

        System.out.println();

        List<String> sommets = new ArrayList<>();
        for (String ville : villes) {
            sommets.add(ville + "+");
            sommets.add(ville + "-");
        }

        genererSolutionsRecursive(new ArrayList<>(), new HashSet<>(), sommets);

        supprimerDoublons();

        solutions.sort(Comparator.comparingInt(s -> s.resume.distanceTotale));

        System.out.println("Nombre total de solutions uniques trouvees : " + solutions.size());
        System.out.println();

        int nbSolutionsAffichees = Math.min(k, solutions.size());

        if (solutions.isEmpty()) {
            System.out.println("Aucune solution valide trouvee pour ce scenario.");
            return;
        }

        System.out.println("LES " + nbSolutionsAffichees + " MEILLEURES SOLUTIONS :");
        System.out.println();

        for (int i = 0; i < nbSolutionsAffichees; i++) {
            afficherSolutionDetaillee(i + 1, solutions.get(i));
        }

        if (k > solutions.size()) {
            System.out.println("Attention : seulement " + solutions.size() +
                    " solutions valides existent (moins que les " + k + " demandees).");
        }
    }

    public List<ResultatSolution> genererKSolutionsResume(int k) throws Exception {
        this.kLimite = k;
        afficherContraintes();

        List<String> sommets = new ArrayList<>();
        for (String ville : villes) {
            sommets.add(ville + "+");
            sommets.add(ville + "-");
        }

        genererSolutionsRecursive(new ArrayList<>(), new HashSet<>(), sommets);
        supprimerDoublons();

        solutions.sort(Comparator.comparingInt(s -> s.resume.distanceTotale));

        return solutions.subList(0, Math.min(k, solutions.size()));
    }

    /**
     * Affiche les contraintes du scénario sous forme lisible
     */
    private void afficherContraintes() {
        System.out.println("CONTRAINTES DU SCENARIO " + numeroScenario + " :");
        System.out.println("   (Chaque vente impose : Vendeur+ doit etre visite AVANT Acheteur-)");
        System.out.println();

        for (int i = 0; i < ventes.size(); i++) {
            String[] vente = ventes.get(i);
            String vendeur = vente[0]; // déjà avec +
            String acheteur = vente[1]; // déjà avec -
            System.out.printf("   %2d. %s -> %s%n", i + 1, vendeur, acheteur);
        }
        System.out.println();
    }

    /**
     * Affiche une solution de manière détaillée
     */
    private void afficherSolutionDetaillee(int rang, ResultatSolution solution) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf(" SOLUTION #%-2d                                    Distance: %-6d km %n",
                rang, solution.resume.distanceTotale);
        System.out.println("--------------------------------------------------------------------------------");

        // Afficher le parcours des villes (sans + et -)
        System.out.println(" Parcours des villes :");
        System.out.print(" ");
        StringBuilder parcours = new StringBuilder();
        for (int i = 0; i < solution.resume.ordreVisite.size(); i++) {
            parcours.append(solution.resume.ordreVisite.get(i));
            if (i < solution.resume.ordreVisite.size() - 1) {
                parcours.append(" -> ");
            }
        }

        // Diviser le parcours en lignes si trop long
        String[] mots = parcours.toString().split(" -> ");
        StringBuilder ligneActuelle = new StringBuilder();

        for (int i = 0; i < mots.length; i++) {
            String prochainMot = mots[i] + (i < mots.length - 1 ? " -> " : "");
            if (ligneActuelle.length() + prochainMot.length() > 72) {
                // Afficher la ligne actuelle
                System.out.println(" " + ligneActuelle);
                ligneActuelle = new StringBuilder(prochainMot);
            } else {
                ligneActuelle.append(prochainMot);
            }
        }

        // Afficher la dernière ligne
        if (ligneActuelle.length() > 0) {
            System.out.println(" " + ligneActuelle);
        }

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println();
    }

    /**
     * Vérifie si on peut placer un sommet à la position courante.
     */
    private boolean peutPlacerSommet(String sommet, List<String> ordreCourant) {
        // Pour un sommet acheteur (ville-), vérifier que le vendeur correspondant
        // est déjà dans l'ordre pour toutes les ventes concernées
        if (sommet.endsWith("-")) {
            String villeAcheteur = sommet.replace("-", "");

            for (String[] vente : ventes) {
                String villeVendeur = vente[0].replace("+", "");
                String villeAcheteurVente = vente[1].replace("-", "");

                if (villeAcheteurVente.equals(villeAcheteur)) {
                    String sommetVendeur = villeVendeur + "+";
                    if (!ordreCourant.contains(sommetVendeur)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Vérifie si une solution respecte toutes les contraintes.
     */
    private boolean estSolutionValide(List<String> ordre) {
        // Vérifier que pour chaque vente A→B, A+ apparaît avant B- dans l'ordre
        for (String[] vente : ventes) {
            String vendeur = vente[0]; // "Ville+"
            String acheteur = vente[1]; // "Ville-"

            int indexVendeur = ordre.indexOf(vendeur);
            int indexAcheteur = ordre.indexOf(acheteur);

            if (indexVendeur == -1 || indexAcheteur == -1 || indexVendeur >= indexAcheteur) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convertit une liste de sommets en parcours de villes.
     */
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

    /**
     * Calcule la distance totale d'un parcours.
     */
    private int calculerDistance(List<String> parcours) throws Exception {
        int distance = 0;
        for (int i = 0; i < parcours.size() - 1; i++) {
            String from = parcours.get(i);
            String to = parcours.get(i + 1);
            if (!from.equals(to)) {
                distance += extraction.distanceVilleToVille(from, to);
            }
        }
        return distance;
    }

    /**
     * Supprime les solutions en doublon (même parcours de villes)
     */
    private void supprimerDoublons() {
        Set<String> parcoursVus = new HashSet<>();
        List<ResultatSolution> solutionsUniques = new ArrayList<>();

        for (ResultatSolution solution : solutions) {
            String cleParcours = String.join("->", solution.resume.ordreVisite);
            if (!parcoursVus.contains(cleParcours)) {
                parcoursVus.add(cleParcours);
                solutionsUniques.add(solution);
            }
        }

        solutions.clear();
        solutions.addAll(solutionsUniques);
    }

    /**
     * Retourne la liste des K meilleures solutions calculées.
     */
    public List<ResumeScenario> getKMeilleuresSolutions(int k) {
        int nbSolutions = Math.min(k, solutions.size());
        List<ResumeScenario> resultats = new ArrayList<>();
        for (int i = 0; i < nbSolutions; i++) {
            resultats.add(solutions.get(i).resume);
        }
        return resultats;
    }

    /**
     * Retourne le nombre total de solutions valides trouvées.
     */
    public int getNombreSolutions() {
        return solutions.size();
    }


    public List<ResultatSolution> getSolutions() {
        return solutions;
    }

    private void genererSolutionsRecursive(List<String> ordreCourant, Set<String> utilises,
                                           List<String> sommetsRestants) throws Exception {
        // Stoppe si on a déjà trouvé assez de solutions
        if (solutions.size() >= kLimite) {
            return;
        }

        // Cas où tous les sommets sont placés
        if (ordreCourant.size() == sommetsRestants.size()) {
            if (estSolutionValide(ordreCourant)) {
                List<String> parcours = convertirEnParcours(ordreCourant);
                int distance = calculerDistance(parcours);
                ResumeScenario resume = new ResumeScenario(numeroScenario, parcours, distance);
                solutions.add(new ResultatSolution(ordreCourant, resume));
            }
            return;
        }

        for (String sommet : sommetsRestants) {
            if (!utilises.contains(sommet)) {
                if (peutPlacerSommet(sommet, ordreCourant)) {
                    ordreCourant.add(sommet);
                    utilises.add(sommet);

                    genererSolutionsRecursive(ordreCourant, utilises, sommetsRestants);

                    ordreCourant.remove(ordreCourant.size() - 1);
                    utilises.remove(sommet);

                    // Si on a atteint la limite, on peut sortir de la boucle tôt
                    if (solutions.size() >= kLimite) {
                        return;
                    }
                }
            }
        }
    }

}