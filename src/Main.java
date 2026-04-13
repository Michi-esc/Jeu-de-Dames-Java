import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.DepthTest;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private static final int TAILLE_CASE = 64;
    private static final int TAILLE_PLATEAU = 10;
    private static final int TAILLE_PX_PLATEAU = TAILLE_CASE * TAILLE_PLATEAU;
    private static final int TEMPS_PAR_JOUEUR = 300;
    private static final String SAVE_FILE = "savegame.dat";

    private final DropShadow ombreUI = new DropShadow(15, Color.color(0, 0, 0, 0.4));

    private Jeu jeu = new Jeu();

    private final GridPane grille = new GridPane();
    private final Pane coucheAnimation = new Pane();
    private final StackPane couchePlateau = new StackPane();
    private final StackPane backgroundLayer = new StackPane();
    private final BackgroundAnimator backgroundAnimator = new BackgroundAnimator(backgroundLayer);
    private final BorderPane gameUiRoot = new BorderPane();

    private final Label statut = new Label();
    private final Label bandeauTour = new Label();
    private final Label scoreNoir = new Label();
    private final Label scoreBlanc = new Label();
    private final Label chronoNoir = new Label();
    private final Label chronoBlanc = new Label();
    private final Label comboLabel = new Label();
    private final VBox historique = new VBox(6);

    private final Rectangle barreTempsNoir = new Rectangle(220, 8);
    private final Rectangle barreTempsBlanc = new Rectangle(220, 8);
    private final Rectangle dernierDepartOverlay = new Rectangle(TAILLE_CASE, TAILLE_CASE);
    private final Rectangle derniereArriveeOverlay = new Rectangle(TAILLE_CASE, TAILLE_CASE);
    private final Circle cibleCaptureOverlay = new Circle(TAILLE_CASE * 0.2);
    private final Line ligneTrajectoireDame = new Line();

    private final ComboBox<String> themeCombo = new ComboBox<>();
    private final ComboBox<String> accessCombo = new ComboBox<>();
    private final ComboBox<String> backgroundCombo = new ComboBox<>();

    private AppTheme.ThemeType themeActuel = AppTheme.ThemeType.CHENE_ROYAL;
    private AppTheme.AccessibilityMode accessActuel = AppTheme.AccessibilityMode.NORMAL;
    private AppTheme.BackgroundMode backgroundActuel = AppTheme.BackgroundMode.DEGRADE_ANIME;

    private Timeline timer;
    private Timeline cacheDernierCoup;
    private FadeTransition blinkCapture;
    private ScaleTransition comboAnim;

    private boolean animationEnCours = false;
    private int animationDestinationX = -1;
    private int animationDestinationY = -1;
    private VBox optionsContent; // Contenu des options (thème, accessibilité, fond)
    private StackPane pauseMenuOverlay;
    private StackPane settingsOverlay;
    private StackPane homeMenuRoot;
    private boolean isPaused = false;
    private int tempsNoir = TEMPS_PAR_JOUEUR;
    private int tempsBlanc = TEMPS_PAR_JOUEUR;

    @Override
    public void start(Stage stage) {
        System.out.println("[DEBUG] Initialisation de l'application...");
        gameUiRoot.setPadding(new Insets(12));
        gameUiRoot.setPickOnBounds(false);
        gameUiRoot.setStyle("-fx-background-color: transparent;");
        gameUiRoot.setVisible(false); // Caché au début

        homeMenuRoot = new StackPane();
        creerHomeMenu();

        StackPane root = new StackPane(backgroundLayer, gameUiRoot, homeMenuRoot);
        backgroundLayer.setMouseTransparent(true);
        backgroundLayer.setDepthTest(DepthTest.DISABLE); // Empêche le conflit avec le rendu 3D du plateau
        StackPane.setAlignment(backgroundLayer, Pos.CENTER);

        Scene scene = new Scene(root, 1360, 900);
        scene.setFill(Color.TRANSPARENT); // Indispensable pour voir à travers la scène

        configurerPlateau(gameUiRoot);
        couchePlateau.setRotate(180); // Les Noirs commencent et sont en bas
        configurerHud(gameUiRoot, stage);
        configurerControles(gameUiRoot, scene);
        chargerPartie(); // Chargement APRÈS la configuration des combos
        
        // Ajout d'un écouteur pour la touche ESC pour mettre en pause
        scene.setOnKeyPressed(event -> { if (event.getCode() == KeyCode.ESCAPE && gameUiRoot.isVisible()) togglePause(); });
        
        creerPauseMenu();
        creerSettingsPanel();
        root.getChildren().addAll(pauseMenuOverlay, settingsOverlay); // Ajout à la racine pour couvrir tout

        // Application de l'ombre aux menus pour les détacher du fond
        pauseMenuOverlay.getChildren().forEach(n -> n.setEffect(ombreUI));
        settingsOverlay.getChildren().forEach(n -> n.setEffect(ombreUI));
        
        System.out.println("[DEBUG] Tentative d'application du fond : " + backgroundActuel);
        backgroundAnimator.apply(scene, backgroundActuel);
        configurerMenuContextuel();
        
        rafraichirGrille();
        demarrerChronometre();

        stage.setTitle("Jeu de Dames 10x10");
        stage.setScene(scene);
        
        System.out.println("[DEBUG] Appel de stage.show()...");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        sauvegarderPartie();
        super.stop();
    }

    private void sauvegarderPartie() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(jeu);
            oos.writeInt(tempsNoir);
            oos.writeInt(tempsBlanc);
            
            List<String> histStrings = new ArrayList<>();
            for (javafx.scene.Node node : historique.getChildren()) {
                if (node instanceof Label) histStrings.add(((Label) node).getText());
            }
            oos.writeObject(histStrings);
            oos.writeObject(themeCombo.getValue());
            oos.writeObject(accessCombo.getValue());
            oos.writeObject(backgroundCombo.getValue());
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    private void chargerPartie() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.jeu = (Jeu) ois.readObject();
            this.tempsNoir = ois.readInt();
            this.tempsBlanc = ois.readInt();
            
            @SuppressWarnings("unchecked")
            List<String> histStrings = (List<String>) ois.readObject();
            historique.getChildren().clear();
            // On réinsère dans le bon ordre (le dernier coup en haut)
            for (int i = histStrings.size() - 1; i >= 0; i--) {
                String s = histStrings.get(i);
                Label entry = new Label(s);
                entry.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-padding: 6 8; -fx-background-radius: 8;");
                historique.getChildren().add(entry);
            }

            String t = (String) ois.readObject();
            String a = (String) ois.readObject();
            String b = (String) ois.readObject();
            
            themeCombo.setValue(t);
            accessCombo.setValue(a);
            backgroundCombo.setValue(b);
            
            themeActuel = AppTheme.mapTheme(t);
            accessActuel = AppTheme.mapAccess(a);
            backgroundActuel = AppTheme.mapBackground(b);
        } catch (Exception e) {
            System.err.println("Impossible de charger la sauvegarde.");
        }
    }

    private void creerHomeMenu() {
        VBox menuBox = new VBox(25);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setMaxSize(500, 600);
        menuBox.setPadding(new Insets(40));
        menuBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); -fx-background-radius: 25; -fx-backdrop-filter: blur(15px);");
        menuBox.setEffect(ombreUI);

        Label titre = new Label("JEU DE DAMES");
        titre.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 48));
        titre.setTextFill(Color.WHITE);
        titre.setEffect(new Glow(0.8));

        Button btnNew = creerBoutonMenu("NOUVELLE PARTIE", "#2ecc71");
        Button btnResume = creerBoutonMenu("REPRENDRE", "#3498db");
        Button btnSettings = creerBoutonMenu("PARAMÈTRES", "#95a5a6");
        Button btnQuit = creerBoutonMenu("QUITTER", "#e74c3c");

        btnNew.setOnAction(e -> {
            reinitialiserPartie();
            homeMenuRoot.setVisible(false);
            gameUiRoot.setVisible(true);
        });

        btnResume.setOnAction(e -> {
            homeMenuRoot.setVisible(false);
            gameUiRoot.setVisible(true);
        });

        btnSettings.setOnAction(e -> settingsOverlay.setVisible(true));
        btnQuit.setOnAction(e -> {
            sauvegarderPartie();
            System.exit(0);
        });

        menuBox.getChildren().addAll(titre, btnNew, btnResume, btnSettings, btnQuit);
        homeMenuRoot.getChildren().add(menuBox);
    }

    private Button creerBoutonMenu(String texte, String couleur) {
        Button b = new Button(texte);
        b.setPrefWidth(300);
        b.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        String styleBase = "-fx-background-color: " + couleur + "; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 15; -fx-cursor: hand;";
        b.setStyle(styleBase);
        b.setOnMouseEntered(e -> b.setStyle(styleBase + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        b.setOnMouseExited(e -> b.setStyle(styleBase));
        return b;
    }

    private void configurerMenuContextuel() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemNouvelle = new MenuItem("Nouvelle Partie");
        MenuItem itemQuitter = new MenuItem("Quitter");
        
        itemNouvelle.setOnAction(e -> reinitialiserPartie());
        itemQuitter.setOnAction(e -> {
            sauvegarderPartie();
            System.exit(0);
        });
        
        contextMenu.getItems().addAll(itemNouvelle, itemQuitter);
        
        couchePlateau.setOnContextMenuRequested(e -> contextMenu.show(couchePlateau, e.getScreenX(), e.getScreenY()));
    }

    private void configurerPlateau(BorderPane uiRoot) {
        grille.setAlignment(Pos.CENTER);
        grille.setHgap(0);
        grille.setVgap(0);
        grille.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        grille.setCache(true);

        coucheAnimation.setMouseTransparent(true);
        coucheAnimation.setPickOnBounds(false);
        coucheAnimation.setPrefSize(TAILLE_PX_PLATEAU, TAILLE_PX_PLATEAU);

        dernierDepartOverlay.setFill(Color.web("#f1c40f55"));
        derniereArriveeOverlay.setFill(Color.web("#2ecc7155"));
        dernierDepartOverlay.setVisible(false);
        derniereArriveeOverlay.setVisible(false);

        cibleCaptureOverlay.setFill(Color.web("#ff4757bb"));
        cibleCaptureOverlay.setVisible(false);

        couchePlateau.getChildren().addAll(grille, dernierDepartOverlay, derniereArriveeOverlay,
                ligneTrajectoireDame, cibleCaptureOverlay, coucheAnimation);

        couchePlateau.setPrefSize(TAILLE_PX_PLATEAU, TAILLE_PX_PLATEAU);
        couchePlateau.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        couchePlateau.setAlignment(Pos.CENTER);
        couchePlateau.setCache(true);

        StackPane centre = new StackPane(couchePlateau);
        centre.setAlignment(Pos.CENTER);

        couchePlateau.scaleXProperty().bind(Bindings.createDoubleBinding(() -> {
            double scaleX = centre.getWidth() / TAILLE_PX_PLATEAU;
            double scaleY = centre.getHeight() / TAILLE_PX_PLATEAU;
            return Math.max(0.35, Math.min(1.4, Math.min(scaleX, scaleY)));
        }, centre.widthProperty(), centre.heightProperty()));
        couchePlateau.scaleYProperty().bind(couchePlateau.scaleXProperty());

        uiRoot.setCenter(centre);
    }

    private void configurerHud(BorderPane uiRoot, Stage stage) {
        statut.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
        statut.setPadding(new Insets(10, 14, 10, 14));
        statut.setStyle("-fx-background-color: rgba(255,255,255,0.85); -fx-background-radius: 14;");
        statut.setEffect(ombreUI);

        bandeauTour.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        comboLabel.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 18));
        comboLabel.setTextFill(Color.web("#e67e22"));
        comboLabel.setVisible(false);

        // Style commun pour les boutons icônes
        String styleIcone = "-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 40; -fx-font-size: 20; -fx-min-width: 45; -fx-min-height: 45; -fx-cursor: hand;";

        Button boutonPleinEcran = new Button("\u26F6"); // Icône carrée/plein écran
        boutonPleinEcran.setStyle(styleIcone);
        boutonPleinEcran.setOnAction(e -> {
            stage.setFullScreen(!stage.isFullScreen());
            boutonPleinEcran.setText(stage.isFullScreen() ? "\u2750" : "\u26F6");
        });

        Button pauseButton = new Button("\u23F8"); // Icône Pause
        pauseButton.setStyle(styleIcone);
        pauseButton.setOnAction(e -> togglePause());

        HBox topBar = new HBox();
        topBar.setSpacing(15);
        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);
        // Pause à gauche, les autres à droite
        topBar.getChildren().addAll(pauseButton, espace, boutonPleinEcran);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(10, 0, 0, 0));
        
        VBox top = new VBox(8, topBar, bandeauTour, statut, comboLabel);
        top.setAlignment(Pos.CENTER);
        uiRoot.setTop(top);

        scoreNoir.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        scoreBlanc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        chronoNoir.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        chronoBlanc.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        barreTempsNoir.setArcHeight(8);
        barreTempsNoir.setArcWidth(8);
        barreTempsBlanc.setArcHeight(8);
        barreTempsBlanc.setArcWidth(8);
        barreTempsNoir.setFill(Color.web("#111111"));
        barreTempsBlanc.setFill(Color.web("#fafafa"));

        VBox gauche = new VBox(10,
                creerCarte("Noirs", scoreNoir, chronoNoir, barreTempsNoir),
                creerCarte("Blancs", scoreBlanc, chronoBlanc, barreTempsBlanc));
        gauche.setPadding(new Insets(8, 10, 8, 8));
        uiRoot.setLeft(gauche);

        Label titreHistorique = new Label("Historique coups");
        titreHistorique.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        VBox droite = new VBox(8, titreHistorique, historique);
        droite.setPrefWidth(270);
        appliquerStyleCarte(droite);
        uiRoot.setRight(droite);
    }

    private void appliquerStyleCarte(Region r) {
        r.setPadding(new Insets(10));
        r.setStyle("-fx-background-color: rgba(255,255,255,0.85); -fx-background-radius: 12;");
        r.setEffect(ombreUI);
    }

    private HBox creerCarte(String titre, Label score, Label chrono, Rectangle barre) {
        Label t = new Label(titre);
        t.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        VBox box = new VBox(6, t, score, chrono, barre);
        appliquerStyleCarte(box);
        HBox wrapper = new HBox(box);
        HBox.setHgrow(box, Priority.ALWAYS);
        return wrapper;
    }

    private void configurerControles(BorderPane uiRoot, Scene scene) {
        themeCombo.getItems().addAll(
            "Chêne Royal", "Marbre de Carrare", "Cuir & Or", "Ardoise & Craie",
            "Tron (Néon)", "Cyberpunk", "Vaisseau Spatial",
            "Aquarelle", "Pop Art", "Papier Découpé", "Bandes Dessinées",
            "Plage", "Forêt Enchantée", "Hiver Arctique"
        );
        accessCombo.getItems().addAll("Normal", "Contraste fort", "Daltonisme");
        backgroundCombo.getItems().addAll("Aucun", "Degrade anime", "Particules", "Lune & Étoiles", "Vortex Cyber", "Nébuleuse", "Pluie", "Pluie de Code", "Feu & Braises", "Abysses", "Poussière d'Or");
        themeCombo.setValue("Chêne Royal");
        accessCombo.setValue("Normal");
        backgroundCombo.setValue("Degrade anime");

        themeCombo.setOnAction(e -> {
            themeActuel = AppTheme.mapTheme(themeCombo.getValue());
            rafraichirGrille();
        });
        accessCombo.setOnAction(e -> {
            accessActuel = AppTheme.mapAccess(accessCombo.getValue());
            rafraichirGrille();
        });
        backgroundCombo.setOnAction(e -> {
            backgroundActuel = AppTheme.mapBackground(backgroundCombo.getValue());
            backgroundAnimator.apply(scene, backgroundActuel);
        });

        Button reset = new Button("Nouvelle partie");
        reset.setStyle("-fx-background-color: #2d89ef; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 16;");
        reset.setOnMouseEntered(e -> reset.setStyle("-fx-background-color: #1b6ec2; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 16;"));
        reset.setOnMouseExited(e -> reset.setStyle("-fx-background-color: #2d89ef; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 16;"));
        reset.setOnAction(e -> reinitialiserPartie());

        Slider sliderSon = new Slider(0, 100, 75);
        sliderSon.setMaxWidth(200);

        Label labelSon = new Label("🔊 Volume Sonore");
        labelSon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        
        // Force tous les labels en noir pour la lisibilité
        labelSon.setTextFill(Color.BLACK);
        Label lTheme = new Label("Thème du plateau"); 
        lTheme.setTextFill(Color.BLACK);
        lTheme.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        Label lAccess = new Label("Accessibilité"); 
        lAccess.setTextFill(Color.BLACK);
        lAccess.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        Label lFond = new Label("Ambiance Arrière-plan"); 
        lFond.setTextFill(Color.BLACK);
        lFond.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        
        optionsContent = new VBox(8, // Stocke le contenu des options dans le champ optionsContent
                lTheme,
                themeCombo,
                lAccess,
                accessCombo,
                lFond,
                backgroundCombo,
                labelSon,
                sliderSon,
                reset);
        optionsContent.setPadding(new Insets(8));
        optionsContent.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-background-radius: 12;");
        // Le panneau d'options n'est plus ajouté ici, il sera dans le menu de paramètres
    }

    private void reinitialiserPartie() {
        jeu.reinitialiser();
        couchePlateau.setRotate(180);
        tempsNoir = TEMPS_PAR_JOUEUR;
        tempsBlanc = TEMPS_PAR_JOUEUR;
        historique.getChildren().clear();
        comboLabel.setVisible(false);
        if (cacheDernierCoup != null) cacheDernierCoup.stop();
        dernierDepartOverlay.setVisible(false);
        derniereArriveeOverlay.setVisible(false);
        cacherAidesTemporaires();
        animationEnCours = false;
        animationDestinationX = -1;
        animationDestinationY = -1;
        rafraichirGrille();
    }

    private void rafraichirGrille() {
        grille.getChildren().clear();
        coucheAnimation.getChildren().clear();
        Plateau plateau = jeu.getPlateau();
        for (int y = 0; y < plateau.getTaille(); y++) {
            for (int x = 0; x < plateau.getTaille(); x++) {
                Piece piece = plateau.getPieceAt(x, y);
                if (animationEnCours && x == animationDestinationX && y == animationDestinationY) {
                    piece = null;
                }
                grille.add(creerCase(x, y, piece), x, y);
            }
        }
        mettreAJourHud();
    }

    private StackPane creerCase(int x, int y, Piece piece) {
        StackPane stack = new StackPane();

        Rectangle fond = new Rectangle(TAILLE_CASE, TAILLE_CASE);
        fond.setArcWidth(4);
        if (themeActuel == AppTheme.ThemeType.BANDE_DESSINEE) {
            fond.setStroke(Color.BLACK);
            fond.setStrokeWidth(2);
        }
        fond.setFill(couleurCaseThemee(x, y));
        stack.getChildren().add(fond);

        if (jeu.caseJouableDepuisSelection(x, y)) {
            Circle cible = new Circle(TAILLE_CASE * 0.1, couleurAideJouable());
            cible.setEffect(new Glow(0.55));
            stack.getChildren().add(cible);
        }

        if (piece != null) {
            stack.getChildren().add(creerNoeudPiece(piece, estPieceSelectionnee(x, y)));
        }

        stack.setOnMouseEntered(e -> {
            fond.setFill(couleurHover(x, y));
            fond.setEffect(new Glow(0.18));
            afficherAidesHover(x, y);
        });

        stack.setOnMouseExited(e -> {
            fond.setFill(couleurCaseThemee(x, y));
            fond.setEffect(null);
            cacherAidesTemporaires();
        });

        stack.setOnMouseClicked(event -> gererClicCase(x, y));
        return stack;
    }

    private StackPane creerNoeudPiece(Piece piece, boolean selectionnee) {
        StackPane p = new StackPane();
        
        // Lie la rotation de la pièce à l'inverse du plateau pour qu'elle reste droite
        p.rotateProperty().bind(couchePlateau.rotateProperty().multiply(-1));
        
        Circle base = new Circle(TAILLE_CASE * 0.34);
        boolean estNoir = piece.getCouleur().equals("NOIR");

        // Style spécifique selon le thème sélectionné
        switch (themeActuel) {
            case MARBRE_CARRARE:
                base.setFill(estNoir ? Color.web("#1a1a1a") : Color.WHITE);
                base.setStroke(Color.web("#7f8c8d"));
                base.setEffect(new Glow(0.35));
                break;
            case TRON_NEON:
                base.setFill(Color.BLACK);
                base.setStroke(estNoir ? Color.web("#00e5ff") : Color.web("#ff9100"));
                base.setStrokeWidth(3);
                base.setEffect(new Glow(0.8));
                break;
            case CUIR_OR:
                base.setFill(estNoir ? Color.web("#d4af37") : Color.web("#e5e4e2"));
                base.setEffect(new InnerShadow(10, Color.BLACK));
                break;
            case CYBERPUNK:
                base.setFill(estNoir ? Color.web("#f368e0") : Color.web("#ff9f43"));
                base.setStroke(Color.web("#00d2d3"));
                base.setEffect(new Glow(0.5));
                break;
            case VAISSEAU_SPATIAL:
                base.setFill(Color.web("#7f8c8d"));
                Circle voyant = new Circle(4, estNoir ? Color.RED : Color.LIME);
                voyant.setTranslateY(-10);
                p.getChildren().add(voyant);
                break;
            case POP_ART:
                base.setFill(estNoir ? Color.web("#e84393") : Color.web("#55efc4"));
                base.setStroke(Color.BLACK);
                base.setStrokeWidth(4);
                break;
            case BANDE_DESSINEE:
                base.setFill(estNoir ? Color.BLACK : Color.WHITE);
                base.setStroke(Color.BLACK);
                base.setStrokeWidth(4);
                break;
            case PAPIER_DECOUPE:
                base.setFill(estNoir ? Color.web("#d7ccc8") : Color.WHITE);
                base.setStroke(Color.web("#a0a0a0"));
                break;
            case AQUARELLE:
                base.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, estNoir ? Color.web("#a29bfe") : Color.web("#fab1a0")),
                        new Stop(1, estNoir ? Color.web("#6c5ce7") : Color.web("#ff7675"))));
                break;
            case HIVER_ARCTIQUE:
                base.setFill(Color.web("#e1f5feaa"));
                base.setStroke(Color.web("#81d4fa"));
                base.setEffect(new InnerShadow(5, Color.WHITE));
                break;
            case FORET_ENCHANTEE:
                base.setFill(estNoir ? Color.web("#1b5e20") : Color.web("#795548"));
                base.setEffect(new Glow(0.4));
                break;
            case PLAGE:
                base.setFill(estNoir ? Color.web("#95a5a6") : Color.web("#f7dc6f"));
                base.setEffect(new InnerShadow(10, Color.BLACK));
                break;
            case ARDOISE_CRAIE:
                base.setFill(estNoir ? Color.web("#2c3e50") : Color.web("#ecf0f1"));
                base.setStroke(Color.web("#bdc3c7"));
                break;
            default: // CHENE_ROYAL
                if (estNoir) {
                    base.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                            new Stop(0, Color.web("#5d5d5d")),
                            new Stop(1, Color.web("#0f0f0f"))));
                } else {
                    base.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                            new Stop(0, Color.web("#ffffff")),
                            new Stop(1, Color.web("#dfe6e9"))));
                }
                base.setStroke(Color.web("#34495e"));
                base.setStrokeWidth(2);
        }
        InnerShadow refletDefault = new InnerShadow(6, 0, -2, Color.color(1, 1, 1, 0.35));
        if (base.getEffect() == null) base.setEffect(refletDefault);
        // Suppression du DropShadow ici pour éviter le crash Unknown BoundsType en 3D

        p.getChildren().add(base);

        if (piece.estDame()) {
            Circle c1 = new Circle(TAILLE_CASE * 0.2);
            c1.setFill(Color.TRANSPARENT);
            c1.setStroke(themeActuel == AppTheme.ThemeType.TRON_NEON ? Color.web("#39ff14") : Color.GOLD);
            c1.setStrokeWidth(3);
            Circle c2 = new Circle(TAILLE_CASE * 0.1);
            c2.setFill(themeActuel == AppTheme.ThemeType.TRON_NEON ? Color.web("#39ff14") : Color.web("#f1c40f"));
            p.getChildren().addAll(c1, c2);
        }

        if (selectionnee) {
            p.setEffect(new Glow(0.65));
            ScaleTransition pulse = new ScaleTransition(Duration.millis(520), p);
            pulse.setFromX(1.0);
            pulse.setFromY(1.0);
            pulse.setToX(1.1);
            pulse.setToY(1.1);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(Animation.INDEFINITE);
            pulse.play();
        }
        return p;
    }

    private void gererClicCase(int x, int y) {
        if (animationEnCours || jeu.isPartieTerminee() || isPaused) { // Ajout de la condition isPaused
            return;
        }

        Integer sx = jeu.getSelectionX();
        Integer sy = jeu.getSelectionY();
        Piece pieceSource = null;
        boolean tentativeMouvement = false;
        boolean capture = false;
        int[] capturePos = null;

        if (sx != null && sy != null) {
            Piece pieceCliquee = jeu.getPlateau().getPieceAt(x, y);
            boolean changementSelection = pieceCliquee != null
                    && pieceCliquee.getCouleur().equals(jeu.getPlateau().getJoueurActuel());
            tentativeMouvement = !changementSelection && !(sx == x && sy == y);
            pieceSource = jeu.getPlateau().getPieceAt(sx, sy);
            if (tentativeMouvement) {
                capture = jeu.getPlateau().capture(sx, sy, x, y);
                capturePos = trouverCaseCapturee(sx, sy, x, y, pieceSource);
            }
        }

        String avant = jeu.getMessageEtat();
        String apres = jeu.cliquerCase(x, y);
        boolean coupValide = !avant.equals(apres) && (apres.startsWith("Coup valide") || apres.startsWith("Fin de la partie") || apres.startsWith("Capture reussie"));

        if (coupValide && tentativeMouvement && pieceSource != null && sx != null && sy != null) {
            lancerAnimationDeplacement(sx, sy, x, y, pieceSource, capture, capturePos);
            ajouterHistorique(capture, sx, sy, x, y);
            afficherDernierCoupTemporaire(sx, sy, x, y);
            jouerTransitionTour();
            
            // Afficher combo seulement si on est à plus de 1 capture d'affilée
            if (capture && jeu.getComboCaptures() > 1) {
                jouerEffetCombo(jeu.getComboCaptures());
            }
            if (jeu.isDernierCoupPromotion()) {
                effetPromotion(x, y);
            }
            if (jeu.isPartieTerminee()) {
                animationFinPartie();
            }
            sauvegarderPartie(); // Auto-save après un coup valide
            return;
        }

        if (apres.startsWith("Coup invalide")) {
            secouerPlateau();
        }
        rafraichirGrille();
    }

    private void lancerAnimationDeplacement(int x1, int y1, int x2, int y2, Piece pieceSource, boolean capture,
            int[] capturePos) {
        animationEnCours = true;
        animationDestinationX = x2;
        animationDestinationY = y2;
        rafraichirGrille();

        StackPane overlayPiece = creerNoeudPiece(pieceSource, false);
        overlayPiece.setMouseTransparent(true);
        overlayPiece.setLayoutX(x1 * TAILLE_CASE);
        overlayPiece.setLayoutY(y1 * TAILLE_CASE);
        overlayPiece.setTranslateX(0);
        overlayPiece.setTranslateY(0);
        coucheAnimation.getChildren().add(overlayPiece);

        TranslateTransition move = new TranslateTransition(Duration.millis(240), overlayPiece);
        move.setToX((x2 - x1) * TAILLE_CASE);
        move.setToY((y2 - y1) * TAILLE_CASE);

        FadeTransition fadeCapture = null;
        StackPane overlayCapture = null;
        if (capture && capturePos != null) {
            overlayCapture = new StackPane();
            Circle marker = new Circle(TAILLE_CASE * 0.18, Color.web("#ff6b81cc"));
            marker.setEffect(new Glow(0.35));
            overlayCapture.getChildren().add(marker);
            overlayCapture.setMouseTransparent(true);
            overlayCapture.setLayoutX(capturePos[0] * TAILLE_CASE + TAILLE_CASE * 0.5);
            overlayCapture.setLayoutY(capturePos[1] * TAILLE_CASE + TAILLE_CASE * 0.5);
            coucheAnimation.getChildren().add(overlayCapture);

            fadeCapture = new FadeTransition(Duration.millis(180), overlayCapture);
            fadeCapture.setFromValue(1);
            fadeCapture.setToValue(0.15);
        }

        SequentialTransition seq = new SequentialTransition(move);
        if (fadeCapture != null) {
            seq.getChildren().add(fadeCapture);
        }
        StackPane finalOverlayCapture = overlayCapture;
        seq.setOnFinished(e -> {
            coucheAnimation.getChildren().remove(overlayPiece);
            if (finalOverlayCapture != null) {
                coucheAnimation.getChildren().remove(finalOverlayCapture);
            }
            animationEnCours = false;
            animationDestinationX = -1;
            animationDestinationY = -1;
            rafraichirGrille();
        });
        seq.play();
    }

    private int[] trouverCaseCapturee(int x1, int y1, int x2, int y2, Piece piece) {
        if (piece == null) {
            return null;
        }
        if (!piece.estDame()) {
            int xm = (x1 + x2) / 2;
            int ym = (y1 + y2) / 2;
            return (xm >= 0 && xm < TAILLE_PLATEAU && ym >= 0 && ym < TAILLE_PLATEAU) ? new int[]{xm, ym} : null;
        }
        int pasX = x2 > x1 ? 1 : -1;
        int pasY = y2 > y1 ? 1 : -1;
        int x = x1 + pasX;
        int y = y1 + pasY;
        while (x != x2 && y != y2 && x >= 0 && x < TAILLE_PLATEAU && y >= 0 && y < TAILLE_PLATEAU) {
            Piece p = jeu.getPlateau().getPieceAt(x, y);
            if (p != null && !p.getCouleur().equals(piece.getCouleur())) {
                return new int[] { x, y };
            }
            x += pasX;
            y += pasY;
        }
        return null;
    }

    private void afficherAidesHover(int x, int y) {
        if (jeu.getSelectionX() == null || jeu.getSelectionY() == null) {
            return;
        }
        int sx = jeu.getSelectionX();
        int sy = jeu.getSelectionY();
        Piece pieceSel = jeu.getPlateau().getPieceAt(sx, sy);
        if (pieceSel == null) {
            return;
        }

        if (pieceSel.estDame() && jeu.caseJouableDepuisSelection(x, y)) {
            ligneTrajectoireDame.setVisible(true);
            ligneTrajectoireDame.setStartX((sx + 0.5) * TAILLE_CASE - TAILLE_PX_PLATEAU / 2.0);
            ligneTrajectoireDame.setStartY((sy + 0.5) * TAILLE_CASE - TAILLE_PX_PLATEAU / 2.0);
            ligneTrajectoireDame.setEndX((x + 0.5) * TAILLE_CASE - TAILLE_PX_PLATEAU / 2.0);
            ligneTrajectoireDame.setEndY((y + 0.5) * TAILLE_CASE - TAILLE_PX_PLATEAU / 2.0);
        }

        if (jeu.getPlateau().capture(sx, sy, x, y)) {
            int[] cap = trouverCaseCapturee(sx, sy, x, y, pieceSel);
            if (cap != null) {
                cibleCaptureOverlay.setVisible(true);
                cibleCaptureOverlay.setTranslateX((cap[0] - 4.5) * TAILLE_CASE);
                cibleCaptureOverlay.setTranslateY((cap[1] - 4.5) * TAILLE_CASE);
                if (blinkCapture != null) {
                    blinkCapture.stop();
                }
                blinkCapture = new FadeTransition(Duration.millis(280), cibleCaptureOverlay);
                blinkCapture.setFromValue(1);
                blinkCapture.setToValue(0.2);
                blinkCapture.setAutoReverse(true);
                blinkCapture.setCycleCount(Animation.INDEFINITE);
                blinkCapture.play();
            }
        }
    }

    private void cacherAidesTemporaires() {
        ligneTrajectoireDame.setVisible(false);
        cibleCaptureOverlay.setVisible(false);
        if (blinkCapture != null) {
            blinkCapture.stop();
        }
    }

    private void afficherDernierCoupTemporaire(int x1, int y1, int x2, int y2) {
        dernierDepartOverlay.setVisible(true);
        derniereArriveeOverlay.setVisible(true);
        dernierDepartOverlay.setTranslateX((x1 - 4.5) * TAILLE_CASE);
        dernierDepartOverlay.setTranslateY((y1 - 4.5) * TAILLE_CASE);
        derniereArriveeOverlay.setTranslateX((x2 - 4.5) * TAILLE_CASE);
        derniereArriveeOverlay.setTranslateY((y2 - 4.5) * TAILLE_CASE);

        if (cacheDernierCoup != null) {
            cacheDernierCoup.stop();
        }
        cacheDernierCoup = new Timeline(new KeyFrame(Duration.seconds(1.6), e -> {
            dernierDepartOverlay.setVisible(false);
            derniereArriveeOverlay.setVisible(false);
        }));
        cacheDernierCoup.play();
    }

    private void effetPromotion(int x, int y) {
        Circle flash = new Circle(TAILLE_CASE * 0.38, Color.web("#f1c40faa"));
        flash.setTranslateX((x - 4.5) * TAILLE_CASE);
        flash.setTranslateY((y - 4.5) * TAILLE_CASE);
        coucheAnimation.getChildren().add(flash);
        FadeTransition f = new FadeTransition(Duration.millis(420), flash);
        f.setFromValue(1);
        f.setToValue(0);
        f.setOnFinished(e -> coucheAnimation.getChildren().remove(flash));
        f.play();
    }

    private void jouerTransitionTour() {
        comboLabel.setVisible(false);
        bandeauTour.setText("Au tour des " + jeu.getPlateau().getJoueurActuel());
        FadeTransition ft = new FadeTransition(Duration.millis(350), bandeauTour);
        ft.setFromValue(0.35);
        ft.setToValue(1);
        ft.play();

        // Rotation du plateau pour que le joueur actuel soit toujours en bas
        double angleCible = jeu.getPlateau().getJoueurActuel().equals("NOIR") ? 180 : 0;
        if (couchePlateau.getRotate() != angleCible) {
            RotateTransition rt = new RotateTransition(Duration.millis(800), couchePlateau);
            rt.setToAngle(angleCible);
            rt.play();
        }
    }

    private void jouerEffetCombo(int combo) {
        comboLabel.setVisible(true);
        comboLabel.setText("x" + combo + " captures d'affilee !");
        comboLabel.setEffect(new Glow(0.8));
        
        if (comboAnim != null) comboAnim.stop();
        
        comboLabel.setScaleX(1);
        comboLabel.setScaleY(1);
        comboAnim = new ScaleTransition(Duration.millis(300), comboLabel);
        comboAnim.setToX(Math.min(2.0, 1.2 + combo * 0.15));
        comboAnim.setToY(Math.min(2.0, 1.2 + combo * 0.15));
        comboAnim.setAutoReverse(true);
        comboAnim.setCycleCount(2);
        comboAnim.play();
    }

    private void secouerPlateau() {
        TranslateTransition t1 = new TranslateTransition(Duration.millis(40), couchePlateau);
        t1.setByX(3);
        TranslateTransition t2 = new TranslateTransition(Duration.millis(40), couchePlateau);
        t2.setByX(-6);
        TranslateTransition t3 = new TranslateTransition(Duration.millis(40), couchePlateau);
        t3.setByX(3);
        new SequentialTransition(t1, t2, t3).play();
    }

    private void animationFinPartie() {
        Label fin = new Label(jeu.getMessageEtat());
        fin.setStyle("-fx-background-color: rgba(44,62,80,0.92); -fx-text-fill: white; -fx-padding: 16 22; -fx-background-radius: 12;");
        fin.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        coucheAnimation.getChildren().add(fin);
        FadeTransition f = new FadeTransition(Duration.seconds(2.2), fin);
        f.setFromValue(1);
        f.setToValue(0.2);
        f.play();
    }

    private void ajouterHistorique(boolean capture, int x1, int y1, int x2, int y2) {
        Label entry = new Label((capture ? "Capture " : "Deplacement ")
                + "(" + x1 + "," + y1 + ") -> (" + x2 + "," + y2 + ")");
        entry.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-padding: 6 8; -fx-background-radius: 8;");
        historique.getChildren().add(0, entry);
        if (historique.getChildren().size() > 14) {
            historique.getChildren().remove(historique.getChildren().size() - 1);
        }
        FadeTransition ft = new FadeTransition(Duration.millis(220), entry);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void demarrerChronometre() {
        if (timer != null) {
            timer.stop();
        }
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (jeu.isPartieTerminee() || animationEnCours) {
                return;
            }
            if (jeu.getPlateau().getJoueurActuel().equals("NOIR")) {
                tempsNoir = Math.max(0, tempsNoir - 1);
            } else {
                tempsBlanc = Math.max(0, tempsBlanc - 1);
            }
            if (tempsNoir == 0 || tempsBlanc == 0) {
                statut.setText("Temps ecoule ! " + (tempsNoir == 0 ? "BLANCS" : "NOIRS") + " gagnent.");
            }
            mettreAJourHud();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void creerPauseMenu() {
        pauseMenuOverlay = new StackPane();
        pauseMenuOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Fond sombre translucide
        pauseMenuOverlay.setVisible(false); // Caché par défaut

        VBox menuContent = new VBox(20);
        menuContent.setAlignment(Pos.CENTER);
        menuContent.setPadding(new Insets(50));
        menuContent.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 15;");
        menuContent.setMaxWidth(400); // Largeur limitée pour un look de menu

        Label title = new Label("Jeu en Pause");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        title.setTextFill(Color.web("#2c3e50"));

        Button resumeButton = new Button("Reprendre le jeu");
        resumeButton.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 20));
        resumeButton.setPrefWidth(250);
        resumeButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 12 24;");
        resumeButton.setOnAction(e -> togglePause());

        Button mainMenuButton = new Button("Menu Principal");
        mainMenuButton.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 20));
        mainMenuButton.setPrefWidth(250);
        mainMenuButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 12 24;");
        mainMenuButton.setOnAction(e -> {
            togglePause();
            gameUiRoot.setVisible(false);
            homeMenuRoot.setVisible(true);
        });

        Button settingsButton = new Button("Paramètres");
        settingsButton.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 20));
        settingsButton.setPrefWidth(250);
        settingsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 12 24;");
        settingsButton.setOnAction(e -> {
            pauseMenuOverlay.setVisible(false);
            settingsOverlay.setVisible(true);
        });

        menuContent.getChildren().addAll(title, resumeButton, mainMenuButton, settingsButton);
        pauseMenuOverlay.getChildren().add(menuContent);
    }

    private void creerSettingsPanel() {
        settingsOverlay = new StackPane();
        settingsOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Fond sombre translucide
        settingsOverlay.setVisible(false); // Caché par défaut

        VBox settingsContentBox = new VBox(15);
        settingsContentBox.setAlignment(Pos.TOP_CENTER);
        settingsContentBox.setPadding(new Insets(30));
        settingsContentBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 15;");
        settingsContentBox.setMaxWidth(450); // Légèrement plus large pour les options

        Label title = new Label("Paramètres du jeu");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        title.setTextFill(Color.web("#2c3e50"));

        // Encapsule optionsContent dans un ScrollPane pour le panneau de paramètres
        ScrollPane scrollableOptions = new ScrollPane(optionsContent);
        scrollableOptions.setFitToWidth(true);
        scrollableOptions.setPrefHeight(300); // Hauteur fixe pour le scroll
        scrollableOptions.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollableOptions.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollableOptions.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");

        Button backButton = new Button("Retour");
        backButton.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 18));
        backButton.setPrefWidth(150);
        backButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10 20;");
        backButton.setOnAction(e -> {
            settingsOverlay.setVisible(false);
            pauseMenuOverlay.setVisible(true);
        });

        settingsContentBox.getChildren().addAll(title, scrollableOptions, backButton);
        settingsOverlay.getChildren().add(settingsContentBox);
    }

    private void togglePause() {
        isPaused = !isPaused;
        pauseMenuOverlay.setVisible(isPaused);
        
        // Désactiver/activer l'interaction avec le plateau de jeu
        couchePlateau.setMouseTransparent(isPaused);
        grille.setMouseTransparent(isPaused); // S'assurer que les clics sur la grille sont ignorés

        if (isPaused) {
            timer.stop(); // Arrêter le chronomètre du jeu
        } else {
            timer.play(); // Reprendre le chronomètre du jeu
            settingsOverlay.setVisible(false); // S'assurer que les paramètres sont cachés si on reprend de là
        }
    }

    private void mettreAJourHud() {
        statut.setText(jeu.getMessageEtat());
        bandeauTour.setText("Au tour des " + jeu.getPlateau().getJoueurActuel());

        int nbNoirs = jeu.compterPions("NOIR");
        int nbBlancs = jeu.compterPions("BLANC");
        int damesNoires = jeu.compterDames("NOIR");
        int damesBlanches = jeu.compterDames("BLANC");
        scoreNoir.setText("Pions: " + nbNoirs + " | Dames: " + damesNoires);
        scoreBlanc.setText("Pions: " + nbBlancs + " | Dames: " + damesBlanches);

        chronoNoir.setText("Temps: " + formatTemps(tempsNoir));
        chronoBlanc.setText("Temps: " + formatTemps(tempsBlanc));
        barreTempsNoir.setWidth(220.0 * tempsNoir / TEMPS_PAR_JOUEUR);
        barreTempsBlanc.setWidth(220.0 * tempsBlanc / TEMPS_PAR_JOUEUR);

        chronoNoir.setTextFill(tempsNoir <= 30 ? (tempsNoir % 2 == 0 ? Color.RED : Color.DARKRED) : Color.BLACK);
        chronoBlanc.setTextFill(tempsBlanc <= 30 ? (tempsBlanc % 2 == 0 ? Color.RED : Color.DARKRED) : Color.BLACK);
    }

    private String formatTemps(int totalSecondes) {
        int min = totalSecondes / 60;
        int sec = totalSecondes % 60;
        return String.format("%02d:%02d", min, sec);
    }

    private boolean estPieceSelectionnee(int x, int y) {
        return jeu.getSelectionX() != null
                && jeu.getSelectionY() != null
                && jeu.getSelectionX() == x
                && jeu.getSelectionY() == y;
    }

    private Paint couleurCaseThemee(int x, int y) {
        return AppTheme.couleurCaseThemee(x, y, themeActuel, accessActuel);
    }

    private Paint couleurHover(int x, int y) {
        return AppTheme.couleurHover(x, y, themeActuel, accessActuel);
    }

    private Color couleurAideJouable() {
        return AppTheme.couleurAideJouable(themeActuel, accessActuel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
