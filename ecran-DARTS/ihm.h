#ifndef IHM_H
#define IHM_H

#include "communication.h"
#include <QWidget>
#include <QTimer>

#define PERIODE_HORLOGE     1000 // en ms


namespace Ui {
class Ihm;
}

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

private:
    Ui::Ihm *ui;
    QTimer *timerHorloge;
    Communication *communication;

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
