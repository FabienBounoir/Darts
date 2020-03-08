#ifndef DARTS_H
#define DARTS_H

/**
 * @file darts.h
 * @brief Déclaration de la classe Darts
 *
 * @version 0.1
 *
 * @author Bounoir Fabien
 */

#include "joueur.h"
#include "solution.h"
#include <QObject>
#include <QVector>

#define TRIPLE_POINT 3
#define DOUBLE_POINT 2
#define SIMPLE_POINT 1

class Joueur;
class Solution;

/**
 * @class Darts
 * @brief Déclaration de la classe Darts
 * @details Cette classe s'occuper du déroulement d'une partie
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
    Solution *getSolution() const;
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
    void nouvelImpact(int,int,int);
    void voleeAnnulee();
    void finPartie(QString, int);
    void etatPartieFini();
    void changementJoueurActif();
    void miseAJourMoyenneVolee();
    void nouvellePartie();      //!< signal émis quand il y a une nouvelle partie

public slots:

private:
    Solution *solution;         //!< Association vers l'objet solution
    QList<Joueur> joueurs;      //!< contient des objets joueurs
    QStringList joueur;         //!< contient les noms des differents joueur
    int nbJoueur;               //!< contient le nombre de joueur
    int joueurActif;            //!< contient le numero du joueur en train de jouer
    int manche;                 //!< contient le numero de la manche actuel
    int pointLancer;            //!< contient les point associer l'impact de la fleche
    int voleeMax;               //!< contient la volées Max
    int nbVolees;               //!< contient le nombre de Volées de la partie en cours
    QString ModeDeJeu;          //!< contient le mode de jeu en cours

    void enleverPointImpact();
    void gererManche();
    void gererVoleeMax();
    QString calculerGagnant();
    void testerImpact(int cercle);
    void testerSiJoueurEliminer();
    void controlerJoueurEliminer();
};

#endif // DARTS_H
