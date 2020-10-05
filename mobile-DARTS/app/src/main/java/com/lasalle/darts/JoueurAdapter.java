package com.lasalle.darts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class JoueurAdapter extends RecyclerView.Adapter<JoueurViewHolder>
{
    private List<Joueur> joueurs = null;
    private final Listener callback;

    public JoueurAdapter(List<Joueur> joueurs, Listener callback)
    {
        if(joueurs != null)
        {
            this.joueurs = joueurs;
        }
        this.callback = callback;
    }

    @NonNull
    @Override
    public JoueurViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.joueurs, parent, false);
        return new JoueurViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JoueurViewHolder holder, int position)
    {
        Joueur joueur = joueurs.get(position);
        holder.afficher(joueur, this.callback);
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

    public interface Listener {
        void onClickBoutonSupprimer(int position);
        void onClickBoutonSelectionner(int position);
    }
}
