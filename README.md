# Projet

## Présentation

Le système **DARTS** est un système numérique permettant de jouer au jeu de fléchettes électroniques.

Le système DARTS est décomposé en trois modules, dont deux modules sont réalisés par des étudiants IR :

* Module de gestion de partie (**Mobile-DARTS**) : les joueurs paramètrent et lancent la partie à partir d'une application sur un terminal mobile (sous Android) ;

* Module de détection des impacts (**Cible-DARTS** ) : la cible est équipée de capteurs permettant d'identifier la zone impactée par les fléchettes envoyées par les joueurs ;

* Module de visualisation de partie (**Écran-DARTS** : les joueurs, les arbitres et le public peuvent visualiser en “temps réel” le déroulement de la partie (nombre de manche, point restant dans la manche, moyenne des volées, ...) sur un écran de télévision.

Plus d'information : https://docs.google.com/document/d/1ErG-AuB1AdsGreX-ljjOWLvSdfRazHPk-4TMnNLcXbg/edit?usp=sharing

## Module de visualisation de partie (Écran-DARTS)

Ce module correspond à la partie "affichage" du système. Il a pour objectifs de réaliser la récupération d'informations envoyées par le terminal mobile, le calcul et l'affichage les statistiques pour la partie actuelle. Il communique en Bluetooth uniquement avec le terminal mobile Android.

Sur l'écran, les joueurs pourront visualiser en continu :

* le nom des joueurs (si existant), la durée écoulée de la partie ;
* le type de jeu en cours, le score et le nombre de manches gagnées par chaque joueur
* la plus haute et la moyenne des volées de chaque joueur

Les données visualisées sont donc :

* Le type de jeu
* Le numéro de la manche
* Le score de la partie en cours
* La moyenne des volées

## Video Presentation Projet

* Presentation général Projet DARTS + démonstration : https://youtu.be/7VGxQW2TQps

* Mode de Jeu Double OUT : https://youtu.be/zoXQc0yn8vI

* Mode de Jeu sans Double OUT : https://youtu.be/EAdACx7pUR8

## Informations

Auteur **Fabien Bounoir** <bounoirfabien@gmail.com>
date 2020
version 1.0
see https://svn.riouxsvn.com/darts-2020/


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
