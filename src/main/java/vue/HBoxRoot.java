package vue;

import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import modele.Extraction;

import java.io.FileNotFoundException;

/**
 * Composant racine de l'interface graphique principale.
 *
 * Ce conteneur organise l'ensemble de la vue :
 * la barre de menu, le panneau de sélection des scénarios,
 * et l'affichage des résultats (tri topologique et heuristique gloutonne).
 */
public class HBoxRoot extends VBox {

    private final AffichageTriTopologique affichageTriTopologiqueInstance;
    private final AffichageHeuristiqueGlouton affichageHeuristiqueInstance;
    private final AffichageKSolutions affichageKSolutionsInstance;
    private final MenuBarRoot menuBarRoot;
    private ScenarioPanel scenarioPanel = new ScenarioPanel();
    private final HBox contenuHbox = new HBox();
    private final Pokedex pokedexInstance = null;
    /**
     * Construit l'interface principale avec les différents composants nécessaires :
     * - la barre de menu permettant de naviguer entre les vues,
     * - le panneau de sélection des scénarios,
     * - le conteneur d'affichage des résultats.
     *
     * @throws FileNotFoundException si les fichiers de données sont introuvables lors du chargement de l'extraction.
     */
    public HBoxRoot(HostServices hostServices) throws Exception {
        this.scenarioPanel = scenarioPanel;
        Extraction extraction = new Extraction();
        // Initialisation des deux vues d'affichage
        affichageTriTopologiqueInstance = new AffichageTriTopologique(extraction, 0);
        affichageHeuristiqueInstance = new AffichageHeuristiqueGlouton(extraction, 0);
        affichageKSolutionsInstance = new AffichageKSolutions(extraction, 0);


        // Barre de menu contrôlant l'affichage dans le contenu central
        menuBarRoot = new MenuBarRoot(
                scenarioPanel,
                affichageTriTopologiqueInstance,
                affichageHeuristiqueInstance,
                affichageKSolutionsInstance,
                contenuHbox,
                pokedexInstance,
                hostServices
        );
        HBox HBoxScenarioPanel = new HBox(scenarioPanel);
        HBoxScenarioPanel.setAlignment(Pos.CENTER);
        HBoxScenarioPanel.setId("HBoxScenarioPanel");
        contenuHbox.setAlignment(Pos.CENTER);
        VBox.setMargin(contenuHbox, new Insets(10));

        HBox.setMargin(affichageTriTopologiqueInstance, new Insets(10));
        HBox.setMargin(affichageHeuristiqueInstance, new Insets(10));
        this.setId("HBoxRoot");
        // Ajout des composants dans la hiérarchie de la fenêtre
        this.getChildren().addAll(menuBarRoot, HBoxScenarioPanel, contenuHbox);
    }
}
