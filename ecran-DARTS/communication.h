#ifndef COMMUNICATION_H
#define COMMUNICATION_H

#include <QObject>

class Communication : public QObject
{
    Q_OBJECT
public:
    explicit Communication(QObject *parent = nullptr);

signals:

public slots:
};

#endif // COMMUNICATION_H