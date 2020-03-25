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

    public boolean retirerPoint(int scoreLancer)
    {
        if (this.score - scoreLancer >= 0)
        {
            this.score = this.score - scoreLancer;
            return true;
        }
        return false;
    }
}
