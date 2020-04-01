package projet.lasalle84.darts;

/**
 * @file Joueur.java
 * @brief Déclaration de la classe Joueur
 * @author Menella Erwan
 */


import android.util.Log;
import java.io.Serializable;


/**
 * @class Joueur
 * @brief Déclaration de la classe Joueur
 */
public class Joueur implements Serializable
{
    private static final String TAG = "Joueur" ;
    private String nom; //!< Le nom du joueur
    private int score; //!< Le score du joueur
    private boolean estEliminer;

    /**
     * @brief Constructeur de la classe Joueur
     *
     * @fn Joueur::Joueur(String nom)
     * @param nom le nom du joueur
     */
    public Joueur(String nom)
    {
        this.nom = nom;
        this.score = 0;
        estEliminer = false;
    }

    /**
     * @brief Accesseur get du nom du joueur
     *
     * @fn Joueur::getNom()
     * @return String le nom du joueur
     */
    public String getNom()
    {
        return nom;
    }

    /**
     * @brief Accesseur set du nom du joueur
     *
     * @fn Joueur::setNom(String nom)
     * @param nom le nom du joueur
     */
    public void setNom(String nom)
    {
        this.nom = nom;
    }

    /**
     * @brief Accesseur get du score du joueur
     *
     * @fn Joueur::getScore()
     * @return int le score du joueur
     */
    public int getScore()
    {
        return score;
    }

    /**
     * @brief Accesseur set du score du joueur
     *
     * @fn Joueur::setScore(int score)
     * @param score le score du joueur
     */
    public void setScore(int score) {
        Log.d(TAG, this.nom + " setScore " + score);
        this.score = score;
    }

    /**
     * @brief fonction qui permet de retirer le score et retourner si le score a était retiré
     *
     * @fn Joueur::retirerPoint(int scoreLancer)
     * @param scoreLancer à retirer
     */
    public boolean retirerPoint(int scoreLancer, Partie maPartie)
    {
        if (this.score - scoreLancer >= 0)
        {
            this.score = this.score - scoreLancer;
            if (this.score == 1 && maPartie.getTypeJeu().estDoubleOut())
            {
                Log.d(TAG, this.getNom() + " est éliminé");
                estEliminer = true;
            }
            return true;
        }
        return false;
    }

    /**
     * @brief Accesseur set si le joueur est eliminer
     *
     * @fn Joueur::setEstEliminer(boolean estEliminer)
     * @param estEliminer si le joueur est eliminer
     */
    public void setEstEliminer(boolean estEliminer)
    {
        this.estEliminer = estEliminer;
    }

    /**
     * @brief Accesseur get si le joueur est eliminer
     *
     * @fn Joueur::setEstEliminer(boolean estEliminer)
     */
    public boolean estEliminer()
    {
        return estEliminer;
    }

}
