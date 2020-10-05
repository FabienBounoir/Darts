/**
 * Bounoir Fabien - Thierry Vaira
 * 
 * Programme utilisé pour decoder les impacts des fléchettes sur la cible
 * V 0.1
 */

#include <Arduino.h>
//#define DEBUG

#define NB_FLECHETTES 3

const byte numLignes = 7;
const byte numColonnes = 10;
const int Rebond = 100;
int nbFlechettes = 0;

//definition de la matrice avec les differents IMPACT
String ClefRef[numLignes][numColonnes] = {
  {"3;9","3;12","3;5","3;20","3;10","3;6","3;13","3;4","3;18","3;1"},
  {"2;9","2;12","2;5","2;20","2;10","2;6","2;13","2;4","2;18","2;1"},
  {"1;9","1;12","1;5","1;20","1;10","1;6","1;13","1;4","1;18","1;1"},
  {"2;25","1;25","0;0","0;0","0;0","0;0","0;0","0;0","0;0","0;0"},
  {"1;14","1;11","1;8","1;16","1;15","1;2","1;17","1;3","1;19","1;7"},
  {"2;14","2;11","2;8","2;16","2;15","2;2","2;17","2;3","2;19","2;7"},
  {"3;14","3;11","3;8","3;16","3;15","3;2","3;17","3;3","3;19","3;7"} //cette ligne ne marche pas
};
byte PinsLignes[numLignes] = {A0, A1, A2, A3, A4, A5, 3}; //se connecter aux pin de ligne 
byte PinsColognes[numColonnes] = {13, 12, 11, 10, 9, 8 ,7, 6, 5, 4 }; //se connecter aux pin Colonne

String getKey();

void setup()
{
  Serial.begin(9600);
  for (int ligne = 0; ligne < numLignes; ligne++)
  {
    pinMode(PinsLignes[ligne],INPUT_PULLUP);       // Définir les pin de ligne comme entrée
    //digitalWrite(PinsLignes[ligne],HIGH);   // Pull-ups
  }
  for (int Colonne = 0; Colonne < numColonnes; Colonne++)
  {
    pinMode(PinsColognes[Colonne],OUTPUT);     // Définir les broches de la Colonne comme sorties 
    digitalWrite(PinsColognes[Colonne],HIGH);
  }
  
  pinMode(2,INPUT_PULLUP);  //pin next player
  #ifdef DEBUG
  Serial.print("CIBLE-DARTS\r\n");
  #endif
}

void loop()
{
    String key = getKey();
    if(key != "")
    {       
      #ifdef DEBUG
      Serial.print("key : ");
      Serial.println(key);
      #endif
      if(nbFlechettes < NB_FLECHETTES)
      {
        //Serial.print("$DART;GAME;" + key + "\r\n");
        //nbFlechettes++;

        nbFlechettes++;
        String n = String(nbFlechettes);
        Serial.print("$DART;GAME;" + key + ";" + n + "\r\n");        
      }
      //delay(100);
    }
    if(digitalRead(2) == LOW) //pin next player
    {
      for(int i=nbFlechettes;i<NB_FLECHETTES;i++)
      {
        //Serial.print("$DART;GAME;0;0\r\n");
        //delay(100);

        String n = String(nbFlechettes);
        Serial.print("$DART;GAME;0;0;" + n + "\r\n");
        delay(100);
      }
      Serial.print("$DART;NEXT\r\n");
      nbFlechettes = 0;
      delay(500);
    }  
}

// return l'impact de la cible
String getKey()
{
  String key = "";

  for(int Colonne = 0; Colonne < numColonnes; Colonne++)
  {
    digitalWrite(PinsColognes[Colonne],LOW);
    for(int ligne = 0; ligne < numLignes; ligne++)     // Scan ligne
    {
      if(digitalRead(PinsLignes[ligne]) == LOW)
      {
        #ifdef DEBUG
        Serial.print("ligne : "); Serial.println(ligne);
        #endif
        delay(Rebond);
        while(digitalRead(PinsLignes[ligne]) == LOW)
            ;                                  // attendre que l'impact soit libérée
        key = ClefRef[ligne][Colonne];
        digitalWrite(PinsColognes[Colonne],HIGH);
        return key;  // renvoie l'endroit de la cible touchée
      }
    }
    digitalWrite(PinsColognes[Colonne],HIGH);
  }
  return key;
}
