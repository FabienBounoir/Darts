#include "darts.h"

#include <QDebug>


/**
* @file darts.cpp
*
* @brief darts s'occupe du deroulement de la partie
*
* @author Bounoir Fabien
*
* @version 0.1
*
*/

/**
 * @brief constructeur de la classe Darts
 *
 * @fn Darts::Darts
 * @param parent
 */
Darts::Darts(QObject *parent) : QObject(parent), joueur(nullptr), nbJoueur(0), joueurActif(0), manche(1), pointLancer(0), voleeMax(0)
{

}

/**
 * @brief destructeur de la classe Darts
 *
 * @fn Darts::~Darts
 */
Darts::~Darts()
{

}

/**
 * @brief retourne la manche
 *
 * @fn Darts::getManche
 * @return int
 */
int Darts::getManche() const
{
    return manche;
}

/**
 * @brief retourne une liste des joueurs
 *
 * @fn Darts::getListJoueur
 * @return QList<Joueur>
 */
QList<Joueur> Darts::getListJoueur() const
{
    return joueurs;
}

/**
 * @brief retourne la volee Max
 *
 * @fn Darts::getVoleeMax
 * @return int
 */
int Darts::getVoleeMax()
{
    return voleeMax;
}

int Darts::getJoueurActif()
{
    return joueurActif;
}

/**
 * @brief  permet de mettre à jour la volée Max
 *
 * @fn Darts::setVoleeMax
 * @param voleeMax
 */
void Darts::setVoleeMax(int voleeMax)
{
    this->voleeMax = voleeMax;
}

/**
 * @brief permet de mettre à jour le numero de manche
 *
 * @fn Darts::setManche
 * @param manche
 */
void Darts::setManche(int manche)
{
    this->manche = manche;
}


/**
 * @brief permet initialiser la partie
 *
 * @fn Darts::initialiserPartie
 * @param joueurList
 * @param modeJeu
 */
void Darts::initialiserPartie(QStringList joueurList, QString modeJeu)
{
    nbJoueur = joueurList.size() - 1;
    qDebug() << "nombre de JOUEUR : " << nbJoueur;
    for(int i = 1; i < joueurList.size() ; i++)
    {
        Joueur player(joueurList.at(i), modeJeu.toInt(), 3);
        joueurs.push_back(player);
    }
}

/**
 * @brief Méthode qui permet de remettre a zero les differents attribut et conteneur pour une nouvelle partie
 *
 * @fn Darts::reinitialiserPartie
 */
void Darts::reinitialiserPartie()
{
    joueurs.clear();
    joueur.clear();
    nbJoueur = 0;
    joueurActif = 0;
    manche = 1;
    pointLancer = 0;
}

/**
 * @brief permet de traiter la reception d'impact
 *
 * @fn Darts::receptionnerImpact
 * @param cercle
 * @param point
 */
void Darts::receptionnerImpact(int cercle, int point)
{
    if(cercle == TRIPLE_POINT)
    {
        pointLancer = point * 3;
    }
    else if(cercle == DOUBLE_POINT)
    {
        pointLancer = point * 2;
    }
    else
    {
        pointLancer = point;
    }

    emit nouvelleImpact(cercle, point, pointLancer);
    qDebug() << joueurs[joueurActif].getNom() << " SCORE : "<<joueurs[joueurActif].getScore() - pointLancer << endl;

    if((joueurs[joueurActif].getScore() - pointLancer)  == 0 && cercle == DOUBLE_POINT)
    {
        gererVoleeMax();
        emit finPartie(joueurs[joueurActif].getNom(), getVoleeMax());
        emit etatPartieFini();
    }
    else
    {
        enleverPointImpact();
        gererManche();
    }
    emit miseAJourPoint();
}

/**
 * @brief Méthode qui met à jour le score du joueur
 *
 * @fn Darts::enleverPointImpact
 */
void Darts::enleverPointImpact()
{
    joueurs[joueurActif].setScore(joueurs[joueurActif].getScore() - pointLancer);

    if(joueurs[joueurActif].getScore() < 0)
    {
         joueurs[joueurActif].setScore(joueurs[joueurActif].getScoreManchePrecedente());
         emit voleeAnnulee();

         joueurs[joueurActif].setNbFlechette(0);
    }
    else
    {
        qDebug() << Q_FUNC_INFO << joueurs[joueurActif].getNom() << "  : " << joueurs[joueurActif].getScore();
        joueurs[joueurActif].setNbFlechette(joueurs[joueurActif].getFlechette() - 1);
    }
}

/**
 * @brief Méthode qui permet de gerer le changement de manche en fonction des flechettes de chaque joueur
 *
 * @fn Darts::gererManche
 */
void Darts::gererManche()
{
    if(joueurs[joueurActif].getFlechette() == 0)
    {
        joueurs[joueurActif].setNbFlechette(3);

        gererVoleeMax();

        joueurs[joueurActif].setScoreManchePrecedente(joueurs[joueurActif].getScore());

        if(joueurActif == nbJoueur - 1)
        {
            joueurActif = 0;
            setManche(getManche() + 1);
            emit changerJoueurActif();
            emit nouvelleManche();
        }
        else
        {
            joueurActif++;
            emit changerJoueurActif();
        }
    }
}

/**
 * @brief Methode qui teste la Volée pour savoir si elle est superieur à la Volée Max
 *
 * @fn Darts::gererVoleeMax
 */
void Darts::gererVoleeMax()
{
    if((joueurs[joueurActif].getScoreManchePrecedente() - joueurs[joueurActif].getScore()) > getVoleeMax())
    {
        setVoleeMax(joueurs[joueurActif].getScoreManchePrecedente() - joueurs[joueurActif].getScore());
    }
}
