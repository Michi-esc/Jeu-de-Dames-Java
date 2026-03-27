import java.util.Scanner;

public class Jeu {

    private Plateau plateau;
    private Scanner scanner;

    public Jeu() {
        plateau = new Plateau();
        scanner = new Scanner(System.in);
    }

    public void jouer() {
        while (true) {
            plateau.afficher();

            System.out.print("Joueur " + plateau.getJoueurActuel()
                    + " - saisissez x1 y1 x2 y2 (ou -1 pour quitter) : ");

            int x1 = scanner.nextInt();
            if (x1 == -1) {
                System.out.println("Fin de la partie.");
                break;
            }

            int y1 = scanner.nextInt();
            int x2 = scanner.nextInt();
            int y2 = scanner.nextInt();
            scanner.nextLine();

            if (plateau.deplacer(x1, y1, x2, y2)) {
                System.out.println("Coup valide !");

                // Vérif fin de partie
                if (plateau.conditionFinAtteinte()) {
                    plateau.afficher();
                    System.out.println("Fin de la partie !");
                    System.out.println(plateau.resultatFinPartie());
                    break;
                }

                else {
                    System.out.println("Coup invalide, réessayez.");
                }

                plateau.changerJoueur();
            }
        }
    }
}