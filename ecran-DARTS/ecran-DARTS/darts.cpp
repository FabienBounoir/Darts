 #include "darts.h"

#include <QDebug>

/**
* @file darts.cpp
*
* @brief Définition de la classe Darts (Module Ecran-DARTS)
*
* @author Bounoir Fabien
*
* @version 1.0
*
*/

/**
 * @brief Constructeur de la classe Darts
 *
 * @fn Darts::Darts
 * @param parent
 */
Darts::Darts(QObject *parent) : QObject(parent), joueur(nullptr), nbJoueur(0), joueurActif(0), manche(1), pointLancer(0), voleeMax(0), nbVolees(0), ModeDeJeu(""),pointVoleeEnCours(0)
{
    qDebug() << Q_FUNC_INFO;
    solution = new Solution(this);
}

/**
 * @brief Destructeur de la classe Darts
 *
 * @fn Darts::~Darts
 */
Darts::~Darts()
{
    qDebug() << Q_FUNC_INFO;
}

/**
 * @brief Retourne la manche
 *
 * @fn Darts::getManche
 * @return int le numéro de manche
 */
int Darts::getManche() const
{
    return manche;
}

/**
 * @brief Retourne la manche
 *
 * @fn Darts::getPointVolees
 * @return int le nombre de point de la manche en cours
 */
int Darts::getPointVolees() const
{
    return pointVoleeEnCours;
}

/**
 * @brief Retourne une liste des joueurs
 *
 * @fn Darts::getListJoueur
 * @return QList<Joueur> la liste des joueurs
 */
QList<Joueur> Darts::getListJoueur() const
{
    return joueurs;
}

/**
 * @brief Retourne la volée max
 *
 * @fn Darts::getVoleeMax
 * @return int la volée max
 */
int Darts::getVoleeMax() const
{
    return voleeMax;
}

/**
 * @brief Retourne le numéro du joueur actif
 *
 * @fn Darts::getJoueurActif
 * @return int le numéro du joueur actif
 */
int Darts::getJoueurActif() const
{
    return joueurActif;
}

/**
 * @brief Retourne le nombre de volées de la partie
 *
 * @fn Darts::getNbVolees
 * @return int le nombre de volées de la partie
 */
int Darts::getNbVolees() const
{
    return nbVolees;
}

/**
 * @brief Retourne le premier Joueur a jouer
 *
 * @fn Darts::getPremierJoueur
 * @return int le premier Joueur a jouer
 */
int Darts::getPremierJoueur() const
{
    return premierJoueur;
}

/**
 * @brief Retourne le dernier joueur a jouer
 *
 * @fn Darts::getDernierJoueur
 * @return int le dernier joueur a jouer
 */
int Darts::getDernierJoueur() const
{
    return dernierJoueur;
}

/**
 * @brief Retourne le mode de jeu
 *
 * @fn Darts::getModeDeJeu
 * @return QString le mode de jeu
 */
QString Darts::getModeDeJeu() const
{
    return ModeDeJeu;
}

/**
 * @brief retourne l'objet solution
 *
 * @fn Darts::getSolution
 * @return Solution* l'objet solution
 */
Solution *Darts::getSolution() const
{
    return solution;
}

/**
 * @brief Permets de mettre à jour le volé max
 *
 * @fn Darts::setVoleeMax
 * @param voleeMax la volée max(int)
 */
void Darts::setVoleeMax(int voleeMax)
{
    this->voleeMax = voleeMax;
}

/**
 * @brief Permets de mettre à jour le numéro de manche
 *
 * @fn Darts::setManche
 * @param manche le numéro de manche (int)
 */
void Darts::setManche(int manche)
{
    this->manche = manche;
}

/**
 * @brief Initialise la partie
 *
 * @fn Darts::initialiserPartie
 * @param joueurList QStringList
 * @param modeJeu QString
 */
