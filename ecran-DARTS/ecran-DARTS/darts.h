#ifndef DARTS_H
#define DARTS_H

/**
 * @file darts.h
 * @brief Déclaration de la classe Darts (Module Ecran-DARTS)
 *
 * @version 1.0
 *
 * @author Bounoir Fabien
 */

#include "joueur.h"
#include "solution.h"
#include <QObject>
#include <QVector>

#define TRIPLE_POINT        3
#define DOUBLE_POINT        2
#define SIMPLE_POINT        1
#define ZERO_POINT          0
#define BULL                25
#define NB_FLECHETTE        3

class Joueur;
class Solution;

/**
 * @class Darts
 * @brief Déclaration de la classe Darts (Module Ecran-DARTS)
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
    Darts *getDarts() const;
    int getVoleeMax() const;
    int getJoueurActif() const;
    int getNbVolees() const;
    int getPointVolees() const;
    int getPremierJoueur() const;
    int getDernierJoueur() const;
    QString getModeDeJeu() const;
    QString testerModeDeJeu();
    Solution *getSolution() const;
    void setVoleeMax(int voleeMax);
    void setManche(int manche);
    void receptionnerImpact(int typePoint, int point);
    void initialiserPartie(QStringList joueurList, QString modeJeu);
    void reinitialiserPartie();
    void arreterPartie();
    void configurationTournois(QStringList joueurList, QString modeJeu, QString nomTournois);
    void demarrerTournois();
    void receptionnerImpactTournois(int typePoint, int point);

signals:
    void miseAJourPoint();                  //!< signal émis pour mettre à jour les points des joueurs
    void nouvelleManche();                  //!< signal émis quand on change de manche
    void nouvelImpact(int,int,int);         //!< signal émis quand il y a un nouvel Impact
    void voleeAnnulee();                    //!< signal émis quand la volées est annulé
    void finPartie(QString, int, bool);           //!< signal émis quand c'est la fin de la partie
    void etatPartieFini();                  //!< signal émis pour mettre l'etat de la partie en fin
    void changementJoueurActif();           //!< signal émis quand le joueur actif change
    void miseAJourMoyenneVolee();           //!< signal émis pour mettre à jour la moyenne des volées
    void miseAJourMoyenneVoleeTournois();   //!< signal émis pour mettre a jour la moyenne des Volées en mode tournois
    void afficherNouvellePartie();          //!< signal émis quand il y a une nouvelle partie
    void changerEtatPartie();               //!< signal émis pour changer l'etat de la partie
    void actualiserCible();                  //!< signal émis pour changer actualiser l'affichage de la cible
    void jouerSon(QString son);             //!< signal émis pour Lancer un son
    void afficherTournois(QString modeJeu, QString nomTournois);    //!< signal émis pour afficher le tournois
    void debuterTournois();                                         //!< signal émis pour debuter le tournois
    void etatPartieTournois();                                      //!< signal émis pour mettre en etat de tournois
    void miseAJourPointTournois();                                  //!< signal émis pour mettre a jour les points du tournois
    void etatPartieAttenteTournois();
    void changementJoueurActifTournoi();
    void finTournois(QString, QString, QList<Joueur>);
    void afficherInfoTournois();

public slots:

private:
    Solution *solution;             //!< Association vers l'objet solution
    QList<Joueur> joueurs;          //!< contient des objets joueurs
    QList<Joueur> joueursTournoisEliminer;          //!< contient des objets joueurs eliminer pendant le tournois
    QStringList joueur;             //!< contient les noms des differents joueur
    int nbJoueur;                   //!< contient le nombre de joueur
    int joueurActif;                //!< contient le numero du joueur en train de jouer
    int manche;                     //!< contient le numero de la manche actuel
    int pointLancer;                //!< contient les point associer l'impact de la fleche
    int voleeMax;                   //!< contient la volées Max
    int nbVolees;                   //!< contient le nombre de Volées de la partie en cours
    QString ModeDeJeu;              //!< contient le mode de jeu en cours
    QString NomTournois;            //!< contient le nom du tournois
    int pointVoleeEnCours;          //!< contient le score de la Volées en cours
    int premierJoueur;              //!< contient le premier joueur du tournois a jouer
    int dernierJoueur;              //!< contient le dernier joueur du tournois a jouer

    void enleverPointImpact();
    void gererManche();
    void gererMancheTournois();
    void gererVoleeMax();
    QString calculerGagnant();
    void testerImpact(int typePoint);
    void testerImpactTournois(int typePoint);
    void testerSiJoueurEliminer();
    void controlerJoueurEliminer();
    void calculerMoyenneVolees();
    void calculerMoyenneVoleesTournois();
    void testerNombreJoueurRestand();
    void testerPoint(int typePoint, int point);
    void calculerPoints(int point, int typePoint);
    void gererFinPartieTournois();
    bool estDernier();
    void initialiserFinTournois();
};

#endif // DARTS_H
