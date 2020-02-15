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

#define TRIPLE_POINT 4
#define DOUBLE_POINT 6

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
    int getVoleeMax();
    void setVoleeMax(int voleeMax);
    void setManche(int manche);

    void receptionnerImpact(int cercle, int point);
    void initialiserPartie(QStringList joueur, QString modeJeu);
    void reinitialiserPartie();

signals:
    void miseAJourPoint();
    void nouvelleManche();
    void nouvelleImpact(int,int,int);
    void voleeAnnulee();
    void finPartie(QString, int);
    void etatPartieFini();

public slots:

private:
    QList<Joueur> joueurs;      //!< contient des objets joueurs
    QStringList joueur;         //!< contient les noms des differents joueur
    int nbJoueur;               //!< contient le nombre de joueur
    int joueurActif;            //!< contient le numero du joueur en train de jouer
    int manche;                 //!< contient le numero de la manche actuel
    int pointLancer;            //!< contient les point associer l'impact de la fleche
    int voleeMax;               //!< contient la volées Max

    void enleverPointImpact();
    void gererManche();
    void gererVoleeMax();
};

#endif // DARTS_H
