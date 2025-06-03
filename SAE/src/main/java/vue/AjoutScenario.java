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

/**
 * Cette classe représente une fenêtre JavaFX permettant à l'utilisateur
 * de créer un nouveau scénario de livraison entre membres (vendeur → acheteur).
 *
 * Fonctionnalités principales :
 * - Interface graphique avec champs dynamiques pour ajouter jusqu'à 15 paires vendeur/acheteur.
 * - Recherche dynamique dans les ComboBox pour faciliter la sélection des membres.
 * - Validation de la cohérence du scénario via le modèle {@link Extraction}.
 * - Ajout du scénario dans les données centralisées si la validation réussit.
 *
 * Utilisation :
 * Le scénario peut être récupéré après validation via {@link #getContenuScenario()}.
 *
 * Dépendances :
 * - JavaFX (Scene, ComboBox, GridPane, Alert, etc.)
 * - Classe métier {@link modele.Extraction} pour les données et la validation.
 *
 * Constructeur :
 * @param extraction instance du modèle {@link Extraction} contenant les membres et la logique métier
 * @throws FileNotFoundException si les ressources nécessaires (images, données) sont introuvables
 *
 * Méthodes publiques :
 * - {@link #getContenuScenario()} : retourne le scénario validé (vendeur → acheteur)
 * - {@link #getStage()} : retourne le stage JavaFX de la fenêtre (utile pour contrôle externe)
 */
public class AjoutScenario {
    private final Extraction extraction;
    private final List<ComboBox<String>> vendeurs = new ArrayList<>();
    private final List<ComboBox<String>> acheteurs = new ArrayList<>();
    private HashMap<String, String> contenuDuScenario = null;
    private final GridPane grid = new GridPane();
    private final int MAX_LIGNES = 15;
    Stage stage;

