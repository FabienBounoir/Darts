package com.lasalle.darts;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class JoueurSelectionneViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private static final String TAG = "JoueurSelectionneVH"; //!< le TAG de la classe pour les logs
    private final TextView nom;
    private final TextView points;
    private final TextView moyenneVolees;
    private final TextView voleeMax;
    private final TextView pointVolee;
    private final TextView voleeJouee;
    private final CardView cardView;
    private Joueur joueur;

    public JoueurSelectionneViewHolder(final View itemView)
    {
        super(itemView);
        nom = ((TextView)itemView.findViewById(R.id.nom));
        points = ((TextView)itemView.findViewById(R.id.points));
        moyenneVolees = ((TextView)itemView.findViewById(R.id.moyenneVolees));
        voleeMax = ((TextView)itemView.findViewById(R.id.voleeMax));
        pointVolee = ((TextView)itemView.findViewById(R.id.pointVolee));
        voleeJouee = ((TextView)itemView.findViewById(R.id.voleeJouee));
        cardView = ((CardView)itemView.findViewById(R.id.card_view));

        itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                List<String> historique = joueur.getPartie().getHistoriqueVolees();
                if(historique.size() == 0)
                    return;
                String strHistorique = "";
                for(int i = 0; i < historique.size(); ++i)
                {
                    strHistorique += historique.get(i);
                    strHistorique += "\n";
                }
                new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Volées")
                        .setMessage(strHistorique)
                        .show();
            }
        });
    }

    public void afficher(Joueur joueur)
    {
        this.joueur = joueur;
        //Log.d(TAG,"Joueur " + joueur.getNom() + " -> " + joueur.getActive());
        if(joueur.getActive())
            //cardView.setCardBackgroundColor(Color.parseColor("#99CC00")); // holo_green_light
            cardView.setCardBackgroundColor(Color.parseColor("#FFBB33")); // holo_orange_light
        else
            cardView.setCardBackgroundColor(Color.parseColor("#33B5E5")); // holo_blue_light
        if(joueur.getPartie().getResultat())
            cardView.setCardBackgroundColor(Color.parseColor("#aa66cc")); // holo_red_light
        nom.setText(joueur.getNom());
        points.setText(joueur.getPoints() + " points");
        moyenneVolees.setText("Moyenne : " + Integer.toString(joueur.getPartie().getMoyenneVolees()));
        voleeMax.setText("Max : " + Integer.toString(joueur.getPartie().getVoleeMax()));
        if(joueur.getPartie().getVolee() > 0)
            pointVolee.setText(Integer.toString(joueur.getPartie().getNbVolees()));
        else
            pointVolee.setText("");
        voleeJouee.setText(joueur.getPartie().getVoleeJouee());
    }

    @Override
    public void onClick(View element)
    {
    }
}
