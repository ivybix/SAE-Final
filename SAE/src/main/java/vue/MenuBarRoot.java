package vue;

import javafx.application.HostServices;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import modele.Extraction;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Barre de menu principale de l'application. Elle permet :
 * - de sélectionner un scénario à afficher,
 * - d'afficher ou masquer les vues du tri topologique ou de l'heuristique gloutonne.
 */
public class MenuBarRoot extends MenuBar {
    private final ToggleGroup toggleGroup = new ToggleGroup();
    private final AffichageTriTopologique affichageTriTopologiqueInstance;
    private final AffichageHeuristiqueGlouton affichageHeuristiqueGloutonInstance;
    private final AffichageKSolutions affichageKSolutionsInstance;
    private final HBox contenuHbox;
    private final ScenarioPanel scenarioPanel;
    private final Extraction extraction;
    private final Pokedex pokedexInstance;
    private final Menu menuScenarios;  // Champ pour le menu scénarios
    /**
     * Constructeur de la barre de menu.
     *
     * @param scenarioPanel                       Panel d'information sur le scénario sélectionné.
     * @param affichageTriTopologiqueInstance     Instance de l'affichage du tri topologique à mettre à jour.
     * @param affichageHeuristiqueGloutonInstance Instance de l'affichage de l'heuristique gloutonne à mettre à jour.
     * @param contenuHbox                         Conteneur principal dans lequel les vues sont affichées dynamiquement.
     * @throws FileNotFoundException si les fichiers nécessaires à l'extraction ne sont pas trouvés.
     **/
    public MenuBarRoot(
            ScenarioPanel scenarioPanel,
            AffichageTriTopologique affichageTriTopologiqueInstance,
            AffichageHeuristiqueGlouton affichageHeuristiqueGloutonInstance,
            AffichageKSolutions affichageKSolutionsInstance,
            HBox contenuHbox,
            Pokedex pokedexInstance,
            HostServices hostServices
    ) throws FileNotFoundException {
        this.affichageTriTopologiqueInstance = affichageTriTopologiqueInstance;
        this.affichageHeuristiqueGloutonInstance = affichageHeuristiqueGloutonInstance;
        this.affichageKSolutionsInstance = affichageKSolutionsInstance;
        this.pokedexInstance = pokedexInstance;
        this.contenuHbox = contenuHbox;
        this.scenarioPanel = scenarioPanel;
        this.extraction = new Extraction();

        // Menu gestion scénario
        Menu menuGestionScenarios = new Menu("Gestion de scenarios");



        MenuItem ajoutScenario = new MenuItem("Ajout de scenario");
        ajoutScenario.setOnAction(event -> {
            try {
                AjoutScenario popup = new AjoutScenario(extraction);
                popup.getStage().setOnHidden(e -> refreshMenuScenarios());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        MenuItem modificationDeScenario = new MenuItem("modification de scenario");

        modificationDeScenario.setOnAction(event -> {
            ModificationScenario popup = null;
            try {
                popup = new ModificationScenario(extraction);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            popup.getStage().setOnHidden(e -> refreshMenuScenarios());
        });
        menuGestionScenarios.getItems().addAll(ajoutScenario, modificationDeScenario);

        // Menu Scénarios
        menuScenarios = new Menu("Scénarios");

        refreshMenuScenarios(); // Remplit le menu avec les scénarios

        // Listener sur le toggleGroup : UNE SEULE FOIS ici
        toggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
              int selectedScenario = (int) newToggle.getUserData();
                try {

                    affichageTriTopologiqueInstance.setExtraction(extraction);
                    affichageTriTopologiqueInstance.setScenarioIndex(selectedScenario);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                affichageKSolutionsInstance.setExtraction(extraction);
                affichageKSolutionsInstance.setScenarioIndex(selectedScenario);

                affichageHeuristiqueGloutonInstance.setExtraction(extraction);


                affichageHeuristiqueGloutonInstance.setScenarioIndex(selectedScenario);
                try {
                    scenarioPanel.setScenario(extraction, selectedScenario);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Sélection par défaut
        if (!toggleGroup.getToggles().isEmpty()) {
            toggleGroup.getToggles().get(0).setSelected(true);
        }

        // Menus Tri Topologique et Heuristique gloutonne (identiques à ton code)
        Menu triTopologique = new Menu("Tri Topologique");
        MenuItem afficherLeTriTopologique = new MenuItem("Afficher le tri topologique");
        afficherLeTriTopologique.setOnAction(e -> {

            contenuHbox.getChildren().removeIf(node ->
                    "affichageTriTopologique".equals(node.getId())
            );


            contenuHbox.getChildren().add(affichageTriTopologiqueInstance);

        });
        MenuItem masquerLeTriTopologique = new MenuItem("Masquer le tri topologique");
        masquerLeTriTopologique.setOnAction(e -> {
            contenuHbox.getChildren().removeIf(node ->
                    "affichageTriTopologique".equals(node.getId())
            );
        });
        triTopologique.getItems().addAll(afficherLeTriTopologique, masquerLeTriTopologique);

        Menu triHeuristiqueGlouton = new Menu("Tri Heuristique");
        MenuItem afficherLeTriHeuristique = new MenuItem("Afficher le tri heuristique");
        afficherLeTriHeuristique.setOnAction(e -> {
            contenuHbox.getChildren().removeIf(node ->
                    "affichageHeuristiqueGlouton".equals(node.getId())
            );
            contenuHbox.getChildren().add(affichageHeuristiqueGloutonInstance);
        });
        MenuItem masquerLeTriHeuristique = new MenuItem("Masquer le tri heuristique");
        masquerLeTriHeuristique.setOnAction(e -> {
            contenuHbox.getChildren().removeIf(node ->
                    "affichageHeuristiqueGlouton".equals(node.getId())
            );
        });
        triHeuristiqueGlouton.getItems().addAll(afficherLeTriHeuristique, masquerLeTriHeuristique);


        Menu triKsolution = new Menu("Tri K Solution");
        MenuItem afficherTriKsolution = new MenuItem("Afficher le tri K Solution");
        afficherTriKsolution.setOnAction(e -> {
            contenuHbox.getChildren().removeIf(node ->
                    "affichageKSolution".equals(node.getId())
            );
            contenuHbox.getChildren().add(affichageKSolutionsInstance);
        });
        MenuItem masquerLeTriKsolution = new MenuItem("Masquer le tri K Solution");
        masquerLeTriKsolution.setOnAction(e -> {
            contenuHbox.getChildren().removeIf(node ->
                    "affichageKSolution".equals(node.getId())
            );
        });
        triKsolution.getItems().addAll(afficherTriKsolution, masquerLeTriKsolution);

        Menu menuPokedex = new Menu("Pokedex");
        MenuItem afficherPokedex = new MenuItem("Afficher pokedex");

        afficherPokedex.setOnAction(event -> {
            try {
                new Pokedex(hostServices);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        menuPokedex.getItems().addAll(afficherPokedex);
        // Ajout des menus à la barre
        this.getMenus().addAll(menuGestionScenarios, menuScenarios, triTopologique, triHeuristiqueGlouton, triKsolution, menuPokedex);
    }

    /**
     * Recharge la liste des scénarios dans le menu.
     */
    private void refreshMenuScenarios() {
        menuScenarios.getItems().clear();
        toggleGroup.getToggles().clear();

        for (int i : extraction.getScenarios().keySet()) {
            RadioMenuItem item = new RadioMenuItem("Scénario n°" + i);
            item.setToggleGroup(toggleGroup);
            item.setUserData(i);
            menuScenarios.getItems().add(item);
        }

        if (!toggleGroup.getToggles().isEmpty()) {
            toggleGroup.getToggles().get(0).setSelected(true);
        }
        System.out.println("Scenarios actuels dans extraction : " + extraction.getScenarios().keySet());

    }
}
