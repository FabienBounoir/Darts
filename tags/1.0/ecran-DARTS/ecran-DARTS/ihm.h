#ifndef IHM_H
#define IHM_H

/**
 * @file ihm.h
 * @brief Déclaration de la classe Ihm (Module Ecran-DARTS)
 *
 * @version 1.0
 *
 * @author Bounoir Fabien
 */

#include "communication.h"
#include "darts.h"
#include <QWidget>
#include <QTimer>
#include <QSound>

#include <QtMultimedia>
#include <QtMultimediaWidgets>

/**
 * @def PERIODE_HORLOGE
 * @brief Définit la périodicité de l'horloge en millisecondes
 */
#define PERIODE_HORLOGE     1000

/**
 * @def CHEMIN_FICHIER_MUSIQUE
 * @brief Définit le chemin pour les sons
 */
#define CHEMIN_FICHIER_MUSIQUE "/son/"

namespace Ui {
class Ihm;
}

/**
 * @class Ihm
 * @brief Déclaration de la classe Ihm (Module Ecran-DARTS)
 * @details Cette classe s'occupe de l'affichage sur l'écran
*/
class Ihm : public QWidget
{
    Q_OBJECT

public:
    explicit Ihm(QWidget *parent = nullptr);
    ~Ihm();

private:
    Ui::Ihm *ui;                        //!< object de notre Ihm
    QTimer *timerHorloge;               //!< objet timerHorloge
    Communication *communication;       //!< objet communication
    Darts *darts;                       //!< objet darts
    QSound musique;                     //!< objet musique
    QSound musiquePause;                //!< objet musiquePause
    int compteurDureePartie;            //!< compteur de secondes pour la durée d'une séance
    int sauverEtatPartie;               //!< Contient l'état de la partie avant l'affichage des règles
    QPixmap sauvegardeImpactEncours;    //!< sauvegarde le QPixmap de l'état de la cible
    QString messageStatut;              //!< contient le message de statut qui est affiché

    //objet pour l'affichage video
    QMediaPlayer *player;               //!< objet player
    QVideoWidget *videoWidget;          //!< objet videoWidget

    /**
     * @enum Page
     * @brief Définit les différentes pages de l'IHM
     *     
     */
    enum Page
    {
        PageAttente = 0,
        PageRegle,
        PageJeu,
        PageStatistique,
        PageTournois,
        PageFinTournois,
        NbPages
    };

    void attribuerRaccourcisClavier();
    void initialiserEvenements();
    void initialiserHorloge();
    void mettreAJourMessageStatut(int typePoint, int point);
    void initialiserAffichageRegle();
    void testerEtatPartie();

public slots:
    void actualiserHeure();
    void allerPage(Ihm::Page page);
    void allerPagePrecedente();
    void allerPageSuivante();
    void fermerApplication();
    void afficherAttenteConfiguration();
    void afficherAttenteConnexion();
    void afficherPretLancerTournois();
    void afficherImpact(int typePoint, int point);
    void afficherPartie();
    void mettreAJourScore();
    void mettreAJourManche();
    void mettreAJourMoyenneVolee();
    void mettreAJourMoyenneVoleeTournois();
    void afficherVoleeAnnulee();
    void afficherNouvellePartie();
    void finirPartie(QString gagnant, int voleeMaxJoueur, bool tournois);
    void mettreAJourJoueur();
    void afficherDureePartie();
    void mettreAJoursolution(QString solution);
    void mettrePausePartie();
    void relancerpartie();
    void mettreAJourCible();
    void mettreAJourMessageStatut(QString);
    void jouerSon(QString son);
    void lancerRegle(QString regle);
    void stateChanged(QMediaPlayer::State state);
    void error(QMediaPlayer::Error error);
    void StopperLectureRegle();
    void initialiserAffichageTournois(QString modeJeu, QString nomTournois);
    void lancerTournois();
    void mettreAJourScoreTournois();
    void mettreAJourJoueurTournoi();
    void afficherFinTournois(QString nomGagnant, QString nomTournois, QList<Joueur> joueurs);
    void afficherInformationTournois();
};

#endif // IHM_H
