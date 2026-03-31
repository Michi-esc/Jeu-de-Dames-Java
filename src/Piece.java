public class Piece {
    private int x;          // colonne
    private int y;          // ligne
    private String couleur; // "NOIR" ou "BLANC"

    public Piece(int x, int y, String couleur) {
        this.x = x;
        this.y = y;
        this.couleur = couleur;}

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
        if (couleur.equals("NOIR")) {
            return "●";
        } else {
            return "○";
        }
    }
}
