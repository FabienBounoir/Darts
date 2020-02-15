#ifndef DARTS_H
#define DARTS_H

/**
 * @file darts.h
 * @brief Declaration de la classe Darts
 *
 * @version 0.1
 *
 * @author Bounoir Fabien
 */

#include "joueur.h"
#include <QObject>
#include <QVector>

class Joueur;

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
    QList<Joueur> joueurs;      //!< @todo describe
    QStringList joueur;         //!< @todo describe
    int nbJoueur;               //!< @todo describe
    int joueurActif;            //!< @todo describe
    int manche;                 //!< @todo describe
    int pointLancer;            //!< @todo describe
    QVector<int> pointJoueur;   //!< Vecteur qui contient les points des differents joueurs

    void enleverPointImpact();  //!< @todo describe
    void gererManche();         //!< @todo describe
};

#endif // DARTS_H
