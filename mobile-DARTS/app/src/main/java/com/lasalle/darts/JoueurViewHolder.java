package com.lasalle.darts;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class JoueurViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private static final String TAG = "JoueurViewHolder"; //!< le TAG de la classe pour les logs
    private final TextView nom;
    private final TextView nbVictoires;
    private final TextView nbParties;
    private ImageButton boutonSupprimer;
    private CheckBox boutonSelectionner;
    private final CardView cardView;
    private Joueur joueur;
    private WeakReference<JoueurAdapter.Listener> callbackWeakRef;

    public JoueurViewHolder(final View itemView)
    {
        super(itemView);
        nom = ((TextView)itemView.findViewById(R.id.nom));
        nbVictoires = ((TextView)itemView.findViewById(R.id.nbVictoires));
        nbParties = ((TextView)itemView.findViewById(R.id.nbParties));
        boutonSupprimer = ((ImageButton)itemView.findViewById(R.id.boutonSupprimer));
        boutonSelectionner = ((CheckBox)itemView.findViewById(R.id.boutonSelectionner));
        cardView = ((CardView)itemView.findViewById(R.id.card_view));
    }

    public void afficher(Joueur joueur, JoueurAdapter.Listener callback)
    {
        this.joueur = joueur;
        nom.setText(joueur.getNom());
        nbVictoires.setText("Nb Victoires : " + joueur.getNbVictoires());
        nbParties.setText("Nb Parties : " + joueur.getNbParties());
        this.boutonSelectionner.setChecked(joueur.getSelectionne());
        if(joueur.getSelectionne())
            cardView.setCardBackgroundColor(Color.parseColor("#99CC00"));
        else
            cardView.setCardBackgroundColor(Color.parseColor("#FFD6DBDA"));
        this.boutonSupprimer.setOnClickListener(this);
        this.nom.setOnClickListener(this);
        this.boutonSelectionner.setOnClickListener(this);
        this.callbackWeakRef = new WeakReference<JoueurAdapter.Listener>(callback);
    }

    @Override
    public void onClick(View element)
    {
        JoueurAdapter.Listener callback = callbackWeakRef.get();

        if(element == boutonSupprimer)
        {
            if (callback != null)
                callback.onClickBoutonSupprimer(getAdapterPosition());
        }
        else if(element == boutonSelectionner)
        {
            joueur.setSelectionne(boutonSelectionner.isChecked());
            if(joueur.getSelectionne())
                cardView.setCardBackgroundColor(Color.parseColor("#99CC00")); // holo_green_light
            else
                cardView.setCardBackgroundColor(Color.parseColor("#FFD6DBDA"));
            if (callback != null)
                callback.onClickBoutonSelectionner(getAdapterPosition());
        }
        else if(element == nom)
        {
            this.boutonSelectionner.setChecked(!joueur.getSelectionne());
            joueur.setSelectionne(!joueur.getSelectionne());
            if(joueur.getSelectionne())
                cardView.setCardBackgroundColor(Color.parseColor("#99CC00")); // holo_green_light
            else
                cardView.setCardBackgroundColor(Color.parseColor("#FFD6DBDA"));
            if (callback != null)
                callback.onClickBoutonSelectionner(getAdapterPosition());
        }
    }
}
