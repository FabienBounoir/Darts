package com.lasalle.darts;

public class Joueur
{
    private int id;
    private String nom;
    private int points;
    private int nbVictoires;
    private int nbParties;
    private boolean selectionne;
    private boolean active;
    private Partie partie;

    public Joueur()
    {
        this.nom = "";
        this.points = 0;
        this.nbVictoires = 0;
        this.nbParties = 0;
        this.selectionne = false;
        this.active = false;
        this.partie = new Partie();
    }

    public Joueur(String nom)
    {
        this.nom = nom;
        this.points = 0;
        this.nbVictoires = 0;
        this.nbParties = 0;
        this.selectionne = false;
        this.active = false;
        this.partie = new Partie();
    }

    public Joueur(String nom, int points)
    {
        this.nom = nom;
        this.points = points;
        this.nbVictoires = 0;
        this.nbParties = 0;
        this.selectionne = false;
        this.active = false;
        this.partie = new Partie();
    }

    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
        partie.setIdJoueur(id);
    }
    public String getNom()
    {
        return nom;
    }
    public void setNom(String nom)
    {
        this.nom = nom;
    }
    public int getPoints()
    {
        return points;
    }
    public void setPoints(int points)
    {
        this.points = points;
    }
    public int getNbVictoires()
    {
        return nbVictoires;
    }
    public void setNbVictoires(int nbVictoires)
    {
        this.nbVictoires = nbVictoires;
    }
    public int getNbParties()
    {
        return nbParties;
    }
    public void setNbParties(int nbParties)
    {
        this.nbParties = nbParties;
    }
    public boolean getSelectionne()
    {
        return selectionne;
    }
    public void setSelectionne(boolean selectionne)
    {
        this.selectionne = selectionne;
    }
    public boolean getActive()
    {
        return active;
    }
    public void setActive(boolean active)
    {
        this.active = active;
    }
    public Partie getPartie()
    {
        return partie;
    }
    public void initialiserPartie(String type)
    {
        if(selectionne)
        {
            active = false;
            partie.initialiser(id, nom, type);
            nbParties++;
        }
    }

    /**
     * Méthode toString permettant la visualisation des caractéristiques d'un joueur.
     *
     * @return String les caractéristiques d'un joueur.
     */
    public String toString()
    {
        return "Nom : " + nom;
    }
}
