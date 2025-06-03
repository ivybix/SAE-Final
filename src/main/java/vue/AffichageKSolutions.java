package vue;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import modele.Extraction;
import modele.ResumeScenario;
import modele.AlgoKSolution;

/**
 * La classe {@code AffichageKSolutions} est un composant graphique JavaFX
 * destiné à afficher les résultats de l'algorithme k-solution pour un scénario donné.
 * Elle présente un résumé visuel incluant l'ordre de visite des villes ainsi que
 * la distance totale parcourue.
 *
 * Cette classe est conçue pour être intégrée à une interface utilisateur
 * permettant la sélection dynamique de différents scénarios.
 *
 *
 */
public class AffichageKSolutions extends VBox {

    private Extraction extraction;
    private int scenarioIndex;
    private ResumeScenario resume;

    /**
     * Construit un objet {@code AffichageKSolutions} configuré pour afficher
     * les résultats d’un scénario spécifique à partir d’une extraction de données.
     *
     * @param extraction    L'extraction contenant les données des scénarios.
     * @param scenarioIndex L'indice du scénario à afficher.
     */
    public AffichageKSolutions(Extraction extraction, int scenarioIndex) {
        this.setMaxWidth(600);
        this.setSpacing(15);
        this.extraction = extraction;
        this.scenarioIndex = scenarioIndex;
        this.setId("affichageKSolution");
        updateView();
    }

    /**
     * Met à jour l’affichage graphique avec les résultats de l'algorithme k-solution
     * pour le scénario actuellement sélectionné.
     * Si le résumé ne peut être généré (par exemple en cas d'erreur ou si le scénario
     * est introuvable), un message d'erreur est affiché à l'utilisateur.
     */
    private void updateView() {
        this.getChildren().clear();
        if (scenarioIndex > 2 ) {
            Label erreur = new Label("Erreur : Scénario sélectionné trop complexe \nScénario : " + scenarioIndex);
            erreur.setId("erreurLabel");
            this.getChildren().add(erreur);
            return;
        }

        try {
            AlgoKSolution algo = new AlgoKSolution(extraction, scenarioIndex);
            resume = algo.genererResumeScenario(scenarioIndex, 1, extraction);
        } catch (Exception e) {
            Label erreur = new Label("Erreur lors du chargement du scénario : " + e.getMessage());
            erreur.setId("erreurLabel");
            this.getChildren().add(erreur);
            e.printStackTrace();
            return;
        }

        Label titre = new Label("Algorithme K-Solution");
        titre.setId("titreAffichage");

        if (resume == null) {
            Label erreur = new Label("Erreur : aucun scénario trouvé pour l'index " + scenarioIndex);
            erreur.setId("erreurLabel");
            this.getChildren().add(erreur);
            return;
        }

        Label ordreLabel = new Label("Ordre de visite :");
        ordreLabel.setId("ordreLabel");

        int maxVillesParLigne = 8;
        StringBuilder cheminBuilder = new StringBuilder();

        for (int i = 0; i < resume.ordreVisite.size(); i++) {
            cheminBuilder.append(resume.ordreVisite.get(i));
            if (i < resume.ordreVisite.size() - 1) {
                cheminBuilder.append(" → ");
            }
            if ((i + 1) % maxVillesParLigne == 0 && i != resume.ordreVisite.size() - 1) {
                cheminBuilder.append("\n");
            }
        }

        Label cheminLabel = new Label(cheminBuilder.toString());
        cheminLabel.setWrapText(true);
        cheminLabel.setId("cheminLabel");

        Label distanceLabel = new Label("Distance totale : " + resume.distanceTotale + " km");
        distanceLabel.setId("distanceLabel");

        this.getChildren().addAll(titre, ordreLabel, cheminLabel, distanceLabel);
    }

    /**
     * Modifie l’indice du scénario actuellement affiché et met à jour l’interface
     * pour refléter le nouveau scénario.
     *
     * @param scenarioIndex Le nouvel indice du scénario à afficher.
     */
    public void setScenarioIndex(int scenarioIndex) {
        this.scenarioIndex = scenarioIndex;
        updateView();
    }

    /**
     * Remplace l'extraction de données utilisée par une nouvelle extraction
     * et rafraîchit l’affichage graphique.
     *
     * @param extraction La nouvelle extraction contenant les scénarios à afficher.
     */
    public void setExtraction(Extraction extraction) {
        this.extraction = extraction;
        updateView();
    }

    /**
     * Retourne l’indice du scénario actuellement affiché dans la vue.
     *
     * @return L’indice du scénario courant.
     */
    public int getScenarioIndex() {
        return scenarioIndex;
    }
}
