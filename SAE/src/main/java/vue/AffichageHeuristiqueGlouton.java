package vue;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import modele.Extraction;
import modele.ResumeScenario;
import modele.HeuristiqueGlouton;

/**
 * Cette classe représente un composant graphique JavaFX qui affiche
 * les résultats de l'algorithme heuristique glouton pour un scénario donné.
 * Elle est composée d’un résumé visuel : ordre de visite des villes et
 * distance totale parcourue.
 *
 * Elle est conçue pour s'intégrer dans une interface graphique où
 * différents scénarios peuvent être sélectionnés dynamiquement.
 */
public class AffichageHeuristiqueGlouton extends VBox {

    private Extraction extraction;
    private int scenarioIndex;
    private ResumeScenario resume;

    /**
     * Constructeur principal de l'affichage.
     *
     * @param extraction     L'extraction des données du scénario.
     * @param scenarioIndex  L'indice du scénario à afficher.
     */
    public AffichageHeuristiqueGlouton(Extraction extraction, int scenarioIndex) {
        this.setMaxWidth(600);
        this.setSpacing(15);
        this.extraction = extraction;
        this.scenarioIndex = scenarioIndex;
        this.setId("affichageHeuristiqueGlouton");
        updateView();
    }

    /**
     * Met à jour l'affichage graphique à partir des données du scénario courant.
     * Si une erreur survient lors du calcul de l'heuristique ou si le résumé est nul,
     * un message d'erreur est affiché.
     */
    private void updateView() {
        this.getChildren().clear();
        this.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0; -fx-border-color: #cccccc;");

        try {
            HeuristiqueGlouton hg = new HeuristiqueGlouton(extraction, scenarioIndex);

            resume = hg.genererResumeScenario(scenarioIndex);
        } catch (Exception e) {
            Label erreur = new Label("Erreur lors du chargement du scénario : " + e.getMessage());
            System.out.println(e);
            erreur.setId("erreurLabel");
            this.getChildren().add(erreur);
            return;
        }
        Label titre = new Label("Tri Heuristique");
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

        this.getChildren().addAll(titre,ordreLabel, cheminLabel, distanceLabel);
    }

    /**
     * Met à jour l’indice du scénario affiché et rafraîchit la vue.
     *
     * @param scenarioIndex Le nouvel indice de scénario.
     */
    public void setScenarioIndex(int scenarioIndex) {
        this.scenarioIndex = scenarioIndex;
        updateView();
    }

    /**
     * Met à jour les données d'extraction et rafraîchit l’affichage.
     *
     * @param extraction La nouvelle extraction contenant les données.
     */
    public void setExtraction(Extraction extraction) {
        this.extraction = extraction;
        updateView();
    }

    /**
     * Retourne l'indice du scénario actuellement affiché.
     *
     * @return L’indice du scénario.
     */
    public int getScenarioIndex() {
        return scenarioIndex;
    }
}
