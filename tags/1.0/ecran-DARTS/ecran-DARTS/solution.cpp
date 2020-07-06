#include "solution.h"

/**
* @file solution.cpp
*
* @brief Définition de la classe Solution (Module Ecran-DARTS)
*
* @author Bounoir Fabien
*
* @version 1.0
*
*/

/**
 * @brief Constructeur de la classe Solution
 *
 * @fn Solution::Solution
 * @param parent
 */
Solution::Solution(QObject *parent) : QObject(parent), solution("")
{
   qDebug() << Q_FUNC_INFO;
}

/**
 * @brief Destructeur de la classe Solution
 *
 * @fn Solution::~Solution
 */
Solution::~Solution()
{
   qDebug() << Q_FUNC_INFO;
}


/**
 * @brief Méthode qui émet un signal pour que l'Ihm affiche la solution trouver
 *
 * @fn Solution::transmettreSolution
 * @param score
 */
void Solution::transmettreSolution(int score)
{
    qDebug() << Q_FUNC_INFO << "Score = " << score << " : " << solution;
    emit solutionTrouver("↣  " + QString::number(score) + " ➤ " + solution + "  ↢");
}

/**
 * @brief Test si la triple choisie et possible
 *
 * @fn Solution::aTriple
 * @param points
 * @param score
 * @return bool
 */
bool Solution::aTriple(int points, const int score)
{
    if((score / (points*3)) >= 1)
    {
        return true;
    }
    return false;
}

/**
 * @brief Méthode qui recherche le meilleur triple pour la solution
 *
 * @fn Solution::rechercherTriple
 * @param score
 * @param combinaison
 * @param start
 * @return bool
 */
bool Solution::rechercherTriple(int &score, QString &combinaison, int start=20)
{
    bool trouve = false;
    for(int i=start; i>1 && !trouve; i--)
    {
        if(aTriple(i, score))
        {
            combinaison = "T" + QString::number(i);
            score -= (i*3);
            trouve = true;
        }
    }
    return trouve;
}

/**
 * @brief Test si le double choisie et possible
 *
 * @fn Solution::aDouble
 * @param points
 * @param score
 * @return bool
 */
bool Solution::aDouble(int points, const int score)
{
    if((score / (points*2)) >= 1)
    {
        return true;
    }
    return false;
}

/**
 * @brief Méthode qui recherche le meilleur double pour la solution
 *
 * @fn Solution::rechercherDouble
 * @param score
 * @param combinaison
 * @return bool
 */
bool Solution::rechercherDouble(int &score, QString &combinaison)
{
    bool trouve = false;
    // Remarque : le 2 est plus facile que le D1 !
    for(int i=BULL; i>1 && !trouve; i--)
    {
        // cibles inexistantes ?
        if(i > 20)
            continue;
        if(aDouble(i, score))
        {
            combinaison = "D" + QString::number(i);
            score -= (i*2);
            trouve = true;
        }
    }
    return trouve;
}

/**
 * @brief Test si le simple choisi et possible
 *
 * @fn Solution::aSimple
 * @param points
 * @param score
 * @return bool
 */
bool Solution::aSimple(int points, const int score)
{
    if((score / points) >= 1)
        return true;
    return false;
}

/**
 * @brief Méthode qui recherche le meilleur simple pour la solution
 *
 * @fn Solution::rechercherSimple
 * @param score
 * @param combinaison
 * @return bool
 */
bool Solution::rechercherSimple(int &score, QString &combinaison)
{
    bool trouve = false;

    for(int i=BULL; i>0 && !trouve; i--)
    {
        // cibles inexistantes ?
        if(i > 20)
            continue;
        if(aSimple(i, score))
        {
            combinaison = QString::number(i);
            score -= i;
            trouve = true;
        }
    }
    return trouve;
}

/**
 * @brief Méthode qui teste si les points son double
 *
 * @fn Solution::estDouble
 * @param points
 * @param score
 * @return bool
 */
bool Solution::estDouble(int points, const int score)
{
    if(score == (points*2))
        return true;
    return false;
}

/**
 * @brief Méthode qui cherche le double pour finir la partie
 *
 * @fn Solution::extraireDouble
 * @param score
 * @param cible
 * @return bool
 */
bool Solution::extraireDouble(int &score, int cible)
{
    if(aDouble(cible, score))
    {
        score -= (cible*2);
        return true;
    }
    return false;
}

/**
 * @brief Méthode qui recherche la meilleure combinaison pour finir
 *
 * @fn Solution::rechercher
 * @param score
 * @param nbFlechettes
 * @param still
 * @return bool
 */
bool Solution::rechercher(int score, int nbFlechettes, bool still)
{
    QString combinaison;
    int s = score;
    int n = RECHERCHE_TRIPLE;
    int start = 20;
    bool trouve = false;

    solution.clear();
    for(int flechettes=nbFlechettes; flechettes>0 && score>0;)
    {
        if(n == RECHERCHE_TRIPLE)
        {
            trouve = rechercherTriple(score, combinaison, start);
            if(!trouve)
            {
                n = RECHERCHE_DOUBLE;
            }
        }
        else if(n == RECHERCHE_DOUBLE)
        {
            trouve = rechercherDouble(score, combinaison);
            if(!trouve)
            {
                n = RECHERCHE_SIMPLE;
            }
        }
        else if(n == RECHERCHE_SIMPLE)
        {
            trouve = rechercherSimple(score, combinaison);
        }

        if(trouve)
        {
            solution +=  " " + combinaison;
            flechettes--;
            if(score == 0)
            {
                return true;
            }
        }

        // pas trouvé ?
        if(flechettes == 0 && score != 0)
        {
            if(still)
                return true;
            // on recommence
            solution.clear();
            score = s;
            flechettes = nbFlechettes;
            n++; // on change de tactique
        }

        if(n == RECHERCHE_FINIE)
        {
            start--; // on essaye un autre triple
            // on recommence
            solution.clear();
            n = RECHERCHE_TRIPLE;
            score = s;
            flechettes = nbFlechettes;
        }

        if(start == 1)
        {
            return false;
        }
    }
    return false;
}

/**
 * @brief Méthode qui trouve la meilleure solution
 *
 * @fn Solution::trouverSolution
 * @param s
 * @param flechettes
 */
void Solution::trouverSolution(int scoreJoueur, int flechettes)
{
    int score = 0;
    bool trouve = false;
    int nbFlechettes = 0;
    solution = "";

    trouve = false;
    for(int i=BULL; i>0 && !trouve; i--)
    {
        // cibles inexistantes ?
        if(i > 20)
            continue;
        score = scoreJoueur; // <- le score à determiner
        nbFlechettes = flechettes; // <- le nombre de fléchettes
        if(extraireDouble(score, i))
        {
            nbFlechettes--;
            if(rechercher(score, nbFlechettes) || score == 0)
            {
                solution += " D" + QString::number(i) + "*";
                transmettreSolution(scoreJoueur);
                trouve = true;
                break;
            }
        }
    }
    if(!trouve)
    {
        rechercher(score, nbFlechettes+1, true);
        //transmettreSolution(scoreJoueur);       //activer pour avoir l'aide tout le temps même quand on ne peut pas finir
    }
}