    /**
     * Construit une nouvelle fenêtre d'ajout de scénario.
     * Initialise l'interface graphique et charge les membres disponibles.
     *
     * @param extraction Instance du modèle {@link Extraction} contenant les membres et les scénarios existants.
     * @throws FileNotFoundException Si les ressources comme les images ne sont pas trouvées.
     */
    public AjoutScenario(Extraction extraction) throws FileNotFoundException {
        this.stage = new Stage();
        this.extraction = extraction;

        try {

        // Chargement de l'icône d'ajout
        stage.getIcons().add(new Image(Files.newInputStream(Paths.get("images/ajout.png"))));
        stage.setTitle("Ajout de scénario");


        List<String> membres = new ArrayList<>(this.extraction.getMembresVilles().keySet());

        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        Label vendeurLabel = new Label("Vendeur");
        Label acheteurLabel = new Label("Acheteur");
        grid.add(vendeurLabel, 0, 0);
        grid.add(acheteurLabel, 1, 0);

        ajouterLigne(membres);

        Button ajouterLigneBtn = new Button("_Ajouter une ligne");
        ajouterLigneBtn.setOnAction(e -> {
            if (vendeurs.size() < MAX_LIGNES) {
                ajouterLigne(membres);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Nombre maximum de lignes d'acheteur et vendeur atteint (15).");
                alert.showAndWait();
            }
        });

        Button enregistrerBtn = new Button("_Enregistrer le scénario");
        enregistrerBtn.setOnAction(e -> {
            try {
                validerScenario(stage);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        HBox boutons = new HBox(10, ajouterLigneBtn, enregistrerBtn);
        boutons.setPadding(new Insets(10));

        VBox root = new VBox(10, grid, boutons);
        root.setPadding(new Insets(10));

        ajouterLigneBtn.setMnemonicParsing(true);
        enregistrerBtn.setMnemonicParsing(true);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des membres.");
            alert.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    /**
     * Ajoute dynamiquement une ligne de sélection vendeur/acheteur à l'interface.
     *
     * @param membres La liste des noms de membres disponibles.
     */
    private void ajouterLigne(List<String> membres) {
        int row = vendeurs.size() + 1;

        ComboBox<String> vendeurCB = new ComboBox<>(FXCollections.observableArrayList(membres));
        ComboBox<String> acheteurCB = new ComboBox<>(FXCollections.observableArrayList(membres));

        recherchePokemon(vendeurCB);
        recherchePokemon(acheteurCB);


        grid.add(vendeurCB, 0, row);
        grid.add(acheteurCB, 1, row);

        vendeurs.add(vendeurCB);
        acheteurs.add(acheteurCB);
        stage.setHeight(stage.getHeight() + 35);
    }

    /**
     * Valide le scénario saisi :
     * - Vérifie que chaque vendeur est différent de son acheteur.
     * - Contrôle la cohérence globale via {@link Extraction#validerScenario(HashMap)}.
     * - Ajoute le scénario dans les structures du modèle si valide.
     * - Affiche des alertes en cas d'erreur ou de succès.
     *
     * @param stage La fenêtre actuelle pour la fermer après validation.
     * @throws IOException Si une ressource (ex: image d'alerte) est introuvable.
     */
    private void validerScenario(Stage stage) throws IOException {
        contenuDuScenario = new HashMap<>();
        List<String> vendeursChoisis = new ArrayList<>();
        List<String> acheteursChoisis = new ArrayList<>();

        for (int i = 0; i < vendeurs.size(); i++) {
            String vendeur = vendeurs.get(i).getValue();
            String acheteur = acheteurs.get(i).getValue();

            if (vendeur != null && acheteur != null && !vendeur.equals(acheteur)) {
                vendeursChoisis.add(vendeur);
                acheteursChoisis.add(acheteur);
                contenuDuScenario.put(vendeur, acheteur);
            }
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Files.newInputStream(Paths.get("images/erreur.png"))));
        // on appelle le modèle pour vérifier cohérence
        boolean scenarioValide = extraction.validerScenario(contenuDuScenario);
        if (!scenarioValide) {

            alert.setTitle("Validation");
            alert.setHeaderText("Scénario non cohérent");
            alert.setContentText("Le scénario comporte des contraintes impossibles à satisfaire (incohérences).");
            alert.showAndWait();
            return;
        }


        if (vendeursChoisis.isEmpty()) {
            alert.setTitle("Validation");
            alert.setHeaderText("Scénario invalide");
            alert.setContentText("Veuillez renseigner au moins une paire vendeur/acheteur valide.");
            alert.showAndWait();
            return;
        }

        extraction.ajouterScenario(extraction.getScenarios().keySet().size(), contenuDuScenario);
        System.out.println("contenu du premier scenario ajouté qui sera le 9: "  + extraction.getScenarios().get(9));

        Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
        confirmation.setTitle("Succès");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Scénario ajouté avec succès !");
        Stage confirmStage = (Stage) confirmation.getDialogPane().getScene().getWindow();
        confirmStage.getIcons().add(new Image(Files.newInputStream(Paths.get("images/succes.png"))));
        confirmation.showAndWait();

        stage.close();
    }

    /**
     * Ajoute une fonctionnalité de recherche dynamique dans une ComboBox.
     * Met à jour la liste des éléments affichés en fonction du texte saisi.
     *
     * @param comboBox La ComboBox à rendre interactive.
     */
    private void recherchePokemon(ComboBox<String> comboBox) {
        comboBox.setEditable(true);
        List<String> originalItems = new ArrayList<>(comboBox.getItems());

        comboBox.getEditor().textProperty().addListener((obs, val1, val2) -> {
            if (val2 == null || val2.isEmpty()) {
                comboBox.setItems(FXCollections.observableArrayList(originalItems));
                return;
            }

            List<String> filtered = new ArrayList<>();
            for (String item : originalItems) {
                if (item.toLowerCase().contains(val2.toLowerCase())) {
                    filtered.add(item);
                }
            }
            comboBox.setItems(FXCollections.observableArrayList(filtered));
        });
    }

    /**
     * Retourne le contenu du scénario créé (paires vendeur→acheteur).
     * @return HashMap<String,String> ou null si aucun scénario n'a été validé.
     */
    public HashMap<String, String> getContenuScenario() {
        return contenuDuScenario;
    }

    /**
     * Retourne la fenêtre JavaFX associée à l'ajout du scénario.
     *
     * @return Le Stage JavaFX de cette interface.
     */
    public Stage getStage() {
        return stage;
    }
}
