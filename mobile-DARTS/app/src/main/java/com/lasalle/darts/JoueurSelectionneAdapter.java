package com.lasalle.darts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class JoueurSelectionneAdapter extends RecyclerView.Adapter<JoueurSelectionneViewHolder>
{
    private List<Joueur> joueurs = null;

    public JoueurSelectionneAdapter(List<Joueur> joueurs)
    {
        if(joueurs != null)
        {
            this.joueurs = joueurs;
        }
    }

    @NonNull
    @Override
    public JoueurSelectionneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.joueurs_selectionnes, parent, false);
        return new JoueurSelectionneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JoueurSelectionneViewHolder holder, int position)
    {
        Joueur joueur = joueurs.get(position);
        holder.afficher(joueur);
    }

    @Override
    public int getItemCount()
    {
        if(joueurs != null)
            return joueurs.size();
        return 0;
    }

    public Joueur getJoueur(int position)
    {
        return this.joueurs.get(position);
    }
}
