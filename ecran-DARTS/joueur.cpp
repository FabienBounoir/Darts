#include "joueur.h"

#include <QtDebug>

/**
* @file joueur.cpp
*
* @brief joueur s'occupe du stockage du nom score et nombre de flechette du joueur
*
* @author Bounoir Fabien
*
* @version 0.1
*
*/

/**
 * @brief constructeur de la classe Joueur
 *
 * @fn Joueur::Joueur
 * @param nom
 * @param score
 * @param nbFlechette
 */
Joueur::Joueur(QString nom, int score , int nbFlechette): nom(nom), score(score), scoreManchePrecedente(score), nbFlechette(nbFlechette)
{
    qDebug() << Q_FUNC_INFO << nom << " " << score << " " << nbFlechette;
}

/**
 * @brief retourne le nom du joueur
 *
 * @fn Joueur::getNom
 * @return QString
 */
QString Joueur::getNom() const
{
    return this->nom;
}

/**
 * @brief retourne le score du joueur
 *
 * @fn Joueur::getScore
 * @return int
 */
int Joueur::getScore() const
{
    return this->score;
}

/**
 * @brief retourne le score de la manche precedente
 *
 * @fn Joueur::getScoreManchePrecedente
 * @return int
 */
int Joueur::getScoreManchePrecedente() const
{
    return this->scoreManchePrecedente;
}

/**
 * @brief retourne le nombre de flechette du joueur
 *
 * @fn Joueur::getFlechette
 * @return int
 */
int Joueur::getFlechette() const
{
    return this->nbFlechette;
}


/**
 * @brief permet de mettre à jour le score du joueur
 *
 * @fn Joueur::setScore
 * @param score
 */
void Joueur::setScore(int score)
{
    this->score = score;
}

/**
 * @brief permet de mettre à jour le score de la manche precedente
 *
 * @fn Joueur::setScoreManchePrecedente
 * @param scoreManchePrecedente
 */
void Joueur::setScoreManchePrecedente(int scoreManchePrecedente)
{
    this->scoreManchePrecedente = scoreManchePrecedente;
}

/**
 * @brief permet de mettre à jour le nombre de flechette du joueur
 *
 * @fn Joueur::setNbFlechette
 * @param nbFlechette
 */
void Joueur::setNbFlechette(int nbFlechette)
{
    this->nbFlechette = nbFlechette;
}
