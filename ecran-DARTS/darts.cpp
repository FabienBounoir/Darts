 #include "darts.h"

#include <QDebug>


/**
* @file darts.cpp
*
* @brief Définition de la classe Darts
*
* @author Bounoir Fabien
*
* @version 0.1
*
*/

/**
 * @brief constructeur de la classe Darts
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
 * @brief destructeur de la classe Darts
 *
 * @fn Darts::~Darts
 */
Darts::~Darts()
{
    qDebug() << Q_FUNC_INFO;
}

/**
 * @brief retourne la manche
 *
 * @fn Darts::getManche
 * @return int le numéro de manche
 */
int Darts::getManche() const
{
    return manche;
}

/**
 * @brief retourne une liste des joueurs
 *
 * @fn Darts::getListJoueur
 * @return QList<Joueur> la liste des joueurs
 */
QList<Joueur> Darts::getListJoueur() const
{
    return joueurs;
}

/**
 * @brief retourne la volée max
 *
 * @fn Darts::getVoleeMax
 * @return int la volée max
 */
int Darts::getVoleeMax()
{
    return voleeMax;
}

/**
 * @brief retourne le numéro du joueur actif
 *
 * @fn Darts::getJoueurActif
 * @return int le numéro du joueur actif
 */
int Darts::getJoueurActif()
{
    return joueurActif;
}

/**
 * @brief retourne le nombre de volées de la partie
 *
 * @fn Darts::getNbVolees
 * @return int le nombre de volées de la partie
 */
int Darts::getNbVolees()
{
    return nbVolees;
}

/**
 * @brief retourne le mode de jeu
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
 * @brief permet de mettre à jour la volée max
 *
 * @fn Darts::setVoleeMax
 * @param voleeMax la volée max(int)
 */
void Darts::setVoleeMax(int voleeMax)
{
    this->voleeMax = voleeMax;
}

/**
 * @brief permet de mettre à jour le numéro de manche
 *
 * @fn Darts::setManche
 * @param manche le numéro de manche (int)
 */
void Darts::setManche(int manche)
{
    this->manche = manche;
}


/**
 * @brief initialise la partie
 *
 * @fn Darts::initialiserPartie
 * @param joueurList QStringList
 * @param modeJeu QString
 */
