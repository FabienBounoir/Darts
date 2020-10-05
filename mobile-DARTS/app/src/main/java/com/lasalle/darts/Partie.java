package com.lasalle.darts;

import android.util.Log;
import android.widget.TableRow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Partie
{
    private static final String TAG = "Partie"; //!< le TAG de la classe pour les logs
    public static final int FINIE = 0;
    public static final int EN_COURS = 1;
    public static final int EN_PAUSE = 2;
    public static final int TERMINEE = 3;
    private int id;
    private int totalVolees;
    private int moyenneVolees;
    private int nbVolees;
    private int voleeMax;
    private String voleeJouee;
    private List<String> historiqueVolees;
    private int volee;
    private boolean resultat;
    private String dateDebut;
    private String duree;
    private String type;
    private int idJoueur;
    private String nomJoueur;

    /**
     * Constructeur par défaut de la classe Partie
     */
    public Partie()
    {
        this.id = -1;
        this.totalVolees = 0;
        this.moyenneVolees = 0;
        this.nbVolees = 0;
        this.voleeMax = 0;
        this.voleeJouee = "";
        this.historiqueVolees = new ArrayList<String>();
        this.volee = 0;
        this.resultat = false;
        this.dateDebut = horodaterBD();
        this.duree = "00:00:00";
        this.type = "";
        this.idJoueur = -1;
        this.nomJoueur = "";
    }

    /**
     * Méthode Constructeur de la classe Partie
     *
     * @param idJoueur
     */
    public Partie(int idJoueur)
    {
        this.id = -1;
        this.totalVolees = 0;
        this.moyenneVolees = 0;
        this.nbVolees = 0;
        this.voleeMax = 0;
        this.voleeJouee = "";
        this.historiqueVolees = new ArrayList<String>();
        this.volee = 0;
        this.resultat = false;
        this.dateDebut = horodaterBD();
        this.duree = "00:00:00";
        this.type = "";
        this.idJoueur = idJoueur;
        this.nomJoueur = "";
    }

    public String horodaterBD()
    {
        Calendar calendar = Calendar.getInstance();
        // Format SQLite : "2018-04-28 14:11:52"
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String strDate = simpleDateFormat.format(calendar.getTime());
        return strDate;
    }

    /**
     * Méthode getId accesseur de l'attribut id.
     *
     * @return id
     */
    public int getId() { return id; }

    /**
     * Méthode setId mutateur de l'attribut id.
     *
     * @param id étant l'id à affecter.
     */
    public void setId(int id) { this.id = id; }

    public int getMoyenneVolees() { return moyenneVolees; }
    public void setMoyenneVolees(int moyenneVolees) { this.moyenneVolees = moyenneVolees; }
    public int getNbVolees() { return nbVolees; }
    public void setNbVolees(int nbVolees) { this.nbVolees = nbVolees; }
    public int getVoleeMax() { return voleeMax; }
    public void setVoleeMax(int voleeMax) { this.voleeMax = voleeMax; }
    public String getVoleeJouee() { return voleeJouee; }
    public void setVoleeJouee(String voleeJouee) { this.voleeJouee = voleeJouee; }
    public void sauvegarderVoleeJouee()
    {
        historiqueVolees.add(voleeJouee);
    }
    public void resetVoleeJouee()
    {
        this.voleeJouee = "";
    }
    public List<String> getHistoriqueVolees() { return historiqueVolees; }
    public int getVolee() { return volee; }
    public void setVolee(int volee) {
        this.volee = volee;
        this.totalVolees += this.volee;
        this.nbVolees++;
        this.moyenneVolees = this.totalVolees / this.nbVolees;
        Log.d(TAG, "Volee : volee = " + volee + " total = " + totalVolees + " nb = " + nbVolees + " moyenne = " + moyenneVolees);
        if(this.volee > this.voleeMax)
            this.voleeMax = this.volee;
    }
    public boolean getResultat() { return resultat; }
    public void setResultat(boolean resultat) { this.resultat = resultat; }
    public String getDateDebut()
    {
        return dateDebut;
    }
    public void setDateDebut(String dateDebut)
    {
        this.dateDebut = dateDebut;
    }
    public String getDuree()
    {
        return duree;
    }
    public void setDuree(String duree)
    {
        this.duree = duree;
    }
    public void setDuree()
    {
        String dateFin = horodaterBD();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date debut = new Date();
        try
        {
            debut = (Date)formatter.parse(dateDebut);
        }
        catch (Exception e)
        {
            //Log.d(TAG, "afficherBoiteDialogueDetailsSeance() : " + e);
        }

        Date fin  = new Date();
        try
        {
            fin = (Date)formatter.parse(dateFin);
        }
        catch (Exception e)
        {
            //Log.d(TAG, "afficherBoiteDialogueDetailsSeance() : " + e);
        }

        long temps = fin.getTime() - debut.getTime();
        this.duree = String.format("%02d:%02d:%02d", (temps/1000) / 3600, ((temps/1000) % 3600) / 60, ((temps/1000) % 60));
    }
    public int getIdJoueur()
    {
        return idJoueur;
    }
    public void setIdJoueur(int idJoueur)
    {
        this.idJoueur = idJoueur;
    }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getNomJoueur() { return nomJoueur; }
    public void setNomJoueur(String nomJoueur) { this.nomJoueur = nomJoueur; }
    public void initialiser(int idJoueur, String nomJoueur, String type)
    {
        this.totalVolees = 0;
        this.moyenneVolees = 0;
        this.nbVolees = 0;
        this.voleeMax = 0;
        this.voleeJouee = "";
        this.historiqueVolees.clear();
        this.volee = 0;
        this.resultat = false;
        this.dateDebut = horodaterBD();
        this.duree = "00:00:00";
        this.type = type;
        this.idJoueur = idJoueur;
        this.nomJoueur = nomJoueur;
    }

    /**
     * Méthode toString permettant la visualisation des caractéristiques de la séance.
     *
     * @return les caractéristiques de la séance.
     */
    public String toString()
    {
        return "Moyenne volées : " + moyenneVolees + "\nNombre de volées : " + nbVolees + "\nVolée max : " + voleeMax + "\nRésultat : " + resultat + "\nDate début : " + dateDebut + "\nDurée : " + duree + "\nType : " + type + "\nID joueur : " + idJoueur;
    }
}