void Darts::initialiserPartie(QStringList joueurList, QString modeJeu)
{
    nbJoueur = joueurList.size() - 1;
    ModeDeJeu = modeJeu;
    qDebug() << Q_FUNC_INFO << "nbJoueur " << nbJoueur << " | Mode De Jeu " << ModeDeJeu;

    if(ModeDeJeu.toInt() >= 101 && ModeDeJeu.toInt() <= 1001)
    {
        for(int i = 1; i < joueurList.size() ; i++)
        {
            Joueur player(joueurList.at(i), ModeDeJeu.toInt(), NB_FLECHETTE);
            joueurs.push_back(player);
        }
        emit afficherNouvellePartie();
        emit changerEtatPartie();
    }
    else if(ModeDeJeu.contains("_DOUBLE_OUT") && (ModeDeJeu.section("_",0,0).toInt() >= 101 && ModeDeJeu.section("_",0,0).toInt() <= 1001))
    {
        for(int i = 1; i < joueurList.size() ; i++)
        {
            Joueur player(joueurList.at(i), ModeDeJeu.section("_",0,0).toInt(), NB_FLECHETTE);
            joueurs.push_back(player);
        }
        emit afficherNouvellePartie();
        emit changerEtatPartie();
    }
    else
    {
        qDebug() << Q_FUNC_INFO << "Erreur Mode De Jeu : " << ModeDeJeu;
        reinitialiserPartie();
        return;
    }
    solution->trouverSolution(joueurs[joueurActif].getScore(),joueurs[joueurActif].getFlechette());
}

/**
 * @brief Méthode qui permet de remettre a zero les differents attribut et conteneur pour une nouvelle partie
 *
 * @fn Darts::reinitialiserPartie
 */
void Darts::reinitialiserPartie()
{
    joueurs.clear();
    joueur.clear();
    joueursTournoisEliminer.clear();
    premierJoueur = 0;
    dernierJoueur = 1;

    ModeDeJeu = "";
    nbJoueur = 0;
    joueurActif = 0;
    manche = 1;
    pointLancer = 0;
    nbVolees = 0;
    voleeMax = 0;
    pointVoleeEnCours = 0;
}

/**
 * @brief Méthode qui teste les Impact pour savoir quel son jouer
 *
 * @fn Darts::testerPoint
 * @param typePoint
 * @param point
 */
void Darts::testerPoint(int typePoint, int point)
{
    if(typePoint == DOUBLE_POINT && point == BULL)
    {
        jouerSon("D25.wav");
    }
    else if(typePoint == ZERO_POINT)
    {
        jouerSon("out.wav");
    }
}

/**
 * @brief Permets de traiter la réception d'impact
 *
 * @fn Darts::receptionnerImpact
 * @param typePoint
 * @param point
 */
void Darts::receptionnerImpact(int typePoint, int point)
{
    calculerPoints(point, typePoint);

    testerPoint(typePoint, point);

    pointVoleeEnCours += pointLancer;

    if(joueurs[joueurActif].getFlechette() == NB_FLECHETTE)
        emit actualiserCible();

    qDebug() << Q_FUNC_INFO << joueurs[joueurActif].getNom() << " SCORE : " <<joueurs[joueurActif].getScore() - pointLancer << endl;

    emit nouvelImpact(typePoint, point, pointLancer);
    joueurs[joueurActif].setScore(joueurs[joueurActif].getScore() - pointLancer);
    testerImpact(typePoint);
    emit miseAJourPoint();
}

/**
 * @brief Teste si le joueur a gagné
 *
 * @fn Darts::testerImpact
 * @param typePoint
 */
void Darts::testerImpact(int typePoint)
{
    if(joueurs[joueurActif].getScore()  == 0 && (typePoint == DOUBLE_POINT) && (ModeDeJeu.contains("_DOUBLE_OUT"))) //fin avec double
    {
        gererVoleeMax();
        nbVolees++;
        emit finPartie("↢  Winner " + joueurs[joueurActif].getNom() + "  ↣", getVoleeMax(), false);
        emit etatPartieFini();
    }
    else if(joueurs[joueurActif].getScore()  == 0 && !ModeDeJeu.contains("_DOUBLE_OUT"))    //fin sans double
    {
        gererVoleeMax();
        nbVolees++;
        emit finPartie("↢  Winner " + joueurs[joueurActif].getNom() + "  ↣" , getVoleeMax(), false);
        emit etatPartieFini();
    }
    else
    {
        enleverPointImpact();
        gererManche();
    }
}

/**
 * @brief Met à jours le score du joueur
 *
 * @fn Darts::enleverPointImpact
 */
