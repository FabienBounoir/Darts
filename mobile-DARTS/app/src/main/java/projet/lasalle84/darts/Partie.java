package projet.lasalle84.darts;

import java.util.Queue;

/**
 * @file Partie.java
 * @brief Déclaration de la classe Partie
 * @author Menella Erwan
 */

/**
 * @class Partie
 * @brief Déclaration de la classe Partie
 */
public class Partie
{
    private int nbJoueurs;              //!< Nombre de joueur
    private int nbManche;               //!< Nombre de manche
    private TypeJeu typeJeu;            //!< Mode de jeu
    private Queue<Joueur> lesJoueurs;   //!< Les objets Joueur stocker dans un conteneur (Queue)

    /**
     * @brief Constructeur de la classe Partie
     *
     * @fn Partie::Partie(Queue<Joueur> lesJoueur, TypeJeu typeJeu)
     * @param lesJoueurs
     * @param typeJeu
     */
    public Partie(Queue<Joueur> lesJoueurs, TypeJeu typeJeu)
    {
        this.lesJoueurs = lesJoueurs;
        this.typeJeu = typeJeu;
    }
}
