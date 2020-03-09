#ifndef IHM_H
#define IHM_H

/**
 * @file ihm.h
 * @brief Déclaration de la classe Ihm
 *
 * @version 0.1
 *
 * @author Bounoir Fabien
 */

#include "communication.h"
#include "darts.h"
#include <QWidget>
#include <QTimer>

/**
 * @def PERIODE_HORLOGE
 * @brief Définit la périodicité de l'horloge en millisecondes
 */
#define PERIODE_HORLOGE     1000

namespace Ui {
class Ihm;
}

/**
 * @class Ihm
 * @brief Déclaration de la classe Ihm
 * @details Cette classe s'occupe de l'affichage sur l'écran
*/
class Ihm : public QWidget
{
    Q_OBJECT

public:
    explicit Ihm(QWidget *parent = nullptr);
    ~Ihm();

private:
    Ui::Ihm *ui;                    //!< object de notre Ihm
    QTimer *timerHorloge;           //!< objet timerHorloge
    Communication *communication;   //!< objet communication
    Darts *darts;                   //!< objet darts
    int compteurDureePartie;        //!< compteur de secondes pour la durée d'une séance

    /**
     * @enum Page
     * @brief Définit les différentes pages de l'IHM
     *     
     */
    enum Page
    {
        PageAttente = 0,
        PageJeu,
        PageStatistique,
        NbPages
    };

    void attribuerRaccourcisClavier();
    void initialiserEvenements();
    void initialiserHorloge();

public slots:
    void actualiserHeure();
    void allerPage(Ihm::Page page);
    void allerPagePrecedente();
    void allerPageSuivante();
    void fermerApplication();
    void afficherAttenteConfiguration();
    void afficherAttenteConnexion();
    void afficherImpact(int cercle, int point);
    void afficherPartie();
    void mettreAJourScore();
    void mettreAJourManche();
    void mettreAJourMoyenneVolee();
    void afficherVoleeAnnulee();
    void afficherNouvellePartie();
    void finirPartie(QString gagnant, int voleeMaxJoueur);
    void mettreAJourJoueur();
    void afficherDureePartie();
    void mettreAJoursolution(QString solution);
    void mettrePausePartie();
    void relancerpartie();
    void mettreAJourCible();
};

#endif // IHM_H
