#include "solution.h"

Solution::Solution(QObject *parent) : QObject(parent)
{

}

void Solution::calculerCombinaisons()
{

}

void Solution::afficherSolution(int score, const QVector<QString> &solution)
{
    qDebug() << "Score = " << score << " : ";
    qDebug() << "taille vecteur" << solution.size() << endl;
    for(int i = 0; i < solution.size(); i++)
    {
        qDebug() << solution.at(i) << " ";
    }
    qDebug() << endl;
}

bool Solution::aTriple(int points, const int score)
{
    if((score / (points*3)) >= 1)
    {
        return true;
    }
    return false;
}

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

bool Solution::aDouble(int points, const int score)
{
    if((score / (points*2)) >= 1)
    {
        return true;
    }
    return false;
}

bool Solution::rechercherDouble(int &score, QString &combinaison)
{
    bool trouve = false;
    // Remarque : le 2 est plus facile que le D1 !
    for(int i=25; i>1 && !trouve; i--)
    {
        if(i >= 21 && i <= 24)
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

bool Solution::aSimple(int points, const int score)
{
    if((score / points) >= 1)
        return true;
    return false;
}

bool Solution::rechercherSimple(int &score, QString &combinaison)
{
    bool trouve = false;

    for(int i=25; i>0 && !trouve; i--)
    {
        if(i >= 21 && i <= 24)
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

bool Solution::estDouble(int points, const int score)
{
    if(score == (points*2))
        return true;
    return false;
}

bool Solution::extraireDouble(int &score, int cible)
{
    if(aDouble(cible, score))
    {
        score -= (cible*2);
        return true;
    }
    return false;
}

bool Solution::rechercher(int score, int nbFlechettes, QVector<QString> solution, bool still)
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
            solution.push_back(combinaison);
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

void Solution::trouverSolution(int s, int flechettes)
{
    int score = 0;
    int n = RECHERCHE_TRIPLE;
    int start = 20;
    bool trouve = false;
    int nbFlechettes = 0;
    QVector<QString> solution;

    trouve = false;
    for(int i=25; i>0 && !trouve; i--)
    {
        if(i >= 21 && i <= 24)
            continue;
        score = s; // <- le score à determiner
        nbFlechettes = flechettes; // <- le nombre de fléchettes
        if(extraireDouble(score, i))
        {
            nbFlechettes--;
            if(rechercher(score, nbFlechettes, solution) || score == 0)
            {
                solution.push_back("D" + QString::number(i) + "*");
                afficherSolution(s, solution);
                trouve = true;
                break;
            }
        }
    }
    if(!trouve)
    {
        //cout << "Score = " << s << " : impossible" << endl;
        rechercher(score, nbFlechettes+1, solution, true);
        afficherSolution(s, solution);
    }
}
