#include "ihm.h"
#include "ui_ihm.h"

#include <QTime>
#include <QAction>
#include <QFileInfo>

/**
* @file ihm.cpp
*
* @brief Définition de la classe Ihm
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
    ui(new Ui::Ihm),
    compteurDureePartie(0)
{
    ui->setupUi(this);
    qDebug() << Q_FUNC_INFO;

    allerPage(Ihm::PageAttente);

    darts = new Darts(this);
    communication = new Communication(darts, this);

    // Horloge
    initialiserHorloge();

    // Raccourcis Quitter/ChangerPage
    attribuerRaccourcisClavier();

    // Démarrer la communication bluetooth
    communication->demarrer();

    initialiserEvenements();

    afficherNouvellePartie();
}

/**
 * @brief destructeur de la classe Ihm
 *
 * @fn Ihm::~Ihm
 */
Ihm::~Ihm()
{
    delete ui;
    qDebug() << Q_FUNC_INFO;
}

/**
 * @brief Méthode qui initialise les connexion signals/slots de Qt
 *
 * @fn Ihm::initialiserEvenements
 */
void Ihm::initialiserEvenements()
{
    connect(communication, SIGNAL(appareilConnecter()) , this, SLOT(afficherAttenteConfiguration()));
    connect(communication, SIGNAL(afficherAttenteConnexion()), this, SLOT(afficherAttenteConnexion()));
    connect(communication->getDarts(), SIGNAL(afficherNouvellePartie()), this, SLOT(afficherPartie()));
    connect(communication, SIGNAL(resetPartie()), this, SLOT(afficherNouvellePartie()));
    connect(communication->getDarts(), SIGNAL(finPartie(QString, int)), this, SLOT(finirPartie(QString, int)));
    connect(communication->getDarts(), SIGNAL(changementJoueurActif()), this, SLOT(mettreAJourJoueur()));
    connect(communication->getDarts(), SIGNAL(nouvelImpact(int, int, int)), this, SLOT(afficherImpact(int,int,int)));
    connect(communication->getDarts(), SIGNAL(miseAJourPoint()), this, SLOT(mettreAJourScore()));
    connect(communication->getDarts(), SIGNAL(nouvelleManche()), this, SLOT(mettreAJourManche()));
    connect(communication->getDarts(), SIGNAL(voleeAnnulee()), this, SLOT(afficherVoleeAnnulee()));
    connect(communication->getDarts(), SIGNAL(miseAJourMoyenneVolee()), this, SLOT(mettreAJourMoyenneVolee()));
    connect(communication->getDarts()->getSolution(), SIGNAL(solutionTrouver(QString)), this, SLOT(mettreAJoursolution(QString)));
    connect(communication, SIGNAL(pause()), this, SLOT(mettrePausePartie()));
    connect(communication, SIGNAL(play()), this, SLOT(relancerpartie()));
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
    ui->labelHeureStatistique->setText(affichageHeure);
}

/**
 * @brief Méthode qui met à jour le score dans L'ihm
 *
 * @fn Ihm::mettreAJourScore
 */
void Ihm::mettreAJourScore()
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
 * @brief Méthode qui affiche la cible correspondante à l'impact (si fichier Image Impact et disponible) et les points de cette Impact
 *
 * @fn Ihm::afficherImpact
 * @param typePoint
 * @param point
 * @param score
 */
void Ihm::afficherImpact(int typePoint, int point, int score)
{
    /** @todo ajouter la superposition des images pour avoir la volées compléte afficher **/

    qDebug() << Q_FUNC_INFO << "Type Point : " << typePoint << "point : " << point;
    if(QFileInfo("../ecran-DARTS/impact/IMPACT_" + QString::number(typePoint) + "_" + QString::number(point) + ".png").exists())       //test si l'image existe
    {
        ui->labelVisualisationimpact->setPixmap(QPixmap("../ecran-DARTS/impact/IMPACT_" + QString::number(typePoint) + "_" + QString::number(point) + ".png"));
    }
    ui->labelStatut->setText(QString::number(score) + " point(s)" );
}

/**
 * @brief Méthode qui initialise l'affichage du mode et des joueurs de la partie
 *
 * @fn Ihm::mettreAJourJoueur
 */
void Ihm::mettreAJourJoueur()
{
    QString nomjoueur;
    for(int i = 0; i < communication->getDarts()->getListJoueur().size(); i++)
    {
        if(i == communication->getDarts()->getJoueurActif())
        {
            nomjoueur += "         " + communication->getDarts()->getListJoueur()[i].getNom() + " <--" + "\n";
        }
        else
        {
            nomjoueur += "         " + communication->getDarts()->getListJoueur()[i].getNom() + "\n";
        }
    }
    ui->nomJoueur->setText(nomjoueur);
}

/**
 * @brief Méthode qui initialise l'affichage du mode et des joueurs de la partie
 *
 * @fn Ihm::mettreAJourMoyenneVolee
 */
void Ihm::mettreAJourMoyenneVolee()
{
    QString moyenneVoleeJoueur;
    for(int i = 0; i < communication->getDarts()->getListJoueur().size(); i++)
    {
        moyenneVoleeJoueur += "         " + communication->getDarts()->getListJoueur()[i].getNom() + " : " + QString::number(communication->getDarts()->getListJoueur()[i].getMoyenneVolee()) +" \n";
        /** @bug fixer warning  */
    }
    ui->labelMoyenneVolees->setVisible(true);
    ui->moyenneVolee->setText(moyenneVoleeJoueur);
    ui->labelMoyenneVoleesStatistique->setVisible(true);
    ui->moyenneVolees->setText(moyenneVoleeJoueur);
}

