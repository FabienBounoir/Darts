package com.lasalle.darts;

import android.content.Context;
import android.database.sqlite.*;
import android.util.Log;

import java.io.File;

/**
 * Classe ServeurSQLite définissant les caratéristiques de la base de données.
 */
public class ServeurSQLite extends SQLiteOpenHelper
{
    /**
     * Attributs de la classe ServeurSQLite.
     */
    public static final String DATABASE_NAME = "darts.db";
    public static final int    DATABASE_VERSION = 1;

    /* TABLE DES JOUEURS */
    public static final String TABLE_JOUEURS = "table_joueurs";
    public static final String COL_ID_JOUEUR = "ID_JOUEUR";
    public static final String COL_NOM = "NOM";
    public static final int    NUM_COL_ID_JOUEUR = 0;
    public static final int    NUM_COL_NOM = 1;

    /* TABLE DES SEANCES */
    public static final String TABLE_PARTIES = "table_parties";
    public static final String COL_ID = "ID_PARTIE";
    public static final String COL_MOYENNE_VOLEES = "MOYENNE_VOLEES";
    public static final String COL_NB_VOLEES = "NB_VOLEES";
    public static final String COL_VOLEE_MAX = "VOLEE_MAX";
    public static final String COL_RESULTAT = "RESULTAT";
    public static final String COL_DATE_DEBUT = "DATE_DEBUT"; //DATETIME : format "YYYY-MM-DD HH:MM:SS"
    public static final String COL_DUREE = "DUREE"; //TIME : format "HH:MM:SS"
    public static final String COL_TYPE = "TYPE";
    public static final int    NUM_COL_ID = 0;
    public static final int    NUM_COL_MOYENNE_VOLEES = 1;
    public static final int    NUM_COL_NB_VOLEES = 2;
    public static final int    NUM_COL_VOLEE_MAX = 3;
    public static final int    NUM_COL_RESULTAT = 4;
    public static final int    NUM_COL_DATE_DEBUT = 5;
    public static final int    NUM_COL_DUREE = 6;
    public static final int    NUM_COL_TYPE = 7;
    public static final int    NUM_COL_ID_JOUEUR_SEANCE = 8;

    public static final String CREATE_BDD_JOUEURS = "CREATE TABLE " + TABLE_JOUEURS + " ("
            + COL_ID_JOUEUR + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NOM + " VARCHAR(255) NOT NULL);";

    public static final String CREATE_BDD_PARTIES = "CREATE TABLE " + TABLE_PARTIES + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_MOYENNE_VOLEES + " INTEGER NOT NULL,"
            + COL_NB_VOLEES + " INTEGER NOT NULL, "
            + COL_VOLEE_MAX + " VARCHAR(1) NOT NULL,"
            + COL_RESULTAT + " INTEGER NOT NULL,"
            + COL_DATE_DEBUT + " DATETIME NOT NULL,"
            + COL_DUREE + " TIME NOT NULL,"
            + COL_TYPE + " VARCHAR(32) NOT NULL,"
            + COL_ID_JOUEUR + " INTEGER NOT NULL,"
            + "CONSTRAINT fk_seances_1 FOREIGN KEY (ID_JOUEUR) REFERENCES table_joueurs (ID_JOUEUR) ON DELETE CASCADE);";

    private static final String INSERT_TABLE_JOUEURS_1 = "INSERT INTO table_joueurs(NOM) VALUES('Thierry');";
    private static final String INSERT_TABLE_JOUEURS_2 = "INSERT INTO table_joueurs(NOM) VALUES('Christophe');";
    private static final String INSERT_TABLE_JOUEURS_3 = "INSERT INTO table_joueurs(NOM) VALUES('Eric');";
    private static final String INSERT_TABLE_JOUEURS_4 = "INSERT INTO table_joueurs(NOM) VALUES('Gilles');";
    private static final String INSERT_TABLE_JOUEURS_5 = "INSERT INTO table_joueurs(NOM) VALUES('Julien');";
    private static final String INSERT_TABLE_JOUEURS_6 = "INSERT INTO table_joueurs(NOM) VALUES('Patrick');";
    private static final String INSERT_TABLE_JOUEURS_7 = "INSERT INTO table_joueurs(NOM) VALUES('Jérôme');";

    /**
     * Méthode ServeurSQLite constructeur de la classe ServeurSQLite.
     *
     * @param context
     */
    public ServeurSQLite(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Méthode onCreate appellée à la création de l'objet et permettant l'exécution des requêtes créant les tables de la base de données.
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // On crée la table des séances
        db.execSQL("pragma foreign_keys = on;");
        db.execSQL(CREATE_BDD_JOUEURS);
        db.execSQL(CREATE_BDD_PARTIES);
        db.execSQL(INSERT_TABLE_JOUEURS_1);
        db.execSQL(INSERT_TABLE_JOUEURS_2);
        db.execSQL(INSERT_TABLE_JOUEURS_3);
        db.execSQL(INSERT_TABLE_JOUEURS_4);
        db.execSQL(INSERT_TABLE_JOUEURS_5);
        db.execSQL(INSERT_TABLE_JOUEURS_6);
        db.execSQL(INSERT_TABLE_JOUEURS_7);

        String path = db.getPath();
        File f = new File(path);
        boolean r = f.setReadable(true, false);
        r = f.setWritable(true, false);
        File parentDir = f.getAbsoluteFile().getParentFile();
        r = parentDir.setReadable(true, false);
        r = parentDir.setWritable(true, false);
    }

    /**
     * Méthode onUpgrade supprimant et recréant toutes les tables de la base de données.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // On supprime la table puis on la recrée
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOUEURS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTIES + ";");
        onCreate(db);
    }

    /**
     * @brief Ajoute les droits en lecture et en écriture à la base de données lors de son ouverture
     * @param db un type SQLiteDatabase qui représente la base de données
     */
    @Override
    public void onOpen(SQLiteDatabase db)
    {
        //onUpgrade(db, 1, 2);

        String path = db.getPath();
        File f = new File(path);
        boolean r = f.setReadable(true, false);
        r = f.setWritable(true, false);
        File parentDir = f.getAbsoluteFile().getParentFile();
        r = parentDir.setReadable(true, false);
        r = parentDir.setWritable(true, false);
    }
}