void Darts::initialiserPartie(QStringList joueurList, QString modeJeu)
{
    nbJoueur = joueurList.size() - 1;
    ModeDeJeu = modeJeu;
    qDebug() << Q_FUNC_INFO << "nbJoueur" << nbJoueur << "modeJeu" << modeJeu;

    if(ModeDeJeu == "501" || ModeDeJeu == "301")
    {
        for(int i = 1; i < joueurList.size() ; i++)
        {
            Joueur player(joueurList.at(i), modeJeu.toInt(), 3);
            joueurs.push_back(player);
        }
        emit afficherNouvellePartie();
        emit changerEtatPartie();
    }
    else if(ModeDeJeu == "501_DOUBLE_OUT")
    {
        for(int i = 1; i < joueurList.size() ; i++)
        {
            Joueur player(joueurList.at(i), 501, 3);
            joueurs.push_back(player);
        }
        emit afficherNouvellePartie();
        emit changerEtatPartie();
    }
    else if(ModeDeJeu == "301_DOUBLE_OUT")
    {
        for(int i = 1; i < joueurList.size() ; i++)
        {
            Joueur player(joueurList.at(i), 301, 3);
            joueurs.push_back(player);
        }
        emit afficherNouvellePartie();
        emit changerEtatPartie();
    }
    else
    {
        qDebug() << Q_FUNC_INFO << "Erreur Mode De Jeu" << ModeDeJeu;
        reinitialiserPartie();
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
    /** @todo delete les objets joueurs **/
    /*for(QList<Joueur>::iterator it = joueurs.begin(); it != joueurs.end(); ++it)
    {
        delete *it;
    }*/

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
 * @brief permet de traiter la réception d'impact
 *
 * @fn Darts::receptionnerImpact
 * @param typePoint
 * @param point
 */
void Darts::receptionnerImpact(int typePoint, int point)
{
    if(typePoint == TRIPLE_POINT)
    {
        pointLancer = point * 3;
    }
    else if(typePoint == DOUBLE_POINT)
    {
        pointLancer = point * 2;
    }
    else if(typePoint == SIMPLE_POINT || typePoint == ZERO_POINT)
    {
        pointLancer = point;
    }
    else
    {
        qDebug() << Q_FUNC_INFO << "Erreur pointLancer" << pointLancer;
    }

    if(joueurs[joueurActif].getFlechette() == 3)
        emit actualiserCible();

    qDebug() << Q_FUNC_INFO << joueurs[joueurActif].getNom() << " SCORE : "<<joueurs[joueurActif].getScore() - pointLancer << endl;

    emit nouvelImpact(typePoint, point, pointLancer);
    joueurs[joueurActif].setScore(joueurs[joueurActif].getScore() - pointLancer);
    testerImpact(typePoint);
    emit miseAJourPoint();
}

/**
 * @brief teste si le joueur a gagné
 *
 * @fn Darts::testerImpact
 * @param typePoint
 * @param pointLancer
 */
void Darts::testerImpact(int typePoint)
{
    if(joueurs[joueurActif].getScore()  == 0 && (typePoint == DOUBLE_POINT) && (ModeDeJeu == "501_DOUBLE_OUT" || ModeDeJeu == "301_DOUBLE_OUT")) //fin avec double
    {
        gererVoleeMax();
        nbVolees++;
        emit finPartie("Winner " + joueurs[joueurActif].getNom(), getVoleeMax());
        emit etatPartieFini();
    }
    else if(joueurs[joueurActif].getScore()  == 0 && (ModeDeJeu == "501" || ModeDeJeu == "301"))    //fin sans double
    {
        gererVoleeMax();
        nbVolees++;
        emit finPartie("Winner " + joueurs[joueurActif].getNom(), getVoleeMax());
        emit etatPartieFini();
    }
    else
    {
        enleverPointImpact();
        gererManche();
    }
}

/**
 * @brief met à jour le score du joueur
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
    else if(joueurs[joueurActif].getScore() == 1 && joueurs.size() == 1)    // test si le joueur est seul à jouer et si il est a 1 point == joueur eliminer donc fin de partie
    {
        /** @todo ajouter la possibiliter que le joueur joue tout seul et que son score tombe a 1 point donc il est eliminer est la partie ce fini**/
        gererVoleeMax();
        emit finPartie("Perdu " + joueurs[joueurActif].getNom(), getVoleeMax());
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
 * @brief permet de gérer le changement de manche en fonction des fléchettes de chaque joueur
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

        joueurs[joueurActif].addHistoriqueVolees((joueurs[joueurActif].getScoreManchePrecedente() - joueurs[joueurActif].getScore()));
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
 * @brief change de joueur si le joueur actuel est eliminé
 *
 * @fn Darts::testerSiJoueurEliminer
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
 * @brief teste si le joueur est à 1 point à la fin de la manche
 *
 * @fn Darts::testerSiJoueurEliminer
 */
void Darts::testerSiJoueurEliminer()
{
    if(joueurs[joueurActif].getScore() == 1)
    {
        joueurs[joueurActif].setEliminer(true);
    }
}

/**
 * @brief calcule la moyenne des volées de chaque joueur
 *
 * @fn Darts::calculerMoyenneVolees
 */
void Darts::calculerMoyenneVolees()
{
    for(int i = 0; i <= joueurs.size() - 1; i++)
    {
        float moyenneVolee = 0;

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
 * @brief teste la volée pour savoir si elle est superieure à la volée Max
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
 * @brief calcule le gagnant de la partie
 *
 * @fn Darts::calculerGagnant
 * @return QString
 */
QString Darts::calculerGagnant()
{
    QString gagnant;
    int scoreGagnant = 1000;
    for(int i = 0; i <= joueurs.size() - 1; i++)
    {
        if(scoreGagnant > joueurs[i].getScore() && !joueurs[i].getEliminer()) // test le personne aillant le score le plus bas mais aussi qu'il ne soit pas eliminer
        {
            scoreGagnant = joueurs[i].getScore();
            gagnant = joueurs[i].getNom();
        }
    }
    return "Winner " + gagnant;
}
