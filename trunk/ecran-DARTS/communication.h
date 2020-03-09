#ifndef COMMUNICATION_H
#define COMMUNICATION_H

/**
 * @file communication.h
 * @brief Déclaration de la classe Communication
 *
 * @version 0.1
 *
 * @author Bounoir Fabien
 */

#include "darts.h"
#include <QObject>
#include <QString>
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
* @brief Déclaration de la classe Communication (via la liaison Bluetooth)
* @details Cette classe s'occupe de la partie communication Bluetooth en mode serveur (configuration, réception et traitement des trames du protocole DART)
*/
class Communication : public QObject
{
    Q_OBJECT
public:
    explicit Communication(Darts *darts, QObject *parent = nullptr);  //!< constructeur de la classe Communication
    ~Communication();                                   //!< destructeur de la classe Communication

    Darts *getDarts() const;        //!< Méthode qui retourne l'objet Darts
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
        Fin = 2,
        Pause
    };

signals:
    void appareilConnecter();           //!< signal émis quand un nouvel appareil est connecté
    void afficherAttenteConnexion();    //!< signal émis quand un appareil se déconnecte
    void resetPartie();                 //!< signal qui reinitialisera la partie en cours
    void pause();                       //!< signal qui mettra en pause la partie
    void play();                        //!< signal qui relancera le chronometrage de la partie la partie

public slots:
    void deviceConnected(const QBluetoothAddress &adresse);         //!< Slot appelé quand un nouvel appareil est connecté
    void deviceDisconnected(const QBluetoothAddress &adresse);      //!< Slot appelé quand un appareil est déconnecté
    void error(QBluetoothLocalDevice::Error erreur);                //!< Slot appelé quand il y a une erreur avec l'appareil bluetooth
    void nouveauClient();                                           //!< Slot appelé quand un nouveau client veut se connecter
    void socketReadyRead();                                         //!< Slot appelé quand une nouvelle trame est disponible
    void socketDisconnected();                                      //!< Slot appelé quand la communication est deconnectée
    void miseAJourEtatPartieAttente();                              //!< Slot appelé pour mettre à jour l'état de la partie à Attente
    void miseAJourEtatPartiePause();                                //!< Slot appelé pour mettre à jour l'état de la partie à Pause
    void miseAJourEtatPartieFin();                                  //!< Slot appelé pour mettre à jour l'état de la partie à Fin
    void miseAJourEtatPartieEnCours();                              //!< Slot appelé pour mettre à jour l'état de la partie à EnCours

private:
    Darts *darts;                               //!< Association avec l'objet darts
    QBluetoothServer *serveur;                  //!< Le serveur Bluetooth
    QBluetoothSocket *socket;                   //!< La socket de communication Bluetooth
    QBluetoothLocalDevice localDevice;          //!< L'interface Bluetooth de la Raspberry Pi    
    QBluetoothServiceInfo serviceInfo;          //!< Informations sur le service bluetooth
    QString localDeviceName;                    //!< Nom du peripherique local
    QString trame;                              //!< La trame recue    
    EtatPartie etatPartie;                      //!< L'état de la partie

    void decomposerTrame();                                         //!< Méthode qui decompose la trame reçue
    QStringList extraireParametresTrameStart(QString &modeJeu);     //!< Méthode qui extrrait les paramètres du trame START
};

#endif // COMMUNICATION_H