void Darts::enleverPointImpact()
{
    if(joueurs[joueurActif].getScore() <= 0) //score Volée inferieure au score = Volée annulée
    {
        joueurs[joueurActif].setScore(joueurs[joueurActif].getScoreManchePrecedente());
        emit voleeAnnulee();

        joueurs[joueurActif].setNbFlechette(0);
    }
    else if(joueurs[joueurActif].getScore() == 1 && joueurs.size() == 1)    // test si le joueur est seul à jouer et s'il est a 1 point == joueur éliminer donc fin de partie
    {
        gererVoleeMax();
        emit jouerSon("perdu.wav");
        emit finPartie("↢  " + joueurs[joueurActif].getNom() + " a perdu  ↣", getVoleeMax(), false);
        emit etatPartieFini();
    }
    else
    {
        nbVolees++;
        joueurs[joueurActif].setNbFlechette(joueurs[joueurActif].getFlechette() - 1);
        solution->trouverSolution(joueurs[joueurActif].getScore(),joueurs[joueurActif].getFlechette());
    }
}

/**
 * @brief Permets de gérer le changement de manche en fonction des fléchettes de chaque joueur
 *
 * @fn Darts::gererManche
 */
void Darts::gererManche()
{
    if(joueurs[joueurActif].getFlechette() == 0) //fin de la volées du joueur
    {
        joueurs[joueurActif].setNbFlechette(NB_FLECHETTE);

        pointVoleeEnCours = 0;

        gererVoleeMax();
        testerSiJoueurEliminer();
        testerNombreJoueurRestand();

        joueurs[joueurActif].addHistoriqueVolees((joueurs[joueurActif].getScoreManchePrecedente() - joueurs[joueurActif].getScore()));

        if((joueurs[joueurActif].getScoreManchePrecedente() - joueurs[joueurActif].getScore()) == 180)
            emit jouerSon("180.wav");

        joueurs[joueurActif].setScoreManchePrecedente(joueurs[joueurActif].getScore());

        if(joueurActif == nbJoueur - 1)  //equivalent a la fin de manche
        {
            joueurActif = 0;

            controlerJoueurEliminer();

            setManche(getManche() + 1);
            emit changementJoueurActif();
            calculerMoyenneVolees();
            emit nouvelleManche();
            solution->trouverSolution(joueurs[joueurActif].getScore(),joueurs[joueurActif].getFlechette());
        }
        else
        {
            joueurActif++;

            controlerJoueurEliminer();

            emit changementJoueurActif();
            solution->trouverSolution(joueurs[joueurActif].getScore(),joueurs[joueurActif].getFlechette());
        }
    }
}

/**
 * @brief Change de joueur si le joueur actuel est éliminé
 *
 * @fn Darts::controlerJoueurEliminer
 */
void Darts::controlerJoueurEliminer()
{
    while(joueurs[joueurActif].getEliminer()) //tant que le joueur est eliminer on passe donc au joueur suivant
    {
        if(joueurActif == nbJoueur - 1)  //si le joueur est le dernier de la manche à jouer
        {
            joueurActif = 0;
            setManche(getManche() + 1);
            calculerMoyenneVolees();
            emit nouvelleManche();
        }
        else                            //si le joueur n'est pas le dernier de la manche
        {
            joueurActif++;
        }
    }
}

/**
 * @brief Teste si le joueur est à 1 point à la fin de la manche
 *
 * @fn Darts::testerSiJoueurEliminer
 */
void Darts::testerSiJoueurEliminer()
{
    if(joueurs[joueurActif].getScore() == 1)    //joueur eliminé si tomber à 1 point
    {
        joueurs[joueurActif].setEliminer(true);
    }
}

/**
 * @brief Calcule la moyenne des volées de chaque joueur
 *
 * @fn Darts::calculerMoyenneVolees
 */
void Darts::calculerMoyenneVolees()
{
    for(int i = 0; i <= joueurs.size() - 1; i++)
    {
        int moyenneVolee = 0;

        for(int j = 0; j < joueurs[i].getHistoriqueVolees().size(); j++)
        {
            moyenneVolee += joueurs[i].getHistoriqueVolees()[j];
        }

        moyenneVolee = moyenneVolee / joueurs[i].getHistoriqueVolees().size();
        joueurs[i].setMoyenneVolee(moyenneVolee);
    }
    emit miseAJourMoyenneVolee();
}

