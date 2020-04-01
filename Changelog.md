\page page_changelog Changelog


r92 | fbounoir | 2020-04-01 13:50:07 +0200 (mer. 01 avril 2020) | 2 lignes

modification du style sheet de l'application
pour prendre en compte lorsque la partie dure plus que 1 heure

r90 | fbounoir | 2020-04-01 11:42:11 +0200 (mer. 01 avril 2020) | 2 lignes

Modification affichage Joueur et Score
--> pour un meilleur affichage lorsqu'il y a trop de joueur

r89 | emenella | 2020-04-01 11:24:40 +0200 (mer. 01 avril 2020) | 1 ligne

Ajout et modification de commentaire + configuration par defauts si il y a aucun joueur reseigner

r88 | fbounoir | 2020-03-30 23:12:16 +0200 (lun. 30 mars 2020) | 1 ligne

correction de bug qui ne mettait pas en pause la partie lors du lancement du jeu

r87 | fbounoir | 2020-03-29 20:01:44 +0200 (dim. 29 mars 2020) | 4 lignes


correction d'un bug :
-> lors du lancement d'une partie avec l'affichage des regles,
cela provoquer un bug qui enlever l'affichage de la cible.

r86 | fbounoir | 2020-03-27 23:39:07 +0100 (ven. 27 mars 2020) | 6 lignes

ajout commentaire de code

changement d'iteration

correction du bug qui relancer l'affichage du logo Pause
quand on recoit une trame REGLE alors qu'on est en Pause

r85 | fbounoir | 2020-03-27 21:08:06 +0100 (ven. 27 mars 2020) | 2 lignes

rectification bug de son lors du lancement des video des regles
correctif verification trame REGLE

r84 | fbounoir | 2020-03-27 19:00:27 +0100 (ven. 27 mars 2020) | 1 ligne

modification trame REGLE et ca gestion

r83 | fbounoir | 2020-03-27 15:56:58 +0100 (ven. 27 mars 2020) | 3 lignes

modification trame START
pour accueillir le nouveau parametre
-->afficher regle (1= true / 0 = false)

r82 | fbounoir | 2020-03-27 14:58:02 +0100 (ven. 27 mars 2020) | 2 lignes

ajout de la gestion trame REGLE 
cette trame et valide quand la partie et deja en cours

r81 | fbounoir | 2020-03-27 12:09:28 +0100 (ven. 27 mars 2020) | 1 ligne

ajout de l'ecran regle pour visualiser les regles du jeu

r80 | emenella | 2020-03-26 17:09:50 +0100 (jeu. 26 mars 2020) | 1 ligne

Ajout IHM de fin partie

r77 | emenella | 2020-03-25 17:42:06 +0100 (mer. 25 mars 2020) | 1 ligne

IHM du déroulement d'une partie avec le score des joueur, les impact de la volley et indication du joueur qui doit jouer.

r76 | tvaira | 2020-03-23 18:25:54 +0100 (lun. 23 mars 2020) | 1 ligne

Maj Doxyfile

r75 | emenella | 2020-03-23 18:24:34 +0100 (lun. 23 mars 2020) | 1 ligne

Déroulement de la partie + envoyer des message vers IHM

r74 | tvaira | 2020-03-23 18:22:43 +0100 (lun. 23 mars 2020) | 2 lignes

Révision de code pour Doxygen


r73 | tvaira | 2020-03-21 07:47:58 +0100 (sam. 21 mars 2020) | 2 lignes

Révision de code


r72 | tvaira | 2020-03-19 09:52:17 +0100 (jeu. 19 mars 2020) | 1 ligne

Révision de code

r71 | tvaira | 2020-03-19 08:18:29 +0100 (jeu. 19 mars 2020) | 2 lignes

Ajout de la méthode calculerPoints()


r70 | emenella | 2020-03-18 09:28:43 +0100 (mer. 18 mars 2020) | 1 ligne

