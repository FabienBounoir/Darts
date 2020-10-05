package com.lasalle.darts;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class HistoriqueActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "HistoriqueActivity"; //!< le TAG de la classe pour les logs
    /**
     * Attributs de la classe
     */
    private ServeurBDD serveurBDD;
    private int idJoueurActuel = -1;
    private Joueur joueurActuel;
    /**
     * Eléments graphiques de l'IHM
     */
    TableLayout tableauParties;
    /**
     * Index des boutons d'action
     */
    public static final int INDEX_BOUTON_DETAILS_PARTIE = 0;
    public static final int INDEX_BOUTON_SUPPRIMER_PARTIE = 1;
    public static final int INDEX_POSITION_CHAMP_ID_PARTIE = 0;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_historique);

        // Se connecter à la base de données
        connecterBaseDeDonnees();

        // Affecter les éléments graphiques de l'IHM
        affecterElementsIHM();

        // Modifier l'IHM pour afficher le joueur
        modifierIHMAfficherPartiesJoueur();

        // Permettre à tous les boutons de suppression d'être cliquables
        rendreBoutonIHMCliquables();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.purgerParties);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Purger les parties déjà existantes du joueur
                if(idJoueurActuel == -1)
                    purgerParties();
                else
                    purgerPartiesJoueur();
            }
        });
        FloatingActionButton retour = (FloatingActionButton) findViewById(R.id.retourHistorique);
        retour.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }

    @Override
    public void onClick(View element)
    {
        // Retrouver l'ID de la partie sélectionnée
        for (int i = 1; i < tableauParties.getChildCount(); ++i) {

            TableRow ligne = (TableRow) tableauParties.getChildAt(i);

            LinearLayout layoutElement = (LinearLayout) element.getParent();

            if (ligne == layoutElement.getParent()) {
                TextView texteIdPartie = (TextView) ligne.getChildAt(INDEX_POSITION_CHAMP_ID_PARTIE);
                int idPartieElementSelectionne = Integer.parseInt(texteIdPartie.getText().toString()); // ID de la partie sélectionnée

                if (element == layoutElement.getChildAt(INDEX_BOUTON_DETAILS_PARTIE))
                {
                    // Afficher la boite de dialogue des détails de la partie sélectionnée
                    afficherBoiteDialogueDetailsPartie(idPartieElementSelectionne);
                }
                else if (element == layoutElement.getChildAt(INDEX_BOUTON_SUPPRIMER_PARTIE))
                {
                    // Afficher la boite de dialogue de suppression de la partie sélectionnée
                    afficherBoiteDialogueSuppressionPartie(idPartieElementSelectionne, i); // i étant la ligne correspondante à la partie sélectionnée
                }
            }
        }
    }

    /**
     * Méthode permettant d'afficher dans un tableau une partie.
     *
     * @param partie étant la partie à afficher
     */
    private void afficherPartie(Partie partie)
    {
        // Déclaration des paramètres visuels des champs
        TableRow.LayoutParams lpLigne = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams lpChamp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f);
        //TableRow.LayoutParams lpChamp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);

        Configuration config = getResources().getConfiguration();
        //Log.d(TAG, "smallestScreenWidthDp = " + config.smallestScreenWidthDp);

        // Déclaration et paramétrage de la ligne accueillant la partie
        TableRow ligne = new TableRow(this);
        ligne.setLayoutParams(lpLigne);
        float taille_police_ecriture = 20f;

        if(config.smallestScreenWidthDp < 720)
            taille_police_ecriture = 10f;
        else
            taille_police_ecriture = 20f;
        // Déclaration et paramétrage du champ ID
        TextView textePartieId = new TextView(this);
        textePartieId.setLayoutParams(lpChamp);
        textePartieId.setGravity(Gravity.CENTER);
        textePartieId.setVisibility(View.GONE);
        textePartieId.setText(Integer.toString(partie.getId()));

        // Déclaration et paramétrage du champ Date
        TextView textePartieDate = new TextView(this);
        textePartieDate.setLayoutParams(lpChamp);
        textePartieDate.setGravity(Gravity.CENTER);
        textePartieDate.setTextSize(taille_police_ecriture);
        textePartieDate.setText(partie.getDateDebut());

        // Déclaration et paramétrage du champ MoyenneVolees
        TextView textePartieMoyVolees = new TextView(this);
        textePartieMoyVolees.setLayoutParams(lpChamp);
        textePartieMoyVolees.setGravity(Gravity.CENTER);
        textePartieMoyVolees.setTextSize(taille_police_ecriture);
        textePartieMoyVolees.setText(Integer.toString(partie.getMoyenneVolees()));

        // Déclaration et paramétrage du champ NbVolees
        TextView textePartieNomJoueur = new TextView(this);
        textePartieNomJoueur.setLayoutParams(lpChamp);
        textePartieNomJoueur.setGravity(Gravity.CENTER);
        textePartieNomJoueur.setTextSize(taille_police_ecriture);
        textePartieNomJoueur.setText(partie.getNomJoueur());

        // Déclaration et paramétrage du champ VoleeMax
        TextView textePartieVoleeMax = new TextView(this);
        textePartieVoleeMax.setLayoutParams(lpChamp);
        textePartieVoleeMax.setGravity(Gravity.CENTER);
        textePartieVoleeMax.setTextSize(taille_police_ecriture);
        textePartieVoleeMax.setText(Integer.toString(partie.getVoleeMax()));

        // Déclaration et paramétrage du champ Resultat
        TextView textePartieResultat = new TextView(this);
        textePartieResultat.setLayoutParams(lpChamp);
        textePartieResultat.setGravity(Gravity.CENTER);
        textePartieResultat.setTextSize(taille_police_ecriture);
        if(partie.getResultat())
            textePartieResultat.setText("Gagné");
        else
            textePartieResultat.setText("Perdu");

        // Déclaration et paramétrage du champ Action accueillant les boutons
        LinearLayout layoutAction = new LinearLayout(this);
        layoutAction.setLayoutParams(lpChamp);

        // Déclaration et paramétrage du champ Action
        ImageButton boutonDetailsPartie = new ImageButton(this);
        boutonDetailsPartie.setLayoutParams(lpChamp);
        //boutonDetailsPartie.setScaleType(ImageView.ScaleType.FIT_CENTER);
        boutonDetailsPartie.setBackgroundColor(Color.argb(0, 0, 0, 0)); // Rendre le fond transparent
        if(config.smallestScreenWidthDp < 720)
            boutonDetailsPartie.setImageResource(R.drawable.ic_loupe_afficher_16);
        else
            boutonDetailsPartie.setImageResource(R.drawable.ic_loupe_afficher_32);
        boutonDetailsPartie.setOnClickListener(this); // Rendre le bouton cliquable

        // Déclaration et paramétrage du bouton de suppression de paramètre
        ImageButton boutonSupprimerPartie = new ImageButton(this);
        boutonSupprimerPartie.setLayoutParams(lpChamp);
        //boutonSupprimerPartie.setScaleType(ImageView.ScaleType.FIT_CENTER);
        boutonSupprimerPartie.setBackgroundColor(Color.argb(0, 0, 0, 0)); // Rendre le fond transparent
        if(config.smallestScreenWidthDp < 720)
            boutonSupprimerPartie.setImageResource(R.drawable.ic_poubelle_supprimer_16);
        else
            boutonSupprimerPartie.setImageResource(R.drawable.ic_poubelle_supprimer_32);
        boutonSupprimerPartie.setOnClickListener(this); // Rendre le bouton cliquable

        // Ajouter les boutons au layout du champ Action
        layoutAction.addView(boutonDetailsPartie, INDEX_BOUTON_DETAILS_PARTIE);
        layoutAction.addView(boutonSupprimerPartie, INDEX_BOUTON_SUPPRIMER_PARTIE);

        // Ajouter les champs à la ligne
        ligne.addView(textePartieId);
        ligne.addView(textePartieDate);
        ligne.addView(textePartieNomJoueur);
        ligne.addView(textePartieResultat);
        ligne.addView(textePartieMoyVolees);
        ligne.addView(textePartieVoleeMax);
        ligne.addView(layoutAction);

        // Ajouter la ligne au tableau des parties à l'index 1 : après l'entête du tableau
        tableauParties.addView(ligne, 1);
    }

    /**
     * Méthode permettant la suppression d'une partie précise.
     *
     * @param idPartieElementSelectionne étant l'ID de la partie à supprimer
     * @param indexLigneTableau étant son index dans le tableau
     */
    private void supprimerPartieSelectionnee(int idPartieElementSelectionne, int indexLigneTableau)
    {
        // Supprimer la partie selectionnée de la base de données
        serveurBDD.supprimerPartie(idPartieElementSelectionne);

        // Supprimer le contenu de la ligne correspondante du tableau des parties
        TableRow ligne = (TableRow) tableauParties.getChildAt(indexLigneTableau);
        tableauParties.removeView(ligne);
    }

    /**
     * Méthode connecterBaseDeDonnees() permettant de se connecter à la base de données.
     */
    private void connecterBaseDeDonnees()
    {
        serveurBDD = new ServeurBDD(this);
        serveurBDD.open();
    }

    /**
     * Méthode affecterElementsIHM() permettant d'affecter les élements graphiques actuels de l'IHM.
     */
    private void affecterElementsIHM()
    {
        // Tableau des parties
        tableauParties = (TableLayout)findViewById(R.id.tableauParties);
    }

    /**
     * Méthode affectant aux attributs les caratéristiques du joueur
     */
    private void recupererInformationsJoueur(int id)
    {
        joueurActuel = serveurBDD.getJoueur(id);
    }

    /**
     * Méthode affichant les parties dans un tableau de l'IHM.
     */
    private void modifierIHMAfficherPartiesJoueur()
    {
        TextView texteHistoriqueParties = (TextView) findViewById(R.id.texteHistoriqueParties);
        //texteHistoriqueParties.setText("Historique des parties de " + joueurActuel.getNom() + " :");
        texteHistoriqueParties.setText("Historique des parties");

        // Récupérer la liste des parties existantes du joueur
        //List<Partie> parties = serveurBDD.getParties(joueurActuel.getId());
        List<Partie> parties = serveurBDD.getParties();

        // Pour chaque partie existante
        for(int i = 0; i < parties.size(); i++)
        {
            // Ajouter la partie dans le tableau de parties
            afficherPartie(parties.get(i));
        }
    }

    /**
     * Méthode rendreBoutonIHMCliquables() permettant aux boutons de l'IHM d'être cliquables.
     */
    private void rendreBoutonIHMCliquables()
    {
        // Boutons présents dans le tableau d'historique de parties du joueur
        int compteurLignes = tableauParties.getChildCount();
        for (int i = 0; i < compteurLignes; ++i) {
            TableRow ligne = (TableRow) tableauParties.getChildAt(i);

            if (ligne.getChildAt(ligne.getChildCount() - 1) instanceof ImageButton) {
                View element = ligne.getChildAt(ligne.getChildCount() - 1); // Le bouton de suppression de partie étant le dernier élément de la ligne
                element.setOnClickListener(this);
            }
        }
    }

    /**
     * Méthode affichant une boite de dialogue avec les informations détaillées de la partie sélectionnée.
     *
     * @param idPartieSelectionnee étant l'id de la partie à afficher
     */
    private void afficherBoiteDialogueDetailsPartie(int idPartieSelectionnee)
    {
        // Retrouver l'objet correspondant grâce à la base de données
        Partie partieSelectionnee = serveurBDD.getPartie(idPartieSelectionnee);

        // Afficher la boîte de dialogue
        AlertDialog.Builder detailsPartie = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View detailsPartieView = factory.inflate(R.layout.details_partie, null);
        detailsPartie.setView(detailsPartieView);

        detailsPartie.setTitle("Détails de la partie");

        // Affecter les informations de la partie aux champs correspondants
        TextView texteValeurJoueur = (TextView) detailsPartieView.findViewById(R.id.texteValeurJoueur);
        TextView texteValeurType = (TextView) detailsPartieView.findViewById(R.id.texteValeurType);
        TextView texteValeurMoyenneVolees = (TextView) detailsPartieView.findViewById(R.id.texteValeurMoyenneVolees);
        TextView texteValeurNbVolees = (TextView) detailsPartieView.findViewById(R.id.texteValeurNbVolees);
        TextView texteValeurVoleeMax = (TextView) detailsPartieView.findViewById(R.id.texteValeurVoleeMax);
        TextView texteValeurResultat = (TextView) detailsPartieView.findViewById(R.id.texteValeurResultat);
        TextView texteValeurDateDebut = (TextView) detailsPartieView.findViewById(R.id.texteValeurDateDebut);
        TextView texteValeurDuree = (TextView) detailsPartieView.findViewById(R.id.texteValeurDuree);

        texteValeurJoueur.setText(partieSelectionnee.getNomJoueur());
        texteValeurType.setText(partieSelectionnee.getType());
        texteValeurMoyenneVolees.setText(Integer.toString(partieSelectionnee.getMoyenneVolees()));
        texteValeurNbVolees.setText(Integer.toString(partieSelectionnee.getNbVolees()));
        texteValeurVoleeMax.setText(Integer.toString(partieSelectionnee.getVoleeMax()));
        if(partieSelectionnee.getResultat())
            texteValeurResultat.setText("Gagné");
        else
            texteValeurResultat.setText("Perdu");
        texteValeurDateDebut.setText(partieSelectionnee.getDateDebut());
        texteValeurDuree.setText(partieSelectionnee.getDuree());

        detailsPartie.setNegativeButton("Retour", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });

        detailsPartie.show();
    }

    /**
     * Méthode affichant une boîte de dialogue qui va supprimer la partie sélectionnée selon la décision prise.
     *
     * @param idPartieSelectionnee étant l'id dans la base de données de la partie sélectionnée
     * @param indexLigneTableau étant l'index correspondant de la ligne du tableau affiché dans l'IHM
     */
    private void afficherBoiteDialogueSuppressionPartie(final int idPartieSelectionnee, final int indexLigneTableau)
    {
        // Afficher la boite de dialogue
        AlertDialog.Builder suppressionPartie = new AlertDialog.Builder(this);

        suppressionPartie.setMessage("Vous êtes sur le point de supprimer définitivement cette partie.");

        suppressionPartie.setPositiveButton("Continuer", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                // Supprimer la partie sélectionnée de la base de données et de l'IHM
                supprimerPartieSelectionnee(idPartieSelectionnee, indexLigneTableau);
            }
        });

        suppressionPartie.setNegativeButton("Annuler", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        suppressionPartie.show();
    }

    /**
     * Méthode affichant une boîte de dialogue qui va supprimer toutes les parties
     */
    private void purgerParties()
    {
        // Afficher la boite de dialogue
        AlertDialog.Builder detailsPartie = new AlertDialog.Builder(this);

        detailsPartie.setMessage("Vous êtes sur le point de supprimer définitivement les parties.");

        detailsPartie.setPositiveButton("Continuer", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                // Purger la table des parties de la base de données
                serveurBDD.purgerTableParties();

                // Supprimer le contenu du tableau des parties
                int compteur = tableauParties.getChildCount();
                for (int i = 1; i < compteur; i++) { // On ne supprime pas la 1ere ligne qui est l'entête
                    View child = tableauParties.getChildAt(i);
                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                }
            }
        });

        detailsPartie.setNegativeButton("Annuler", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        detailsPartie.show();
    }

    /**
     * Méthode affichant une boîte de dialogue qui va supprimer les parties du joueur actuel selon la décision prise.
     */
    private void purgerPartiesJoueur()
    {
        // Afficher la boite de dialogue
        AlertDialog.Builder detailsPartie = new AlertDialog.Builder(this);

        detailsPartie.setMessage("Vous êtes sur le point de supprimer définitivement les parties du joueur " + joueurActuel.getNom() + ".");

        detailsPartie.setPositiveButton("Continuer", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                // Purger la table des parties de la base de données
                serveurBDD.purgerPartiesJoueur(idJoueurActuel);

                // Supprimer le contenu du tableau des parties
                int compteur = tableauParties.getChildCount();
                for (int i = 1; i < compteur; i++) { // On ne supprime pas la 1ere ligne qui est l'entête
                    View child = tableauParties.getChildAt(i);
                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                }
            }
        });

        detailsPartie.setNegativeButton("Annuler", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        detailsPartie.show();
    }
}
