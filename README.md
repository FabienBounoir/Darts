\page page_README README

## Projet DARTS

Le système **DARTS** est un système numérique permettant de jouer au jeu de fléchettes électroniques.

Le système DARTS est décomposé en trois modules, dont deux modules sont réalisés par des étudiants IR :

* Module de gestion de partie​ (**Mobile-DARTS**)​ : les joueurs paramètrent et lancent la partie à partir d'une application sur un terminal mobile (sous Android) ;

* Module de détection des impacts​ (Cible-DARTS)​ : la cible est équipée de capteurs permettant d'identifier la zone impactée par les fléchettes envoyées par les joueurs ;

* Module de visualisation de partie​ (**Écran-DARTS**) ​ : les joueurs, les arbitres et le public peuvent visualiser en “temps réel” le déroulement de la partie (nombre de manche, point restant dans la manche, moyenne des volées, ...) sur un écran de télévision.

## Module de gestion de partie (Mobile-DARTS) : Erwan Menella

Sur le terminal mobile Android, l'application doit permettre de paramétrer et démarrer une partie.

Pour cela, les joueurs pourront :

* saisir leur nom
* paramétrer la partie :
    * le type de jeu : 501 double out, 301 double out, ...
    * le nombre de joueurs,
    * le nombre de manches gagnantes,
* lancer la partie
* gérer et visualiser le déroulement de la partie :
    * changer automatiquement de joueur
    * affichage du nombre de points
    * renseigner le module Cible d'une fléchette hors cible

Pour démarrer une partie, il est nécessaire d'être connecté en Bluetooth avec le module Cible-DARTS. La connexion avec le module Écran-DARTS n'est pas indispensable pour le fonctionnement du système DARTS. On pourra visualiser sur l'interface l'état de connexion de chaque module.

Le terminal mobile possède une base de données permettant de stocker les statistiques des joueurs pour chaque partie.

## Module de visualisation de partie (Écran-DARTS) : Fabien Bounoir

Ce module correspond à la partie "affichage" du système. Il a pour objectifs de réaliser la récupération d'informations​ envoyées par le terminal mobile, ​le calcul et l'affichage les statistiques pour la partie actuelle. Il communique en Bluetooth uniquement avec le terminal mobile Android.

Sur l'écran, les joueurs pourront visualiser en continu :

* le nom des joueurs (si existant), la durée écoulée de la partie ;
* le type de jeu en cours, le score et le nombre de manches gagnées par chaque joueur
* la plus haute et la moyenne des volées de chaque joueur

Les données visualisées sont donc :

* Le type de jeu
* Le numéro de la manche
* Le score de la partie en cours
* La moyenne des volées

[Dépôt SVN](https://svn.riouxsvn.com/darts-2020)

## Numéro de version : 0.1

## Auteurs

\author \a Erwan Menella <menella.erwan@gmail.com>

\author \a Fabien Bounoir <bounoirfabien@gmail.com>

## Base de données SQLite

~~~ {.sql}

~~~

\mainpage Le projet DARTS

Le système **DARTS** est un système numérique permettant de jouer au jeu de fléchettes électroniques.

\author \a Erwan Menella <menella.erwan@gmail.com>

\author \a Fabien Bounoir <bounoirfabien@gmail.com>

\section section_tdm Table des matières
- \ref page_README
- \ref page_changelog
- \ref page_about
- \ref page_licence

\page page_about A propos
\author \a Erwan Menella <menella.erwan@gmail.com>
\author \a Fabien Bounoir <bounoirfabien@gmail.com>
\version 0.1
\date \b 2020

\page page_licence Licence GPL

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
