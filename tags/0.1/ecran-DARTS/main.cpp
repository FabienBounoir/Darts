#include "ihm.h"
#include <QApplication>

/**
* @file main.cpp
*
* @brief Programme principal ecran-DARTS
*
* @details Crée et affiche la fenêtre principale de l'application ecran-DARTS
*
* @author Bounoir Fabien
*
* @version 0.1
*
* @fn main(int argc, char *argv[])
* @param argc
* @param argv[]
* @return int
*
*/
int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    Ihm w;
    w.showFullScreen();

    return a.exec();
}
