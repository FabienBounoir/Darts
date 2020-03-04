#ifndef IHM_H
#define IHM_H

/**
 * @file ihm.h
 * @brief Declaration de la classe Ihm
 *
 * @version 0.1
 *
 * @author Bounoir Fabien
 */

#include "communication.h"
#include "darts.h"
#include <QWidget>
#include <QTimer>

#define PERIODE_HORLOGE     1000 // en ms


namespace Ui {
class Ihm;
}

/**
 * @class Ihm ihm.h "ihm.h"
 * @brief Declaration de la classe Ihm
*/
class Ihm : public QWidget
{
    Q_OBJECT

public:

    explicit Ihm(QWidget *parent = nullptr);
    ~Ihm();

public slots:

    void actualiserHeure();
    void allerPagePrecedente();
    void allerPageSuivante();
    void fermerApplication();
    void nouvelleAppareilConnecter();
    void appareilDeconnecter();
    void afficherImpact(int cercle, int point, int score);
    void afficherPartie();
    void miseAJourScore();
    void mettreAJourManche();
    void mettreAJourMoyenneVolee();
    void AfficherVoleeAnnulee();
    void nouvellePartie();
    void partieFini(QString gagnant, int voleeMaxJoueur);
    void mettreAJourJoueur();
    void afficherDureePartie();
    
private:
    Ui::Ihm *ui; /**< object de notre Ihm */
    QTimer *timerHorloge; /**< objet timerHorloge */
    Communication *communication; /**< objet communication */
    Darts *darts; /**< objet darts */
    int compteurDureePartie; //!< compteur de secondes pour la duréee d'une séance

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
    void initialiserConnect();
};

#endif // IHM_H
