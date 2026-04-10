import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;
import javafx.scene.Scene;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class BackgroundAnimator {

    private final Pane backgroundLayer;
    private final List<Animation> runningAnimations = new ArrayList<>();

    public BackgroundAnimator(Pane backgroundLayer) {
        this.backgroundLayer = backgroundLayer;
    }

    public void apply(Scene scene, AppTheme.BackgroundMode mode) {
        System.out.println("[DEBUG] BackgroundAnimator: Application du mode " + mode);
        stopAll();
        backgroundLayer.getChildren().clear();

        // 0. Création d'une base solide pour s'assurer que la couche est visible
        Rectangle fond = new Rectangle();
        fond.widthProperty().bind(scene.widthProperty());
        fond.heightProperty().bind(scene.heightProperty());
        backgroundLayer.getChildren().add(fond);
        fond.setCache(true);

        // 1. Traitement des nouveaux modes programmatiques
        switch (mode) {
            case GIF_MOON_STARS: fond.setFill(Color.web("#0a0a1a")); creerLuneEtoiles(scene); return;
            case VORTEX_CYBER:   fond.setFill(Color.BLACK); creerVortex(scene); return;
            case NEBULA:         fond.setFill(Color.web("#050510")); creerNebuleuse(scene); return;
            case RAIN_WINDOW:    fond.setFill(Color.web("#1a1a2e")); creerPluie(scene); return;
            case MATRIX:         fond.setFill(Color.BLACK); creerMatrix(scene); return;
            case FIRE:           fond.setFill(Color.web("#1a0500")); creerFeu(scene); return;
            case DEEP_SEA:       fond.setFill(Color.web("#001a2e")); creerAbysses(scene); return;
            case GOLD_DUST:      fond.setFill(Color.web("#0a0a05")); creerPoussiereOr(scene); return;
            default: break;
        }

        // 2. Traitement des modes Géométriques / Dégradés
        if (mode == AppTheme.BackgroundMode.AUCUN) {
            fond.setFill(Color.web("#dcd8d2"));
        } else if (mode == AppTheme.BackgroundMode.DEGRADE_ANIME) {
            fond.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#2c3e50")),
                    new Stop(1, Color.web("#4ca1af"))));
            System.out.println("[DEBUG] Création des orbes pour DEGRADE_ANIME");

            Circle c1 = creerOrbeFond(Color.web("#ffffff22"), 240, -280, -160);
            Circle c2 = creerOrbeFond(Color.web("#00e5ff22"), 200, 260, 180);

            backgroundLayer.getChildren().addAll(c1, c2);
            ajouterFlottementLent(c1, 260, 170, 11);
            ajouterFlottementLent(c2, -220, -150, 13);
        } else if (mode == AppTheme.BackgroundMode.PARTICULES) {
            fond.setFill(Color.web("#0f1720"));
            System.out.println("[DEBUG] Création des 20 particules");
            for (int i = 0; i < 20; i++) {
                double r = 18 + (i % 5) * 6;
                Circle p = creerOrbeFond(Color.web(i % 2 == 0 ? "#7bed9f22" : "#70a1ff22"), r, -300 + i * 36, -200 + i * 22);
                backgroundLayer.getChildren().add(p);
                ajouterFlottementLent(p, (i % 2 == 0 ? 80 : -90), (i % 3 == 0 ? 70 : -60), 8 + (i % 5));
            }
        }
    }

    private void creerLuneEtoiles(Scene scene) {
        Random r = new Random();
        for (int i = 0; i < 150; i++) { // Plus d'étoiles
            Circle star = new Circle(r.nextDouble() * 1.5 + 0.5, Color.WHITE);
            star.setTranslateX(r.nextDouble() * 1400 - 700);
            star.setTranslateY(r.nextDouble() * 900 - 450);
            star.setOpacity(r.nextDouble());
            star.setCache(true);
            star.setCacheHint(CacheHint.SPEED);
            backgroundLayer.getChildren().add(star);
            
            // Animation de scintillement (Fade + Scale)
            FadeTransition ft = new FadeTransition(Duration.seconds(0.5 + r.nextDouble() * 2), star);
            ft.setFromValue(star.getOpacity());
            ft.setToValue(0.1);
            ft.setAutoReverse(true);
            ft.setCycleCount(Animation.INDEFINITE);
            ft.setDelay(Duration.seconds(r.nextDouble() * 5));
            
            ScaleTransition st = new ScaleTransition(ft.getDuration(), star);
            st.setFromX(1.0); st.setToX(0.5);
            st.setFromY(1.0); st.setToY(0.5);
            st.setAutoReverse(true);
            st.setCycleCount(Animation.INDEFINITE);
            
            ParallelTransition pt = new ParallelTransition(ft, st);
            pt.play();
            runningAnimations.add(pt);
        }
        Circle moon = new Circle(70, Color.web("#f5f3ce"));
        moon.setTranslateX(400); moon.setTranslateY(-300);
        moon.setEffect(new Glow(0.8));
        moon.setOpacity(0.9);
        moon.setCache(true);
        moon.setCacheHint(CacheHint.QUALITY);
        backgroundLayer.getChildren().add(moon);
    }

    private void creerNebuleuse(Scene scene) {
        Color[] colors = {Color.web("#8e44ad44"), Color.web("#3498db44"), Color.web("#e91e6344"), Color.web("#2980b944")};
        Random r = new Random();
        for (int i = 0; i < 10; i++) { // Légère réduction du nombre pour la fluidité
            Circle blob = new Circle(180 + r.nextDouble() * 250, colors[i % colors.length]);
            blob.setEffect(new GaussianBlur(80)); // Réduction du rayon de flou (120 -> 80)
            blob.setBlendMode(BlendMode.ADD); // Fusion lumineuse des couleurs
            blob.setTranslateX(r.nextDouble() * 800 - 400);
            blob.setTranslateY(r.nextDouble() * 600 - 300);
            
            // Optimisation critique pour les effets de flou
            blob.setCache(true);
            blob.setCacheHint(CacheHint.SPEED);
            
            backgroundLayer.getChildren().add(blob);
            
            // Rotation très lente pour l'effet tourbillon de gaz
            RotateTransition rt = new RotateTransition(Duration.seconds(30 + r.nextDouble() * 30), blob);
            rt.setByAngle(r.nextBoolean() ? 360 : -360);
            rt.setCycleCount(Animation.INDEFINITE);
            rt.play();
            runningAnimations.add(rt);
            ajouterFlottementLent(blob, r.nextDouble() * 100 - 50, r.nextDouble() * 100 - 50, 15 + r.nextDouble() * 10);
        }
    }

    private void creerVortex(Scene scene) {
        Group g = new Group();
        Random r = new Random();
        for (int i = 0; i < 30; i++) { // Augmentation du nombre d'anneaux
            Circle ring = new Circle(100 + i * 25);
            ring.setFill(null);
            ring.setStroke(Color.web(i % 2 == 0 ? "#00e5ff" : "#ff00ff"));
            ring.setStrokeWidth(r.nextDouble() * 3 + 1);
            ring.setOpacity(0.2 + r.nextDouble() * 0.3);
            ring.setEffect(new Glow(0.6));
            ring.setScaleY(0.5); // Effet de perspective/disque
            g.getChildren().add(ring);
        }
        
        // On cache le groupe entier plutôt que chaque anneau
        g.setCache(true);
        g.setCacheHint(CacheHint.ROTATE);
        
        backgroundLayer.getChildren().add(g); // Centre automatiquement dans le StackPane
        RotateTransition rt = new RotateTransition(Duration.seconds(20), g);
        rt.setByAngle(360);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.play();
        runningAnimations.add(rt);
    }

    private void creerPluie(Scene scene) {
        Random r = new Random();
        for (int i = 0; i < 80; i++) { // 80 gouttes suffisent pour un bel effet fluide
            double length = 10 + r.nextDouble() * 20;
            Rectangle drop = new Rectangle(1, length, Color.web("#70a1ffaa"));
            double startX = r.nextDouble() * 1400 - 700;
            drop.setTranslateX(startX);
            drop.setTranslateY(-600);
            drop.setRotate(10); // Pluie de biais (vent)
            drop.setCache(true);
            drop.setCacheHint(CacheHint.SPEED);
            
            // Profondeur : gouttes plus petites et plus sombres au loin
            if (r.nextBoolean()) {
                drop.setOpacity(0.4);
                drop.setScaleX(0.5);
            }
            
            backgroundLayer.getChildren().add(drop);
            
            TranslateTransition tt = new TranslateTransition(Duration.millis(800 + r.nextInt(1200)), drop);
            tt.setToY(600);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.setInterpolator(Interpolator.LINEAR);
            tt.setDelay(Duration.millis(r.nextInt(2000)));
            tt.play();
            runningAnimations.add(tt);
        }
    }

    private void creerMatrix(Scene scene) {
        Random r = new Random();
        for (int i = 0; i < 60; i++) {
            Rectangle line = new Rectangle(2, 40 + r.nextInt(100), Color.web("#00ff41cc"));
            line.setTranslateX(r.nextDouble() * 1400 - 700);
            line.setTranslateY(-800);
            line.setEffect(new Glow(0.8));
            backgroundLayer.getChildren().add(line);

            TranslateTransition tt = new TranslateTransition(Duration.millis(1500 + r.nextInt(2500)), line);
            tt.setToY(800);
            tt.setInterpolator(Interpolator.LINEAR);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.setDelay(Duration.seconds(r.nextDouble() * 4));
            tt.play();
            runningAnimations.add(tt);
        }
    }

    private void creerFeu(Scene scene) {
        Random r = new Random();
        for (int i = 0; i < 50; i++) {
            Circle ember = new Circle(r.nextDouble() * 4 + 1, Color.web(r.nextBoolean() ? "#ff4d00" : "#ffae00"));
            ember.setTranslateY(500);
            ember.setTranslateX(r.nextDouble() * 1000 - 500);
            ember.setEffect(new GaussianBlur(2));
            backgroundLayer.getChildren().add(ember);

            TranslateTransition tt = new TranslateTransition(Duration.seconds(2 + r.nextDouble() * 3), ember);
            tt.setByY(-1000);
            tt.setByX(r.nextDouble() * 200 - 100);
            tt.setCycleCount(Animation.INDEFINITE);
            
            FadeTransition ft = new FadeTransition(tt.getDuration(), ember);
            ft.setFromValue(0.8); ft.setToValue(0);
            ft.setCycleCount(Animation.INDEFINITE);

            ParallelTransition pt = new ParallelTransition(ember, tt, ft);
            pt.setDelay(Duration.seconds(r.nextDouble() * 5));
            pt.play();
            runningAnimations.add(pt);
        }
    }

    private void creerAbysses(Scene scene) {
        Random r = new Random();
        for (int i = 0; i < 8; i++) {
            Circle wave = new Circle(300 + r.nextDouble() * 200, Color.web("#00d2ff11"));
            wave.setEffect(new GaussianBlur(100));
            wave.setBlendMode(BlendMode.SCREEN);
            backgroundLayer.getChildren().add(wave);
            ajouterFlottementLent(wave, r.nextDouble() * 300 - 150, r.nextDouble() * 200 - 100, 20 + r.nextDouble() * 10);
        }
        for (int i = 0; i < 25; i++) {
            Circle bubble = new Circle(r.nextDouble() * 5 + 2, Color.web("#ffffff33"));
            bubble.setTranslateY(600);
            bubble.setTranslateX(r.nextDouble() * 1200 - 600);
            backgroundLayer.getChildren().add(bubble);
            TranslateTransition tt = new TranslateTransition(Duration.seconds(5 + r.nextDouble() * 10), bubble);
            tt.setByY(-1200);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.setDelay(Duration.seconds(r.nextDouble() * 10));
            tt.play();
            runningAnimations.add(tt);
        }
    }

    private void creerPoussiereOr(Scene scene) {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            Circle gold = new Circle(r.nextDouble() * 2 + 0.5, Color.web("#d4af37"));
            gold.setTranslateX(r.nextDouble() * 1400 - 700);
            gold.setTranslateY(r.nextDouble() * 900 - 450);
            gold.setEffect(new Glow(0.5));
            backgroundLayer.getChildren().add(gold);

            FadeTransition ft = new FadeTransition(Duration.seconds(2 + r.nextDouble() * 3), gold);
            ft.setFromValue(0.2); ft.setToValue(0.9);
            ft.setAutoReverse(true); ft.setCycleCount(Animation.INDEFINITE);

            ScaleTransition st = new ScaleTransition(ft.getDuration(), gold);
            st.setFromX(0.5); st.setToX(1.2);
            st.setAutoReverse(true); st.setCycleCount(Animation.INDEFINITE);

            ParallelTransition pt = new ParallelTransition(ft, st);
            pt.setDelay(Duration.seconds(r.nextDouble() * 5));
            pt.play();
            runningAnimations.add(pt);
        }
    }

    private Circle creerOrbeFond(Color couleur, double rayon, double tx, double ty) {
        Circle c = new Circle(rayon, couleur);
        c.setTranslateX(tx);
        c.setTranslateY(ty);
        c.setEffect(new GaussianBlur(22));
        c.setMouseTransparent(true);
        c.setCache(true);
        c.setCacheHint(CacheHint.SPEED);
        return c;
    }

    private void ajouterFlottementLent(Circle c, double dx, double dy, double sec) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(sec), c);
        tt.setByX(dx);
        tt.setByY(dy);
        tt.setAutoReverse(true);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.play();
        runningAnimations.add(tt);
    }

    private void stopAll() {
        for (Animation anim : runningAnimations) {
            anim.stop();
        }
        runningAnimations.clear();
    }
}
