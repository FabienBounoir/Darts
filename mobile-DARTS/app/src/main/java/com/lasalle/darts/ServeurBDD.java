package com.lasalle.darts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe ServeurBDD définnissant les caratéristiques et le comportement d'un serveur de base de données.
 */
public class ServeurBDD
{
    private static final String TAG = "ServeurBDD"; //!< le TAG de la classe pour les logs
    /*
     * Attributs de la classe ServeurBDD.
     */
    private SQLiteDatabase bdd = null;
    private ServeurSQLite serveurSQLite = null;

    /**
     * Méthode ServeurBDD constructeur de la classe ServeurBDD.
     *
     * @param context
     */
    public ServeurBDD(Context context)
    {
        // cn crée la BDD et ses tables
        serveurSQLite = new ServeurSQLite(context);
    }

    /**
     * Méthode open ouvrant la base de données en écriture.
     */
    public void open()
    {
        // on ouvre la BDD en écriture
        if (bdd == null)
            bdd = serveurSQLite.getWritableDatabase();
    }

    /**
     * Méthode close fermant la base de données.
     */
    public void close()
    {
        if (bdd != null)
            if (bdd.isOpen())
                bdd.close();
    }

    /**
     * Méthode getBDD accesseur de l'attribut bdd.
     *
     * @return
     */
    public SQLiteDatabase getBDD()
    {
        return bdd;
    }

    /**
     * Méthode permettant l'insertion d'une partie dans la base de données.
     *
     * @param partie à insérer dans la base de données.
     * @return l'id de la partie dans la base de données.
     */
    public long insererPartie(Partie partie)
    {
        ContentValues values = new ContentValues();

        values.put(ServeurSQLite.COL_MOYENNE_VOLEES, partie.getMoyenneVolees());
        values.put(ServeurSQLite.COL_NB_VOLEES, partie.getNbVolees());
        values.put(ServeurSQLite.COL_VOLEE_MAX, partie.getVoleeMax());
        values.put(ServeurSQLite.COL_RESULTAT, partie.getResultat());
        values.put(ServeurSQLite.COL_DATE_DEBUT, partie.getDateDebut());
        values.put(ServeurSQLite.COL_DUREE, partie.getDuree());
        values.put(ServeurSQLite.COL_TYPE, partie.getType());
        values.put(ServeurSQLite.COL_ID_JOUEUR, partie.getIdJoueur());

        return bdd.insert(ServeurSQLite.TABLE_PARTIES, null, values);
    }

    /**
     * Méthode permettant la suppression d'une partie dans la base de données.
     *
     * @param id étant l'id de la partie à supprimer.
     * @return
     */
    public int supprimerPartie(int id)
    {
        return bdd.delete(ServeurSQLite.TABLE_PARTIES, ServeurSQLite.COL_ID + " = " + id, null);
    }

    /**
     * Méthode permettant l'accès à une partie grâce à son attribut id.
     *
     * @param id étant l'id de la partie à retourner.
     * @return
     */
    public Partie getPartie(int id)
    {
        Cursor c = bdd.query(ServeurSQLite.TABLE_PARTIES, new String[] {ServeurSQLite.COL_ID, ServeurSQLite.COL_MOYENNE_VOLEES, ServeurSQLite.COL_NB_VOLEES, ServeurSQLite.COL_VOLEE_MAX, ServeurSQLite.COL_RESULTAT, ServeurSQLite.COL_DATE_DEBUT, ServeurSQLite.COL_DUREE, ServeurSQLite.COL_TYPE, ServeurSQLite.COL_ID_JOUEUR}, ServeurSQLite.COL_ID + " = " + id, null, null, null, null);

        return cursorToPartie(c, true);
    }

    /**
     * Méthode permettant la purge (suppression totale) de la table des parties.
     */
    public void purgerTableParties()
    {
        bdd.execSQL("DROP TABLE IF EXISTS " + serveurSQLite.TABLE_PARTIES); // Supprimer la table
        bdd.execSQL(ServeurSQLite.CREATE_BDD_PARTIES); // Recréer la table
    }

    /**
     * Méthode permettant la suppression des parties d'un joueur.
     *
     * @param idJoueur étant l'id du joueur auquel on doit purger les parties.
     * @return
     */
    public int purgerPartiesJoueur(int idJoueur)
    {
        return bdd.delete(ServeurSQLite.TABLE_PARTIES, ServeurSQLite.COL_ID_JOUEUR + " = " + idJoueur, null);
    }

