package modele;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static java.lang.Integer.parseInt;

/**
 * Classe Extraction qui lit et stocke les données des fichiers ressources :
 *  distances.txt : matrice des distances entre villes,
 *  fichiers scenario* : échanges entre membres,
 *  fichiers membres* : association membre → ville.
 * Fournit des méthodes pour accéder aux scénarios, aux villes, aux distances
 * et pour extraire les ventes sous forme de relations vendeurs → acheteurs.
 */
public class Extraction {
    private final HashMap<Integer, HashMap<String, String>> scenarios;
    private final TreeMap<String, ArrayList<Integer>> distanceVilles;
    private final HashMap<String, String> membresVilles;
    private List<String> listeVillesOrdonnee;
    /**
     * Constructeur qui charge tous les fichiers dans le dossier Ressources.
     *
     * @throws FileNotFoundException si un fichier de ressources est introuvable.
     */
    public Extraction() throws FileNotFoundException {
        File ressources = new File("src", File.separator + "main" + File.separator + "java" + File.separator + "Ressources");
        this.membresVilles = new HashMap<>();
        this.scenarios = new HashMap<>();
        this.distanceVilles = new TreeMap<>();
        this.listeVillesOrdonnee = new ArrayList<>();

        int Nscenario = 0;

        for (File f : Objects.requireNonNull(ressources.listFiles())) {

            if (f.getName().equals("distances.txt")) {
                Scanner lecteur = new Scanner(f);

                while (lecteur.hasNextLine()) {
                    String data = lecteur.nextLine().trim();
                    if (!data.isEmpty()) {
                        String[] tokens = data.split(" ");
                        String ville = tokens[0];
                        listeVillesOrdonnee.add(ville); // Enregistre la ville dans l'ordre

                        ArrayList<Integer> distances = new ArrayList<>();
                        for (int i = 1; i < tokens.length; i++) {
                            try {
                                distances.add(Integer.parseInt(tokens[i]));
                            } catch (NumberFormatException e) {
                                // Ignorer les erreurs de conversion
                            }
                        }
                        distanceVilles.put(ville, distances);
                    }
                }
                lecteur.close();
            }

            if (f.getName().startsWith("scenario")) {
                Scanner lecteur = new Scanner(f);
                HashMap<String, String> echanges = new HashMap<>();
                while (lecteur.hasNextLine()) {
                    String data = lecteur.nextLine();
                    if (data.contains(" -> ")) {
                        String[] parts = data.split(" -> ");
                        echanges.put(parts[0].trim(), parts[1].trim());
                    }
                }
                scenarios.put(Nscenario, echanges);
                lecteur.close();
                Nscenario++;
            }

            if (f.getName().startsWith("membres")) {
                Scanner lecteur = new Scanner(f);
                while (lecteur.hasNextLine()) {
                    String data = lecteur.nextLine();
                    if (!data.isEmpty()) {
                        String[] parts = data.split(" ");
                        membresVilles.put(parts[0].trim(), parts[1].trim());
                    }
                }
                lecteur.close();
            }
        }
    }
    /**
     * Retourne la map des scénarios chargés.
     * Chaque scénario est une map pseudoVendeur → pseudoAcheteur.
     *
     * @return HashMap<Integer, HashMap<String, String>> des scénarios.
     */
    public HashMap<Integer, HashMap<String, String>> getScenarios() {
        return scenarios;
    }
    /**
     * Retourne la map des membres et leurs villes associées.
     *
     * @return HashMap<String, String> membre -> ville.
     */
    public HashMap<String, String> getMembresVilles() {
        return membresVilles;
    }

