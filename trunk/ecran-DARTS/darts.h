#ifndef DARTS_H
#define DARTS_H

#include "joueur.h"
#include <QObject>
#include <QVector>

class Joueur;

/**
 * @file darts.h
 * @brief Declaration de la classe Darts
 *
 * @version 0.1
 *
 * @author Bounoir Fabien
 */

/**
 * @class Darts darts.h "darts.h"
 * @brief Declaration de la classe Darts
*/
class Darts : public QObject
{
    Q_OBJECT
public:
    explicit Darts(QObject *parent = nullptr);
    ~Darts();

    QList<Joueur> getListJoueur() const;
    int getManche() const;
    Darts *getDarts();
    void setManche(int manche);

    void receptionnerImpact(int cercle, int point);
    void initialiserPartie(QStringList joueur, QString modeJeu);

signals:
    void miseAJourPoint();
    void nouvelleManche();
    void nouvelleImpact(int,int,int);
    void voleeAnnulee();

public slots:

private:
    QList<Joueur> joueurs; /**< TODO: describe */
    QStringList joueur; /**< TODO: describe */
    int nbJoueur; /**< TODO: describe */
    int joueurActif; /**< TODO: describe */
    int manche; /**< TODO: describe */
    int pointLancer; /**< TODO: describe */
    QVector<int> pointJoueur; //!< Un vecteur qui contient les points des differents joueurs /**< TODO: describe */
    void enleverPointImpact();
    void gererManche();
};

#endif // DARTS_H
