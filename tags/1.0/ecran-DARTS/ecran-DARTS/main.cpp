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
* @version 1.0
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

// Pour la documentation Doxygen

/**
 * @brief
 * La classe QObject est la classe de base de tous les objets Qt. Elle permet à ces objets Qt de disposer entre autres du mécanisme de communication signal/slot
 * @class QObject
 */

/**
 * @brief
 * La classe QWidget est la classe de base de tous les objets graphiques d'interface utilisateur
 * @class QWidget
 */
