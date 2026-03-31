public class Plateau {

    private Piece[][] cases = new Piece[8][8];
    private String joueurActuel = "NOIR";// ou "BLANC"

    public Plateau() {
        initialiser();
    }

    public void initialiser() {
        // Pions NOIRS
        cases[2][1] = new Piece(1, 2, "NOIR");
        cases[2][3] = new Piece(3, 2, "NOIR");
        cases[2][5] = new Piece(5, 2, "NOIR");
        cases[2][7] = new Piece(7, 2, "NOIR");

        // Pions BLANCS
        cases[5][0] = new Piece(0, 5, "BLANC");
        cases[5][2] = new Piece(2, 5, "BLANC");
        cases[5][4] = new Piece(4, 5, "BLANC");
        cases[5][6] = new Piece(6, 5, "BLANC");
    }

    public void afficher() {
        System.out.println("  0 1 2 3 4 5 6 7");
        for (int y = 0; y < 8; y++) {
            System.out.print(y + " ");
            for (int x = 0; x < 8; x++) {
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
        return cases[y][x];
    }

    public boolean estDeplacementSimpleValide(int x1, int y1, int x2, int y2) {

        //  Vérif coordonnées valides
        if (x2 < 0 || x2 > 7 || y2 < 0 || y2 > 7) {
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

            int xMilieu = (x1 + x2) / 2;
            int yMilieu = (y1 + y2) / 2;

            // Suppression du pion mangé
            cases[yMilieu][xMilieu] = null;

            // Nouveau déplacement du pion
            cases[y1][x1] = null;
            cases[y2][x2] = p;
            p.setX(x2);
            p.setY(y2);

            return true;
        }

        // Déplacement simple
        if (estDeplacementSimpleValide(x1, y1, x2, y2)) {
            Piece p = cases[y1][x1];
            cases[y1][x1] = null;
            cases[y2][x2] = p;
            p.setX(x2);
            p.setY(y2);
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
        if (x2 < 0 || x2 > 7 || y2 < 0 || y2 > 7) {
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

        // Vérif déplacement de 2 cases en diagonale possible
        if (Math.abs(x2 - x1) != 2 || Math.abs(y2 - y1) != 2) {
            return false;
        }

        // Vérif sens du déplacement
        if (p.getCouleur().equals("NOIR") && y2 != y1 + 2) {
            return false;
        }

        if (p.getCouleur().equals("BLANC") && y2 != y1 - 2) {
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

    public int compterPions(String couleur) {
        int count = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (cases[y][x] != null && cases[y][x].getCouleur().equals(couleur)) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean conditionFinAtteinte() {

        int minYNoir = 8;     // très grand
        int maxYBlanc = -1;  // très petit

        boolean noirExiste = false;
        boolean blancExiste = false;

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
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
