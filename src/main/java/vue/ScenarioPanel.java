package vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import modele.Extraction;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Classe ScenarioPanel : panneau d'affichage d'un scénario de ventes.
 *
 * Affiche la liste des transactions (vendeur → acheteur) dans un ScrollPane,
 * avec un titre indiquant l'index du scénario en cours.
 *
 * Le panneau utilise une Extraction pour récupérer les données.
 *
 * Le style est géré via des IDs et classes CSS pour faciliter la personnalisation.
 *
 * Méthodes principales :
 * - setScenario(int) : charge et affiche un scénario donné.
 * - getIndex() : récupère l'index du scénario courant.
 * - setIndex(int) : modifie l'index du scénario.
 *
 * @author Samy et Romain
 * @see modele.Extraction
 */
public class ScenarioPanel extends VBox {
    private static int index = 0;
    private final VBox scrollContent = new VBox();
    private Extraction extraction;
    private final ScrollPane scrollPane = new ScrollPane();
    private final Label titre = new Label("Contenu du scénario 0");

    private final Label sousTitre = new Label("Scénario crée par l'utilisateur");

    /**
     * Constructeur par défaut.
     * Initialise les composants graphiques, définit les IDs CSS,
     * configure le ScrollPane et charge le scénario initial (index 0).
     *
     * @throws FileNotFoundException si les données d'extraction ne sont pas trouvées.
     */
    public ScenarioPanel() throws FileNotFoundException {
        this.extraction = new Extraction();
        scrollPane.setMaxHeight(400);
        this.setAlignment(Pos.TOP_CENTER);
        this.setId("scenarioPanel");
        this.setSpacing(8);
        titre.setId("titre_scenarioPanel");

        scrollPane.setContent(scrollContent);
        scrollPane.setFitToWidth(true);

        scrollContent.setId("scrollContent");

        sousTitre.setId("sous-titre_scenarioPanel");
        this.getChildren().addAll(titre, sousTitre, scrollPane);

        setScenario(extraction, index);
    }



    /**
     * Charge et affiche les données du scénario à l'index donné.
     * Met à jour le titre et liste les transactions dans le ScrollPane.
     * Ignore les entrées sans villes correspondantes.
     *
     * @param scenarioIndex Index du scénario à afficher.
     */
    public void setScenario(Extraction monExtraction,int scenarioIndex) throws FileNotFoundException {
        index = scenarioIndex;

        scrollContent.getChildren().clear();
        titre.setText("Contenu du scénario " + scenarioIndex);
        sousTitre.setVisible(false);
        if (index > 8) sousTitre.setVisible(true);
        Map<String, String> scenario = monExtraction.getScenarios().get(scenarioIndex);
        if (scenario == null) return;

        for (Map.Entry<String, String> entry : scenario.entrySet()) {
            String vendeur = entry.getKey();
            String acheteur = entry.getValue();
            String ville1 = monExtraction.getMembresVilles().get(vendeur);
            String ville2 = monExtraction.getMembresVilles().get(acheteur);

            if (ville1 == null || ville2 == null) continue;

            Label ligne = new Label("🛒 " + vendeur + " (" + ville1 + ") → " + acheteur + " (" + ville2 + ")");
            ligne.getStyleClass().add("ligneScenario");
            scrollContent.getChildren().add(ligne);
        }
    }

    /**
     * Récupère l'index du scénario actuellement affiché.
     *
     * @return L'index du scénario.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Modifie l'index du scénario courant.
     *
     * @param nouvelIndex Nouvel index à définir.
     */
    public void setIndex(int nouvelIndex) {
        index = nouvelIndex;
    }
}
