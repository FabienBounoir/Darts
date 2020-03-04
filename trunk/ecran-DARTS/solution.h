#ifndef SOLUTION_H
#define SOLUTION_H

#include <QObject>
#include <QString>
#include <QDebug>

#define MAX (3*20 + 3*20 + 2*25)

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
    void calculerCombinaisons();
    void trouverSolution(int score, int flechettes);

private:
    void afficherSolution(int score, const QVector<QString> &solution);
    bool aTriple(int points, const int score);
    bool rechercherTriple(int &score, QString &combinaison, int start);
    bool aDouble(int points, const int score);
    bool rechercherDouble(int &score, QString &combinaison);
    bool aSimple(int points, const int score);
    bool rechercherSimple(int &score, QString &combinaison);
    bool estDouble(int points, const int score);
    bool extraireDouble(int &score, int cible);
    bool rechercher(int score, int nbFlechettes, QVector<QString> solution, bool still=false);

signals:

public slots:
};

#endif // SOLUTION_H
