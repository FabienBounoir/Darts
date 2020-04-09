 #include "darts.h"

#include <QDebug>

/**
* @file darts.cpp
*
* @brief Définition de la classe Darts (Module Ecran-DARTS)
*
* @author Bounoir Fabien
*
* @version 0.2
*
*/

/**
 * @brief Constructeur de la classe Darts
 *
 * @fn Darts::Darts
 * @param parent
 */
Darts::Darts(QObject *parent) : QObject(parent), joueur(nullptr), nbJoueur(0), joueurActif(0), manche(1), pointLancer(0), voleeMax(0), nbVolees(0), ModeDeJeu("")
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
int Darts::getVoleeMax()
{
    return voleeMax;
}

/**
 * @brief Retourne le numéro du joueur actif
 *
 * @fn Darts::getJoueurActif
 * @return int le numéro du joueur actif
 */
int Darts::getJoueurActif()
{
    return joueurActif;
}

/**
 * @brief Retourne le nombre de volées de la partie
 *
 * @fn Darts::getNbVolees
 * @return int le nombre de volées de la partie
 */
int Darts::getNbVolees()
{
    return nbVolees;
}

/**
 * @brief Retourne le mode de jeu
 *
 * @fn Darts::getModeDeJeu
 * @return QString le mode de jeu
 */
QString Darts::getModeDeJeu()
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
            Joueur player(joueurList.at(i), ModeDeJeu.toInt(), 3);
            joueurs.push_back(player);
        }
        emit afficherNouvellePartie();
        emit changerEtatPartie();
    }
    else if(ModeDeJeu.contains("_DOUBLE_OUT") && (ModeDeJeu.section("_",0,0).toInt() >= 101 && ModeDeJeu.section("_",0,0).toInt() <= 1001))
    {
        for(int i = 1; i < joueurList.size() ; i++)
        {
            Joueur player(joueurList.at(i), ModeDeJeu.section("_",0,0).toInt(), 3);
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
    ModeDeJeu = "";
    nbJoueur = 0;
    joueurActif = 0;
    manche = 1;
    pointLancer = 0;
    nbVolees = 0;
    voleeMax = 0;
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
    else if(typePoint == ZERO_POINT && point == 0)
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

    if(joueurs[joueurActif].getFlechette() == 3)
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
        emit finPartie("↢  Winner " + joueurs[joueurActif].getNom() + "  ↣", getVoleeMax());
        emit etatPartieFini();
    }
    else if(joueurs[joueurActif].getScore()  == 0 && !ModeDeJeu.contains("_DOUBLE_OUT"))    //fin sans double
    {
        gererVoleeMax();
        nbVolees++;
        emit finPartie("↢  Winner " + joueurs[joueurActif].getNom() + "  ↣" , getVoleeMax());
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
        emit finPartie("↢  " + joueurs[joueurActif].getNom() + " a perdu  ↣", getVoleeMax());
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
        joueurs[joueurActif].setNbFlechette(3);

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
    emit finPartie(calculerGagnant(), getVoleeMax());
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
