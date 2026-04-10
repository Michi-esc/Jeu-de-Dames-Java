<<<<<<< HEAD
import java.io.Serializable;
=======
public class Jeu {
>>>>>>> 5efb9be3b877d7f96a52895722f1f6d4ad7ac8f6

public class Jeu implements Serializable {
    private static final long serialVersionUID = 1L;
    private Plateau plateau;
    private Integer selectionX;
    private Integer selectionY;
    private String messageEtat;
    private boolean partieTerminee;
<<<<<<< HEAD
    private boolean dernierCoupPromotion = false;
    private int comboCaptures = 0;
    private Piece pieceEnCombo = null;
=======
>>>>>>> 5efb9be3b877d7f96a52895722f1f6d4ad7ac8f6

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

<<<<<<< HEAD
        if (pieceEnCombo != null && (selectionX == null)) {
            selectionX = pieceEnCombo.getX();
            selectionY = pieceEnCombo.getY();
        }

        if (selectionX == null || selectionY == null) {
            // Réinitialiser le combo seulement quand on commence une nouvelle sélection
            if (pieceEnCombo == null) {
                comboCaptures = 0;
            }

            if (pieceCliquee == null) {
                messageEtat = "Aucune piece sur cette case.";
                return messageEtat;
            }
            if (!pieceCliquee.getCouleur().equals(plateau.getJoueurActuel())) {
                messageEtat = "Ce n'est pas une piece du joueur " + plateau.getJoueurActuel() + ".";
                return messageEtat;
            }

            if (pieceEnCombo != null && pieceCliquee != pieceEnCombo) {
                messageEtat = "Vous devez continuer la capture avec la meme piece !";
                return messageEtat;
            }

            selectionX = x;
            selectionY = y;
            messageEtat = "Piece selectionnee en (" + x + ", " + y + "). Choisissez la destination.";
            return messageEtat;
        }

        if (selectionX == x && selectionY == y) {
            if (pieceEnCombo != null) {
                messageEtat = "Capture multiple en cours. Vous ne pouvez pas annuler.";
                return messageEtat;
            }
            selectionX = null;
            selectionY = null;
            messageEtat = "Selection annulee.";
            return messageEtat;
        }

        if (pieceCliquee != null && pieceCliquee.getCouleur().equals(plateau.getJoueurActuel())) {
            if (pieceEnCombo != null) {
                messageEtat = "Vous devez terminer votre rafale.";
                return messageEtat;
            }
            selectionX = x;
            selectionY = y;
            messageEtat = "Piece selectionnee en (" + x + ", " + y + "). Choisissez la destination.";
            return messageEtat;
        }

        Piece pieceAvant = plateau.getPieceAt(selectionX, selectionY);
        boolean etaitDame = pieceAvant != null && pieceAvant.estDame();

        boolean estCapture = plateau.capture(selectionX, selectionY, x, y);

        if (plateau.deplacer(selectionX, selectionY, x, y)) {
            Piece pieceApres = plateau.getPieceAt(x, y);
            dernierCoupPromotion = !etaitDame && pieceApres != null && pieceApres.estDame();
            
            if (estCapture) {
                comboCaptures++;
                if (plateau.peutCapturer(pieceApres)) {
                    pieceEnCombo = pieceApres;
                    selectionX = x;
                    selectionY = y;
                    messageEtat = "Capture reussie ! Continuez la rafale.";
                    return messageEtat;
                }
            }

            pieceEnCombo = null;
            selectionX = null;
            selectionY = null;

            plateau.changerJoueur();
            if (plateau.conditionFinAtteinte()) {
                partieTerminee = true;
                messageEtat = "Fin de la partie ! " + plateau.resultatFinPartie();
                return messageEtat;
            }

=======
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
>>>>>>> 5efb9be3b877d7f96a52895722f1f6d4ad7ac8f6
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
<<<<<<< HEAD
        comboCaptures = 0;
        pieceEnCombo = null;
=======
>>>>>>> 5efb9be3b877d7f96a52895722f1f6d4ad7ac8f6
        messageEtat = "Selectionnez une piece du joueur " + plateau.getJoueurActuel();
    }

    public void afficherConsole() {
        plateau.afficher();
        System.out.println(messageEtat);
        if (selectionX != null && selectionY != null) {
            System.out.println("Selection: (" + selectionX + ", " + selectionY + ")");
        }
    }

    public boolean caseJouableDepuisSelection(int x, int y) {
        if (selectionX == null || selectionY == null) return false;
        return plateau.estDeplacementSimpleValide(selectionX, selectionY, x, y) || plateau.capture(selectionX, selectionY, x, y);
    }

    public int compterPions(String couleur) { return plateau.compterPions(couleur); }
    public int compterDames(String couleur) { return plateau.compterDames(couleur); }
    
    public int getComboCaptures() { return comboCaptures; }
    public boolean isDernierCoupPromotion() { return dernierCoupPromotion; }
}