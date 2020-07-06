#ifndef SOLUTION_H
#define SOLUTION_H

/**
 * @file solution.h
 * @brief Déclaration de la classe solution (Module Ecran-DARTS)
 *
 * @version 1.0
 *
 * @author Bounoir Fabien
 */

#include <QObject>
#include <QString>
#include <QDebug>

#define BULL                25

// Rechercher en premier ?
#define RECHERCHE_TRIPLE    1
#define RECHERCHE_DOUBLE    2
#define RECHERCHE_SIMPLE    3
#define RECHERCHE_FINIE     4

/**
 * @class Solution
 * @brief Déclaration de la classe Solution (Module Ecran-DARTS)
 * @details Cette classe s'occupe de rechercher les différentes solutions pour finir la partie
*/
class Solution : public QObject
{
    Q_OBJECT
public:
    explicit Solution(QObject *parent = nullptr);
    ~Solution();
    void trouverSolution(int s, int flechettes);

private:
    QString solution;                       //!< contient la solution pour finir la partie

    void transmettreSolution(int score);
    bool aTriple(int points, const int score);
    bool rechercherTriple(int &score, QString &combinaison, int start);
    bool aDouble(int points, const int score);
    bool rechercherDouble(int &score, QString &combinaison);
    bool aSimple(int points, const int score);
    bool rechercherSimple(int &score, QString &combinaison);
    bool estDouble(int points, const int score);
    bool extraireDouble(int &score, int cible);
    bool rechercher(int score, int nbFlechettes, bool still=false);

signals:
    void solutionTrouver(QString solution);

public slots:
};

#endif // SOLUTION_H