Ajout du Bluetooth (connection : OK, envoyer : OK, recevoir : En Cours)

r69 | tvaira | 2020-03-18 08:07:15 +0100 (mer. 18 mars 2020) | 1 ligne

fichier à ignorer

r68 | fbounoir | 2020-03-14 22:55:40 +0100 (sam. 14 mars 2020) | 5 lignes

modification affichage :
- la moyenne  n'affiche plus les decimales.
- changement de la police de la partie actualiter de la partie pour etre compatible avec la raspberry
- ajout de fleche pour mettre en avant la possibiliter de finir la partie
- modification de son de l'ihm

r67 | fbounoir | 2020-03-14 11:37:07 +0100 (sam. 14 mars 2020) | 4 lignes


modification Police interface jeu.
modification layout ecran de jeu


r66 | fbounoir | 2020-03-14 00:47:14 +0100 (sam. 14 mars 2020) | 1 ligne

correction probleme declaration fichier ressource.qrc

r65 | fbounoir | 2020-03-14 00:35:15 +0100 (sam. 14 mars 2020) | 1 ligne

modification design IHM

r64 | fbounoir | 2020-03-13 20:29:12 +0100 (ven. 13 mars 2020) | 2 lignes

remédiation de code
ajout de commentaire de code

r63 | fbounoir | 2020-03-13 20:10:01 +0100 (ven. 13 mars 2020) | 4 lignes

modification affichage :
modification affichage de la partie (score, type ...)

ajout d'un fond pour la partie affichage de la partie.

r62 | fbounoir | 2020-03-13 19:14:58 +0100 (ven. 13 mars 2020) | 2 lignes

modification affichage :
message de statut sur l'ecran de Jeu

r61 | fbounoir | 2020-03-13 17:11:05 +0100 (ven. 13 mars 2020) | 1 ligne

modification etat Pause partie

r60 | emenella | 2020-03-13 17:07:36 +0100 (ven. 13 mars 2020) | 1 ligne

Mis en place du bluetooth

r59 | emenella | 2020-03-13 17:02:18 +0100 (ven. 13 mars 2020) | 1 ligne

Mis en place du bluetooth

r58 | emenella | 2020-03-13 16:57:59 +0100 (ven. 13 mars 2020) | 1 ligne

Mis en place du bluetooth

r57 | fbounoir | 2020-03-13 14:16:47 +0100 (ven. 13 mars 2020) | 1 ligne

ajout icone Pause

r56 | fbounoir | 2020-03-12 22:39:15 +0100 (jeu. 12 mars 2020) | 1 ligne

ajout son Ecran d'attente

r55 | emenella | 2020-03-12 15:42:53 +0100 (jeu. 12 mars 2020) | 1 ligne

Ajout de methode pour recuperer le nom du joueur

r54 | fbounoir | 2020-03-12 15:30:09 +0100 (jeu. 12 mars 2020) | 1 ligne

optimisation du code

r53 | fbounoir | 2020-03-11 22:24:16 +0100 (mer. 11 mars 2020) | 1 ligne

optimisation du code

r52 | fbounoir | 2020-03-11 19:34:09 +0100 (mer. 11 mars 2020) | 4 lignes

ajout compatibiliter Mode de Jeu :
101 à 1001 --> Sans double out
101 à 1001 --> avec double out


r51 | fbounoir | 2020-03-11 18:55:21 +0100 (mer. 11 mars 2020) | 4 lignes


optimisation du code
optimisation du visuel
compression background

r50 | fbounoir | 2020-03-11 12:20:37 +0100 (mer. 11 mars 2020) | 1 ligne

ajout des Visuel Imapct dans le depot

r49 | fbounoir | 2020-03-10 19:19:35 +0100 (mar. 10 mars 2020) | 1 ligne

optimisation du code

r48 | fbounoir | 2020-03-10 16:54:17 +0100 (mar. 10 mars 2020) | 2 lignes

ajout possibiliter de jouer seul
si le joueur tombe a 1 point il est eliminer

