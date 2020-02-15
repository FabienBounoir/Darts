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
    ui->ecranDarts->setCurrentIndex(Ihm::PageAttente);

    communication = new Communication(this);
    darts = new Darts(this);

    //Timer
    timerHorloge = new QTimer(this); // Instancie dynamiquement le temporisateur du rafraichissement de l'heure
    connect(timerHorloge, SIGNAL(timeout()),this,SLOT(actualiserHeure())); // Pour le déclenchement périodique de l'affichage de l'heure
    timerHorloge->start(PERIODE_HORLOGE); // Toutes les secondes (1000 ms)

    //raccourcis Quitter/ChangerPage
    attribuerRaccourcisClavier();

    communication->demarrer();

    initialiserConnect();

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
 * @brief Méthode qui initialise les connects
 *
 * @fn Ihm::initialiserConnect
 */
void Ihm::initialiserConnect()
{
    connect(communication, SIGNAL(appareilConnecter()) ,this ,SLOT(nouvelleAppareilConnecter()));
    connect(communication, SIGNAL(appareilDeconnecter() ),this ,SLOT(appareilDeconnecter()));
    connect(communication, SIGNAL(nouvellePartie(QString, QStringList)),this ,SLOT(afficherPartie(QString,QStringList)));
    connect(communication, SIGNAL(resetPartie()),this ,SLOT(nouvellePartie()));
    connect(communication->getDarts(), SIGNAL(finPartie(QString, int)),this ,SLOT(partieFini(QString, int)));
    connect(communication->getDarts(), SIGNAL(nouvelleImpact(int, int, int)) ,this ,SLOT(afficherImpact(int,int,int)));
    connect(communication->getDarts(), SIGNAL(miseAJourPoint()), this , SLOT(miseAJourScore()));
    connect(communication->getDarts(), SIGNAL(nouvelleManche()), this , SLOT(mettreAJourManche()));
    connect(communication->getDarts(), SIGNAL(voleeAnnulee()), this , SLOT(AfficherVoleeAnnulee()));
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

/**
 * @brief Méthode qui met à jour le score dans L'ihm
 *
 * @fn Ihm::miseAJourScore
 */
void Ihm::miseAJourScore()
{
    QString score;

    for(int i = 0; i < communication->getDarts()->getListJoueur().size(); i++)
    {
        score += "         " + communication->getDarts()->getListJoueur()[i].getNom() + " : " + QString::number(communication->getDarts()->getListJoueur()[i].getScore()) + "\n";
    }
    ui->scoreActuel->setText(score);
}

/**
 * @brief Méthode qui met à jour le numero de la manche
 *
 * @fn Ihm::mettreAJourManche
 */
void Ihm::mettreAJourManche()
{
    ui->manche->setText(QString::number(communication->getDarts()->getManche()));
}

/**
 * @brief Méthode qui affiche la cible correspondante à l'impact et les points de cette Impact
 *
 * @fn Ihm::afficherImpact
 * @param cercle
 * @param point
 * @param score
 */
void Ihm::afficherImpact(int cercle, int point, int score)
{
    qDebug() << Q_FUNC_INFO << "cercle : " << cercle << "point : " << point;
    ui->labelVisualisationimpact->setPixmap(QPixmap("../ecran-DARTS/impact/IMPACT_" + QString::number(cercle) + "_" + QString::number(point) + ".png"));
    ui->labelStatut->setText(QString::number(score) + " point(s)" );
}

/**
 * @brief Méthode qui initialise l'affichage du mode et des joueurs de la partie
 *
 * @fn Ihm::afficherPartie
 * @param mode
 * @param joueur
 */
void Ihm::afficherPartie(QString mode, QStringList joueur)
{
    qDebug() << Q_FUNC_INFO << "mode de jeu : " << mode << "  Joueur : " << joueur;
    ui->typeJeu->setText(mode + " Double out");
    QString nomjoueur;
    for(int i = 1; i < joueur.size(); i++)
    {
        nomjoueur += "         " +joueur.at(i) + "\n";
    }
    ui->nomJoueur->setText(nomjoueur);
    ui->ecranDarts->setCurrentIndex(Ihm::PageJeu);

    miseAJourScore();
}

/**
 * @brief Méthode qui affiche le message de statut "volée annulée"
 *
 * @fn Ihm::AfficherVoleeAnnulee
 */
void Ihm::AfficherVoleeAnnulee()
{
    ui->labelStatut->setText("Volée annulée !");
}

/**
 * @brief Méthode qui met à jour l'affichage quand la partie est fini
 *
 * @fn Ihm::partieFini
 * @param gagnant
 * @param voleeMaxJoueur
 */
void Ihm::partieFini(QString gagnant, int voleeMaxJoueur)
{
    ui->winnerPartie->setText("Winner " + gagnant);
    ui->voleeMax->setText(QString::number(voleeMaxJoueur) + " points");
    ui->ecranDarts->setCurrentIndex(Ihm::PageStatistique);
}

/**
 * @brief Methode qui met à jour l'affichage pour lancer une nouvelle partie
 *
 * @fn Ihm::nouvellePartie
 */
void Ihm::nouvellePartie()
{
     ui->ecranDarts->setCurrentIndex(Ihm::PageAttente);
     ui->manche->setText("1");
     ui->nomJoueur->setText("");
     ui->scoreActuel->setText("");
     ui->typeJeu->setText("");
     ui->winnerPartie->setText("Winner ....");
     ui->labelVisualisationimpact->setPixmap(QPixmap("../ecran-DARTS/ressources/cible.png"));
     ui->labelStatut->setText("");
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