    /**
     * Pour un scénario donné, retourne une map villeVendeur → liste villesAcheteurs.
     *
     * @param numScenario numéro du scénario à traiter.
     * @return Map<String, List<String>> des livraisons par ville.
     */
    public Map<String, List<String>> getVilles(int numScenario) {
        Map<String, List<String>> villes = new HashMap<>();

        for (Map.Entry<String, String> entry : this.getScenarios().get(numScenario).entrySet()) {
            String vendeur = entry.getKey();
            String acheteur = entry.getValue();
            String ville = this.getMembresVilles().get(vendeur);
            String ville2 = this.getMembresVilles().get(acheteur);


            villes.putIfAbsent(ville, new ArrayList<>());

            villes.get(ville).add(ville2);
        }
        return villes;
    }
    /**
     * Calcule la distance entre deux villes.
     *
     * @param villeDepart nom de la ville de départ.
     * @param villeArriver nom de la ville d'arrivée.
     * @return distance en entier.
     * @throws Exception si une des villes n'est pas reconnue.
     */
    public int distanceVilleToVille(String villeDepart, String villeArriver) throws Exception {
        villeDepart = villeDepart.trim();
        villeArriver = villeArriver.trim();

        int indexArrivee = listeVillesOrdonnee.indexOf(villeArriver);
        if (indexArrivee == -1) {
            throw new Exception("Ville arrivée inconnue : " + villeArriver);
        }

        List<Integer> distancesFromDepart = distanceVilles.get(villeDepart);
        if (distancesFromDepart == null) {
            throw new Exception("Ville départ inconnue : " + villeDepart);
        }

        int distance = distancesFromDepart.get(indexArrivee);
        return distance;
    }

    /**
     * Retourne la map complète des distances ville -> liste de distances.
     *
     * @return TreeMap<String, ArrayList<Integer>> des distances.
     */
    public TreeMap<String, ArrayList<Integer>> getDistances() {
        return distanceVilles;
    }
    /**
     * Pour un scénario donné, retourne la liste des ventes sous forme de couples
     * sommets orientés (villeVendeur+, villeAcheteur-).
     *
     * @param monScenario numéro du scénario.
     * @return List<String[]> liste des ventes [vendeur+, acheteur-].
     */
    public List<String[]> getVentes(int monScenario) {
        List<String[]> ventes = new ArrayList<>();

        Map<String, String> scenario = this.getScenarios().get(monScenario);
        Map<String, String> membresVilles = this.getMembresVilles();

        for (Map.Entry<String, String> entry : scenario.entrySet()) {
            String pseudoVendeur = entry.getKey();
            String pseudoAcheteur = entry.getValue();

            String villeVendeur = membresVilles.get(pseudoVendeur);
            String villeAcheteur = membresVilles.get(pseudoAcheteur);

            String sommetVendeur = villeVendeur + "+";
            String sommetAcheteur = villeAcheteur + "-";

            ventes.add(new String[]{sommetVendeur, sommetAcheteur});
        }

        return ventes;
    }

    /**
     * Ajoute un nouveau scénario à la collection des scénarios existants.
     * Le scénario est ajouté avec un identifiant automatiquement calculé
     * comme la clé maximale existante + 1.
     *
     * @param contenu HashMap<String, String> représentant les paires
     *                vendeur → acheteur du scénario à ajouter.
     */
    public void ajouterScenario(int numScenario,HashMap<String,String> contenu) {
        scenarios.put(numScenario, contenu);
        System.out.println("Ajout scénario avec clé " + numScenario + " : " + scenarios.keySet());
    }



    /**
     * Valide un scénario donné en vérifiant que chaque paire vendeur → acheteur
     * respecte les conditions suivantes :
     * - Le scénario n'est pas vide.
     * - Chaque vendeur et acheteur existe dans la map des membres avec leur ville.
     * - Un vendeur ne peut pas être le même que l'acheteur.
     *
     * @param contenuDuScenario HashMap<String, String> représentant les paires
     *                          vendeur → acheteur à valider.
     * @return boolean true si le scénario est valide, false sinon.
     */

    public boolean validerScenario(HashMap<String, String> contenuDuScenario) {
        if (contenuDuScenario.isEmpty()) {
            return false; // Pas de paires, scénario vide
        }

        for (Map.Entry<String, String> entry : contenuDuScenario.entrySet()) {
            String vendeur = entry.getKey();
            String acheteur = entry.getValue();

            // Vérifier que le vendeur et l'acheteur existent dans la map membresVilles
            if (!membresVilles.containsKey(vendeur) || !membresVilles.containsKey(acheteur)) {
                return false;
            }
            // Vérifier que vendeur et acheteur ne sont pas identiques
            if (vendeur.equals(acheteur)) {
                return false;
            }
        }

        return true;
    }

}