package com.lasalle.darts;

public class Protocole
{
    /**
     * Général
     */
    public static final String EN_TETE = "$DART";
    public static final String DELIMITEUR_CHAMP = ";";
    public static final String DELIMITEUR_FIN = "\r\n";
    /**
     * Trame GAME
     */
    //public static final int TRAME_GAME_NB_CHAMPS = 4;
    public static final int TRAME_GAME_NB_CHAMPS = 5;
    public static final String TRAME_GAME = "GAME";
    public static final String TRAME_NEXT = "NEXT";
}
