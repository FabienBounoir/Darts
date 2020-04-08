#include "ihm.h"
#include "ui_ihm.h"

#include <QTime>
#include <QAction>
#include <QFileInfo>
#include <QPainter>

/**
* @file ihm.cpp
*
* @brief Définition de la classe Ihm (Module Ecran-DARTS)
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
    musique(qApp->applicationDirPath() + CHEMIN_FICHIER_MUSIQUE + "music.wav",this),
    musiquePause(qApp->applicationDirPath() + CHEMIN_FICHIER_MUSIQUE + "pause.wav",this),
    compteurDureePartie(0),
    messageStatut("Volée ➤")
{
    ui->setupUi(this);
    qDebug() << Q_FUNC_INFO;

    darts = new Darts(this);
    communication = new Communication(darts, this);

    // Initialiser l'horloge
    initialiserHorloge();

    // Raccourcis Quitter/ChangerPage (mode debug)
    attribuerRaccourcisClavier();

    // Démarrer la communication bluetooth
    communication->demarrer();

    // Initialiser les connexions signal/slot
    initialiserEvenements();

    // Afficher la page d'attente
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
    connect(darts, SIGNAL(afficherNouvellePartie()), this, SLOT(afficherPartie()));
    connect(communication, SIGNAL(resetPartie()), this, SLOT(afficherNouvellePartie()));
    connect(darts, SIGNAL(finPartie(QString, int)), this, SLOT(finirPartie(QString, int)));
    connect(darts, SIGNAL(changementJoueurActif()), this, SLOT(mettreAJourJoueur()));
    connect(darts, SIGNAL(nouvelImpact(int, int, int)), this, SLOT(afficherImpact(int,int)));
    connect(darts, SIGNAL(miseAJourPoint()), this, SLOT(mettreAJourScore()));
    connect(darts, SIGNAL(nouvelleManche()), this, SLOT(mettreAJourManche()));
    connect(darts, SIGNAL(voleeAnnulee()), this, SLOT(afficherVoleeAnnulee()));
    connect(darts, SIGNAL(miseAJourMoyenneVolee()), this, SLOT(mettreAJourMoyenneVolee()));
    connect(darts->getSolution(), SIGNAL(solutionTrouver(QString)), this, SLOT(mettreAJoursolution(QString)));
    connect(darts, SIGNAL(actualiserCible()), this, SLOT(mettreAJourCible()));
    connect(communication, SIGNAL(pause()), this, SLOT(mettrePausePartie()));
    connect(communication, SIGNAL(play()), this, SLOT(relancerpartie()));
    connect(communication, SIGNAL(erreurBluetooth(QString)), this, SLOT(mettreAJourMessageStatut(QString)));
    connect(darts, SIGNAL(jouerSon(QString)), this, SLOT(jouerSon(QString)));
}

/**
 * @brief Méthode qui initialise les raccourcis clavier
 *
 * @fn Ihm::attribuerRaccourcisClavier
 */
void Ihm::attribuerRaccourcisClavier()
{
    QAction *quitter = new QAction(this);
    quitter->setShortcut(QKeySequence(QKeySequence(Qt::Key_Up)));       //fleche du haut pour quitter l'application
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

    for(int i = 0; i < darts->getListJoueur().size(); i++)
    {
        score += darts->getListJoueur()[i].getNom() + " : " + QString::number(darts->getListJoueur()[i].getScore()) + "\n"; // "         " +
    }
    //ui->nomJoueur->setStyleSheet()
    ui->scoreActuel->setText(score);
}

/**
 * @brief Méthode qui met à jour le numero de la manche
 *
 * @fn Ihm::mettreAJourManche
 */
void Ihm::mettreAJourManche()
{
    ui->manche->setText(QString::number(darts->getManche()));
}

/**
 * @brief Méthode qui affiche la cible correspondante à l'impact (si fichier Image Impact et disponible) et les points de cette Impact
 *
 * @fn Ihm::afficherImpact
 * @param typePoint
 * @param point
 */
void Ihm::afficherImpact(int typePoint, int point)
{
    if(QFileInfo(qApp->applicationDirPath() + "/impact/IMPACT_" + QString::number(typePoint) + "_" + QString::number(point) + ".png").exists())       //test si l'image existe
    {
        QImage impact(qApp->applicationDirPath() + "/impact/IMPACT_" + QString::number(typePoint) + "_" + QString::number(point) + ".png");

        QPixmap cibleImpacte = ui->labelVisualisationimpact->pixmap()->copy(); // on récupère l'image précédente;

        QPainter p(&cibleImpacte);

        p.drawImage(QPoint(0, 0), impact);

        p.end();

        ui->labelVisualisationimpact->setPixmap(cibleImpacte);
    }

    mettreAJourMessageStatut(typePoint, point);
}

/**
 * @brief   methode qui met à jour le message de statut de la Volée en cour
 *
 * @fn Ihm::mettreAJourMessageStatut(int typePoint, int point)
 * @param typePoint
 * @param point
 */
void Ihm::mettreAJourMessageStatut(int typePoint, int point)
{

    switch(typePoint){
        case TRIPLE_POINT:
            messageStatut += " T" + QString::number(point);
        break;
        case DOUBLE_POINT:
            messageStatut += " D" + QString::number(point);
        break;
        default:
            messageStatut += " " + QString::number(point);
        break;
    }
    ui->labelStatut->setStyleSheet("color: rgb(109, 43,107);");
    ui->labelStatut->setText(messageStatut);
}


