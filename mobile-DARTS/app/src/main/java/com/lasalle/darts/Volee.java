package com.lasalle.darts;

public class Volee
{
    private final int NB_FLECHETTES = 3;
    private int nbFlechettes;
    private int[] typePoint;
    private int[] numeroCible;
    private int points;
    private boolean terminee;

    public Volee()
    {
        this.nbFlechettes = 0;
        this.typePoint = new int[NB_FLECHETTES];
        this.numeroCible = new int[NB_FLECHETTES];
        this.points = 0;
        this.terminee = false;
    }

    public void initialiser()
    {
        this.nbFlechettes = 0;
        this.points = 0;
        this.terminee = false;
    }

    public void inserer(int typePoint, int numeroCible)
    {
        if(terminee)
            return;
        this.typePoint[nbFlechettes] = typePoint;
        this.numeroCible[nbFlechettes] = numeroCible;
        this.points += (typePoint * numeroCible);
        this.nbFlechettes++;
        if(nbFlechettes == NB_FLECHETTES)
        {
            this.terminee = true;
        }
    }

    public void inserer(int typePoint, int numeroCible, int numeroFlechette)
    {
        if(numeroFlechette < 1 || numeroFlechette > NB_FLECHETTES)
            return;
        if(terminee)
            return;
        this.typePoint[numeroFlechette] = typePoint;
        this.numeroCible[numeroFlechette] = numeroCible;
        this.points += (typePoint * numeroCible);
        this.nbFlechettes++;
        if(nbFlechettes == NB_FLECHETTES || numeroFlechette == NB_FLECHETTES)
        {
            this.terminee = true;
        }
    }

    public int getNbFlechettes()
    {
        return this.nbFlechettes;
    }

    public int getPoints()
    {
        return this.points;
    }

    public int getTypePoint(int numeroFlechette)
    {
        if(numeroFlechette < nbFlechettes)
            return this.typePoint[numeroFlechette];
        else
            return -1;
    }

    public int getNumeroCible(int numeroFlechette)
    {
        if(numeroFlechette < nbFlechettes)
            return this.numeroCible[numeroFlechette];
        else
            return -1;
    }

    public boolean getTerminee()
    {
        return this.terminee;
    }
}
