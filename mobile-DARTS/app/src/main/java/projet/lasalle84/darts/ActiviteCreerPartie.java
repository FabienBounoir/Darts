package projet.lasalle84.darts;
/**
 * @file ActiviteCreerPartie.java
 * @brief Déclaration de la classe ActivitéCréerPartie
 * @author Menella Erwan
 */
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.List;

public class ActiviteCreerPartie extends AppCompatActivity implements View.OnClickListener
{

    private static final String TAG = "IHMCreerPartie"; //!< le TAG de la classe pour les logs

    private Button ajouterJoueur;
    private Spinner modeDeJeu;
    private ListView Joueur;
    private ArrayAdapter<Joueur> Adaptateur;
    /**
     * @brief Méthode appelée à la création de l'activité
     *
     * @fn ActiviteCreerPartie::onCreate(Bundle savedInstanceState)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creer_partie);
        recupererWidgets();
        initialiserWidgets();
        Adaptateur = new ArrayAdapter<Joueur>(this,android.R.layout.simple_spinner_item);
        //Joueur.setAdapter(Adaptateur);
    }

    /**
     * @brief Méthode appelée lors d'un clic sur un widget de l'activité
     *
     * @fn ActiviteCreerPartie::onClick(View element)
     * @param element le widget cliqué
     */

    @Override
    public void onClick(View element)
    {
        Log.d(TAG, "onClick()");

        if(element == ajouterJoueur)
        {
            Log.d(TAG, "clic ajouterJoueur");
            Intent intent = new Intent(ActiviteCreerPartie.this, ActiviteAjouterJoueur.class);
            startActivityForResult(intent,1);
        }
    }

    /**
     * @brief Méthode pour récuperer les Widget
     * @fn ActiviteCreerPartie::recupererWidgets()
     */
    private void recupererWidgets()
    {
        Log.d(TAG, "recupererWidgets()");

        ajouterJoueur = (Button) findViewById(R.id.AjouterJoueur);
        modeDeJeu = (Spinner) findViewById(R.id.modeJeu);
        Joueur = (ListView) findViewById(R.id.Listejoueur);

    }

    /**
     * @brief Méthode pour initialiser les Widget
     * @fn ActiviteCreerPartie::initialiserWidgets()
     */
    private void initialiserWidgets()
    {
        Log.d(TAG, "initialiserWidgets()");
        ajouterJoueur.setOnClickListener(this);
    }

    /**
     * @brief Méthode qui permet de recupérer le nom du joueur
     * @fn ActiviteCreerPartie::onActivityResult(int requestCode, int resultCode, Intent data)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK) {
                String nomJoueur = data.getStringExtra("Joueur");
                Log.d(TAG,"nom du joueur: " + nomJoueur);
                Joueur MonJoueur = new Joueur(nomJoueur);
                Adaptateur.add(MonJoueur);
            }
        }
    }

}
