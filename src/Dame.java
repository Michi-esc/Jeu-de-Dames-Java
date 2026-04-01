public class Dame extends Piece {

    public Dame(int x, int y, String couleur) {
        super(x, y, couleur);
    }

    @Override
    public String afficher() {
        if (getCouleur().equals("NOIR")) {
            return "♛";
        } else {
            return "♕";
        }
    }

    @Override
    public boolean estDame() {
        return true;
    }
}
