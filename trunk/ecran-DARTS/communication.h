#ifndef COMMUNICATION_H
#define COMMUNICATION_H

#include "darts.h"
#include <QObject>
#include <QBluetoothLocalDevice>
#include <QBluetoothServer>
#include <unistd.h>

#define TYPE_TRAME "$DART"
#define DELIMITEUR_FIN "\r\n"

static const QString serviceUuid(QStringLiteral("00001101-0000-1000-8000-00805F9B34FB"));
static const QString serviceNom(QStringLiteral("Ecran-Darts"));

/**
 * @file communication.h
 * @brief Declaration de la classe Communication
 *
 * @version 0.1
 *
 * @author Bounoir Fabien
 */

/**
* @class Communication communication.h "communication.h"
*
* @brief Declaration de la classe Communication (Liaison Bluetooth)
*/
class Communication : public QObject
{
    Q_OBJECT
public:
    explicit Communication(QObject *parent = nullptr);
    ~Communication();

    Darts *getDarts() const;

    void parametrerBluetooth();     //!< Méthode qui configure la connexion bluetooth en mode serveur
    void demarrer();                //!< Méthode qui démarre le serveur
    void arreter();                 //!< Méthode qui arrête le serveur

signals:
    void appareilConnecter();       //!< signal emit quand un nouvelle appareil est connecté
    void appareilDeconnecter();     //!< signal emit quand un l'appareil se déconnecté
    void nouvellePartie(QString mode, QStringList joueur);

public slots:
    void deviceConnected(const QBluetoothAddress &adresse);         //!< Méthode appeler quand un nouvelle appareil est connecté
    void deviceDisconnected(const QBluetoothAddress &adresse);      //!< Méthode appeler quand un l'appareil est déconnecté
    void error(QBluetoothLocalDevice::Error erreur);                //!< Méthode appeler quand il y a un erreur avec l'appareil bluetooth
    void nouveauClient();                                           //!< Méthode appeler quand un nouveau client veut se connecter
    void socketReadyRead();                                         //!< Méthode appeler quand une nouvelle trame est disponible
    void socketDisconnected();                                      //!< Méthode appeler quand l'appareil est deconnecté

private:
    QBluetoothLocalDevice localDevice;  //!< L'interface Bluetooth de la Raspberry Pi
    QBluetoothServer *serveur;          //!< Le serveur Bluetooth
    QBluetoothSocket *socket;           //!< la socket de communication Bluetooth
    QBluetoothServiceInfo serviceInfo;  //!< information sur le service bluetooth

    QString localDeviceName = "Ecran-Darts"; /**< nom du peripherique */
    QString trame; /**< contient la trame recu */
    Darts *darts; /**< objet darts */

    void decomposerTrame();

};

#endif // COMMUNICATION_H
