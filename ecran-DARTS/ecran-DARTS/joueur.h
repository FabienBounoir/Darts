#ifndef JOUEUR_H
#define JOUEUR_H

#include <QString>
#include <QVector>

/**
 * @file joueur.h
 * @brief Déclaration de la classe Joueur (Module Ecran-DARTS)
 *
 * @version 1.0
 *
 * @author Bounoir Fabien
 */

/**
 * @class Joueur
 * @brief Déclaration de la classe Joueur (Module Ecran-DARTS)
 * @details Cette classe s'occupe du stockage du nom, score et nombre de fléchettes du joueur
*/
class Joueur
{
public:
    Joueur(QString nom, int score , int nbFlechette);

    QString getNom() const;
    int getScore() const;
    int getScoreManchePrecedente() const;
    int getFlechette() const;
    int getMoyenneVolee() const;
    QVector<float> getHistoriqueVolees() const;
    bool getEliminer() const;
    void setEliminer(bool elimine);
    void setMoyenneVolee(int moyenneVolee);
    void setScore(int score);
    void setScoreManchePrecedente(int scoreManchePrecedente);
    void setNbFlechette(int nbFlechette);
    void addHistoriqueVolees(float volee);

private:
    QString nom; //!< contient le nom du joueur
    QVector<float> historiqueVolees; //!< contient l'historique des volées du joueur
    int score; //!< contient le score du joueur
    int moyenneVolee; //!< contient la moyenne des volées du joueur
    int scoreManchePrecedente; //!< contient le score de la manche precedente
    int nbFlechette; //!< contient le nombre de flechette restante au joueur
    bool elimine; //!< contient un état true/false pour savoir si le joueur est éliminé.
};

#endif // JOUEUR_H