r47 | emenella | 2020-03-10 16:46:51 +0100 (mar. 10 mars 2020) | 1 ligne

Ajout d'activité pour créer une partie et des joueurs

r46 | emenella | 2020-03-10 16:45:49 +0100 (mar. 10 mars 2020) | 1 ligne

Ajout d'activité pour créer une partie et des joueurs

r45 | fbounoir | 2020-03-09 22:27:57 +0100 (lun. 09 mars 2020) | 1 ligne

ajout de la visualisation de la volées entière au lieu d'avoir juste l'impact actuel

r44 | tvaira | 2020-03-09 18:30:02 +0100 (lun. 09 mars 2020) | 1 ligne

Modification méthode extraireParametresTrameStart()

r43 | tvaira | 2020-03-09 18:27:39 +0100 (lun. 09 mars 2020) | 1 ligne

Modification méthode extraireParametresTrameStart()

r42 | tvaira | 2020-03-09 18:24:15 +0100 (lun. 09 mars 2020) | 1 ligne

Modification méthode extraireParametresTrameStart()

r41 | fbounoir | 2020-03-09 17:20:58 +0100 (lun. 09 mars 2020) | 1 ligne

amelioration du code

r40 | tvaira | 2020-03-09 08:25:31 +0100 (lun. 09 mars 2020) | 2 lignes

Révision de code (2° partie)


r39 | fbounoir | 2020-03-08 11:45:28 +0100 (dim. 08 mars 2020) | 1 ligne

correction de signaux manquant

r38 | tvaira | 2020-03-08 10:18:52 +0100 (dim. 08 mars 2020) | 1 ligne

Revision de code

r37 | tvaira | 2020-03-08 09:15:14 +0100 (dim. 08 mars 2020) | 2 lignes

Révision de code


r36 | fbounoir | 2020-03-06 19:23:27 +0100 (ven. 06 mars 2020) | 1 ligne

mise à jour du code pour accueilir le nouveau protocol de transmission V0.2

r35 | fbounoir | 2020-03-06 11:58:56 +0100 (ven. 06 mars 2020) | 1 ligne

ajout de la suppresion des joueurs

r34 | fbounoir | 2020-03-05 19:14:12 +0100 (jeu. 05 mars 2020) | 1 ligne

ajout de la gestion des joueurs eliminer (passer au joueur suivant si eliminer)

r33 | fbounoir | 2020-03-05 18:08:09 +0100 (jeu. 05 mars 2020) | 1 ligne

ajout de la mise en pause de la partie si l'appareil bluetooth se deconnecte

r32 | fbounoir | 2020-03-05 16:09:14 +0100 (jeu. 05 mars 2020) | 2 lignes

correction probleme protocol trame PLAY


r31 | fbounoir | 2020-03-05 15:05:13 +0100 (jeu. 05 mars 2020) | 1 ligne

ajout gestion trame PAUSE & START

r30 | fbounoir | 2020-03-04 21:07:34 +0100 (mer. 04 mars 2020) | 1 ligne

ajout commentaire de code pour la classe Solution

r29 | fbounoir | 2020-03-04 20:04:33 +0100 (mer. 04 mars 2020) | 1 ligne

ajout recherche meilleur solution possible pour finir la partie

r28 | fbounoir | 2020-03-04 19:05:58 +0100 (mer. 04 mars 2020) | 1 ligne

ajout classe Solution

r27 | fbounoir | 2020-03-04 15:36:16 +0100 (mer. 04 mars 2020) | 1 ligne

ajout chronometre partie

r26 | fbounoir | 2020-02-19 12:15:59 +0100 (mer. 19 févr. 2020) | 7 lignes

ajout compatibiliter avec les modes 
501_DOUBLE_OUT
301_DOUBLE_OUT
501
301

et optimisation du code

r25 | fbounoir | 2020-02-18 22:53:37 +0100 (mar. 18 févr. 2020) | 1 ligne

