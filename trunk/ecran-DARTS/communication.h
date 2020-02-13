#ifndef COMMUNICATION_H
#define COMMUNICATION_H

#include <QObject>
#include <QBluetoothLocalDevice>
#include <QBluetoothServer>
#include <unistd.h>

static const QString serviceUuid(QStringLiteral("00001101-0000-1000-8000-00805F9B34FB"));
static const QString serviceNom(QStringLiteral("Ecran-Darts"));

class Communication : public QObject
{
    Q_OBJECT
public:
    explicit Communication(QObject *parent = nullptr);
    ~Communication();

    void parametrerBluetooth();
    void demarrer();
    void arreter();

signals:
    void appareilConnecter();
    void appareilDeconnecter();

public slots:
    void deviceConnected(const QBluetoothAddress &adresse);
    void deviceDisconnected(const QBluetoothAddress &adresse);
    void error(QBluetoothLocalDevice::Error erreur);
    void nouveauClient();
    void socketReadyRead();
    void socketDisconnected();

private:
    QBluetoothLocalDevice localDevice;  //!< L'interface Bluetooth de la Raspberry Pi
    QBluetoothServer *serveur;  //!< Le serveur Bluetooth
    QBluetoothSocket *socket; //!< la socket de communication Bluetooth
    QBluetoothServiceInfo serviceInfo;

    QString localDeviceName = "Ecran-Darts";

};

#endif // COMMUNICATION_H
