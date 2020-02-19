#ifndef COMMUNICATION_H
#define COMMUNICATION_H

/**
 * @file communication.h
 * @brief Declaration de la classe Communication
 *
 * @version 0.1
 *
 * @author Bounoir Fabien
 */

#include "darts.h"
#include <QObject>
#include <QBluetoothLocalDevice>
#include <QBluetoothServer>
#include <unistd.h>

/**
 * @def TYPE_TRAME
 * @brief Définit le type de trame du protocole DARTS
 */
#define TYPE_TRAME "$DART"
/**
 * @def DELIMITEUR_FIN
 * @brief Définit le délimiteur de fin de trame du protocole DARTS
 */
#define DELIMITEUR_FIN "\r\n"

static const QString serviceUuid(QStringLiteral("00001101-0000-1000-8000-00805F9B34FB"));
static const QString serviceNom(QStringLiteral("Ecran-Darts"));

/**
* @class Communication communication.h "communication.h"
*
* @brief Declaration de la classe Communication (Liaison Bluetooth)
*/
class Communication : public QObject
{
    Q_OBJECT
public:
    explicit Communication(QObject *parent = nullptr);  //!< constructeur de la classe Communication
    ~Communication();                                   //!< destructeur de la classe Communication

    Darts *getDarts() const;
    void parametrerBluetooth();     //!< Méthode qui configure la connexion Bluetooth en mode serveur
    void demarrer();                //!< Méthode qui démarre le serveur
    void arreter();                 //!< Méthode qui arrête le serveur

    /**
     * @brief contient les different etat de la partie
     * @enum EtatSeance
     */
    enum EtatPartie
    {
        Attente = 0,
        EnCours = 1,
        Fin
    };

signals:
    void appareilConnecter();       //!< signal émis quand un nouvel appareil est connecté
    void appareilDeconnecter();     //!< signal émis quand un l'appareil se déconnecté
    void resetPartie();                                         //!< signal qui reset la partie en cour

public slots:
    void deviceConnected(const QBluetoothAddress &adresse);         //!< Slot appelée quand un nouvelle appareil est connecté
    void deviceDisconnected(const QBluetoothAddress &adresse);      //!< Slot appelée quand un l'appareil est déconnecté
    void error(QBluetoothLocalDevice::Error erreur);                //!< Slot appelée quand il y a un erreur avec l'appareil bluetooth
    void nouveauClient();                                           //!< Slot appelée quand un nouveau client veut se connecter
    void socketReadyRead();                                         //!< Slot appelée quand une nouvelle trame est disponible
    void socketDisconnected();                                      //!< Slot appelée quand l'appareil est deconnecté
    void miseAJourEtatPartieFin();                                  //!< Slot appelée pour mettre a jour l'etat de la partie fin
    void miseAJourEtatPartieEnCours();                              //!< Slot appelée pour mettre a jour l'etat de la partie en cours

private:
    QBluetoothLocalDevice localDevice;          //!< L'interface Bluetooth de la Raspberry Pi
    QBluetoothServer *serveur;                  //!< Le serveur Bluetooth
    QBluetoothSocket *socket;                   //!< La socket de communication Bluetooth
    QBluetoothServiceInfo serviceInfo;          //!< Informations sur le service bluetooth
    QString localDeviceName = "Ecran-Darts";    //!< Nom du peripherique local
    QString trame;                              //!< La trame recue
    Darts *darts;                               //!< Association objet darts
    EtatPartie etatPartie;                      //!< contient l'etat de la partie

    void decomposerTrame();                     //!< methoe qui decompose la trame reçu

};

#endif // COMMUNICATION_H