correction d'un bug qui empêchent la remise à zéro du volé max en changeant de partie

r24 | fbounoir | 2020-02-18 14:40:35 +0100 (mar. 18 févr. 2020) | 1 ligne

ajout de la gestion des joueurs n'ayant pas de nom

r23 | fbounoir | 2020-02-18 12:06:15 +0100 (mar. 18 févr. 2020) | 2 lignes

ajout de la gestion de la trame STOP


r22 | fbounoir | 2020-02-16 23:49:40 +0100 (dim. 16 févr. 2020) | 1 ligne

ajout calcule Moyenne volées

r21 | fbounoir | 2020-02-16 13:27:07 +0100 (dim. 16 févr. 2020) | 1 ligne

commencement creation moyenne Volées

r20 | fbounoir | 2020-02-16 10:39:03 +0100 (dim. 16 févr. 2020) | 1 ligne

ajout comptage nombre de Volées

r19 | fbounoir | 2020-02-16 10:07:29 +0100 (dim. 16 févr. 2020) | 1 ligne

ajout visualisation joueur en train de jouer

r18 | fbounoir | 2020-02-15 22:33:55 +0100 (sam. 15 févr. 2020) | 1 ligne

mise a jour documentation Methode / Attribut

r17 | fbounoir | 2020-02-15 22:33:23 +0100 (sam. 15 févr. 2020) | 1 ligne

ajout Methode qui met a jour la Volées Max

r16 | fbounoir | 2020-02-15 21:49:46 +0100 (sam. 15 févr. 2020) | 1 ligne

ajout calcule volée Max

r15 | fbounoir | 2020-02-15 21:01:26 +0100 (sam. 15 févr. 2020) | 1 ligne

ajout test pour finir partie

r14 | tvaira | 2020-02-15 11:17:59 +0100 (sam. 15 févr. 2020) | 4 lignes

Ajout Doxygen pour les define
Ajout des TODO
Vérification du code


r13 | fbounoir | 2020-02-15 01:53:43 +0100 (sam. 15 févr. 2020) | 1 ligne

ajout classe Joueur

r12 | fbounoir | 2020-02-14 16:25:09 +0100 (ven. 14 févr. 2020) | 1 ligne

ajout initialisation point joueur

r11 | fbounoir | 2020-02-14 14:35:10 +0100 (ven. 14 févr. 2020) | 1 ligne

ajout methode calcul point Impact

r10 | fbounoir | 2020-02-14 11:40:03 +0100 (ven. 14 févr. 2020) | 1 ligne

ajout classe Darts

r9 | emenella | 2020-02-14 08:06:05 +0100 (ven. 14 févr. 2020) | 1 ligne

Ajout du projet Android

r8 | fbounoir | 2020-02-13 21:01:44 +0100 (jeu. 13 févr. 2020) | 1 ligne

ajout fichier Doxyfile

r7 | fbounoir | 2020-02-13 20:08:23 +0100 (jeu. 13 févr. 2020) | 1 ligne

ajout methode decomposer trame

r6 | fbounoir | 2020-02-13 16:13:12 +0100 (jeu. 13 févr. 2020) | 1 ligne

ajout connection bluetooth en mode serveur

r5 | fbounoir | 2020-02-12 22:52:48 +0100 (mer. 12 févr. 2020) | 1 ligne

ajout classe communication

r4 | fbounoir | 2020-02-12 16:16:51 +0100 (mer. 12 févr. 2020) | 1 ligne

ajout fichier ressources.qrc

r3 | fbounoir | 2020-02-12 15:47:20 +0100 (mer. 12 févr. 2020) | 1 ligne

mise a jour Ihm

r2 | fbounoir | 2020-02-12 11:44:10 +0100 (mer. 12 févr. 2020) | 1 ligne

demarrage projet

r1 | www-data | 2020-02-01 15:05:21 +0100 (sam. 01 févr. 2020) | 1 ligne

Creating initial repository structure

