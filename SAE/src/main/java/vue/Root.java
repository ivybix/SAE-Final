package vue;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;

/**
 * Classe principale de l'application JavaFX.
 *
 * Initialise la fenêtre principale, configure la scène,
 * applique la feuille de style CSS et ajoute une icône.
 */
public class Root extends Application {

    /**
     * Point d'entrée JavaFX : initialise la scène principale,
     * charge l'interface utilisateur, applique les styles et l'icône de la fenêtre.
     *
     * @param stage La fenêtre principale de l'application (fournie par JavaFX).
     * @throws Exception si une erreur survient lors du chargement des ressources.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Création de la racine de l'interface
        VBox root = new HBoxRoot(getHostServices());
        Scene scene = new Scene(root, 800, 600);

        // Configuration de la fenêtre
        stage.setTitle("SAE - Samy et Romain");
        root.setSpacing(10);
        stage.setScene(scene);
        stage.setMaximized(true);  // Affichage plein écran

        // Chargement de l'icône de l'application
        stage.getIcons().add(new Image(Files.newInputStream(Paths.get("images/logo.png"))));

        // Application de la feuille de style CSS
        File css = new File("CSS/stylesheet.css");
        scene.getStylesheets().add(css.toURI().toString());

        stage.show();
    }

    /**
     * Méthode main standard qui lance l'application JavaFX.
     *
     * @param args Arguments de la ligne de commande (non utilisés ici).
     */
    public static void main(String[] args) {
        Application.launch();
    }
}
