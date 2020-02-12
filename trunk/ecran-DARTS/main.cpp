#include "ihm.h"
#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    Ihm w;
    w.show();

    return a.exec();
}
