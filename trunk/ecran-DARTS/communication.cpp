#include "communication.h"
#include <QDebug>

/**
* @file communication.cpp
*
* @brief Définition de la classe Communication
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
Communication::Communication(Darts *darts, QObject *parent) : QObject(parent), darts(darts), serveur(nullptr), socket(nullptr), localDeviceName("Ecran-Darts"), trame("")
{
    qDebug() << Q_FUNC_INFO;

    parametrerBluetooth();

    miseAJourEtatPartieAttente();
}

/**
 * @brief destructeur de la classe Communication
 *
 * @fn Communication::~Communication
 */
Communication::~Communication()
{
    arreter();
    qDebug() << Q_FUNC_INFO;
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
        qDebug() << Q_FUNC_INFO << "Communication Bluetooth locale valide : " << localDevice.isValid();
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

        connect(darts, SIGNAL(etatPartieFini()), this , SLOT(miseAJourEtatPartieFin()));
        connect(darts, SIGNAL(changerEtatPartie()), this , SLOT(miseAJourEtatPartieEnCours()));
    }
}

/**
 * @brief demarre le serveur
 *
 * @fn Communication::demarrer
 */
void Communication::demarrer()
{
    if (!localDevice.isValid())
        return;

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
    if (!localDevice.isValid())
        return;

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
 * @brief Méthode appelée quand un nouveau client se connecte
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
 * @brief Méthode qui decompose la trame reçue et signale l'état en fonction du type de trame
 *
 * @fn Communication::decomposerTrame
 */
void Communication::decomposerTrame()
{
    if(trame.startsWith(TYPE_TRAME) && trame.endsWith(DELIMITEUR_FIN))
    {
        trame.remove(DELIMITEUR_FIN);
        if(trame.contains("START") && (etatPartie == EtatPartie::Attente || etatPartie == EtatPartie::Fin))      /** $DART;START;2;fabien;erwan */
        {
            emit resetPartie();
            darts->reinitialiserPartie();

            QStringList joueurs = extraireParametresTrameStart();

            darts->initialiserPartie(joueurs, trame.section(";",2,2));

        }
        else if(trame.contains("GAME") && etatPartie == EtatPartie::EnCours)      /** $DART;GAME;3;7 */
        {
            darts->receptionnerImpact(trame.section(";",2,2).toInt(), trame.section(";",3,3).toInt());
        }
        else if(trame.contains("PAUSE") && etatPartie == EtatPartie::EnCours)     /** $DART;PAUSE */
        {
            emit pause();
            miseAJourEtatPartiePause();
        }
        else if(trame.contains("PLAY") && etatPartie == EtatPartie::Pause)       /** $DART;PLAY */
        {
            emit play();
            miseAJourEtatPartieEnCours();
        }
        else if(trame.contains("RESET")) // quelque soit l'état de la partie    /** $DART;RESET */
        {
            emit resetPartie();
            darts->reinitialiserPartie();
            miseAJourEtatPartieAttente();
        }
        else if(trame.contains("STOP") && (etatPartie == EtatPartie::EnCours))  /** $DART;STOP */
        {
            miseAJourEtatPartieFin();
            darts->arreterPartie();
        }
        else
        {
            qDebug() << Q_FUNC_INFO << trame;
        }
    }
}

/**
 * @brief Méthode qui decompose la trame Start
 *
 * @fn Communication::extraireParametresTrameStart
 */
QStringList Communication::extraireParametresTrameStart()
{
    QStringList joueurs;

    for(int i = 0;i <= trame.section(";",3,3).toInt();i++)
    {
        if(trame.section(";",3+i,3+i) == "")    //test si le joueur a un nom
        {
            joueurs.push_back("Joueur[" + QString::number(i) + "]");
        }
        else
        {
            joueurs.push_back(trame.section(";",3+i,3+i));
        }
    }

    return joueurs;
}

/**
 * @brief méthode appelée quand la socket est déconnectée
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
 * @brief méthode appelée quand l'appareil est connecté
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
    qDebug() << message << endl;

    if(etatPartie == EtatPartie::Pause) // si l'appareil est reconnecte, la partie reprend
    {
        emit play();
        miseAJourEtatPartieEnCours();
    }
}

/**
 * @brief méthode appelée quand l'appareil est deconnecté
 *
 * @fn Communication::deviceDisconnected
 * @param adresse
 */
void Communication::deviceDisconnected(const QBluetoothAddress &adresse)
{
    qDebug() << Q_FUNC_INFO << adresse;
    emit afficherAttenteConnexion();

    if(etatPartie == EtatPartie::EnCours) // si l'appareil se deconnecte pendant la partie, il la met donc en pause
    {
        emit pause();
        miseAJourEtatPartiePause();
    }
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
 * @brief Méthode qui met à jour l'état de la partie Attente
 *
 * @fn Communication::miseAJourEtatPartieAttente
 */
void Communication::miseAJourEtatPartieAttente()
{
    qDebug() << Q_FUNC_INFO << "EtatPartie::Attente";
    etatPartie = EtatPartie::Attente;
}

/**
 * @brief Méthode qui met à jour l'état de la partie Pause
 *
 * @fn Communication::miseAJourEtatPartiePause
 */
void Communication::miseAJourEtatPartiePause()
{
    qDebug() << Q_FUNC_INFO << "EtatPartie::Pause";
    etatPartie = EtatPartie::Pause;
}

/**
 * @brief Méthode qui met à jour l'état de la partie Fin
 *
 * @fn Communication::miseAJourEtatPartieFin
 */
void Communication::miseAJourEtatPartieFin()
{
    qDebug() << Q_FUNC_INFO << "EtatPartie::Fin";
    etatPartie = EtatPartie::Fin;
}

/**
 * @brief Méthode qui met à jour l'état de la partie EnCours
 *
 * @fn Communication::miseAJourEtatPartieEnCours
 */
void Communication::miseAJourEtatPartieEnCours()
{
    qDebug() << Q_FUNC_INFO << "EtatPartie::EnCours";
    etatPartie = EtatPartie::EnCours;
}
