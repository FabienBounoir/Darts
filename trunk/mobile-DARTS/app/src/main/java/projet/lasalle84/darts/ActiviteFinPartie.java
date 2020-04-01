package projet.lasalle84.darts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @file ActiviteFinPartie.java
 * @brief Déclaration de la classe ActiviteFinPartie
 * @author Menella Erwan
 */
public class ActiviteFinPartie extends AppCompatActivity implements View.OnClickListener {

    /**
     *Constantes
     */
    private final String TAG = "IHMFinPartie";
    /**
     *Attributs
     */
    private TextView texteGagnant = null;           //<! Widget texte avec le nom du gagnant
    private ListView classement = null;             //<! Widget avec la liste des joueurs avec leur score
    private Button partieFini = null;               //<! Bouton pour fermer la page
    private ArrayList<Joueur> lesJoueur = null;     //<! Conteneur avec des objets Joueur
    private String nomGagnant = null;               //<! Nom du Gagnant
    private ArrayAdapter<String> adapteur = null;   //<! Adapteur avec le classement des joueurs

    /**
     * @brief Méthode appelée à la création de l'activité
     *
     * @fn ActiviteFinPartie::onCreate(Bundle savedInstanceState)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activite_fin_partie);
        recupererWidgets();
        initialiserWidgets();
        recupererInfomationPartie();
        trierLesJoueurs();
        afficherJoueurs();
    }

    /**
     * @brief Méthode appelée à la création de l'activité
     *
     * @fn ActiviteFinPartie::onClick(View element)
     */
    @Override
    public void onClick(View element)
    {
        Log.d(TAG, "onClick() " + element.toString());

        if(element == partieFini)
        {
            super.finish();
        }
    }

    /**
     * @brief Méthode permet recupérer les widgets du layout
     *
     * @fn ActiviteFinPartie::recupererWidgets()
     */
    public void recupererWidgets()
    {
        Log.d(TAG, "recupererWidgets()");
        texteGagnant = (TextView) findViewById(R.id.texteGagnant);
        classement = (ListView) findViewById(R.id.ResultaPartie);
        partieFini = (Button) findViewById(R.id.boutonFin);

    }

    /**
     * @brief Méthode permet initialiser les widgets
     *
     * @fn ActiviteFinPartie::initialiserWidgets()
     */
    public void initialiserWidgets()
    {
        Log.d(TAG, "initialiserWidgets()");
        partieFini.setOnClickListener(this);
    }

    /**
     * @brief Méthode appelée à la création de l'activité
     *
     * @fn ActiviteFinPartie::recupererInfomationPartie()
     */
    public void recupererInfomationPartie()
    {
        Log.d(TAG, "recupererInfomationPartie()");
        lesJoueur = (ArrayList<Joueur>) getIntent().getSerializableExtra("joueurs");
        nomGagnant = getIntent().getStringExtra("gagnant");

    }

    /**
     * @brief Méthode permet de trier les joueur par rapport au score
     *
     * @fn ActiviteFinPartie::trierLesJoueurs()
     */
    public void trierLesJoueurs()
    {
        Log.d(TAG, "trierLesJoueurs()");
        lesJoueur.sort(Comparator.comparingInt(Joueur::getScore));

    }

    /**
     * @brief Méthode appelée à la création de l'activité
     *
     * @fn ActiviteFinPartie::afficherJoueurs()
     */
    public void afficherJoueurs()
    {
        Log.d(TAG, "afficherJoueurs()");
        adapteur = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
        for (int i = 0; i < lesJoueur.size(); i++)
        {
            adapteur.add(lesJoueur.get(i).getNom() + " : " + lesJoueur.get(i).getScore());
        }
        classement.setAdapter(adapteur);

        texteGagnant.setText(nomGagnant + " a gagné la partie !");
    }
}
