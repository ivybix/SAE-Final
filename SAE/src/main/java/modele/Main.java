package modele;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principale de l'application permettant d'exécuter et comparer
 * deux algorithmes de parcours pour un scénario donné :
 *
 * 
 *   Tri topologique des villes via la classe {@link TriTopologique}.
 *   Heuristique gloutonne via la classe {@link HeuristiqueGlouton}.
 * 
 *
 * Le programme demande à l'utilisateur de saisir un numéro de scénario existant,
 * puis affiche successivement les résultats des deux algorithmes :
 * 
 *   Le résultat textuel du tri topologique.
 *   Le résumé du parcours calculé par l'heuristique gloutonne, 
 *       sous la forme d'un objet {@link ResumeScenario} affichant l'ordre des visites et la distance totale.
 * 
 *
 * Le programme gère les erreurs liées à l'absence des fichiers de données ou autres exceptions
 * en affichant des messages adaptés.
 */
public class Main {

    /**
     * Point d'entrée principal de l'application.
     *
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        try {
            Extraction extraction = new Extraction();

            // Demande à l'utilisateur quel scénario il veut tester
            Scanner scanner = new Scanner(System.in);
            System.out.print("Entrez le numéro du scénario à exécuter (0 à " + (extraction.getScenarios().size() - 1) + ") : ");
            int scenarioChoisi = scanner.nextInt();

            if (scenarioChoisi < 0 || scenarioChoisi >= extraction.getScenarios().size()) {
                System.out.println("Numéro de scénario invalide.");
                return;
            }

            // Tri topologique 
            System.out.println("\n===== Résultat du Tri Topologique =====");
            String triTopoResultat = TriTopologique.trierVilles(extraction, scenarioChoisi);
            System.out.println(triTopoResultat);

            // Heuristique gloutonne
            System.out.println("\n===== Résultat de l'Heuristique Gloutonne =====");
            HeuristiqueGlouton heuristique = new HeuristiqueGlouton(extraction, scenarioChoisi);
            ResumeScenario resumeGlouton = heuristique.genererResumeScenario(scenarioChoisi);
            System.out.println(resumeGlouton);

            // K meilleurs solution
            System.out.println("\n===== Résultat des K Meilleures Solutions =====");
            System.out.print("Combien de solutions souhaitez-vous afficher ? (1 à 100) : ");
            int k = scanner.nextInt();

            if (k < 1 || k > 100) {
                System.out.println("Valeur de k invalide. Veuillez choisir un nombre entre 1 et 100.");
                return;
            }

            AlgoKSolution algo = new AlgoKSolution(extraction, scenarioChoisi);
            algo.genererKSolutions(k);

        } catch (FileNotFoundException e) {
            System.out.println("Fichier de données non trouvé : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur lors du calcul : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