/**
 * @brief Méthode qui met a jour le mode et jeu et la page actif
 *
 * @fn Ihm::afficherPartie
 */
void Ihm::afficherPartie()
{
    ui->typeJeu->setText(communication->getDarts()->getModeDeJeu());

    mettreAJourJoueur();
    compteurDureePartie = 0;
    connect(timerHorloge, SIGNAL(timeout()),this,SLOT(afficherDureePartie())); // Pour le comptage et l'affichage de la durée d'une séance

    ui->ecranDarts->setCurrentIndex(Ihm::PageJeu);

    mettreAJourScore();
}

/**
 * @brief Méthode qui affiche le message de statut "volée annulée"
 *
 * @fn Ihm::afficherVoleeAnnulee
 */
void Ihm::afficherVoleeAnnulee()
{
    ui->labelStatut->setText("Volée annulée !");
}

/**
 * @brief Méthode qui met à jour l'affichage quand la partie est fini
 *
 * @fn Ihm::finirPartie
 * @param gagnant
 * @param voleeMaxJoueur
 */
void Ihm::finirPartie(QString gagnant, int voleeMaxJoueur)
{
    disconnect(timerHorloge, SIGNAL(timeout()),this,SLOT(afficherDureePartie())); // Pour le comptage et l'affichage de la durée d'une séance
    ui->winnerPartie->setText("Winner " + gagnant);
    ui->voleeMax->setText(QString::number(voleeMaxJoueur) + " points");
    ui->nbVolees->setText(QString::number(communication->getDarts()->getNbVolees()));
    ui->ecranDarts->setCurrentIndex(Ihm::PageStatistique);
}

/**
 * @brief Methode qui met à jour l'affichage pour lancer une nouvelle partie
 *
 * @fn Ihm::afficherNouvellePartie
 */
void Ihm::afficherNouvellePartie()
{
     ui->ecranDarts->setCurrentIndex(Ihm::PageAttente);
     ui->manche->setText("1");
     ui->nomJoueur->setText("");
     ui->scoreActuel->setText("");
     ui->typeJeu->setText("");
     ui->winnerPartie->setText("Winner ....");
     ui->labelVisualisationimpact->setPixmap(QPixmap("../ecran-DARTS/ressources/cible.png"));
     ui->labelStatut->setText("");
     ui->moyenneVolee->setText("");
     ui->nbVolees->setText("");
     ui->voleeMax->setText("");
     ui->moyenneVolees->setText("");
     ui->labelMoyenneVolees->setVisible(false);
     ui->labelMoyenneVoleesStatistique->setVisible(false);
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
 * @fn Ihm::allerPage
 * @partam page la page du QStackedWidget à afficher
 */
void Ihm::allerPage(Ihm::Page page)
{
    ui->ecranDarts->setCurrentIndex(page);
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
 * @fn Ihm::afficherAttenteConfiguration
 */
void Ihm::afficherAttenteConfiguration()
{
    ui->labelStatutAttente->setText("Attente configuration de la partie");
}

/**
 * @brief Méthode qui permet de mettre a jour le message de status "appareil deconnecté"
 *
 * @fn Ihm::afficherAttenteConnexion
 */
void Ihm::afficherAttenteConnexion()
{
    ui->labelStatutAttente->setText("En attente de connexion ");
}

/**
 * @brief Affiche la durée d'une Seance(slot)
 *
 * @fn Ihm::afficherDureePartie
 *
 */
void Ihm::afficherDureePartie()
{
    QTime duree(0, 0);
    compteurDureePartie++;
    QTime dureeSeance = duree.addSecs(compteurDureePartie);
    if(compteurDureePartie >= 3600)
    {
        ui->labelTempsPartie->setText(dureeSeance.toString("hh : mm : ss"));
        ui->tempsPartie->setText(dureeSeance.toString("hh : mm : ss"));
    }
    else
    {
        ui->labelTempsPartie->setText(dureeSeance.toString("mm : ss"));
        ui->tempsPartie->setText(dureeSeance.toString("mm : ss"));
    }
}

/**
 * @brief Affiche les solutions possibles pour finir la parties
 *
 * @fn Ihm::mettreAJoursolution
 */
void Ihm::mettreAJoursolution(QString solution)
{
    ui->labelStatut->setText(solution);
}

/**
 * @brief met en pause le chronometrage de la partie
 *
 * @fn Ihm::mettrePausePartie
 */
void Ihm::mettrePausePartie()
{
    disconnect(timerHorloge, SIGNAL(timeout()),this,SLOT(afficherDureePartie())); // mettre en pause le chronometrage de la partie
    ui->labelTempsPartie->setStyleSheet("color: rgb(179, 0,5);");
    qDebug() << "Partie en pause" << endl;
}

/**
 * @brief relancer le chronometrage de la partie
 *
 * @fn Ihm::relancerpartie
 */
void Ihm::relancerpartie()
{
    ui->labelTempsPartie->setStyleSheet("color: rgb(109, 43,107);");
    connect(timerHorloge, SIGNAL(timeout()),this,SLOT(afficherDureePartie())); // relancer le chronometrage de la partie
    qDebug() << "Partie relancer" << endl;
}

/**
 * @brief initialise l'horloge pour un affichage périodique
 *
 * @fn Ihm::initialiserHorloge
 */
void Ihm::initialiserHorloge()
{
    timerHorloge = new QTimer(this); // Instancie dynamiquement le temporisateur du rafraichissement de l'heure
    connect(timerHorloge, SIGNAL(timeout()),this,SLOT(actualiserHeure())); // Pour le déclenchement périodique de l'affichage de l'heure
    timerHorloge->start(PERIODE_HORLOGE);  // Toutes les secondes (1000 ms)
}
