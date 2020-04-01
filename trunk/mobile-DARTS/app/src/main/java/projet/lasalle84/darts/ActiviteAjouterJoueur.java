package projet.lasalle84.darts;
/**
 * @file ActiviteAjouterJoueur.java
 * @brief Déclaration de la classe ActiviteAjouterJoueur
 * @author Menella Erwan
 */
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.Serializable;

/**
 * @class ActiviteAjouterJoueur
 * @brief Déclaration de la classe ActiviteAjouterJoueur
 */
public class ActiviteAjouterJoueur extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "IHMAjouterJoueur"; //!< le TAG de la classe pour les logs
    private Button boutonValider;   //!< Bouton valider
    private EditText saisir;        //!< Zone de saisir du nom du joueur
    /**
     * @brief Méthode appelée à la création de l'activité
     *
     * @fn ActiviteAjouterJoueur::onCreate(Bundle savedInstanceState)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_joueur);
        recupererWidget();
        initiliserWidget();
    }

    /**
     * @brief Récupère les widgets du layout de l'activité
     *
     * @fn ActiviteAjouterJoueur::recupererWidgets()
     *
     */
    public void recupererWidget()
    {
        Log.d(TAG,"recupererWidget()");
        boutonValider = (Button) findViewById(R.id.bouton);
        saisir = (EditText) findViewById(R.id.Nom);
    }

    /**
     * @brief Récupère les widgets du layout de l'activité
     *
     * @fn MainActivity::initialiserWidgets()
     *
     */
    public void initiliserWidget()
    {
        Log.d(TAG,"initiliserWidget()");
        boutonValider.setOnClickListener(this);
        saisir.setOnClickListener(this);

    }

    /**
     * @brief Méthode appelée lors d'un clic sur un widget de l'activité
     *
     * @fn ActiviteAjouterJoueur::onClick(View element)
     * @param element le widget cliqué
     */
    @Override
    public void onClick(View element)
    {
        Log.d(TAG,"Onclick()");

        if(element == boutonValider)
        {
            Log.d(TAG, "clic BoutonValider ");
            finish();
        }
    }

    /**
     * @brief Méthode appelée lors l'utilisateur valide
     *
     * @fn ActiviteAjouterJoueur::finish()
     */
    @Override
    public void finish() {
        Log.d(TAG,"finish()");

        if (saisir.getText() != null)
        {
            Intent retour = new Intent();
            retour.putExtra("Joueur", saisir.getText().toString().trim());
            setResult(RESULT_OK, retour);
            super.finish();
        }
    }
}
