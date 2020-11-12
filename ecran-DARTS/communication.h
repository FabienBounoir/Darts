#ifndef COMMUNICATION_H
#define COMMUNICATION_H

/**
 * @file communication.h
 * @brief Déclaration de la classe Communication (Module Ecran-DARTS)
 *
 * @version 1.0
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

static const QString serviceUuid(QStringLiteral("0000110a-0000-1000-8000-00805f9b34fb"));
static const QString serviceNom(QStringLiteral("Ecran-Darts"));

/**
* @class Communication communication.h "communication.h"
*
* @brief Déclaration de la classe Communication via la liaison Bluetooth (Module Ecran-DARTS)
* @details Cette classe s'occupe de la partie communication Bluetooth en mode serveur (configuration, réception et traitement des trames du protocole DART)
*/
class Communication : public QObject
{
    Q_OBJECT
public:
    explicit Communication(Darts *darts, QObject *parent = nullptr);  //!< constructeur de la classe Communication
    ~Communication();                                                 //!< destructeur de la classe Communication

    void parametrerBluetooth();     //!< Méthode qui configure la connexion Bluetooth en mode serveur
    void demarrer();                //!< Méthode qui démarre le serveur
    void arreter();                 //!< Méthode qui arrête le serveur
    int getEtatPartie();            //!< Méthode qui permet de recuperer l'etat de la partie

    void miseAJourEtatPartieRegle();                                //!< Méthode appelé pour mettre à jour l'état de la partie à regle
    void miseAJourEtatPartieAttente();                              //!< Méthode appelé pour mettre à jour l'état de la partie à Attente
    void miseAJourEtatPartiePause();                                //!< Méthode appelé pour mettre à jour l'état de la partie à Pause

    /**
     * @brief contient les different etat de la partie
     * @enum EtatPartie
     */
    enum EtatPartie
    {
        Attente = 0,
        EnCours = 1,
        Fin = 2,
        Pause = 3,
        Regle = 4,      //État attente pendant la lecture des règles, seule une trame reset peut être reçu pour arrêter la lecture.
        AttenteTournois = 5,
        Tournois
    };

signals:
    void appareilConnecter();                       //!< signal émis quand un nouvel appareil est connecté
    void afficherAttenteConnexion();                //!< signal émis quand un appareil se déconnecte
    void resetPartie();                             //!< signal qui reinitialisera la partie en cours
    void pause();                                   //!< signal qui mettra en pause la partie
    void play();                                    //!< signal qui relancera le chronometrage de la partie la partie
    void erreurBluetooth(QString erreur);           //!< signal emit quand un probleme de configuration bluetooth est detecté
    void afficherRegle(QString regle);              //!< signal emit pour afficher les regles
    void stopperRegle();                            //!< signal emit pour stopper la lecture des regles
    void jouerSon(QString);

public slots:
    void miseAJourEtatPartieFin();                                  //!< Slot appelé pour mettre à jour l'état de la partie à Fin
    void miseAJourEtatPartieEnCours();                              //!< Slot appelé pour mettre à jour l'état de la partie à EnCours
    void miseAJourEtatPartieTournois();                             //!< Slot appelé pour mettre à jour l'état de la partie à tournois
    void miseAJourEtatPartieAttenteTournois();                      //!< Slot appelé pour mettre à jour l'état de la partie à attenteTournois

private slots:
    void deviceConnected(const QBluetoothAddress &adresse);         //!< Slot appelé quand un nouvel appareil est connecté
    void deviceDisconnected(const QBluetoothAddress &adresse);      //!< Slot appelé quand un appareil est déconnecté
    void error(QBluetoothLocalDevice::Error erreur);                //!< Slot appelé quand il y a une erreur avec l'appareil bluetooth
    void nouveauClient();                                           //!< Slot appelé quand un nouveau client veut se connecter
    void socketReadyRead();                                         //!< Slot appelé quand une nouvelle trame est disponible
    void socketDisconnected();                                      //!< Slot appelé quand la communication est deconnectée


private:
    Darts *darts;                               //!< Association avec l'objet darts
    QBluetoothServer *serveur;                  //!< Le serveur Bluetooth
    QBluetoothSocket *socket;                   //!< La socket de communication Bluetooth
    QBluetoothLocalDevice localDevice;          //!< L'interface Bluetooth de la Raspberry Pi    
    QBluetoothServiceInfo serviceInfo;          //!< Informations sur le service bluetooth
    QString localDeviceName;                    //!< Nom du peripherique local
    QString trame;                              //!< La trame extraite des données reçues
    QString trames;                             //!< Les trames recues
    EtatPartie etatPartie;                      //!< L'état de la partie

    void decomposerTrame();                     //!< Méthode qui decompose la trame reçue
    void extraireParametresTrameStart(QStringList &joueurs, QString &modeJeu);     //!< Méthode qui extrait les paramètres de la trame START
    void extraireParametresTrameRegle();                                            //!< Méthode qui extrait les paramètres de la trame REGLE
    void reamorcerPartie();                                                         //!< Méthode qui relance la partie
    void decomposerTrameTournois();                                                 //!< Méthode qui extrait les paramètres de la trame TOURNOIS
    bool estValide();                                                               //!< Méthode qui test si la trame est valide
    int etatPrecedent;                                                              //!< Contient l'etat dans lequel se trouver l'application
    void relancerPartie();                                                          //!< Méthode qui gere l'initialisation d'une partie de flechette
};

#endif // COMMUNICATION_H
