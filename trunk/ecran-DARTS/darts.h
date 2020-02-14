#ifndef DARTS_H
#define DARTS_H

#include <QObject>
#include <QVector>

class Darts : public QObject
{
    Q_OBJECT
public:
    explicit Darts(QObject *parent = nullptr);
    ~Darts();

    QStringList getJoueur() const;
    QString getModeJeu() const;
    QVector<int> getPointJoueur() const;
    void setJoueur(QStringList joueur);
    void setModeJeu(QString modeJeu);
    Darts *getDarts();

    void receptionnerImpact(int cercle, int point);

signals:
    void miseAJourPoint();

public slots:

private:
    QStringList joueur;
    QString modeJeu;

    QVector<int> pointJoueur; //!< Un vecteur qui contient les points des differents joueurs
};

#endif // DARTS_H