/**
 * @brief Teste la volée pour savoir si elle est supérieure à la volée Max
 *
 * @fn Darts::gererVoleeMax
 */
void Darts::gererVoleeMax()
{
    if((joueurs[joueurActif].getScoreManchePrecedente() - joueurs[joueurActif].getScore()) > getVoleeMax())
    {
        setVoleeMax(joueurs[joueurActif].getScoreManchePrecedente() - joueurs[joueurActif].getScore());
    }
}

/**
 * @brief arrête la partie
 *
 * @fn Darts::arreterPartie
 */
void Darts::arreterPartie()
{
    emit finPartie(calculerGagnant(), getVoleeMax(), false);
    emit etatPartieFini();
}

/**
 * @brief Calcule le gagnant de la partie si la partie doit s'arrêter avant la fin
 *
 * @fn Darts::calculerGagnant
 * @return QString
 */
QString Darts::calculerGagnant()
{
    QString gagnant;
    int scoreGagnant = 99999;
    for(int i = 0; i <= joueurs.size() - 1; i++)
    {
        if(scoreGagnant > joueurs[i].getScore() && !joueurs[i].getEliminer()) // test le personne aillant le score le plus bas score mais aussi qu'il ne soit pas eliminer
        {
            scoreGagnant = joueurs[i].getScore();
            gagnant = joueurs[i].getNom();
        }
    }
    return "↢  Winner " + gagnant + "  ↣";
}

/**
 * @brief Teste s'il reste qu'un joueur n'était pas éliminé
 *
 * @fn Darts::testerNombreJoueurRestand
 */
void Darts::testerNombreJoueurRestand()
{
    int eliminer = 0;

    for(int i = 0; i <= joueurs.size() - 1; i++)
    {
        if(joueurs[i].getEliminer())
        {
            eliminer++;
        }
    }

    if(eliminer == joueurs.size() - 1 && joueurs.size() != 1)
    {
        arreterPartie();
    }
}

/**
 * @brief Teste s'il reste qu'un joueur n'était pas éliminé
 *
 * @fn Darts::calculerPoints
 * @param point la zone
 * @param typePoint un simple, double ou triple
 */
void Darts::calculerPoints(int point, int typePoint)
{
    switch(typePoint)
    {
        case TRIPLE_POINT:
            pointLancer = point * TRIPLE_POINT;
        break;
        case DOUBLE_POINT:
            pointLancer = point * DOUBLE_POINT;
        break;
        case SIMPLE_POINT:
            pointLancer = point;
        break;
        case  ZERO_POINT:
            pointLancer = point * ZERO_POINT;
        break;
        default:
            qDebug() << Q_FUNC_INFO << "type de point non valide:" << typePoint;
        break;
    }
}

/**
 * @brief Méthode qui teste le mode de jeu
 *
 * @fn Darts::testerModeDeJeu
 * @return QString
 */
QString Darts::testerModeDeJeu()
{
    QString regle ="";

    if(getModeDeJeu().contains("DOUBLE_OUT"))
    {
        regle = "DOUBLE_OUT";
    }
    else
    {
        regle = "SANS_DOUBLE_OUT";
    }
    return regle;
}

/**
 * @brief methode utiliser pour configurer le tournois
 *
 * @fn Darts::configurationTournois
 * @param joueurList
 * @param modeJeu
 * @param nomTournois
 */
void Darts::configurationTournois(QStringList joueurList, QString modeJeu, QString nomTournois)
{
    nbJoueur = joueurList.size() - 1;
    ModeDeJeu = modeJeu;
    NomTournois = nomTournois;
    qDebug() << Q_FUNC_INFO << "Nom Tournois " << nomTournois << "nbJoueur " << nbJoueur << " | Mode De Jeu " << ModeDeJeu;

    if(ModeDeJeu.toInt() >= 101 && ModeDeJeu.toInt() <= 1001)
    {
        for(int i = 1; i < joueurList.size() ; i++)
        {
            Joueur player(joueurList.at(i), ModeDeJeu.toInt(), NB_FLECHETTE);
            joueurs.push_back(player);
        }
    }
    else if(ModeDeJeu.contains("_DOUBLE_OUT") && (ModeDeJeu.section("_",0,0).toInt() >= 101 && ModeDeJeu.section("_",0,0).toInt() <= 1001))
    {
        for(int i = 1; i < joueurList.size() ; i++)
        {
            Joueur player(joueurList.at(i), ModeDeJeu.section("_",0,0).toInt(), NB_FLECHETTE);
            joueurs.push_back(player);
        }
    }
    else
    {
        qDebug() << Q_FUNC_INFO << "Erreur Mode De Jeu : " << ModeDeJeu;
        reinitialiserPartie();
        return;
    }
    solution->trouverSolution(joueurs[joueurActif].getScore(),joueurs[joueurActif].getFlechette());

    premierJoueur = 0;
    dernierJoueur = 1;

    emit afficherTournois(modeJeu, nomTournois);
    emit etatPartieAttenteTournois();
    emit afficherInfoTournois();
}

