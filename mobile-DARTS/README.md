\mainpage Le projet 

\tableofcontents

\section section_tdm Table des matières
- \ref page_README
- \ref page_changelog
- \ref page_about
- \ref page_licence

\section section_infos Informations

\author Erwan Menella <menella.erwan@gmail.com>
\date 2020
\version 0.2
\see https://svn.riouxsvn.com/darts-2020/


\page page_README README

[TOC]

# Projet {#projet}

## Présentation {#presentation}

Le système **DARTS** est un système numérique permettant de jouer au jeu de fléchettes électroniques.

Le système DARTS est décomposé en trois modules, dont deux modules sont réalisés par des étudiants IR :

* Module de gestion de partie​ (**Mobile-DARTS**)​ : les joueurs paramètrent et lancent la partie à partir d'une application sur un terminal mobile (sous Android) ;

* Module de détection des impacts​ (Cible-DARTS)​ : la cible est équipée de capteurs permettant d'identifier la zone impactée par les fléchettes envoyées par les joueurs ;

* Module de visualisation de partie​ (**Écran-DARTS**) ​ : les joueurs, les arbitres et le public peuvent visualiser en “temps réel” le déroulement de la partie (nombre de manche, point restant dans la manche, moyenne des volées, ...) sur un écran de télévision.

## Module de gestion de partie (Mobile-DARTS) {#mobile}

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

## Informations {#informations}

\author Erwan Menella <menella.erwan@gmail.com>
\date 2020
\version 0.2
\see https://svn.riouxsvn.com/darts-2020/


\page page_about A propos

\author Erwan Menella <menella.erwan@gmail.com>
\date 2020
\version 0.2
\see https://svn.riouxsvn.com/darts-2020/


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