/**
 * @brief Méthode qui initialise l'affichage du mode et des joueurs de la partie
 *
 * @fn Ihm::mettreAJourJoueur
 */
void Ihm::mettreAJourJoueur()
{
    QString nomjoueur;
    for(int i = 0; i < darts->getListJoueur().size(); i++)
    {
        if(i == darts->getJoueurActif())    // test si le joueur est le joueur qui doit jouer
        {
            nomjoueur += "   ⟼ " + darts->getListJoueur()[i].getNom() + "\n";  //joueur joue
        }
        else
        {
            nomjoueur += "            " + darts->getListJoueur()[i].getNom() + "\n";       //joueur en attente de son tour //
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
    for(int i = 0; i < darts->getListJoueur().size(); i++)
    {
        moyenneVoleeJoueur += darts->getListJoueur()[i].getNom() + " : " + QString::number(darts->getListJoueur()[i].getMoyenneVolee()) +" \n"; //"         " +
    }
    ui->labelMoyenneVolees->setVisible(true);
    ui->lineScoreActuel->setVisible(true);
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
    musique.stop();

    ui->typeJeu->setText(darts->getModeDeJeu());

    mettreAJourJoueur();
    compteurDureePartie = 0;
    connect(timerHorloge, SIGNAL(timeout()),this,SLOT(afficherDureePartie())); // Pour le comptage et l'affichage de la durée d'une séance

    allerPage(Ihm::PageJeu);

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
    musique.play();

    disconnect(timerHorloge, SIGNAL(timeout()), this, SLOT(afficherDureePartie())); // Pour le comptage et l'affichage de la durée d'une séance
    ui->winnerPartie->setText(gagnant);
    ui->voleeMax->setText(QString::number(voleeMaxJoueur) + " points");
    ui->nbVolees->setText(QString::number(darts->getNbVolees()));
    allerPage(Ihm::PageStatistique);
}

/**
 * @brief Methode qui met à jour l'affichage pour lancer une nouvelle partie
 *
 * @fn Ihm::afficherNouvellePartie
 */
void Ihm::afficherNouvellePartie()
{
     allerPage(Ihm::PageAttente);
     ui->manche->setText("1");
     ui->nomJoueur->setText("");
     ui->scoreActuel->setText("");
     ui->typeJeu->setText("");
     ui->winnerPartie->setText("Winner ....");
     ui->labelStatut->setText("");
     ui->moyenneVolee->setText("");
     ui->nbVolees->setText("");
     ui->voleeMax->setText("");
     ui->moyenneVolees->setText("");
     ui->labelMoyenneVolees->setVisible(false);
     ui->lineScoreActuel->setVisible(false);
     ui->labelMoyenneVoleesStatistique->setVisible(false);

     mettreAJourCible();

     // configurer la musique
     musique.setLoops(QSound::Infinite);
     musiquePause.setLoops(QSound::Infinite);
     musiquePause.stop();
     // jouer la musique
     musique.play();
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
 * @param page la page du QStackedWidget à afficher
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
    ui->labelStatutAttente->setText("En attente de connexion");
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
    ui->labelStatut->setStyleSheet("color: rgb(179, 0,5);");
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
    sauvegardeImpactEncours = ui->labelVisualisationimpact->pixmap()->copy();
    QImage pause(":pause.png");
    QPixmap cibleImpacte = ui->labelVisualisationimpact->pixmap()->copy(); // on récupère l'image précédente;
    QPainter p(&cibleImpacte);
    p.drawImage(QPoint(0, 0), pause);
    p.end();
    ui->labelVisualisationimpact->setPixmap(cibleImpacte);
    ui->labelTempsPartie->setStyleSheet("color: rgb(179, 0,5);");

    musiquePause.play();
}

/**
 * @brief relancer le chronometrage de la partie
 *
 * @fn Ihm::relancerpartie
 */
void Ihm::relancerpartie()
{
    ui->labelVisualisationimpact->setPixmap(sauvegardeImpactEncours);
    ui->labelTempsPartie->setStyleSheet("color: rgb(109, 43,107);");
    connect(timerHorloge, SIGNAL(timeout()),this,SLOT(afficherDureePartie())); // relancer le chronometrage de la partie
    qDebug() << "Partie relancer" << endl;
    musiquePause.stop();
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

/**
 * @brief methode qui reinitialise l'affichage de la cible
 *
 * @fn Ihm::mettreAJourCible
 */
void Ihm::mettreAJourCible()
{
    ui->labelVisualisationimpact->setPixmap(QPixmap(":/ressources/cible.png"));
    messageStatut = "Volée ➤";
}

/**
 * @brief methode qui met à jour le message de statut
 *
 * @fn Ihm::mettreAJourMessageStatut(QString statut)
 * @param statut
 */
void Ihm::mettreAJourMessageStatut(QString statut)
{
    ui->labelStatutAttente->setText(statut);
}


/**
 * @brief Methode qui permet de jouer un son
 *
 * @fn Ihm::jouerSon
 * @param son
 */
void Ihm::jouerSon(QString son)
{
    QSound::play(qApp->applicationDirPath() + CHEMIN_FICHIER_MUSIQUE + son);
    if(!QFileInfo(qApp->applicationDirPath() + CHEMIN_FICHIER_MUSIQUE + son).exists())
    {
        qDebug() << Q_FUNC_INFO << "Pour avoir les sons, ajouter le pack disponible à cette adresse :"<<endl;
        qDebug() << Q_FUNC_INFO << "https://drive.google.com/file/d/1vlS_oySnAM7ocsf9FyaZ1P4JyHIQrLiR/view?usp=sharing"<<endl;
    }
}