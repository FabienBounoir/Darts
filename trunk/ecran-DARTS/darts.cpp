#include "darts.h"

#include <QDebug>

Darts::Darts(QObject *parent) : QObject(parent), joueur(NULL), modeJeu(""), pointJoueur(0)
{

}

Darts::~Darts()
{

}

QStringList Darts::getJoueur() const
{
    return joueur;
}

QString Darts::getModeJeu() const
{
    return modeJeu;
}

QVector<int> Darts::getPointJoueur() const
{
    return pointJoueur;
}

void Darts::setJoueur(QStringList joueur)
{
    this->joueur = joueur;
}

void Darts::setModeJeu(QString modeJeu)
{
    this->modeJeu = modeJeu;

    pointJoueur.resize(joueur.size() - 1);

    for(int i = 0; i < joueur.size() - 1 ;i++)
    {
        pointJoueur[i] = modeJeu.toInt();
    }
    qDebug() << "pointJoueur : " << pointJoueur <<endl;
    emit miseAJourPoint();
}

void Darts::receptionnerImpact(int cercle, int point)
{
    int pointLancer;
    if(cercle == 4)
    {
        pointLancer = point * 3;
    }
    else if(cercle == 6)
    {
        pointLancer = point * 2;
    }
    else
    {
        pointLancer = point;
    }

    qDebug() << "point : " << pointLancer << endl;
}
