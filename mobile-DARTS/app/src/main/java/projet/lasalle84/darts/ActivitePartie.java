package projet.lasalle84.darts;
/**
 * @file ActiviteAjouterJoueur.java
 * @brief Déclaration de la classe ActivitePartie
 * @author Menella Erwan
 */
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @class ActivitePartie
 * @brief Déclaration de la classe ActivitePartie
 */
public class ActivitePartie extends AppCompatActivity implements View.OnClickListener
{
    /**
     * Constantes
     */
    private final static String TAG = "IHMPartie";                                  //!< Tag pour Log
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;                     //!< Code que le bluetooth est ativer
    public final static int JOUEUR_SUIVANT = 0;
    public final static int SET_SCORE = 1;
    public final static int RETIRER_POINT = 2;
    public final static int IMPACT = 3;
    /**
     * Attributs
     */
    private Button boutonSeConnecter;                               //!< Le bouton de connexion
    private Button boutonTirManque;                                 //!< Le bouton
    private Button boutonLancerPartie;
    private ArrayList<Joueur> mesJoueurs = null;
    private BluetoothAdapter bluetoothAdapter;
    private Partie maPartie = null;
    private TypeJeu modeJeu = null;

    /**
     * @brief Méthode appelée à la création de l'activité
     *
     * @fn ActivitePartie::onCreate(Bundle savedInstanceState)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partie);
        recupererWidgets();
        initialiserWidgets();
        activerBluetooth();
        ChargerLesParametre();
        maPartie = new Partie(mesJoueurs, modeJeu, bluetoothAdapter, handlerUI);
    }

    public void ChargerLesParametre()
    {
        mesJoueurs = new ArrayList<Joueur>();
        mesJoueurs = (ArrayList<Joueur>) getIntent().getSerializableExtra("LesJoueurs");
        Iterator<Joueur> it = mesJoueurs.iterator();

        while(it.hasNext())
        {
            Log.d(TAG, "le joueur "+ it.next().getNom() + "est chargé");
        }
        modeJeu = new TypeJeu(getIntent().getIntExtra("TypeMode",0));
    }

    /**
     * @brief Méthode appelée lors d'un clic sur un widget de l'activité
     *
     * @fn ActivitePartie::onClick(View element)
     * @param element le widget cliqué
     */
    @Override
    public void onClick(View element)
    {
        Log.d(TAG, "onClick() " + element.toString());

        if (element == boutonSeConnecter)
        {
            boutonSeConnecter.setVisibility(View.GONE);
            boutonTirManque.setVisibility(View.VISIBLE);
            boutonLancerPartie.setVisibility(View.VISIBLE);
            maPartie.demarrer();
        }
        else if(element == boutonLancerPartie)
        {

        }

    }
    public void activerBluetooth()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth non activé !", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (!bluetoothAdapter.isEnabled())
            {
                Toast.makeText(getApplicationContext(), "Bluetooth non activé !", Toast.LENGTH_SHORT).show();
                Intent activeBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(activeBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Bluetooth activé", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * @brief Récupère les widgets du layout de l'activité
     *
     * @fn ActivitePartie::recupererWidgets()
     *
     */
    public void recupererWidgets()
    {
        Log.d(TAG,"recupererWidgets()");
        boutonSeConnecter = (Button) findViewById(R.id.SeConnecter);
        boutonTirManque = (Button) findViewById(R.id.TirManque);
        boutonLancerPartie = (Button) findViewById(R.id.LancerPartie);
    }

    /**
     * @brief Récupère les widgets du layout de l'activité
     *
     * @fn ActivitePartie::initialiserWidgets()
     *
     */
    public void initialiserWidgets()
    {
        Log.d(TAG,"initialiserWidgets()");
        boutonTirManque.setVisibility(View.GONE);
        boutonTirManque.setOnClickListener(this);
        boutonSeConnecter.setOnClickListener(this);
        boutonLancerPartie.setVisibility(View.GONE);
        boutonLancerPartie.setOnClickListener(this);
    }

    /**
     * @brief Méthode qui permet d'avoir le retour d'une activité
     *
     * @fn ActivitePartie::onActivityResult(int requestCode, int resultCode, Intent data)
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(TAG,"onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE_ENABLE_BLUETOOTH)
            return;
        if (resultCode == RESULT_OK)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth activé", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Bluetooth non activé !", Toast.LENGTH_SHORT).show();
        }
    }

    final private Handler handlerUI = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            Bundle b = msg.getData();

            switch (b.getInt("action"))
            {
                case JOUEUR_SUIVANT:
                    Log.d(TAG, "JOUEUR_SUIVANT");
                    break;
                case SET_SCORE:
                    Log.d(TAG, "SET_SCORE");
                    break;
                case RETIRER_POINT:
                    Log.d(TAG, "RETIRER_POINT");
                    break;
                case IMPACT:
                    Log.d(TAG, "IMPACT");
                    break;
            }
        }
    };
}

