#include "darts.h"

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
