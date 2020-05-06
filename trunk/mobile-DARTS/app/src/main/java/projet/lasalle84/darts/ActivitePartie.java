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
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;                     //!< Code que le bluetooth est activé
    /**
     * Attributs
     */
    private Button boutonTirManque = null;                                  //!< Le bouton si la cible a était manquée
    private Button boutonLancerPartie = null;                               //!< Bouton pour lancer la partie
    private ArrayList<Joueur> mesJoueurs = null;                            //!< liste de Joueur
    private BluetoothAdapter bluetoothAdapter = null;                       //!< Bluetooth Adapteur
    private Partie maPartie = null;                                         //!< La partie
    private TypeJeu modeJeu = null;                                         //!< Object Type de Jeu
    private ArrayAdapter<String> adapteur = null;                           //!< Adapteur qui affiche quelle joueur doit joueur et score
    private ListView listJoueur = null;                                     //!< Listview
    private TextView affichageImpact = null;                                //!< Texte qui affiche la volley actuelle
    private Button boutonPause = null;                                      //!< Bouton Pause
    private boolean AfficheRegle;

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
        initialiserListeJoueur();
        maPartie = new Partie(mesJoueurs, modeJeu, AfficheRegle, bluetoothAdapter, handlerUI);
    }

    /**
     * @brief Méthode qui permet charger des joueurs et les paramètre de la partie
     *
     * @fn ActivitePartie::ChargerLesParametre()
     */
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
        AfficheRegle = getIntent().getBooleanExtra("AfficheRegle", true);
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

        if (element == boutonLancerPartie)
        {
            new Thread()
            {
                @Override
                public void run() {
                    super.run();
                    maPartie.demarrer();
                }
            }.start();
            boutonLancerPartie.setVisibility(View.INVISIBLE);
            boutonTirManque.setVisibility(View.VISIBLE);
        }
        else if (element == boutonTirManque)
        {
            maPartie.cibleManquer();
        }
        else if (element == boutonPause)
        {
            if (boutonPause.getText().equals("Pause"))
            {
                boutonPause.setText("Reprendre");
            }
            else
            {
                boutonPause.setText("Pause");
            }
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
        boutonTirManque = (Button) findViewById(R.id.TirManque);
        boutonLancerPartie = (Button) findViewById(R.id.LancerPartie);
        listJoueur = (ListView) findViewById(R.id.MesJoueur);
        affichageImpact = (TextView) findViewById(R.id.Impact);
        boutonPause = (Button) findViewById(R.id.Pause);
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
        boutonTirManque.setOnClickListener(this);
        boutonTirManque.setVisibility(View.INVISIBLE);
        boutonLancerPartie.setVisibility(View.INVISIBLE);
        boutonLancerPartie.setOnClickListener(this);
        boutonPause.setVisibility(View.INVISIBLE); // Fonction Pause non-fonctionnel
        boutonPause.setOnClickListener(this);
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
                case Partie.JOUEUR_SUIVANT:
                    Log.d(TAG, "JOUEUR_SUIVANT" + "Joueur: " + b.getString("joueur"));
                    AfficheQuiDoitJouer(b.getString("joueur"));
                    break;
                case Partie.SET_SCORE:
                    Log.d(TAG, "SET_SCORE " + "Joueur: " + b.getString("joueur") + " Score: " + b.getInt("score"));
                    actualiserScoreIHM(b.getString("joueur"), b.getInt("score"));
                    break;
                case Partie.IMPACT:
                    Log.d(TAG, "IMPACT " + "Joueur: " + b.getString("joueur") + " TypePoint: " + b.getInt("typePoint") + " NumeroCible: " + b.getInt("numeroCible"));
                    b.getInt("typePoint");
                    b.getInt("numeroCible");
                    afficherImpact( b.getInt("typePoint"),b.getInt("numeroCible"));
                    break;
                case Partie.GAGNANT:
                    Log.d(TAG, "GAGNANT " + b.getString("gagnant"));
                    afficheGagnant(b.getString("gagnant"));
                    break;
                case Partie.CONNEXION_CIBLE:
                    Log.d(TAG, "CONNEXION_CIBLE ");
                    boutonLancerPartie.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    /**
     * @brief Méthode qui permet d'avoir le retour d'une activité
     *
     * @fn ActivitePartie::onActivityResult(int requestCode, int resultCode, Intent data)
     *
     */
    public void initialiserListeJoueur()
    {
        Log.d(TAG, "initialiserListeJoueur()");
        adapteur = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
        for (int i = 0; i < mesJoueurs.size(); i++)
        {
            adapteur.add(mesJoueurs.get(i).getNom());
        }
        listJoueur.setAdapter(adapteur);
    }

    /**
     * @brief Méthode qui actualise le score du joueur sur IHM
     *
     * @fn ActivitePartie::actualiserScoreIHM(String joueur, int score)
     *
     */
    public void actualiserScoreIHM(String joueur, int score)
    {
        Log.d(TAG, "actualiserScoreIHM()");
        int idJoueur = 0;

        for (int i = 0; i < mesJoueurs.size(); i++)
        {
            if (mesJoueurs.get(i).getNom().equals(joueur))
            {
                idJoueur = i;
            }
        }
        mesJoueurs.get(idJoueur).setScore(score);
        String chaineCaractere = mesJoueurs.get(idJoueur).getNom() + ": " + mesJoueurs.get(idJoueur).getScore();
        adapteur.remove(adapteur.getItem(idJoueur));
        adapteur.insert(chaineCaractere, idJoueur);
    }

    /**
     * @brief Méthode qui affiche qui doit jouer
     *
     * @fn ActivitePartie::AfficheQuiDoitJouer(String joueur)
     *
     */
    public void AfficheQuiDoitJouer(String joueur)
    {
        Log.d(TAG, "AfficheQuiDoitJouer()");
        String chaineCaractere;
        for (int i = 0; i < mesJoueurs.size(); i++)
        {
            if (mesJoueurs.get(i).getNom().equals(joueur))
            {
                chaineCaractere = "-> " + mesJoueurs.get(i).getNom() + ": " + mesJoueurs.get(i).getScore();
            }
            else {
                chaineCaractere = mesJoueurs.get(i).getNom() + ": " + mesJoueurs.get(i).getScore();
            }
            adapteur.remove(adapteur.getItem(i));
            adapteur.insert(chaineCaractere, i);

        }
        affichageImpact.setText("");
    }

    /**
     * @brief Méthode qui affiche l'impact de la volley actuelle
     *
     * @fn ActivitePartie::afficherImpact(int typeImpact, int numeroImpact)
     *
     */
    public void afficherImpact(int typeImpact, int numeroImpact)
    {
        Log.d(TAG, "afficherImpact()");
        String lettreTypeImpact = new String();
        switch (typeImpact)
        {
            case 0:
                lettreTypeImpact = "MISS";
                break;
            case 1:
                lettreTypeImpact = "S";
                break;
            case 2:
                lettreTypeImpact = "D";
                break;
            case 3:
                lettreTypeImpact = "T";
                break;
        }
        if (lettreTypeImpact == "MISS")
        {
            affichageImpact.setText(affichageImpact.getText().toString().concat("MISS "));
        }
        else
        {
            affichageImpact.setText(affichageImpact.getText().toString().concat(lettreTypeImpact + Integer.toString(numeroImpact) + " "));
        }
    }

    /**
     * @brief Méthode qui affiche IHM de fin
     *
     * @fn ActivitePartie::AfficheGagnant(String gagnant)
     *
     */
    public void afficheGagnant(String gagnant)
    {
        Log.d(TAG, "afficheGagnant()");
        Intent IHMGagnant = new Intent(ActivitePartie.this, ActiviteFinPartie.class);
        List<Joueur>lesJoueur = new ArrayList<Joueur>();
        for (int i = 0; i < mesJoueurs.size(); i++)
        {
            if (mesJoueurs.get(i).getNom().equals(gagnant))
            {
                IHMGagnant.putExtra("gagnant", mesJoueurs.get(i).getNom());
            }
            else
            {
                lesJoueur.add(mesJoueurs.get(i));
            }

            IHMGagnant.putExtra("joueurs", (Serializable) lesJoueur);
            startActivity(IHMGagnant);
            finish();
        }
    }

    @Override
    public void finish()
    {
        super.finish();
        maPartie.deconnecterPeripheriquesBluetooth();
    }
}

