package modele;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Implémente une heuristique gloutonne pour déterminer un parcours optimisé
 * de livraisons entre villes basé sur un scénario donné.
 *
 * Le parcours commence à Velizy, visite les vendeurs pour récupérer les cartes,
 * puis les acheteurs correspondants pour les livrer, en minimisant la distance parcourue à chaque étape.
 *
 * La classe utilise les données extraites via {@link Extraction} pour construire les sommets
 * (villes) et les commandes (livraisons) à réaliser.
 */
public class HeuristiqueGlouton {
    private final List<String> sommets;
    private static List<Commande> commandes;
    private static Extraction extraction;

    static {
        try {
            extraction = new Extraction();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialise l'heuristique pour un scénario donné.
     * Charge les villes et les commandes associées au scénario.
     *
     * @param scenarioChoisi L'index du scénario à charger.
     * @throws FileNotFoundException Si les fichiers de données ne sont pas trouvés.
     */
    public HeuristiqueGlouton(Extraction extraction, int scenarioChoisi) throws FileNotFoundException {
        this.extraction = extraction;
        sommets = new ArrayList<>();
        sommets.add("Velizy");
        sommets.addAll(extraction.getVilles(scenarioChoisi).keySet());

        commandes = new ArrayList<>();
        for (Map.Entry<String, String> entry : extraction.getScenarios().get(scenarioChoisi).entrySet()) {
            String vendeur = extraction.getMembresVilles().get(entry.getKey());
            String acheteur = extraction.getMembresVilles().get(entry.getValue());

            // Patch simple pour SaintEtienne
            vendeur = normaliserSaintEtienne(vendeur);
            acheteur = normaliserSaintEtienne(acheteur);

            if (vendeur != null && acheteur != null && !vendeur.equals(acheteur)) {
                commandes.add(new Commande(vendeur, acheteur, entry.getKey(), entry.getValue()));
            }
        }
    }

    /**
     * Normalise les variantes de noms de ville SaintEtienne pour éviter les incohérences.
     *
     * @param ville Nom de la ville à normaliser.
     * @return "SaintEtienne" si la ville correspond à une variante de ce nom, sinon la ville inchangée.
     */
    private String normaliserSaintEtienne(String ville) {
        if (ville == null) return null;
        // Remplace toutes variantes par "SaintEtienne"
        if (ville.replaceAll("[\\s\\-éÉèÈêÊ]", "").equalsIgnoreCase("saintetienne")) {
            return "SaintEtienne";
        }
        return ville;
    }

    /**
     * Calcule un parcours glouton optimisé pour la tournée des livraisons.
     * Commence à Velizy, visite tous les vendeurs puis les acheteurs en minimisant
     * la distance entre chaque étape.
     *
     * @return La liste ordonnée des villes visitées dans le parcours.
     * @throws Exception En cas d'erreur lors du calcul des distances.
     */
    public static List<String> parcoursGlouton() throws Exception {
        Set<String> villesAVisiter = new HashSet<>();
        Map<String, List<String>> livraisons = new HashMap<>();
        for (Commande c : commandes) {
            villesAVisiter.add(c.vendeur);
            villesAVisiter.add(c.acheteur);
            livraisons.computeIfAbsent(c.vendeur, k -> new ArrayList<>()).add(c.acheteur);
        }

        List<String> parcours = new ArrayList<>();
        Set<String> cartesRamassees = new HashSet<>();
        Set<Commande> commandesLivrees = new HashSet<>();

        String villeActuelle = "Velizy";
        parcours.add(villeActuelle);

        while (commandesLivrees.size() < commandes.size()) {
            String prochaineVille = null;
            int minDistance = Integer.MAX_VALUE;

            for (Commande c : commandes) {
                if (!cartesRamassees.contains(c.vendeur)) {
                    int d = extraction.distanceVilleToVille(villeActuelle, c.vendeur);
                    if (d < minDistance) {
                        minDistance = d;
                        prochaineVille = c.vendeur;
                    }
                } else if (cartesRamassees.contains(c.vendeur) && !commandesLivrees.contains(c)) {
                    int d = extraction.distanceVilleToVille(villeActuelle, c.acheteur);
                    if (d < minDistance) {
                        minDistance = d;
                        prochaineVille = c.acheteur;
                    }
                }
            }

            if (prochaineVille == null) break;
            villeActuelle = prochaineVille;
            parcours.add(villeActuelle);

            for (Commande c : commandes) {
                if (villeActuelle.equals(c.vendeur)) {
                    cartesRamassees.add(c.vendeur);
                } else if (villeActuelle.equals(c.acheteur) && cartesRamassees.contains(c.vendeur)) {
                    commandesLivrees.add(c);
                }
            }
        }

        if (!villeActuelle.equals("Velizy")) parcours.add("Velizy");
        return parcours;
    }
    /**
     * Calcule la distance totale du parcours donné.
     *
     * @param parcours Liste ordonnée des villes du parcours.
     * @return La distance totale en kilomètres.
     * @throws Exception En cas d'erreur lors de l'accès aux distances entre villes.
     */
    public static int calculDistance(List<String> parcours) throws Exception {
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
     * Retourne la liste des commandes à effectuer dans le scénario.
     *
     * @return Liste des {@link Commande}.
     */
    public List<Commande> getCommandes() {
        return commandes;
    }

    /**
     * Retourne la liste des sommets (villes) impliquées dans le scénario.
     *
     * @return Liste des noms de villes.
     */
    public List<String> getSommets() {
        return sommets;
    }
    /**
     * Génère un résumé du scénario, comprenant le parcours calculé et la distance totale.
     *
     * @param numeroScenario Numéro du scénario à résumer.
     * @return Un objet {@link ResumeScenario} contenant les résultats.
     * @throws Exception En cas d'erreur lors du calcul du parcours ou de la distance.
     */
    public static ResumeScenario genererResumeScenario(int numeroScenario) throws Exception {
        List<String> parcours = parcoursGlouton();
        int distance = calculDistance(parcours);
        return new ResumeScenario(numeroScenario, parcours, distance);
    }


    /**
     * Représente une commande de livraison entre un vendeur et un acheteur,
     * associée aux noms des membres (cartes) correspondants.
     */
    public static class Commande {
        public final String vendeur;
        public final String acheteur;
        public final String nomVendeur;
        public final String nomAcheteur;

        /**
         * Constructeur d'une commande.
         *
         * @param vendeur Ville du vendeur.
         * @param acheteur Ville de l'acheteur.
         * @param nomVendeur Nom du membre vendeur (carte).
         * @param nomAcheteur Nom du membre acheteur (carte).
         */
        public Commande(String vendeur, String acheteur, String nomVendeur, String nomAcheteur) {
            this.vendeur = vendeur;
            this.acheteur = acheteur;
            this.nomVendeur = nomVendeur;
            this.nomAcheteur = nomAcheteur;
        }

        @Override
        public String toString() {
            return nomVendeur + "(" + vendeur + ") -> " + nomAcheteur + "(" + acheteur + ").";
        }
    }
}