/**
 * @brief methode appelé pour demarrer le tournois
 *
 * @fn Darts::demarrerTournois
 */
void Darts::demarrerTournois()
{
    emit actualiserCible();
    emit changementJoueurActifTournoi();
    emit debuterTournois();
    emit etatPartieTournois();  
}

/**
 * @brief methode qui gere le deroulement de la partie tournois
 *
 * @fn Darts::receptionnerImpactTournois
 * @param typePoint
 * @param point
 */
void Darts::receptionnerImpactTournois(int typePoint, int point)
{
    calculerPoints(point, typePoint);

    testerPoint(typePoint, point);

    pointVoleeEnCours += pointLancer;

    if(joueurs[joueurActif].getFlechette() == NB_FLECHETTE)
        emit actualiserCible();

    emit nouvelImpact(typePoint, point, pointLancer);

    joueurs[joueurActif].setScore(joueurs[joueurActif].getScore() - pointLancer);

    testerImpactTournois(typePoint);
    emit miseAJourPointTournois();
}

/**
 * @brief teste l'impact pour le mode tournois
 *
 * @fn Darts::testerImpactTournois
 * @param typePoint
 */
void Darts::testerImpactTournois(int typePoint)
{
    if(joueurs[joueurActif].getScore()  == 0 && (typePoint == DOUBLE_POINT) && (ModeDeJeu.contains("_DOUBLE_OUT"))) //fin avec double
    {
        gererVoleeMax();
        nbVolees++;
        emit afficherInfoTournois();
        emit finPartie("↢  " + joueurs[joueurActif].getNom() +" Winner de la manche" +"  ↣" , getVoleeMax(), true);
        gererFinPartieTournois();
        emit etatPartieAttenteTournois();
    }
    else if(joueurs[joueurActif].getScore()  == 0 && !ModeDeJeu.contains("_DOUBLE_OUT"))    //fin sans double
    {
        gererVoleeMax();
        nbVolees++;
        emit afficherInfoTournois();
        emit finPartie("↢  " + joueurs[joueurActif].getNom() +" Winner de la manche" +"  ↣" , getVoleeMax(), true);
        gererFinPartieTournois();
        emit etatPartieAttenteTournois();
    }
    else
    {
        enleverPointImpact();
        gererMancheTournois();
    }
}


/**
 * @brief Permets de gérer le changement de manche en fonction des fléchettes de chaque joueur
 *
 * @fn Darts::gererMancheTournois
 */
void Darts::gererMancheTournois()
{
    if(joueurs[joueurActif].getFlechette() == 0) //fin de la volées du joueur
    {
        joueurs[joueurActif].setNbFlechette(NB_FLECHETTE);

        pointVoleeEnCours = 0;

        gererVoleeMax();
        //testerSiJoueurEliminer(); /** @todo changer pour faire gagner l'autre joueur*/

        joueurs[joueurActif].addHistoriqueVolees((joueurs[joueurActif].getScoreManchePrecedente() - joueurs[joueurActif].getScore()));

        if((joueurs[joueurActif].getScoreManchePrecedente() - joueurs[joueurActif].getScore()) == 180)
            emit jouerSon("180.wav");

        joueurs[joueurActif].setScoreManchePrecedente(joueurs[joueurActif].getScore());

        if(joueurActif == dernierJoueur)  //equivalent a la fin de manche
        {
            joueurActif = premierJoueur;

            setManche(getManche() + 1);
            emit changementJoueurActifTournoi();
            calculerMoyenneVoleesTournois();
            emit nouvelleManche();
            solution->trouverSolution(joueurs[joueurActif].getScore(),joueurs[joueurActif].getFlechette());
        }
        else
        {
            joueurActif++;

            emit changementJoueurActifTournoi();
            solution->trouverSolution(joueurs[joueurActif].getScore(),joueurs[joueurActif].getFlechette());
        }
    }
}

