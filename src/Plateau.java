<<<<<<< HEAD
import java.io.Serializable;

public class Plateau implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int TAILLE = 10;
    private Piece[][] cases = new Piece[TAILLE][TAILLE];
    private String joueurActuel = "NOIR";// ou "BLANC"

    public Plateau() {
        initialiser();
    }

    public void initialiser() {
        // Nettoyage du plateau
        for (int y = 0; y < TAILLE; y++) {
            for (int x = 0; x < TAILLE; x++) {
                cases[y][x] = null;
            }
        }

        // Pions NOIRS sur les 4 premières lignes (cases foncées)
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < TAILLE; x++) {
                if ((x + y) % 2 == 1) {
                    cases[y][x] = new Piece(x, y, "NOIR");
                }
            }
        }

        // Pions BLANCS sur les 4 dernières lignes (cases foncées)
        for (int y = TAILLE - 4; y < TAILLE; y++) {
            for (int x = 0; x < TAILLE; x++) {
                if ((x + y) % 2 == 1) {
                    cases[y][x] = new Piece(x, y, "BLANC");
                }
            }
        }
    }

    public void afficher() {
        System.out.println("  0 1 2 3 4 5 6 7 8 9");
        for (int y = 0; y < TAILLE; y++) {
            System.out.print(y + " ");
            for (int x = 0; x < TAILLE; x++) {
                if (cases[y][x] == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(cases[y][x].afficher() + " ");
                }
            }
            System.out.println();
        }
    }

    public Piece getPieceAt(int x, int y) {
        if (!coordonneesValides(x, y)) {
            return null;
        }
        return cases[y][x];
    }

    public int getTaille() {
        return TAILLE;
    }

    public boolean estDeplacementSimpleValide(int x1, int y1, int x2, int y2) {

        //  Vérif coordonnées valides
        if (!coordonneesValides(x1, y1) || !coordonneesValides(x2, y2)) {
            return false;
        }

        // Vérif pièce de départ existante
        Piece p = cases[y1][x1];
        if (p == null) {
            return false;
        }

        // Vérif couleur joueur
        if (!p.getCouleur().equals(joueurActuel)) {
            return false;
        }

        // Vérif case d'arrivée vide
        if (cases[y2][x2] != null) {
            return false;
        }

        // Règle : Si une capture est possible, le déplacement simple est interdit
        if (aDesCapturesPossibles(joueurActuel)) {
            return false;
        }

        if (p.estDame()) {
            return deplacementSimpleDameValide(x1, y1, x2, y2);
        }

        // Vérif déplacement diagonal d'une case
        if (Math.abs(x2 - x1) != 1 || Math.abs(y2 - y1) != 1) {
            return false;
        }

        // Vérif sens de déplacement
        if (p.getCouleur().equals("NOIR") && y2 != y1 + 1) {
            return false;
        }

        if (p.getCouleur().equals("BLANC") && y2 != y1 - 1) {
            return false;
        }

        return true;
    }

    public boolean deplacer(int x1, int y1, int x2, int y2) {
        // Vérif capture possible
        if (capture(x1, y1, x2, y2)) {
            Piece p = cases[y1][x1];
            supprimerPieceCapturee(x1, y1, x2, y2, p);

            // Nouveau déplacement du pion
            cases[y1][x1] = null;
            cases[y2][x2] = p;
            p.setX(x2);
            p.setY(y2);
            promouvoirSiNecessaire(p);

            return true;
        }

        // Déplacement simple
        if (estDeplacementSimpleValide(x1, y1, x2, y2)) {
            Piece p = cases[y1][x1];
            cases[y1][x1] = null;
            cases[y2][x2] = p;
            p.setX(x2);
            p.setY(y2);
            promouvoirSiNecessaire(p);
            return true;
        }


        return false;
    }

    public String getJoueurActuel() {
        return joueurActuel;
    }

    public void changerJoueur() {
        if (joueurActuel.equals("NOIR")) {
            joueurActuel = "BLANC";
        } else {
            joueurActuel = "NOIR";
        }
    }

    public boolean capture(int x1, int y1, int x2, int y2) {

        // Vérif coordonnées valides
        if (!coordonneesValides(x1, y1) || !coordonneesValides(x2, y2)) {
            return false;
        }

        Piece p = cases[y1][x1];
        if (p == null) {
            return false;
        }

        // Vérif couleurjoueur
        if (!p.getCouleur().equals(joueurActuel)) {
            return false;
        }

        if (p.estDame()) {
            return captureDameValide(x1, y1, x2, y2, p);
        }

        // Vérif déplacement de 2 cases en diagonale possible
        if (Math.abs(x2 - x1) != 2 || Math.abs(y2 - y1) != 2) {
            return false;
        }

        // Vérif case d'arrivée vide
        if (cases[y2][x2] != null) {
            return false;
        }

        // Case du milieu
        int xMilieu = (x1 + x2) / 2;
        int yMilieu = (y1 + y2) / 2;

        Piece pionMilieu = cases[yMilieu][xMilieu];

        // Vérif pion adverse au milieu
        if (pionMilieu == null) {
            return false;
        }

        if (pionMilieu.getCouleur().equals(p.getCouleur())) {
            return false;
        }

        return true;
    }

    private boolean coordonneesValides(int x, int y) {
        return x >= 0 && x < TAILLE && y >= 0 && y < TAILLE;
    }

    private boolean deplacementSimpleDameValide(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        if (Math.abs(dx) != Math.abs(dy) || dx == 0) {
            return false;
        }

        int pasX = dx > 0 ? 1 : -1;
        int pasY = dy > 0 ? 1 : -1;
        int x = x1 + pasX;
        int y = y1 + pasY;
        while (coordonneesValides(x, y) && (x != x2 || y != y2)) {
            if (cases[y][x] != null) {
                return false;
            }
            x += pasX;
            y += pasY;
        }
        return true;
    }

    private boolean captureDameValide(int x1, int y1, int x2, int y2, Piece dame) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        if (Math.abs(dx) != Math.abs(dy) || dx == 0) {
            return false;
        }

        if (cases[y2][x2] != null) {
            return false;
        }

        int pasX = dx > 0 ? 1 : -1;
        int pasY = dy > 0 ? 1 : -1;
        int x = x1 + pasX;
        int y = y1 + pasY;
        int nbAdverses = 0;

        while (coordonneesValides(x, y) && (x != x2 || y != y2)) {
            Piece intermediaire = cases[y][x];
            if (intermediaire != null) {
                if (intermediaire.getCouleur().equals(dame.getCouleur())) {
                    return false;
                }
                nbAdverses++;
                if (nbAdverses > 1) {
                    return false;
                }
            }
            x += pasX;
            y += pasY;
        }

        return nbAdverses == 1;
    }

    private void supprimerPieceCapturee(int x1, int y1, int x2, int y2, Piece piece) {
        if (!piece.estDame()) {
            int xMilieu = (x1 + x2) / 2;
            int yMilieu = (y1 + y2) / 2;
            cases[yMilieu][xMilieu] = null;
            return;
        }

        int pasX = x2 > x1 ? 1 : -1;
        int pasY = y2 > y1 ? 1 : -1;
        int x = x1 + pasX;
        int y = y1 + pasY;

        while (x != x2 && y != y2 && coordonneesValides(x, y)) {
            Piece intermediaire = cases[y][x];
            if (intermediaire != null && !intermediaire.getCouleur().equals(piece.getCouleur())) {
                cases[y][x] = null;
                return;
            }
            x += pasX;
            y += pasY;
        }
    }

    private void promouvoirSiNecessaire(Piece piece) {
        if (piece.estDame()) {
            return;
        }

        if ((piece.getCouleur().equals("NOIR") && piece.getY() == TAILLE - 1) ||
            (piece.getCouleur().equals("BLANC") && piece.getY() == 0)) {
            piece.setDame(true);
        }
    }

    public int compterPions(String couleur) {
        int count = 0;
        for (int y = 0; y < TAILLE; y++) {
            for (int x = 0; x < TAILLE; x++) {
                if (cases[y][x] != null && cases[y][x].getCouleur().equals(couleur)) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean peutBouger(Piece p) {
        for (int dy = -2; dy <= 2; dy++) {
            for (int dx = -2; dx <= 2; dx++) {
                if (dx == 0 && dy == 0) continue;
                if (capture(p.getX(), p.getY(), p.getX() + dx, p.getY() + dy)) return true;
                if (estDeplacementSimpleValide(p.getX(), p.getY(), p.getX() + dx, p.getY() + dy)) return true;
            }
        }
        // Pour les dames, on pourrait vérifier plus loin, mais si elle peut bouger loin, elle peut bouger de 1
        return false;
    }

    public boolean aDesCapturesPossibles(String couleur) {
        for (int y = 0; y < TAILLE; y++) {
            for (int x = 0; x < TAILLE; x++) {
                Piece p = cases[y][x];
                if (p != null && p.getCouleur().equals(couleur)) {
                    if (peutCapturer(p)) return true;
                }
            }
        }
        return false;
    }

    public boolean peutCapturer(Piece p) {
        // On vérifie tout le plateau pour voir si cette pièce peut sauter n'importe où
        for (int targetY = 0; targetY < TAILLE; targetY++) {
            for (int targetX = 0; targetX < TAILLE; targetX++) {
                if (capture(p.getX(), p.getY(), targetX, targetY)) return true;
            }
        }
        return false;
    }

    public boolean conditionFinAtteinte() {
        for (int y = 0; y < TAILLE; y++) {
            for (int x = 0; x < TAILLE; x++) {
                Piece p = cases[y][x];
                if (p != null && p.getCouleur().equals(joueurActuel)) {
                    if (peutBouger(p) || peutCapturer(p)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public String resultatFinPartie() {
        int nbNoirs = compterPions("NOIR");
        int nbBlancs = compterPions("BLANC");

        if (nbNoirs > nbBlancs) {
            return "Victoire des NOIRS";
        } else if (nbBlancs > nbNoirs) {
            return "Victoire des BLANCS";
        } else {
            return "ÉGALITÉ";
        }
    }

    public int compterDames(String couleur) {
        int count = 0;
        for (int y = 0; y < TAILLE; y++) {
            for (int x = 0; x < TAILLE; x++) {
                if (cases[y][x] != null && cases[y][x].getCouleur().equals(couleur) && cases[y][x].estDame()) {
                    count++;
                }
            }
        }
        return count;
    }
}
=======
public class Plateau {

    private static final int TAILLE = 10;
    private Piece[][] cases = new Piece[TAILLE][TAILLE];
    private String joueurActuel = "NOIR";// ou "BLANC"

    public Plateau() {
        initialiser();
    }

    public void initialiser() {
        // Nettoyage du plateau
        for (int y = 0; y < TAILLE; y++) {
            for (int x = 0; x < TAILLE; x++) {
                cases[y][x] = null;
            }
        }

        // Pions NOIRS sur les 4 premières lignes (cases foncées)
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < TAILLE; x++) {
                if ((x + y) % 2 == 1) {
                    cases[y][x] = new Piece(x, y, "NOIR");
                }
            }
        }

        // Pions BLANCS sur les 4 dernières lignes (cases foncées)
        for (int y = TAILLE - 4; y < TAILLE; y++) {
            for (int x = 0; x < TAILLE; x++) {
                if ((x + y) % 2 == 1) {
                    cases[y][x] = new Piece(x, y, "BLANC");
                }
            }
        }
    }

    public void afficher() {
        System.out.println("  0 1 2 3 4 5 6 7 8 9");
        for (int y = 0; y < TAILLE; y++) {
            System.out.print(y + " ");
            for (int x = 0; x < TAILLE; x++) {
                if (cases[y][x] == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(cases[y][x].afficher() + " ");
                }
            }
            System.out.println();
        }
    }

    public Piece getPieceAt(int x, int y) {
        if (!coordonneesValides(x, y)) {
            return null;
        }
        return cases[y][x];
    }

    public int getTaille() {
        return TAILLE;
    }

    public boolean estDeplacementSimpleValide(int x1, int y1, int x2, int y2) {

        //  Vérif coordonnées valides
        if (!coordonneesValides(x1, y1) || !coordonneesValides(x2, y2)) {
            return false;
        }

        // Vérif pièce de départ existante
        Piece p = cases[y1][x1];
        if (p == null) {
            return false;
        }

        // Vérif couleur joueur
        if (!p.getCouleur().equals(joueurActuel)) {
            return false;
        }

        // Vérif case d'arrivée vide
        if (cases[y2][x2] != null) {
            return false;
        }

        if (p.estDame()) {
            return deplacementSimpleDameValide(x1, y1, x2, y2);
        }

        // Vérif déplacement diagonal d'une case
        if (Math.abs(x2 - x1) != 1 || Math.abs(y2 - y1) != 1) {
            return false;
        }

        // Vérif sens de déplacement
        if (p.getCouleur().equals("NOIR") && y2 != y1 + 1) {
            return false;
        }

        if (p.getCouleur().equals("BLANC") && y2 != y1 - 1) {
            return false;
        }

        return true;
    }

    public boolean deplacer(int x1, int y1, int x2, int y2) {
        // Vérif capture possible
        if (capture(x1, y1, x2, y2)) {
            Piece p = cases[y1][x1];
            supprimerPieceCapturee(x1, y1, x2, y2, p);

            // Nouveau déplacement du pion
            cases[y1][x1] = null;
            cases[y2][x2] = p;
            p.setX(x2);
            p.setY(y2);
            promouvoirSiNecessaire(p);

            return true;
        }

        // Déplacement simple
        if (estDeplacementSimpleValide(x1, y1, x2, y2)) {
            Piece p = cases[y1][x1];
            cases[y1][x1] = null;
            cases[y2][x2] = p;
            p.setX(x2);
            p.setY(y2);
            promouvoirSiNecessaire(p);
            return true;
        }


        return false;
    }

    public String getJoueurActuel() {
        return joueurActuel;
    }

    public void changerJoueur() {
        if (joueurActuel.equals("NOIR")) {
            joueurActuel = "BLANC";
        } else {
            joueurActuel = "NOIR";
        }
    }

    public boolean capture(int x1, int y1, int x2, int y2) {

        // Vérif coordonnées valides
        if (!coordonneesValides(x1, y1) || !coordonneesValides(x2, y2)) {
            return false;
        }

        Piece p = cases[y1][x1];
        if (p == null) {
            return false;
        }

        // Vérif couleurjoueur
        if (!p.getCouleur().equals(joueurActuel)) {
            return false;
        }

        if (p.estDame()) {
            return captureDameValide(x1, y1, x2, y2, p);
        }

        // Vérif déplacement de 2 cases en diagonale possible
        if (Math.abs(x2 - x1) != 2 || Math.abs(y2 - y1) != 2) {
            return false;
        }

        // Vérif case d'arrivée vide
        if (cases[y2][x2] != null) {
            return false;
        }

        // Case du milieu
        int xMilieu = (x1 + x2) / 2;
        int yMilieu = (y1 + y2) / 2;

        Piece pionMilieu = cases[yMilieu][xMilieu];

        // Vérif pion adverse au milieu
        if (pionMilieu == null) {
            return false;
        }

        if (pionMilieu.getCouleur().equals(p.getCouleur())) {
            return false;
        }

        return true;
    }

    private boolean coordonneesValides(int x, int y) {
        return x >= 0 && x < TAILLE && y >= 0 && y < TAILLE;
    }

    private boolean deplacementSimpleDameValide(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        if (Math.abs(dx) != Math.abs(dy) || dx == 0) {
            return false;
        }

        int pasX = dx > 0 ? 1 : -1;
        int pasY = dy > 0 ? 1 : -1;
        int x = x1 + pasX;
        int y = y1 + pasY;
        while (x != x2 && y != y2) {
            if (cases[y][x] != null) {
                return false;
            }
            x += pasX;
            y += pasY;
        }
        return true;
    }

    private boolean captureDameValide(int x1, int y1, int x2, int y2, Piece dame) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        if (Math.abs(dx) != Math.abs(dy) || dx == 0) {
            return false;
        }

        if (cases[y2][x2] != null) {
            return false;
        }

        int pasX = dx > 0 ? 1 : -1;
        int pasY = dy > 0 ? 1 : -1;
        int x = x1 + pasX;
        int y = y1 + pasY;
        int nbAdverses = 0;

        while (x != x2 && y != y2) {
            Piece intermediaire = cases[y][x];
            if (intermediaire != null) {
                if (intermediaire.getCouleur().equals(dame.getCouleur())) {
                    return false;
                }
                nbAdverses++;
                if (nbAdverses > 1) {
                    return false;
                }
            }
            x += pasX;
            y += pasY;
        }

        return nbAdverses == 1;
    }

    private void supprimerPieceCapturee(int x1, int y1, int x2, int y2, Piece piece) {
        if (!piece.estDame()) {
            int xMilieu = (x1 + x2) / 2;
            int yMilieu = (y1 + y2) / 2;
            cases[yMilieu][xMilieu] = null;
            return;
        }

        int pasX = x2 > x1 ? 1 : -1;
        int pasY = y2 > y1 ? 1 : -1;
        int x = x1 + pasX;
        int y = y1 + pasY;

        while (x != x2 && y != y2) {
            Piece intermediaire = cases[y][x];
            if (intermediaire != null && !intermediaire.getCouleur().equals(piece.getCouleur())) {
                cases[y][x] = null;
                return;
            }
            x += pasX;
            y += pasY;
        }
    }

    private void promouvoirSiNecessaire(Piece piece) {
        if (piece.estDame()) {
            return;
        }

        if (piece.getCouleur().equals("NOIR") && piece.getY() == TAILLE - 1) {
            cases[piece.getY()][piece.getX()] = new Dame(piece.getX(), piece.getY(), piece.getCouleur());
        } else if (piece.getCouleur().equals("BLANC") && piece.getY() == 0) {
            cases[piece.getY()][piece.getX()] = new Dame(piece.getX(), piece.getY(), piece.getCouleur());
        }
    }

    public int compterPions(String couleur) {
        int count = 0;
        for (int y = 0; y < TAILLE; y++) {
            for (int x = 0; x < TAILLE; x++) {
                if (cases[y][x] != null && cases[y][x].getCouleur().equals(couleur)) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean conditionFinAtteinte() {

        int minYNoir = TAILLE;     // très grand
        int maxYBlanc = -1;  // très petit

        boolean noirExiste = false;
        boolean blancExiste = false;

        for (int y = 0; y < TAILLE; y++) {
            for (int x = 0; x < TAILLE; x++) {
                Piece p = cases[y][x];
                if (p != null) {
                    if (p.getCouleur().equals("NOIR")) {
                        noirExiste = true;
                        if (y < minYNoir) {
                            minYNoir = y;
                        }
                    } else if (p.getCouleur().equals("BLANC")) {
                        blancExiste = true;
                        if (y > maxYBlanc) {
                            maxYBlanc = y;
                        }
                    }
                }
            }
        }

        // Vérif minimum pion présent
        if (!noirExiste || !blancExiste) {
            return true;
        }

        return minYNoir >= maxYBlanc;
    }

    public String resultatFinPartie() {
        int nbNoirs = compterPions("NOIR");
        int nbBlancs = compterPions("BLANC");

        if (nbNoirs > nbBlancs) {
            return "Victoire des NOIRS";
        } else if (nbBlancs > nbNoirs) {
            return "Victoire des BLANCS";
        } else {
            return "ÉGALITÉ";
        }
    }
}
>>>>>>> 5efb9be3b877d7f96a52895722f1f6d4ad7ac8f6
