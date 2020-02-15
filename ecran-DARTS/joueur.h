#ifndef JOUEUR_H
#define JOUEUR_H

#include <QObject>

/**
 * @file joueur.h
 * @brief Declaration de la classe Joueur
 *
 * @version 0.1
 *
 * @author Bounoir Fabien
 */

/**
 * @class Joueur joueur.h "joueur.h"
 * @brief Declaration de la classe Joueur
*/
class Joueur
{
public:
    Joueur(QString nom, int score , int nbFlechette);

    QString getNom() const;
    int getScore() const;
    int getScoreManchePrecedente() const;
    int getFlechette() const;
    void setScore(int score);
    void setScoreManchePrecedente(int scoreManchePrecedente);
    void setNbFlechette(int nbFlechette);

signals:

public slots:

private:
    QString nom; /**< contient le nom du joueur */
    int score; /**< contient le score du joueur */
    int scoreManchePrecedente; /**< contient le score de la manche precedente */
    int nbFlechette; /**< contient le nombre de flechette restante au joueur */
};

#endif // JOUEUR_H
