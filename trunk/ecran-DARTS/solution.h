#ifndef SOLUTION_H
#define SOLUTION_H

#include <QObject>
#include <QString>
#include <QDebug>

// Rechercher en premier ?
#define RECHERCHE_TRIPLE    1
#define RECHERCHE_DOUBLE    2
#define RECHERCHE_SIMPLE    3
#define RECHERCHE_FINIE     4

class Solution : public QObject
{
    Q_OBJECT
public:
    explicit Solution(QObject *parent = nullptr);
    void trouverSolution(int score, int flechettes);

private:
    QString solution;                       //!< contient la solution pour finir la partie
    void afficherSolution(int score);
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