    /**
     * Méthode retournant toutes les parties présentes dans la table des parties.
     *
     * @return liste d'objets Partie.
     */
    public List<Partie> getParties()
    {
        List<Partie> parties = new ArrayList<Partie>();

        Cursor cursor = bdd.query(ServeurSQLite.TABLE_PARTIES, new String[] {ServeurSQLite.COL_ID, ServeurSQLite.COL_MOYENNE_VOLEES, ServeurSQLite.COL_NB_VOLEES, ServeurSQLite.COL_VOLEE_MAX, ServeurSQLite.COL_RESULTAT, ServeurSQLite.COL_DATE_DEBUT, ServeurSQLite.COL_DUREE, ServeurSQLite.COL_TYPE, ServeurSQLite.COL_ID_JOUEUR}, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Partie partie = cursorToPartie(cursor, false);
            parties.add(partie);
            cursor.moveToNext();
        }

        cursor.close();

        return parties;
    }

    /**
     * Méthode retournant toutes les parties présentes dans la table des parties ayant pour id idJoueur.
     *
     * @param idJoueur
     * @return liste d'objets Partie.
     */
    public List<Partie> getParties(int idJoueur)
    {
        List<Partie> parties = new ArrayList<Partie>();

        Cursor cursor = bdd.query(ServeurSQLite.TABLE_PARTIES, new String[] {ServeurSQLite.COL_ID, ServeurSQLite.COL_MOYENNE_VOLEES, ServeurSQLite.COL_NB_VOLEES, ServeurSQLite.COL_VOLEE_MAX, ServeurSQLite.COL_RESULTAT, ServeurSQLite.COL_DATE_DEBUT, ServeurSQLite.COL_DUREE, ServeurSQLite.COL_TYPE, ServeurSQLite.COL_ID_JOUEUR},  ServeurSQLite.COL_ID_JOUEUR + " = " + idJoueur, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Partie partie = cursorToPartie(cursor, false);
            parties.add(partie);
            cursor.moveToNext();
        }

        cursor.close();

        return parties;
    }

    /**
     * Méthode cursorToPartie permettant de convertir un curseur en un objet de type Seance.
     *
     * @param c
     * @param one
     * @return
     */
    private Partie cursorToPartie(Cursor c, boolean one)
    {
        if (c.getCount() == 0)
            return null;

        if(one == true)
            c.moveToFirst();

        Partie partie = new Partie();

        partie.setId(c.getInt(ServeurSQLite.NUM_COL_ID));
        partie.setMoyenneVolees(c.getInt(ServeurSQLite.NUM_COL_MOYENNE_VOLEES));
        partie.setNbVolees(c.getInt(ServeurSQLite.NUM_COL_NB_VOLEES));
        partie.setVoleeMax(c.getInt(ServeurSQLite.NUM_COL_VOLEE_MAX));
        partie.setResultat(c.getInt(ServeurSQLite.NUM_COL_RESULTAT)>0);
        partie.setDateDebut(c.getString(ServeurSQLite.NUM_COL_DATE_DEBUT));
        partie.setDuree(c.getString(ServeurSQLite.NUM_COL_DUREE));
        partie.setType(c.getString(ServeurSQLite.NUM_COL_TYPE));
        partie.setIdJoueur(c.getInt(ServeurSQLite.NUM_COL_ID_JOUEUR_SEANCE));
        Joueur joueur = getJoueur(c.getInt(ServeurSQLite.NUM_COL_ID_JOUEUR_SEANCE));
        partie.setNomJoueur(joueur.getNom());

        if(one == true)
            c.close();

        return partie;
    }

    /**
     * Méthode insererJoueur permettant l'insertion d'un joueur.
     *
     * @param joueur étant le joueur à insérer dans la base de données.
     * @return l'id dans la base de données.
     */
    public long insererJoueur(Joueur joueur)
    {
        ContentValues values = new ContentValues();

        //values.put(ServeurSQLite.COL_ID_JOUEUR, joueur.getId());
        values.put(ServeurSQLite.COL_NOM, joueur.getNom());

        return bdd.insert(ServeurSQLite.TABLE_JOUEURS, null, values);
    }

