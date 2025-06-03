package Controleur;

import javafx.application.HostServices;
import javafx.beans.value.ChangeListener;
import javafx.event.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import modele.*;
import vue.*;

import java.io.FileNotFoundException;

/**
 * Classe Controleur centralisant la gestion des événements utilisateur
 * dans l'application JavaFX.
 * Elle s'occupe de l'affichage/masquage des différentes vues (tri topologique,
 * heuristique, K solutions) ainsi que du changement de scénario.
 *
 * Cette classe suit le patron MVC en jouant le rôle de contrôleur
 * entre les composants Vue (JavaFX) et le Modèle (Extraction).
 */
public class Controleur implements EventHandler<ActionEvent> {

    private AffichageTriTopologique affichageTriTopologiqueInstance;
    private AffichageKSolutions affichageKSolutionsInstance;
    private AffichageHeuristiqueGlouton affichageHeuristiqueGloutonInstance;

    private Extraction extraction;
    private ScenarioPanel scenarioPanel;
    private HBox contenuHbox;
    private MenuBarRoot menuBarRoot;
    private Pokedex pokedex;
    private HostServices hostServices;

    /**
     * Méthode principale appelée lorsqu'une action est déclenchée
     * par un élément ayant un identifiant (MenuItem par exemple).
     * Elle interprète l'identifiant pour déclencher l'action associée.
     *
     * @param event L'événement ActionEvent déclenché.
     */
    @Override
    public void handle(ActionEvent event) {
        Object source = event.getSource();

        if (source instanceof MenuItem menuItem) {
            String id = menuItem.getId();
            switch (id) {
                case "afficherPokedex" -> {
                    try {
                        new Pokedex(hostServices);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                case "afficherLeTriTopologique" -> {
                    contenuHbox.getChildren().removeIf(node ->
                            "affichageTriTopologique".equals(node.getId())
                    );
                    contenuHbox.getChildren().add(affichageTriTopologiqueInstance);
                }
                case "masquerLeTriTopologique" -> {
                    contenuHbox.getChildren().removeIf(node ->
                            "affichageTriTopologique".equals(node.getId())
                    );
                }
                case "afficherLeTriHeuristic" -> {
                    contenuHbox.getChildren().removeIf(node ->
                            "affichageHeuristiqueGlouton".equals(node.getId())
                    );
                    contenuHbox.getChildren().add(affichageHeuristiqueGloutonInstance);
                }
                case "masquerLeTriHeuristic" -> {
                    contenuHbox.getChildren().removeIf(node ->
                            "affichageHeuristiqueGlouton".equals(node.getId())
                    );
                }
                case "afficherTriKSolution" -> {
                    contenuHbox.getChildren().removeIf(node ->
                            "affichageKSolution".equals(node.getId())
                    );
                    contenuHbox.getChildren().add(affichageKSolutionsInstance);
                }
                case "masquerLeTriKSolution" -> {
                    contenuHbox.getChildren().removeIf(node ->
                            "affichageKSolution".equals(node.getId())
                    );
                }
                default -> System.out.println("Action inconnue : " + id);
            }
        }
    }

    /**
     * Crée un listener pour détecter le changement de scénario sélectionné.
     * Applique le scénario sélectionné à tous les modules d'affichage.
     *
     * @return ChangeListener à attacher au ToggleGroup des scénarios.
     */
    public ChangeListener<Toggle> monToggleListener() {
        return (obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                int selectedScenario = (int) newToggle.getUserData();
                try {
                    affichageTriTopologiqueInstance.setExtraction(extraction);
                    affichageTriTopologiqueInstance.setScenarioIndex(selectedScenario);

                    affichageHeuristiqueGloutonInstance.setExtraction(extraction);
                    affichageHeuristiqueGloutonInstance.setScenarioIndex(selectedScenario);

                    affichageKSolutionsInstance.setExtraction(extraction);
                    affichageKSolutionsInstance.setScenarioIndex(selectedScenario);

                    scenarioPanel.setScenario(extraction, selectedScenario);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    // --- Setters avec JavaDoc ---

    /**
     * Définit l'instance du modèle Extraction utilisée dans le contrôleur.
     *
     * @param extraction L'instance d'Extraction à utiliser.
     */
    public void setExtraction(Extraction extraction) {
        this.extraction = extraction;
    }

    /**
     * Définit le panneau de scénario utilisé pour l'affichage.
     *
     * @param scenarioPanel Instance de ScenarioPanel à mettre à jour.
     */
    public void setScenarioPanel(ScenarioPanel scenarioPanel) {
        this.scenarioPanel = scenarioPanel;
    }

    /**
     * Définit la zone centrale d'affichage dans laquelle les vues sont ajoutées ou retirées.
     *
     * @param contenuHbox HBox centrale de la scène.
     */
    public void setContenuHbox(HBox contenuHbox) {
        this.contenuHbox = contenuHbox;
    }

    /**
     * Définit la barre de menus principale.
     *
     * @param menuBarRoot Instance de MenuBarRoot.
     */
    public void setMenuBarRoot(MenuBarRoot menuBarRoot) {
        this.menuBarRoot = menuBarRoot;
    }

    /**
     * Définit l'accès aux services HostServices pour ouvrir des liens externes (navigateur).
     *
     * @param hostServices L'objet HostServices de l'application.
     */
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    /**
     * Définit l'instance du Pokédex utilisée.
     *
     * @param pokedex Instance de Pokedex.
     */
    public void setPokedex(Pokedex pokedex) {
        this.pokedex = pokedex;
    }

    /**
     * Définit l'instance de la vue d'affichage du tri topologique.
     *
     * @param affichageTriTopologiqueInstance Instance à afficher.
     */
    public void setAffichageTriTopologiqueInstance(AffichageTriTopologique affichageTriTopologiqueInstance) {
        this.affichageTriTopologiqueInstance = affichageTriTopologiqueInstance;
    }

    /**
     * Définit l'instance de la vue d'affichage heuristique glouton.
     *
     * @param affichageHeuristiqueGloutonInstance Instance à afficher.
     */
    public void setAffichageHeuristiqueGloutonInstance(AffichageHeuristiqueGlouton affichageHeuristiqueGloutonInstance) {
        this.affichageHeuristiqueGloutonInstance = affichageHeuristiqueGloutonInstance;
    }

    /**
     * Définit l'instance de la vue d'affichage des K meilleures solutions.
     *
     * @param affichageKSolutionsInstance Instance à afficher.
     */
    public void setAffichageKSolutionsInstance(AffichageKSolutions affichageKSolutionsInstance) {
        this.affichageKSolutionsInstance = affichageKSolutionsInstance;
    }
}
