package vue;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modele.Extraction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe permettant de modifier un scénario existant.
 *
 * Cette classe affiche une fenêtre JavaFX avec une ChoiceBox permettant de sélectionner un scénario.
 * Une grille présente les paires vendeur/acheteur sous forme de ComboBox éditables avec autocomplétion.
 * L'utilisateur peut modifier ces paires puis enregistrer les modifications,
 * qui sont validées avant d'être sauvegardées dans l'objet Extraction.
 */
public class ModificationScenario {
    private final Extraction extraction;
    private final ChoiceBox<Integer> scenarioChoiceBox = new ChoiceBox<>();
    private final GridPane grid = new GridPane();
    private final List<ComboBox<String>> vendeurs = new ArrayList<>();
    private final List<ComboBox<String>> acheteurs = new ArrayList<>();
    private final VBox root = new VBox(10);
    private Stage stage;

    /**
     * Constructeur qui initialise la fenêtre de modification des scénarios.
     *
     * @param extraction l'objet Extraction contenant les scénarios et les membres
     * @throws IOException en cas d'erreur lors du chargement des icônes ou ressources
     */
    public ModificationScenario(Extraction extraction) throws IOException {
        this.stage = new Stage();
        this.extraction = extraction;

        stage.getIcons().add(new Image(Files.newInputStream(Paths.get("images/modification.png"))));
        stage.setTitle("Modification de scénario");

        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        Label choixLabel = new Label("Choisir un scénario à modifier :");
        scenarioChoiceBox.setItems(FXCollections.observableArrayList(extraction.getScenarios().keySet()));
        scenarioChoiceBox.setOnAction(e -> afficherScenario(scenarioChoiceBox.getValue()));

        Button enregistrerBtn = new Button("_Enregistrer les modifications");
        enregistrerBtn.setOnAction(e -> {
            try {
                enregistrerModifications(scenarioChoiceBox.getValue());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        enregistrerBtn.setMnemonicParsing(true);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(choixLabel, scenarioChoiceBox, grid, enregistrerBtn);

        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Affiche dans la grille les paires vendeur/acheteur du scénario sélectionné.
     * Chaque paire est modifiable via des ComboBox avec autocomplétion.
     *
     * @param numScenario numéro du scénario à afficher
     */
    private void afficherScenario(int numScenario) {
        grid.getChildren().clear();
        vendeurs.clear();
        acheteurs.clear();

        Map<String, String> scenario = extraction.getScenarios().get(numScenario);
        if (scenario == null) return;

        List<String> membres = new ArrayList<>(extraction.getMembresVilles().keySet());

        grid.add(new Label("Vendeur"), 0, 0);
        grid.add(new Label("Acheteur"), 1, 0);

        int row = 1;
        for (Map.Entry<String, String> entry : scenario.entrySet()) {
            ComboBox<String> vendeurCB = new ComboBox<>(FXCollections.observableArrayList(membres));
            ComboBox<String> acheteurCB = new ComboBox<>(FXCollections.observableArrayList(membres));
            vendeurCB.setValue(entry.getKey());
            acheteurCB.setValue(entry.getValue());

            recherchePokemon(vendeurCB);
            recherchePokemon(acheteurCB);

            grid.add(vendeurCB, 0, row);
            grid.add(acheteurCB, 1, row);

            vendeurs.add(vendeurCB);
            acheteurs.add(acheteurCB);
            row++;
        }

        stage.sizeToScene();
    }

    /**
     * Active une fonctionnalité d'autocomplétion sur une ComboBox éditable.
     *
     * Lors de la saisie, la liste des éléments affichés est filtrée en fonction du texte entré.
     *
     * @param comboBox la ComboBox à rendre éditable avec autocomplétion
     */
    private void recherchePokemon(ComboBox<String> comboBox) {
        comboBox.setEditable(true);
        List<String> originalItems = new ArrayList<>(comboBox.getItems());

        comboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            if (comboBox.getValue() != null && comboBox.getValue().equals(newVal)) return;

            List<String> filtered = new ArrayList<>();
            for (String item : originalItems) {
                if (item.toLowerCase().contains(newVal.toLowerCase())) {
                    filtered.add(item);
                }
            }

            int caretPos = comboBox.getEditor().getCaretPosition();

            comboBox.setItems(FXCollections.observableArrayList(filtered));
            comboBox.getEditor().setText(newVal);
            comboBox.getEditor().positionCaret(caretPos);

            if (!comboBox.isShowing()) comboBox.show();
        });
    }

    /**
     * Enregistre les modifications du scénario sélectionné après validation.
     *
     * Vérifie que les paires vendeur/acheteur sont valides et que le scénario est cohérent.
     * Affiche des alertes en cas d'erreur ou confirme la réussite.
     *
     * @param scenarioID identifiant du scénario modifié
     * @throws IOException en cas d'erreur lors du chargement des icônes pour les alertes
     */
    private void enregistrerModifications(int scenarioID) throws IOException {
        HashMap<String, String> nouveauContenu = new HashMap<>();

        for (int i = 0; i < vendeurs.size(); i++) {
            String vendeur = vendeurs.get(i).getValue();
            String acheteur = acheteurs.get(i).getValue();

            if (vendeur != null && acheteur != null && !vendeur.equals(acheteur)) {
                nouveauContenu.put(vendeur, acheteur);
            }
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Files.newInputStream(Paths.get("images/erreur.png"))));

        if (nouveauContenu.isEmpty()) {
            alert.setTitle("Erreur");
            alert.setHeaderText("Scénario invalide");
            alert.setContentText("Aucune paire vendeur/acheteur valide.");
            alert.showAndWait();
            return;
        }

        if (!extraction.validerScenario(nouveauContenu)) {
            alert.setTitle("Erreur");
            alert.setHeaderText("Scénario non cohérent");
            alert.setContentText("Des incohérences empêchent la validation du scénario.");
            alert.showAndWait();
            return;
        }

        extraction.getScenarios().put(scenarioID, nouveauContenu);

        Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
        confirmation.setTitle("Succès");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Scénario modifié avec succès !");
        Stage confirmStage = (Stage) confirmation.getDialogPane().getScene().getWindow();
        confirmStage.getIcons().add(new Image(Files.newInputStream(Paths.get("images/succes.png"))));
        confirmation.showAndWait();

        stage.close();
    }

    /**
     * Retourne la fenêtre (Stage) utilisée par cette classe.
     *
     * @return la Stage de la fenêtre de modification
     */
    public Stage getStage() {
        return stage;
    }
}