    /**
     * Méthode supprimerJoueur permettant la suppression d'un joueur.
     *
     * @param id étant l'id du joueur à supprimer.
     * @return
     */
    public int supprimerJoueur(int id)
    {
        int retour = bdd.delete(ServeurSQLite.TABLE_JOUEURS, ServeurSQLite.COL_ID_JOUEUR + " = " + id, null);
        return retour;
    }

    /**
     * Méthode supprimerJoueur permettant la suppression d'un joueur.
     *
     * @param nom String le nom du joueur à supprimer.
     * @return
     */
    public int supprimerJoueur(String nom)
    {
        return bdd.delete(ServeurSQLite.TABLE_JOUEURS, ServeurSQLite.COL_NOM + " = '" + nom + "'", null);
    }

    /**
     * Méthode getJoueur permettant l'accès à un joueur grâce à son attribut id.
     *
     * @param id étant l'id du joueur à retourner.
     * @return
     */
    public Joueur getJoueur(int id)
    {
        Cursor c = bdd.query(ServeurSQLite.TABLE_JOUEURS, new String[] {ServeurSQLite.COL_ID_JOUEUR, ServeurSQLite.COL_NOM}, ServeurSQLite.COL_ID_JOUEUR + " = " + id, null, null, null, null);

        return cursorToJoueur(c, true);
    }

    /**
     * Méthode getJoueur permettant l'accès à un joueur grâce à son nom.
     *
     * @param nom String le nom du joueur à retourner.
     * @return
     */
    public Joueur getJoueur(String nom)
    {
        Cursor c = bdd.query(ServeurSQLite.TABLE_JOUEURS, new String[] {ServeurSQLite.COL_ID_JOUEUR, ServeurSQLite.COL_NOM}, ServeurSQLite.COL_NOM + " = '" + nom + "'", null, null, null, null);

        return cursorToJoueur(c, true);
    }

    /**
     * Méthode purgerTableJoueurs permettant la purge (suppression totale) de la table des joueurs.
     */
    public void purgerTableJoueurs()
    {
        bdd.execSQL("DROP TABLE IF EXISTS " + serveurSQLite.TABLE_JOUEURS); // Supprimer la table
        bdd.execSQL(ServeurSQLite.CREATE_BDD_JOUEURS); // Recréer la table
    }

    /**
     * Méthode getJoueurs retournant tous les joueurs présents dans la table des joueurs.
     *
     * @return liste d'objets Joueur.
     */
    public List<Joueur> getJoueurs()
    {
        List<Joueur> joueurs = new ArrayList<Joueur>();

        Cursor cursor = bdd.query(ServeurSQLite.TABLE_JOUEURS, new String[] {ServeurSQLite.COL_ID_JOUEUR, ServeurSQLite.COL_NOM}, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Joueur joueur = cursorToJoueur(cursor, false);
            joueurs.add(joueur);
            cursor.moveToNext();
        }

        cursor.close();

        return joueurs;
    }

    /**
     * Méthode cursorToJoueur permettant de convertir un curseur en un objet de type Joueur.
     *
     * @param c
     * @param one
     * @return
     */
    private Joueur cursorToJoueur(Cursor c, boolean one)
    {
        if (c.getCount() == 0)
            return null;

        if(one == true)
            c.moveToFirst();

        Joueur joueur = new Joueur();

        joueur.setId(c.getInt(ServeurSQLite.NUM_COL_ID_JOUEUR));
        joueur.setNom(c.getString(ServeurSQLite.NUM_COL_NOM));

        String requeteSQL = "SELECT COUNT(*) FROM table_parties WHERE ID_JOUEUR = ? AND RESULTAT=1";
        int nbVictoires = getValeur(requeteSQL, String.valueOf(c.getInt(ServeurSQLite.NUM_COL_ID_JOUEUR)));
        joueur.setNbVictoires(nbVictoires);

        requeteSQL = "SELECT COUNT(*) FROM table_parties WHERE ID_JOUEUR = ?";
        int nbParties = getValeur(requeteSQL, String.valueOf(c.getInt(ServeurSQLite.NUM_COL_ID_JOUEUR)));
        joueur.setNbParties(nbParties);

        if(one == true)
            c.close();

        return joueur;
    }

    public int getValeur(String requete, String id)
    {
        Cursor c = bdd.rawQuery(requete, new String[] { id });

        if (c.getCount() != 1)
            return -1;

        c.moveToFirst();

        int valeur = c.getInt(0);

        c.close();

        return valeur;
    }
}
