import java.io.Serializable;

public class Piece implements Serializable {
    private static final long serialVersionUID = 1L;

    private int x;          // colonne
    private int y;          // ligne
    private String couleur; // "NOIR" ou "BLANC"
    private boolean dame;   // état de promotion

    public Piece(int x, int y, String couleur) {
        this.x = x;
        this.y = y;
        this.couleur = couleur;
        this.dame = false;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public String afficher() {
        if (dame) return couleur.equals("NOIR") ? "♛" : "♕";
        if (couleur.equals("NOIR")) {
            return "●";
        } else {
            return "○";
        }
    }

    public boolean estDame() {
        return dame;
    }

    public void setDame(boolean dame) {
        this.dame = dame;
    }
}
