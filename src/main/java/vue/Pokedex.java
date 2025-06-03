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

/**
 * Classe Pokedex qui affiche une fenêtre JavaFX listant les Pokémons avec leur ville associée.
 *
 * Cette classe construit une interface graphique contenant une liste déroulante
 * de Pokémons extraits via la classe {@link Extraction}, affichant pour chaque Pokémon :
 *
 *   son nom en français,
 *   la ville associée,
 *   une image provenant d'une URL basée sur son nom anglais,
 *   un lien hypertexte vers sa fiche officielle sur pokemon.com.
 *
 *
 *
 * Les images sont chargées depuis Internet et un placeholder local est utilisé en cas d'erreur.
 * Le nom des Pokémons est normalisé pour générer les URLs correctes.
 *
 *
 * La fenêtre JavaFX est affichée dès la création d'une instance.
 *
 *
 * @see Extraction
 * @see Pokemons
 * @see javafx.application.HostServices
 */
public class Pokedex {

    /**
     * Image utilisée en remplacement lorsqu'une image Pokémon ne peut être chargée.
     */
    private static final Image IMAGE_PLACEHOLDER = chargerImagePlaceholder();

    /**
     * Instance d'Extraction permettant d'accéder aux données membres/villes.
     */
    private Extraction extraction;

    /**
     * Services d'hôte JavaFX permettant d'ouvrir des liens dans un navigateur.
     */
    private HostServices hostServices;

    /**
     * Constructeur qui initialise les données et crée la fenêtre JavaFX affichant la liste des Pokémons.
     *
     * Pour chaque Pokémon extrait, une ligne contenant l'image, le nom, la ville et un lien vers la fiche officielle est créée.
     *
     *
     * @param hostServices service JavaFX pour ouvrir des liens externes dans un navigateur
     * @throws Exception si une erreur survient lors de l'initialisation ou du chargement des ressources
     */
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

    /**
     * Charge une image placeholder locale utilisée quand une image Pokémon ne peut pas être chargée depuis Internet.
     *
     * Tente de charger l'image "images/placeholder.png" depuis le disque,
     * et en cas d'erreur, retourne une image générique depuis une URL en ligne.
     *
     *
     * @return l'image placeholder chargée
     */
    private static Image chargerImagePlaceholder() {
        try (InputStream flux = Files.newInputStream(Paths.get("images/placeholder.png"))) {
            return new Image(flux, 100, 100, true, true);
        } catch (IOException e) {
            System.err.println("Erreur chargement placeholder.png : " + e.getMessage());
            return new Image("https://via.placeholder.com/100");
        }
    }

    /**
     * Normalise un nom de Pokémon pour générer une URL valide.
     *
     * La normalisation consiste à :
     * 
     *   mettre en minuscules,
     *   supprimer les accents et signes diacritiques,
     *   remplacer les espaces et underscores par des tirets.
     * 
     * 
     *
     * @param nom le nom à normaliser
     * @return le nom normalisé compatible avec les URL des images et fiches Pokémon
     */
    private String normaliserNom(String nom) {
        String normalise = Normalizer.normalize(nom.toLowerCase(), Normalizer.Form.NFD);
        normalise = normalise.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return normalise.replace(' ', '-').replace('_', '-');
    }
}
