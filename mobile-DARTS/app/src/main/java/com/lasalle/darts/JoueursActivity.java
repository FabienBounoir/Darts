package com.lasalle.darts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

public class JoueursActivity extends AppCompatActivity implements JoueurAdapter.Listener
{
    private static final String TAG = "JoueursActivity"; //!< le TAG de la classe pour les logs
    private static List<Joueur> joueurs = null;
    private RecyclerView recyclerView;
    private JoueurAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ServeurBDD serveurBDD;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_joueurs);

        serveurBDD = new ServeurBDD(this);
        serveurBDD.open();

        recyclerView = (RecyclerView)findViewById(R.id.listeJoueurs);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        joueurs = MainActivity.getJoueurs();
        adapter = new JoueurAdapter(joueurs, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ajouterJoueur();
            }
        });
        FloatingActionButton retour = (FloatingActionButton) findViewById(R.id.retour);
        retour.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }

    private List<Joueur> recupererJoueurs()
    {
        List<Joueur> listeJoueurs = serveurBDD.getJoueurs();
        return listeJoueurs;
    }

    private void ajouterJoueur()
    {
        AlertDialog.Builder ajoutJoueur = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View ajoutJoueurView = factory.inflate(R.layout.ajout_joueur, null);
        ajoutJoueur.setView(ajoutJoueurView);

        ajoutJoueur.setTitle("Ajouter un nouveau joueur");

        ajoutJoueur.setPositiveButton("Valider", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                //Lorsque l'on cliquera sur le bouton "OK", on récupère l'EditText correspondant à notre vue personnalisée (cad à alertDialogView)
                EditText nomJoueur = (EditText)ajoutJoueurView.findViewById(R.id.editTextNom);
                Joueur joueur = new Joueur(nomJoueur.getText().toString());
                Toast.makeText(getApplicationContext(), "Ajout du joueur " + joueur.getNom(), Toast.LENGTH_SHORT).show();
                long id = serveurBDD.insererJoueur(joueur);
                joueur.setId((int)id);
                joueurs.add(joueur);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        ajoutJoueur.setNegativeButton("Annuler", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        ajoutJoueur.show();
    }

    @Override
    public void onClickBoutonSupprimer(int position)
    {
        Joueur joueur = adapter.getJoueur(position);
        if(serveurBDD.supprimerJoueur(joueur.getId()) > 0)
        {
            Toast.makeText(getApplicationContext(), "Suppression du joueur " + joueur.getNom(), Toast.LENGTH_SHORT).show();
            joueurs.remove(getPosition(joueur.getId()));
            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                public void run()
                {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onClickBoutonSelectionner(int position)
    {
        Joueur joueur = adapter.getJoueur(position);
        joueurs.set(position, joueur);
    }

    private int getPosition(int id)
    {
        for(int i = 0; i < joueurs.size(); ++i)
        {
            if(joueurs.get(i).getId() == id)
                return i;
        }
        return -1;
    }

    public static List<Joueur> getJoueurs()
    {
        return joueurs;
    }
}
