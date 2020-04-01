package projet.lasalle84.darts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * @file MainActivity.java
 * @brief Déclaration de la classe MainActivity
 * @author Menella Erwan
 */

/**
 * @class MainActivity
 * @brief Déclaration de la classe MainActivity
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    /**
     * Constantes
     */
    private static final String TAG = "IHMEcranPrincipal"; //!< le TAG de la classe pour les logs

    /**
     * Widgets
     */
    private Button boutonDemarrerPartie;
    private Button boutonGererJoueurs;
    private Button boutonVoirHistorique;


    /**
     * @brief Méthode appelée à la création de l'activité
     *
     * @fn MainActivity::onCreate(Bundle savedInstanceState)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        // Associer le layout à l'activité
        setContentView(R.layout.activity_main);

        recupererWidgets();

        initialiserWidgets();
    }

    /**
     * @brief Méthode appelée lors d'un clic sur un widget de l'activité
     *
     * @fn MainActivity::onClick(View element)
     * @param element le widget cliqué
     */
    @Override
    public void onClick(View element)
    {
        Log.d(TAG, "onClick()");

        if(element == boutonDemarrerPartie)
        {
            Log.d(TAG, "clic boutonDemarrerPartie");
            Intent activiteCreerPartie = new Intent(MainActivity.this, ActiviteCreerPartie.class);
            startActivity(activiteCreerPartie);

        }
        else if(element == boutonGererJoueurs)
        {
            Log.d(TAG, "clic boutonGererJoueurs");
        }
        else if(element == boutonVoirHistorique)
        {
            Log.d(TAG, "clic boutonVoirHistorique");
        }
    }

    /**
     * @brief Récupère les widgets du layout de l'activité
     *
     * @fn MainActivity::recupererWidgets()
     *
     */
    private void recupererWidgets()
    {
        Log.d(TAG, "recupererWidgets()");

        boutonDemarrerPartie = (Button)findViewById(R.id.boutonDemarrerPartie);
        boutonGererJoueurs = (Button)findViewById(R.id.boutonGererJoueurs);
        boutonVoirHistorique = (Button)findViewById(R.id.boutonVoirHistorique);
    }

    /**
     * @brief Récupère les widgets du layout de l'activité
     *
     * @fn MainActivity::initialiserWidgets()
     *
     */
    private void initialiserWidgets()
    {
        Log.d(TAG, "initialiserWidgets()");

        // Rend les boutons cliquables
        boutonDemarrerPartie.setOnClickListener(this);
        boutonGererJoueurs.setOnClickListener(this);
        boutonVoirHistorique.setOnClickListener(this);

        boutonGererJoueurs.setVisibility(View.INVISIBLE);
        boutonVoirHistorique.setVisibility(View.INVISIBLE);
    }
}

