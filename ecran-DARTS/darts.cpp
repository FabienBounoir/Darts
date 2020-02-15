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
Darts::Darts(QObject *parent) : QObject(parent), joueur(nullptr), nbJoueur(0), joueurActif(0), manche(1), pointLancer(0), pointJoueur(0)
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
    nbJoueur = joueurList.size() - 2;
    for(int i = 1; i < joueurList.size() ; i++)
    {
        Joueur player(joueurList.at(i), modeJeu.toInt(), 3);
        joueurs.push_back(player);
    }
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
    /**
     * @todo Définir les constantes Double et Triple
     */
    if(cercle == 4)
    {
        pointLancer = point * 3;
    }
    else if(cercle == 6)
    {
        pointLancer = point * 2;
    }
    else
    {
        pointLancer = point;
    }

    emit nouvelleImpact(cercle, point, pointLancer);

    enleverPointImpact();
    gererManche();

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
         joueurActif++;
    }

    qDebug() << Q_FUNC_INFO << joueurs[joueurActif].getNom() << "  : " << joueurs[joueurActif].getScore();
    joueurs[joueurActif].setNbFlechette(joueurs[joueurActif].getFlechette() - 1);
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
        joueurs[joueurActif].setScoreManchePrecedente(joueurs[joueurActif].getScore());

        if(joueurActif == nbJoueur)
        {
            joueurActif = 0;
            setManche(getManche() + 1);
            emit nouvelleManche();
        }
        else
        {
            joueurActif++;
        }
    }
}
