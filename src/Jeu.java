public class Jeu {

    private Plateau plateau;
    private Integer selectionX;
    private Integer selectionY;
    private String messageEtat;
    private boolean partieTerminee;

    public Jeu() {
        plateau = new Plateau();
        selectionX = null;
        selectionY = null;
        messageEtat = "Selectionnez une piece du joueur " + plateau.getJoueurActuel();
        partieTerminee = false;
    }

    public String cliquerCase(int x, int y) {
        if (partieTerminee) {
            return messageEtat;
        }

        Piece pieceCliquee = plateau.getPieceAt(x, y);

        if (selectionX == null || selectionY == null) {
            if (pieceCliquee == null) {
                messageEtat = "Aucune piece sur cette case.";
                return messageEtat;
            }
            if (!pieceCliquee.getCouleur().equals(plateau.getJoueurActuel())) {
                messageEtat = "Ce n'est pas une piece du joueur " + plateau.getJoueurActuel() + ".";
                return messageEtat;
            }

            selectionX = x;
            selectionY = y;
            messageEtat = "Piece selectionnee en (" + x + ", " + y + "). Choisissez la destination.";
            return messageEtat;
        }

        if (selectionX == x && selectionY == y) {
            selectionX = null;
            selectionY = null;
            messageEtat = "Selection annulee.";
            return messageEtat;
        }

        if (pieceCliquee != null && pieceCliquee.getCouleur().equals(plateau.getJoueurActuel())) {
            selectionX = x;
            selectionY = y;
            messageEtat = "Piece selectionnee en (" + x + ", " + y + "). Choisissez la destination.";
            return messageEtat;
        }

        if (plateau.deplacer(selectionX, selectionY, x, y)) {
            selectionX = null;
            selectionY = null;

            if (plateau.conditionFinAtteinte()) {
                partieTerminee = true;
                messageEtat = "Fin de la partie ! " + plateau.resultatFinPartie();
                return messageEtat;
            }

            plateau.changerJoueur();
            messageEtat = "Coup valide. Au tour des " + plateau.getJoueurActuel() + ".";
            return messageEtat;
        }

        messageEtat = "Coup invalide. Choisissez une autre destination.";
        return messageEtat;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public boolean isPartieTerminee() {
        return partieTerminee;
    }

    public String getMessageEtat() {
        return messageEtat;
    }

    public Integer getSelectionX() {
        return selectionX;
    }

    public Integer getSelectionY() {
        return selectionY;
    }

    public void reinitialiser() {
        plateau = new Plateau();
        selectionX = null;
        selectionY = null;
        partieTerminee = false;
        messageEtat = "Selectionnez une piece du joueur " + plateau.getJoueurActuel();
    }

    public void afficherConsole() {
        plateau.afficher();
        System.out.println(messageEtat);
        if (selectionX != null && selectionY != null) {
            System.out.println("Selection: (" + selectionX + ", " + selectionY + ")");
        }
    }
}