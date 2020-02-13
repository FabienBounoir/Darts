#include "communication.h"
#include <QDebug>

Communication::Communication(QObject *parent) : QObject(parent)
{
    parametrerBluetooth();
}

Communication::~Communication()
{
    arreter();
}

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

    }
}

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

void Communication::arreter()
{
    if (!serveur)
        return;

    serviceInfo.unregisterService();

    /*if (socket)
    {
        if (socket->isOpen())
        {
           socket->close();
        }
        delete socket;
        socket = NULL;
    }*/

    //delete serveur;
    //serveur = NULL;
}

void Communication::nouveauClient()
{
    // on récupère la socket
    socket = serveur->nextPendingConnection();
    if (!socket)
        return;

    connect(socket, SIGNAL(disconnected()), this, SLOT(socketDisconnected()));
    connect(socket, SIGNAL(readyRead()), this, SLOT(socketReadyRead()));
}

void Communication::socketReadyRead()
{
    qDebug() << Q_FUNC_INFO;
    QByteArray donnees;

    while (socket->bytesAvailable())
    {
        donnees += socket->readAll();
        usleep(150000); // cf. timeout
    }

    qDebug() << QString::fromUtf8("Données reçues : ") << QString(donnees);
}

void Communication::socketDisconnected()
{
    qDebug() << Q_FUNC_INFO;
    QString message = QString::fromUtf8("Périphérique déconnecté ");
    qDebug() << message;
}

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

void Communication::deviceDisconnected(const QBluetoothAddress &adresse)
{
    qDebug() << Q_FUNC_INFO << adresse;
    emit appareilDeconnecter();
}

void Communication::error(QBluetoothLocalDevice::Error erreur)
{
    qDebug() << Q_FUNC_INFO << erreur;
}

