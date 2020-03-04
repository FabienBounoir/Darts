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
#include "solution.h"
#include <QObject>
#include <QVector>

#define TRIPLE_POINT 4
#define DOUBLE_POINT 6

class Joueur;
class Solution;

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
    int getJoueurActif();
    int getNbVolees();
    QString getModeDeJeu();
    void setVoleeMax(int voleeMax);
    void setManche(int manche);
    void calculerMoyenneVolees();
    void receptionnerImpact(int cercle, int point);
    void initialiserPartie(QStringList joueur, QString modeJeu);
    void reinitialiserPartie();
    void arreterPartie();

signals:
    void miseAJourPoint();
    void nouvelleManche();
    void nouvelleImpact(int,int,int);
    void voleeAnnulee();
    void finPartie(QString, int);
    void etatPartieFini();
    void changerJoueurActif();
    void miseAJourMoyenneVolee();
    void nouvellePartie();      //!< signal emit quand il y a une nouvelle partie

public slots:

private:
    Solution *solution;
    QList<Joueur> joueurs;      //!< contient des objets joueurs
    QStringList joueur;         //!< contient les noms des differents joueur
    int nbJoueur;               //!< contient le nombre de joueur
    int joueurActif;            //!< contient le numero du joueur en train de jouer
    int manche;                 //!< contient le numero de la manche actuel
    int pointLancer;            //!< contient les point associer l'impact de la fleche
    int voleeMax;               //!< contient la volées Max
    int nbVolees;               //!< contient le nombre de Volées de la partie en cours
    QString ModeDeJeu;        //!< contient le mode de jeu en cours

    void enleverPointImpact();
    void gererManche();
    void gererVoleeMax();
    QString calculerGagnant();
    void testerImpact(int cercle);
};

#endif // DARTS_H
