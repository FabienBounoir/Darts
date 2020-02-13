#include "ihm.h"
#include "ui_ihm.h"

#include <QTime>
#include <QAction>

/**
* @file ihm.cpp
*
* @brief classe qui s'occupe de l'affichage dans l'ihm
*
* @author Bounoir Fabien
*
* @version 0.1
*
*/

/**
 * @brief constructeur de la classe Ihm
 *
 * @fn Ihm::Ihm
 * @param parent
 */
Ihm::Ihm(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::Ihm)
{
    ui->setupUi(this);

    communication = new Communication(this);

    //Timer
    timerHorloge = new QTimer(this); // Instancie dynamiquement le temporisateur du rafraichissement de l'heure
    connect(timerHorloge, SIGNAL(timeout()),this,SLOT(actualiserHeure())); // Pour le déclenchement périodique de l'affichage de l'heure
    timerHorloge->start(PERIODE_HORLOGE); // Toutes les secondes (1000 ms)

    //raccourcis Quitter/ChangerPage
    attribuerRaccourcisClavier();

    communication->demarrer();

    connect(communication, SIGNAL(appareilConnecter()) ,this ,SLOT(nouvelleAppareilConnecter()));
    connect(communication, SIGNAL(appareilDeconnecter() ),this ,SLOT(appareilDeconnecter()));
    connect(communication, SIGNAL(nouveauImpact(QString,QString)) ,this ,SLOT(afficherImpact(QString,QString)));

}

/**
 * @brief destructeur de la classe Ihm
 *
 * @fn Ihm::~Ihm
 */
Ihm::~Ihm()
{
    delete ui;
}

/**
 * @brief Méthode qui initialise les raccourcis clavier
 *
 * @fn Ihm::attribuerRaccourcisClavier
 */
void Ihm::attribuerRaccourcisClavier()
{
    QAction *quitter = new QAction(this);
    //quitter->setShortcut(QKeySequence(QKeySequence::Quit));
    quitter->setShortcut(QKeySequence(QKeySequence(Qt::Key_Up)));
    addAction(quitter);
    connect(quitter, SIGNAL(triggered()), this, SLOT(fermerApplication()));   // Pour fermer l'application

#ifndef QT_NO_DEBUG_OUTPUT
    QAction *actionAllerDroite = new QAction(this);
    actionAllerDroite->setShortcut(QKeySequence(Qt::Key_Right));
    addAction(actionAllerDroite);
    connect(actionAllerDroite, SIGNAL(triggered()), this, SLOT(allerPageSuivante()));// Pour passer à l'écran suivant
    QAction *actionAllerGauche = new QAction(this);
    actionAllerGauche->setShortcut(QKeySequence(Qt::Key_Left));
    addAction(actionAllerGauche);
    connect(actionAllerGauche, SIGNAL(triggered()), this, SLOT(allerPagePrecedente()));// Pour revenir à l'écran précédent
#endif
}

/**
 * @brief Méthode qui met a jour l'heure sur l'application
 *
 * @fn Ihm::actualiserHeure
 */
void Ihm::actualiserHeure()
{
    QString affichageHeure;
    QTime heure = QTime::currentTime();
    affichageHeure = "<font color=\"#6D2B6B\">" + heure.toString("hh : mm ") + "</font>";
    ui->labelHeureAttente->setText(affichageHeure);
    ui->labelHeurePartie->setText(affichageHeure);
    ui->labelHeureStatistique->setText(affichageHeure);
}

void Ihm::afficherImpact(QString cercle, QString point)
{
    qDebug() <<"cercle : " << cercle <<"point : " << point <<endl;
    ui->labelVisualisationimpact->setPixmap(QPixmap("../ecran-DARTS/impact/IMPACT_" + cercle + "_" + point + ".png"));
    ui->labelStatut->setText("Impact sur le cercle " + cercle + " à l'emplacement " + point );
}

/**
 * @brief Méthode qui permet de changer de QStackedWidget avec la suivante
 *
 * @fn Ihm::allerPageSuivante
 */
void Ihm::allerPageSuivante()
{
    int ecranCourant = Page(ui->ecranDarts->currentIndex());
    int ecranSuivant = (ecranCourant+1)%int(Ihm::NbPages);
    ui->ecranDarts->setCurrentIndex(ecranSuivant);
}

/**
 * @brief Méthode qui permet de changer de QStackedWidget avec la Précédente
 *
 * @fn Ihm::allerPagePrecedente
 */
void Ihm::allerPagePrecedente()
{
    int ecranCourant = ui->ecranDarts->currentIndex();
    int ecranPrecedent = (ecranCourant-1)%int(Ihm::NbPages);
    if(ecranPrecedent == -1)
        ecranPrecedent = NbPages-1;
    ui->ecranDarts->setCurrentIndex(ecranPrecedent);
}

/**
 * @brief Méthode qui permet de quitter l'application
 *
 * @fn Ihm::fermerApplication
 */
void Ihm::fermerApplication()
{
    this->close();
}

/**
 * @brief Méthode qui permet de mettre a jour le message de status "nouvelle appareil connecté"
 *
 * @fn Ihm::nouvelleAppareilConnecter
 */
void Ihm::nouvelleAppareilConnecter()
{
    ui->labelStatutAttente->setText("Attente configuration de la partie");
}

/**
 * @brief Méthode qui permet de mettre a jour le message de status "appareil deconnecté"
 *
 * @fn Ihm::appareilDeconnecter
 */
void Ihm::appareilDeconnecter()
{
    ui->labelStatutAttente->setText("En attente de connexion ");
}
