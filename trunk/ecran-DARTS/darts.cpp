#include "darts.h"

#include <QDebug>

Darts::Darts(QObject *parent) : QObject(parent), joueur(NULL), modeJeu("")
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

void Darts::setJoueur(QStringList joueur)
{
    this->joueur = joueur;
}

void Darts::setModeJeu(QString modeJeu)
{
    this->modeJeu = modeJeu;
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
