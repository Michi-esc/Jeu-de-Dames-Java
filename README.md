# 👑 Jeu de Dames 10x10 - [Nom de votre Entreprise Fictive]

## 🏢 Contexte du Projet & Gestion d'équipe

Ce projet de développement d'un jeu de dames en Java a été réalisé dans le cadre d'un cours de programmation orientée objet (POO) à l'université. L'objectif était de créer une application complète respectant les règles classiques du jeu de dames international, tout en offrant une interface graphique moderne et intuitive.

**🎯 Cible visée :** 
Le jeu s'adresse aux passionnés de jeux de réflexion traditionnels, aux joueurs occasionnels cherchant une interface moderne, ainsi qu'aux personnes nécessitant des options d'accessibilité visuelle (daltonisme, contrastes forts).

**👥 Répartition des rôles :**
* **Adama** : Développeur Backend & Logique (Conception du moteur de jeu `Jeu.java`, `Plateau.java`, algorithmes de déplacements, rafales de captures et gestion des sauvegardes).
* **Michael** : Développeur Frontend & UI/UX (Création de l'interface JavaFX `Main.java`, animations avancées `BackgroundAnimator`, gestion du chronomètre, menus et intégration des thèmes graphiques).


---

## ✨ Fonctionnalités & Règles du Jeu

Le jeu respecte fidèlement les règles classiques du jeu de dames international :
* **Plateau 10x10** : 20 pions par joueur (Noirs contre Blancs).
* **Mode 2 Joueurs** : Jouable en local (Hotseat).
* **Déplacements et Captures** : Déplacements en diagonale. La prise est obligatoire et les rafales (captures multiples) sont gérées automatiquement par le moteur.
* **Promotion en Dame** : Un pion atteignant la dernière ligne adverse est promu en Dame (♛/♕), gagnant la capacité de se déplacer sur de longues diagonales.
* **Fin de partie** : Le jeu détecte automatiquement la victoire lorsqu'un joueur n'a plus de pièces ou se retrouve bloqué.
* **Fonctionnalités bonus** : Chronomètre par joueur, historique des coups, système de sauvegarde/chargement, thèmes graphiques dynamiques.

---

## 🎨 Justification de l'Interface Graphique (GUI)

Nous avons fait le choix technique de développer l'interface avec **JavaFX**. Ce choix se justifie pour plusieurs raisons :
1. **Expérience Utilisateur (UX) fluide** : JavaFX nous a permis d'intégrer des animations (transitions, survol, rotations de plateau) qui rendent le jeu vivant.
2. **Robustesse et Gestion des erreurs** : L'interface empêche les actions non autorisées. Lors d'une tentative de coup invalide, un retour visuel (tremblement du plateau) et textuel est donné sans faire planter l'application.
3. **Personnalisation visuelle** : L'utilisation de `Canvas` et des `Nodes` JavaFX a facilité la création de nombreux thèmes et arrière-plans animés programmatiques (`BackgroundAnimator`), tout en supportant nativement l'intégration de modes d'accessibilité.

---

## 🏗️ Architecture et Qualité de la POO

Le code source a été structuré pour respecter les principes de la Programmation Orientée Objet (POO) :

* **Encapsulation stricte** : Tous les attributs des classes (`Piece`, `Plateau`, `Jeu`) sont privés. Ils ne sont modifiables que via des méthodes spécifiques et des getters/setters, protégeant l'intégrité de l'état du jeu à tout moment.
* **Responsabilités claires (Single Responsibility Principle)** :
  * `Piece` : Entité pure stockant ses coordonnées, sa couleur et son statut (pion ou dame).
  * `Plateau` : Logique spatiale. Valide la géométrie des déplacements et exécute les règles du plateau.
  * `Jeu` : Contrôleur du déroulement. Gère le tour par tour, les sélections du joueur et l'enchaînement des combos (rafales).
  * `Main` (GUI) : Se contente d'afficher l'état transmis par la classe `Jeu` et de capter les clics.
* **Polymorphisme & Héritage (Conceptuel)** : Bien que l'état de "Dame" soit actuellement géré par un booléen pour des raisons de simplicité, l'architecture permettrait facilement de créer une classe parente abstraite `Piece` et deux classes filles `Pion` et `Dame` héritant et redéfinissant une méthode `peutSeDeplacer()` si les règles venaient à se complexifier.

---

## 🚀 Installation et Lancement

**Prérequis** : Java JDK 11 ou supérieur et les bibliothèques JavaFX.

1. Clonez ce dépôt.
2. Ouvrez un terminal dans le dossier contenant les sources (`src`).
3. Compilez et exécutez le programme en liant JavaFX :

```bash
javac --module-path /chemin/vers/lib/javafx --add-modules javafx.controls,javafx.graphics Main.java
java --module-path /chemin/vers/lib/javafx --add-modules javafx.controls,javafx.graphics Main
```
*(Note: Si vous utilisez un IDE comme IntelliJ IDEA ou Eclipse, il vous suffit de configurer JavaFX dans les "Project Structure" / "Libraries").*

---

## 📊 Bilan : Difficultés et Perspectives

### Difficultés rencontrées
* **L'algorithme des prises multiples (Rafales)** : Imposer à un joueur de continuer à jouer avec la même pièce lorsqu'une suite de captures est possible a nécessité de revoir la gestion de l'état dans la classe `Jeu`.
* **Synchronisation Vue/Modèle** : Éviter que l'utilisateur ne clique sur une autre case pendant que l'animation d'un déplacement est encore en cours de lecture.

### Ce que le projet nous a apporté
* Une mise en pratique concrète et approfondie des concepts de la **POO**.
* La découverte du framework **JavaFX**, de la gestion des scènes et des événements (Event Handlers).
* Une meilleure maîtrise du travail collaboratif et de la conception logicielle en amont (architecture MVC implicite).

### Perspectives d'évolution (V2.0)
Lors d'une future itération, nous pourrions envisager :
1. **Mode Solo (IA)** : Implémenter un algorithme *Minimax* pour permettre de jouer contre l'ordinateur.
2. **Mode Multijoueur en ligne** : Utiliser les *Sockets* Java (TCP/IP) pour connecter deux joueurs à distance.
3. **Historique avancé** : Pouvoir "rejouer" visuellement une ancienne partie enregistrée coup par coup.