/**
 * @brief Calcule la moyenne des volées de chaque joueur pour le tournois
 *
 * @fn Darts::calculerMoyenneVoleesTournois
 */
void Darts::calculerMoyenneVoleesTournois()
{
    for(int i = premierJoueur; i <= dernierJoueur; i++)
    {
        int moyenneVolee = 0;

        for(int j = 0; j < joueurs[i].getHistoriqueVolees().size(); j++)
        {
            moyenneVolee += joueurs[i].getHistoriqueVolees()[j];
        }

        moyenneVolee = moyenneVolee / joueurs[i].getHistoriqueVolees().size();
        joueurs[i].setMoyenneVolee(moyenneVolee);
    }
    emit miseAJourMoyenneVoleeTournois();
}

/**
 * @brief methode qui gere le deroulement en fin de manche
 *
 * @fn Darts::gererFinPartieTournois
 */
void Darts::gererFinPartieTournois()
{
    if(joueurActif == premierJoueur)
    {
        joueurs[premierJoueur].setEliminer(false);
        joueurs[dernierJoueur].setEliminer(true);
    }

    if(joueurActif == dernierJoueur)
    {
        joueurs[dernierJoueur].setEliminer(false);
        joueurs[premierJoueur].setEliminer(true);
    }

    if(premierJoueur + 2 < (joueurs.size() - 1)  && dernierJoueur + 2 <= (joueurs.size() - 1))
    {
        premierJoueur += 2;
        dernierJoueur += 2;
        nbVolees = 0;

        qDebug() << "premierJoueur " << premierJoueur << endl;
        qDebug() << "dernierJoueur " << dernierJoueur << endl;
    }
    else if(estDernier())
    {
        qDebug() << "Gagnant tournois " << joueurs[joueurActif].getNom() << endl;
        initialiserFinTournois();
        emit etatPartieFini();
    }
    else
    {
        for(int i = joueurs.size() - 1; i >= 0; i--)
        {
            if(joueurs[i].getEliminer())
            {
                joueursTournoisEliminer.push_back(joueurs[i]);
                joueurs.removeAt(i);
            }
        }
        premierJoueur = 0;
        dernierJoueur = 1;
        nbVolees = 0;

        for(int i = 0; i < joueurs.size(); i++)
        {

            joueurs[i].setScore(ModeDeJeu.section("_",0,0).toInt());
            joueurs[i].setMoyenneVolee(0);
            joueurs[i].setScoreManchePrecedente(ModeDeJeu.section("_",0,0).toInt());
            joueurs[i].setNbFlechette(NB_FLECHETTE);
        }
    }

    joueurActif = premierJoueur;
    setManche(1);

    emit afficherTournois(ModeDeJeu, NomTournois);

}

/**
 * @brief test pour savoir si la personne a gagner le tournois
 *
 * @fn Darts::estDernier
 * @return bool
 */
bool Darts::estDernier()
{
    int estdernier = 0;
    for(int i = 0; i < joueurs.size(); i++)
    {
        qDebug() << joueurs[i].getNom() << " : " << joueurs[i].getEliminer() << endl;
        if(joueurs[i].getEliminer() == false)
            estdernier++;
    }

    if(estdernier == 1)
        return true;

    return false;
}

/**
 * @brief methode qui gere la fin du tournois
 *
 * @fn Darts::initialiserFinTournois
 */
void Darts::initialiserFinTournois()
{
    QList<Joueur> joueurTournois;

    for(int i = 0 ; i < joueurs.size(); i++)
    {
        joueurTournois.push_back(joueurs[i]);
    }

    for(int i = 0 ; i < joueursTournoisEliminer.size(); i++)
    {
        joueurTournois.push_back(joueursTournoisEliminer[i]);
    }

    emit finTournois(joueurs[joueurActif].getNom(),NomTournois,joueurTournois);
}
