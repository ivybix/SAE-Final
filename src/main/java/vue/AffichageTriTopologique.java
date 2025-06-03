package vue;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import modele.Extraction;
import modele.TriTopologique;
import modele.ResumeScenario;

import java.io.FileNotFoundException;

/**
 * Composant graphique JavaFX pour afficher le résultat d’un tri topologique
 * appliqué à un scénario de livraison.
 *
 * Cette vue présente l’ordre de visite des villes ainsi que la distance totale,
 * en se basant sur les données extraites et un algorithme de tri topologique.
 */
public class AffichageTriTopologique extends VBox {

    private Extraction extraction;
    private int scenarioIndex;
    private ResumeScenario resume;

    /**
     * Construit l'affichage du tri topologique pour un scénario donné.
     *
     * @param extraction     L'extraction contenant les données des scénarios.
     * @param scenarioIndex  L'index du scénario à afficher.
     */
    public AffichageTriTopologique(Extraction extraction, int scenarioIndex) throws FileNotFoundException {
        this.setMaxWidth(600);
        this.setSpacing(15);
        this.extraction = extraction;
        this.scenarioIndex = scenarioIndex;
        this.setId("affichageTriTopologique");
        updateView();
    }

    /**
     * Met à jour l'affichage selon les données du scénario courant.
     * Affiche un message d'erreur si le résumé est introuvable.
     */
    private void updateView() throws FileNotFoundException {
        this.getChildren().clear();

        this.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0; -fx-border-color: #cccccc;");
        Label titre = new Label("Tri Topologique");
        titre.setId("titreAffichage");
        resume = TriTopologique.getResumeScenario(extraction, scenarioIndex);

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
     * Définit un nouvel index de scénario et rafraîchit l'affichage.
     *
     * @param scenarioIndex L'index du scénario à charger.
     */
    public void setScenarioIndex(int scenarioIndex) throws FileNotFoundException {
        this.scenarioIndex = scenarioIndex;
        updateView();
    }

    /**
     * Définit une nouvelle extraction de données et rafraîchit l'affichage.
     *
     * @param extraction L'objet Extraction mis à jour.
     */
    public void setExtraction(Extraction extraction) throws FileNotFoundException {
        this.extraction = extraction;
        updateView();
    }

    /**
     * Retourne l’index du scénario actuellement affiché.
     *
     * @return L’index du scénario sélectionné.
     */
    public int getScenarioIndex() {
        return scenarioIndex;
    }
}
