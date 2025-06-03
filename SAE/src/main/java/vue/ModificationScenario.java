package vue;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modele.Extraction;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModificationScenario {
    private final Extraction extraction;
    private final ChoiceBox<Integer> scenarioChoiceBox = new ChoiceBox<>();
    private final GridPane grid = new GridPane();
    private final List<ComboBox<String>> vendeurs = new ArrayList<>();
    private final List<ComboBox<String>> acheteurs = new ArrayList<>();
    private final VBox root = new VBox(10);
    private Stage stage;

    public ModificationScenario(Extraction extraction) throws IOException {
        this.stage = new Stage();
        this.extraction = extraction;
            // Chargement de l'icône d'ajout
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

    private void recherchePokemon(ComboBox<String> comboBox) {
        comboBox.setEditable(true);
        List<String> originalItems = new ArrayList<>(comboBox.getItems());

        comboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            // Éviter de modifier les items si le texte est identique à la sélection
            if (comboBox.getValue() != null && comboBox.getValue().equals(newVal)) return;

            List<String> filtered = new ArrayList<>();
            for (String item : originalItems) {
                if (item.toLowerCase().contains(newVal.toLowerCase())) {
                    filtered.add(item);
                }
            }

            // Mémoriser la position du curseur pour ne pas sauter
            int caretPos = comboBox.getEditor().getCaretPosition();

            // Mettre à jour les items sans déclencher en cascade
            comboBox.setItems(FXCollections.observableArrayList(filtered));
            comboBox.getEditor().setText(newVal);
            comboBox.getEditor().positionCaret(caretPos);

            // Garde la popup ouverte
            if (!comboBox.isShowing()) comboBox.show();
        });
    }


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

        if (!extraction.validerScenario( nouveauContenu)) {
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

    public Stage getStage() {
        return stage;
    }
}
