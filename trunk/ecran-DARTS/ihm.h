#ifndef IHM_H
#define IHM_H

#include "communication.h"
#include "darts.h"
#include <QWidget>
#include <QTimer>

#define PERIODE_HORLOGE     1000 // en ms


namespace Ui {
class Ihm;
}

/**
 * @file ihm.h
 * @brief Declaration de la classe Ihm
 *
 * @version 0.1
 *
 * @author Bounoir Fabien
 */

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
    void afficherPartie(QString mode, QStringList joueur);
    void miseAJourScore();
    void mettreAJourManche();
    void AfficherVoleeAnnulee();

private:
    Ui::Ihm *ui; /**< object de notre Ihm */
    QTimer *timerHorloge; /**< objet timerHorloge */
    Communication *communication; /**< objet communication */
    Darts *darts; /**< objet darts */

    /**
     * @brief contient les differentes pages de L'application
     *
     * @enum Page
     */
    enum Page
    {
        PageAttente = 0,
        PageJeu,
        PageStatistique,
        NbPages
    };

    void attribuerRaccourcisClavier();
};

#endif // IHM_H
