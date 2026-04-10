import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

public final class AppTheme {

    public enum ThemeType { 
        CHENE_ROYAL, MARBRE_CARRARE, CUIR_OR, ARDOISE_CRAIE,
        TRON_NEON, CYBERPUNK, VAISSEAU_SPATIAL,
        AQUARELLE, POP_ART, PAPIER_DECOUPE, BANDE_DESSINEE,
        PLAGE, FORET_ENCHANTEE, HIVER_ARCTIQUE 
    }
    public enum AccessibilityMode { NORMAL, CONTRASTE_FORT, DALTONISME }
    public enum BackgroundMode { AUCUN, DEGRADE_ANIME, PARTICULES, GIF_MOON_STARS, VORTEX_CYBER, NEBULA, RAIN_WINDOW, MATRIX, FIRE, DEEP_SEA, GOLD_DUST }

    private AppTheme() {
    }

    public static ThemeType mapTheme(String value) {
        switch (value) {
            case "Marbre de Carrare": return ThemeType.MARBRE_CARRARE;
            case "Cuir & Or": return ThemeType.CUIR_OR;
            case "Ardoise & Craie": return ThemeType.ARDOISE_CRAIE;
            case "Tron (Néon)": return ThemeType.TRON_NEON;
            case "Cyberpunk": return ThemeType.CYBERPUNK;
            case "Vaisseau Spatial": return ThemeType.VAISSEAU_SPATIAL;
            case "Aquarelle": return ThemeType.AQUARELLE;
            case "Pop Art": return ThemeType.POP_ART;
            case "Papier Découpé": return ThemeType.PAPIER_DECOUPE;
            case "Bandes Dessinées": return ThemeType.BANDE_DESSINEE;
            case "Plage": return ThemeType.PLAGE;
            case "Forêt Enchantée": return ThemeType.FORET_ENCHANTEE;
            case "Hiver Arctique": return ThemeType.HIVER_ARCTIQUE;
            default: return ThemeType.CHENE_ROYAL;
        }
    }

    public static AccessibilityMode mapAccess(String value) {
        if ("Contraste fort".equals(value)) {
            return AccessibilityMode.CONTRASTE_FORT;
        }
        if ("Daltonisme".equals(value)) {
            return AccessibilityMode.DALTONISME;
        }
        return AccessibilityMode.NORMAL;
    }

    public static BackgroundMode mapBackground(String value) {
        if ("Aucun".equals(value)) {
            return BackgroundMode.AUCUN;
        }
        if ("Particules".equals(value)) {
            return BackgroundMode.PARTICULES;
        }
        if ("Lune & Étoiles".equals(value)) {
            return BackgroundMode.GIF_MOON_STARS;
        }
        if ("Vortex Cyber".equals(value)) {
            return BackgroundMode.VORTEX_CYBER;
        }
        if ("Nébuleuse".equals(value)) {
            return BackgroundMode.NEBULA;
        }
        if ("Pluie".equals(value)) {
            return BackgroundMode.RAIN_WINDOW;
        }
        if ("Pluie de Code".equals(value)) {
            return BackgroundMode.MATRIX;
        }
        if ("Feu & Braises".equals(value)) {
            return BackgroundMode.FIRE;
        }
        if ("Abysses".equals(value)) {
            return BackgroundMode.DEEP_SEA;
        }
        if ("Poussière d'Or".equals(value)) {
            return BackgroundMode.GOLD_DUST;
        }
        return BackgroundMode.DEGRADE_ANIME;
    }

    public static Paint couleurCaseThemee(int x, int y, ThemeType theme, AccessibilityMode access) {
        boolean claire = (x + y) % 2 == 0;
        if (access == AccessibilityMode.CONTRASTE_FORT) {
            return claire ? Color.web("#ffffff") : Color.web("#1a1a1a");
        }
        if (access == AccessibilityMode.DALTONISME) {
            return claire ? Color.web("#f4f1de") : Color.web("#3d405b");
        }
        switch (theme) {
            case MARBRE_CARRARE: 
                return claire ? new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.WHITE), new Stop(1, Color.web("#f5f5f5")))
                              : new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#2c3e50")), new Stop(0.5, Color.web("#1a1a1a")), new Stop(1, Color.web("#2c3e50")));
            case CUIR_OR:
                return claire ? new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#f5e6be")), new Stop(1, Color.web("#d4af37")))
                              : new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#5d4037")), new Stop(1, Color.web("#3e2723")));
            case ARDOISE_CRAIE:
                return claire ? Color.web("#ffffff") 
                              : new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#34495e")), new Stop(1, Color.web("#2c3e50")));
            case TRON_NEON: return claire ? Color.web("#121212") : Color.web("#000000");
            case CYBERPUNK: return claire ? Color.web("#2d004d") : Color.web("#1a002e");
            case VAISSEAU_SPATIAL:
                return claire ? new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#bdc3c7")), new Stop(1, Color.web("#95a5a6")))
                              : new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#34495e")), new Stop(1, Color.web("#2c3e50")));
            case AQUARELLE:
                return claire ? new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#fce4ec")), new Stop(1, Color.web("#f8bbd0")))
                              : new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#e1bee7")), new Stop(1, Color.web("#d1c4e9")));
            case POP_ART: return claire ? Color.web("#f06292") : Color.web("#4caf50");
            case PAPIER_DECOUPE: return claire ? Color.web("#ffffff") : Color.web("#d7ccc8");
            case BANDE_DESSINEE: return claire ? Color.web("#ffffff") : Color.web("#eeeeee");
            case PLAGE: return claire ? Color.web("#4db6ac") : Color.web("#fff176");
            case FORET_ENCHANTEE: return claire ? Color.web("#81c784") : Color.web("#5d4037");
            case HIVER_ARCTIQUE: return claire ? Color.web("#ffffff") : Color.web("#bbdefb");
            default: // Chêne Royal (Texture bois procedurale)
                if (claire) {
                    return new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#efe7d4")),
                        new Stop(0.5, Color.web("#e5dcc5")),
                        new Stop(1, Color.web("#efe7d4")));
                } else {
                    // On simule les nervures du bois avec un gradient multi-stops
                    return new LinearGradient(0, 0, 0.2, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#8b5a2b")),
                        new Stop(0.2, Color.web("#7a4f23")),
                        new Stop(0.4, Color.web("#8b5a2b")),
                        new Stop(0.6, Color.web("#6d4c41")),
                        new Stop(0.8, Color.web("#8b5a2b")),
                        new Stop(1, Color.web("#5d4037")));
                }
        }
    }

    public static Paint couleurHover(int x, int y, ThemeType theme, AccessibilityMode access) {
        boolean claire = (x + y) % 2 == 0;
        if (access == AccessibilityMode.CONTRASTE_FORT) {
            return claire ? Color.web("#f0f0f0") : Color.web("#2a2a2a");
        }
        if (access == AccessibilityMode.DALTONISME) {
            return claire ? Color.web("#faf7e5") : Color.web("#4b5075");
        }
        Paint base = couleurCaseThemee(x, y, theme, access);
        if (base instanceof Color) {
            return ((Color) base).deriveColor(0, 1, 1.1, 1);
        }
        return base;
    }

    public static Color couleurAideJouable(ThemeType theme, AccessibilityMode access) {
        if (access == AccessibilityMode.CONTRASTE_FORT) {
            return Color.web("#00ff00");
        }
        if (access == AccessibilityMode.DALTONISME) {
            return Color.web("#4cc9f0");
        }
        return theme == ThemeType.TRON_NEON ? Color.web("#39ff14cc") : Color.web("#2ecc71cc");
    }
}
