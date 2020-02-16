#include "communication.h"
#include <QDebug>

/**
* @file communication.cpp
*
* @brief classe qui s'occupe de la parti communication (configuration, reception trame)
*
* @author Bounoir Fabien
*
* @version 0.1
*
*/

/**
 * @brief constructeur de la classe Communication
 *
 * @fn Communication::Communication
 * @param parent
 */
Communication::Communication(QObject *parent) : QObject(parent), serveur(nullptr), socket(nullptr), trame("")
{
    darts = new Darts(this);

    parametrerBluetooth();
    etatPartie = EtatPartie::Attente;
}

/**
 * @brief destructeur de la classe Communication
 *
 * @fn Communication::~Communication
 */
Communication::~Communication()
{
    arreter();
}

/**
 * @brief retourne l'objet darts
 *
 * @fn Communication::getDarts
 * @return Darts
 */
Darts *Communication::getDarts() const
{
    return darts;
}

/**
 * @brief configure la communication bluetooth
 *
 * @fn Communication::parametrerBluetooth
 */
void Communication::parametrerBluetooth()
{
    if (!localDevice.isValid())
    {
        qDebug() << Q_FUNC_INFO << "Connection bluetooth valide : " << localDevice.isValid();
        return;
    }
    else
    {
        // Turn Bluetooth on
        localDevice.powerOn();

        // Read local device name
        localDeviceName = localDevice.name();

        // Make it visible to others
        localDevice.setHostMode(QBluetoothLocalDevice::HostConnectable);

        // Get connected devices
        QList<QBluetoothAddress> remotes;
        remotes = localDevice.connectedDevices();

        connect(&localDevice, SIGNAL(deviceConnected(QBluetoothAddress)), this, SLOT(deviceConnected(QBluetoothAddress)));
        connect(&localDevice, SIGNAL(deviceDisconnected(QBluetoothAddress)), this, SLOT(deviceDisconnected(QBluetoothAddress)));
        connect(&localDevice, SIGNAL(error(QBluetoothLocalDevice::Error)), this, SLOT(error(QBluetoothLocalDevice::Error)));

        connect(darts, SIGNAL(etatPartieFini()), this , SLOT(miseAJourEtatPartie()));

    }
}

/**
 * @brief demarre le serveur
 *
 * @fn Communication::demarrer
 */
void Communication::demarrer()
{
    if (!serveur)
    {
        serveur = new QBluetoothServer(QBluetoothServiceInfo::RfcommProtocol, this);
        connect(serveur, SIGNAL(newConnection()), this, SLOT(nouveauClient()));

        QBluetoothUuid uuid = QBluetoothUuid(serviceUuid);
        serviceInfo = serveur->listen(uuid, serviceNom);
    }
}

/**
 * @brief arrete le serveur
 *
 * @fn Communication::arreter
 */
void Communication::arreter()
{
    if (!serveur)
        return;

    serviceInfo.unregisterService();

    if (socket)
    {
        if (socket->isOpen())
        {
           socket->close();
        }
        delete socket;
        socket = nullptr;
    }

    delete serveur;
    serveur = nullptr;
}

/**
 * @brief Méthode appeler quand un nouveau client se connecte
 *
 * @fn Communication::nouveauClient
 */
void Communication::nouveauClient()
{
    // on récupère la socket
    socket = serveur->nextPendingConnection();
    if (!socket)
        return;

    connect(socket, SIGNAL(disconnected()), this, SLOT(socketDisconnected()));
    connect(socket, SIGNAL(readyRead()), this, SLOT(socketReadyRead()));
}

/**
 * @brief Méthode appelée quand une trame est disponible
 *
 * @fn Communication::socketReadyRead
 */
void Communication::socketReadyRead()
{
    qDebug() << Q_FUNC_INFO;
    QByteArray donnees;

    while (socket->bytesAvailable())
    {
        donnees += socket->readAll();
        usleep(150000); // cf. timeout
    }

    trame = QString(donnees);

    qDebug() << QString::fromUtf8("Données reçues : ") << QString(donnees);

    decomposerTrame();
}

/**
 * @brief Méthode qui decompose la trame reçu
 *
 * @fn Communication::decomposerTrame
 */
void Communication::decomposerTrame()
{
    if(trame.startsWith(TYPE_TRAME) && trame.endsWith(DELIMITEUR_FIN))
    {
        QStringList joueur;
        trame.remove(DELIMITEUR_FIN);
        if(trame.contains("START") && (etatPartie == EtatPartie::Attente || etatPartie == EtatPartie::Fin))      /** $DART;START;2;fabien;erwan */
        {
            emit resetPartie();
            darts->reinitialiserPartie();

            etatPartie = EtatPartie::EnCours;
            for(int i = 0;i <= trame.section(";",3,3).toInt();i++)
            {
                joueur.push_back(trame.section(";",3+i,3+i));
            }

            darts->initialiserPartie(joueur, trame.section(";",2,2));
            emit nouvellePartie(trame.section(";",2,2));

        }
        else if(trame.contains("GAME") && etatPartie == EtatPartie::EnCours)      /** $DART;GAME;3;7 */
        {
            darts->receptionnerImpact(trame.section(";",2,2).toInt(), trame.section(";",3,3).toInt());
        }
        else if(trame.contains("RESET") && (etatPartie == EtatPartie::Attente || etatPartie == EtatPartie::EnCours || etatPartie == EtatPartie::Fin))
        {
            emit resetPartie();
            darts->reinitialiserPartie();
            etatPartie = EtatPartie::Attente;
        }
    }
}

/**
 * @brief fonction appelée quand l'appareil est deconnecté
 *
 * @fn Communication::socketDisconnected
 */
void Communication::socketDisconnected()
{
    qDebug() << Q_FUNC_INFO;
    QString message = QString::fromUtf8("Périphérique déconnecté ");
    qDebug() << message;
}

/**
 * @brief fonction appelée quand l'appareil est connecté
 *
 * @fn Communication::deviceConnected
 * @param adresse
 */
void Communication::deviceConnected(const QBluetoothAddress &adresse)
{
    qDebug() << Q_FUNC_INFO << adresse << localDevice.pairingStatus(adresse);
    QString message = QString::fromUtf8("Demande connexion du client ") + adresse.toString();
    emit appareilConnecter();
    if (localDevice.pairingStatus(adresse) == QBluetoothLocalDevice::Paired || localDevice.pairingStatus(adresse) == QBluetoothLocalDevice::AuthorizedPaired)
        message += " [" + QString::fromUtf8("appairé") + "]";
    else
        message += " [" + QString::fromUtf8("non appairé") + "]" ;

}

/**
 * @brief fonction appelée quand l'appareil est deconnecté
 *
 * @fn Communication::deviceDisconnected
 * @param adresse
 */
void Communication::deviceDisconnected(const QBluetoothAddress &adresse)
{
    qDebug() << Q_FUNC_INFO << adresse;
    emit appareilDeconnecter();
}

/**
 * @brief Méthode appelée quand il y a une erreur avec l'appareil connecté
 *
 * @fn Communication::error
 * @param erreur
 */
void Communication::error(QBluetoothLocalDevice::Error erreur)
{
    qDebug() << Q_FUNC_INFO << erreur;
}

/**
 * @brief  Méthode qui met a jour l'etat de la partie
 *
 * @fn Communication::miseAJourEtatPartie
 */
void Communication::miseAJourEtatPartie()
{
    etatPartie = EtatPartie::Fin;
}
