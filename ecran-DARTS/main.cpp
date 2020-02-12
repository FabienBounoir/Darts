#include "ihm.h"
#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    Ihm w;
    w.showFullScreen();

    return a.exec();
}
