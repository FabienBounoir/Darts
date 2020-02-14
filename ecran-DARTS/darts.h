#ifndef DARTS_H
#define DARTS_H

#include <QObject>

class Darts : public QObject
{
    Q_OBJECT
public:
    explicit Darts(QObject *parent = nullptr);
    ~Darts();

    QStringList getJoueur() const;
    QString getModeJeu() const;
    void setJoueur(QStringList joueur);
    void setModeJeu(QString modeJeu);

    Darts *getDarts();

signals:

public slots:

private:
    QStringList joueur;
    QString modeJeu;
};

#endif // DARTS_H
