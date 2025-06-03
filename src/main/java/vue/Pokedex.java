package vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.application.HostServices;
import modele.Extraction;
import modele.Pokemons;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Map;

public class Pokedex {

    private static final Image IMAGE_PLACEHOLDER = chargerImagePlaceholder();

    private Extraction extraction;
    private HostServices hostServices;

    public Pokedex(HostServices hostServices) throws Exception {
        this.hostServices = hostServices;
        this.extraction = new Extraction();

        Stage stage = new Stage();
        VBox conteneur = new VBox(2);

        conteneur.setPadding(new Insets(10));
        stage.getIcons().add(new Image(Files.newInputStream(Paths.get("images/logo.png"))));
        Map<String, String> francaisVersAnglais = Pokemons.noms;

        for (Map.Entry<String, String> entree : extraction.getMembresVilles().entrySet()) {
            String nomFrancais = entree.getKey();
            String ville = entree.getValue();
            String nomAnglais = francaisVersAnglais.getOrDefault(nomFrancais, nomFrancais);

            HBox ligne = new HBox(15);
            ligne.setAlignment(Pos.CENTER_LEFT);

            String urlImage = "https://img.pokemondb.net/artwork/" + normaliserNom(nomAnglais) + ".jpg";
            Image imagePokemon;

            try {
                imagePokemon = new Image(urlImage, 100, 100, true, true, true);
                if (imagePokemon.isError()) imagePokemon = IMAGE_PLACEHOLDER;
            } catch (Exception e) {
                imagePokemon = IMAGE_PLACEHOLDER;
            }

            ImageView vueImage = new ImageView(imagePokemon);
            vueImage.setFitWidth(100);
            vueImage.setFitHeight(100);

            VBox info = new VBox(5);
            Text texteNom = new Text(nomFrancais);
            Text texteVille = new Text("Ville : " + ville);

            String urlFiche = "https://www.pokemon.com/fr/pokedex/" + normaliserNom(nomAnglais);
            Hyperlink lienFiche = new Hyperlink("Voir la fiche");
            lienFiche.setOnAction(e -> hostServices.showDocument(urlFiche));

            info.getChildren().addAll(texteNom, texteVille, lienFiche);
            ligne.getChildren().addAll(vueImage, info);
            conteneur.getChildren().add(ligne);
        }

        ScrollPane scroll = new ScrollPane(conteneur);
        scroll.setFitToWidth(true);

        Scene scene = new Scene(scroll, 600, 800);
        stage.setScene(scene);
        stage.setTitle("Pokedex - Liste des Pokémons");
        stage.show();
    }

    private static Image chargerImagePlaceholder() {
        try (InputStream flux = Files.newInputStream(Paths.get("images/placeholder.png"))) {
            return new Image(flux, 100, 100, true, true);
        } catch (IOException e) {
            System.err.println("Erreur chargement placeholder.png : " + e.getMessage());
            return new Image("https://via.placeholder.com/100");
        }
    }

    private String normaliserNom(String nom) {
        String normalise = Normalizer.normalize(nom.toLowerCase(), Normalizer.Form.NFD);
        normalise = normalise.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return normalise.replace(' ', '-').replace('_', '-');
    }
}
