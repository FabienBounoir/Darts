package projet.lasalle84.darts;


public class TypeJeu {

    private boolean doubleOut;
    private int pointDepart;

    public TypeJeu()
    {

    }

    public TypeJeu(int pointDepart, boolean doubleOut)
    {
        this.pointDepart = pointDepart;
        this.doubleOut = doubleOut;
    }

    public TypeJeu(int idModeJeu)
    {
        switch (idModeJeu)
        {
            case 0:
                this.pointDepart = 501;
                this.doubleOut = true;
                break;
            case 1:
                this.pointDepart = 301;
                this.doubleOut = true;
                break;
        }
    }
    public int getPointDepart()
    {
        return pointDepart;
    }

    public void setPointDepart(int pointDepart)
    {
        this.pointDepart = pointDepart;
    }

    public boolean estDoubleOut()
    {
        return doubleOut;
    }

    public void setDoubleOut(boolean doubleOut)
    {
        this.doubleOut = doubleOut;
    }


}
