package projet.lasalle84.darts;
/**
 * @file ActiviteAjouterJoueur.java
 * @brief Déclaration de la classe ActivitePartie
 * @author Menella Erwan
 */
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

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
    private final static String NOM_PERIPHERIQUE_BLUETOOTH_ECRAN = "ecran-darts";   //!< le nom du périphérique Bluetooth du module écran
    private final static String NOM_PERIPHERIQUE_BLUETOOTH_CIBLE = "impact-darts";  //!< le nom du périphérique Bluetooth du module cible
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;                     //!< Code que le bluetooth est ativer
    /**
     * Attributs
     */
    private BluetoothAdapter bluetoothAdapter;                      //!<
    private Set<BluetoothDevice> devices;                           //!< Les peripheriques qui sont appairés
    private Peripherique raspberry = null;                          //!< Peripherique raspberry connecté en Bluetooth
    private Peripherique darts = null;                              //!< Peripherique darts connecté en Bluetooth
    private Button boutonSeConnecter;                               //!< Le bouton de connexion
    private Button boutonTirManque;                                 //!< Le bouton

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
        recupererPeripheriques();
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
            connecterPeripheriquesBluetooth();
            boutonSeConnecter.setVisibility(View.GONE);
            boutonTirManque.setVisibility(View.VISIBLE);
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
    }

    /**
     * @brief Méthode permet d"active le bluetooth
     *
     * @fn ActivitePartie::ActiverBlutooth()
     *
     */
    public void activerBluetooth()
    {
        Log.d(TAG,"activerBlutooth()");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
        {
            Log.d(TAG,"Bluetooth non activé !");
            Toast.makeText(getApplicationContext(), "Bluetooth non activé !", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (!bluetoothAdapter.isEnabled())
            {
                Log.d(TAG,"Bluetooth non activé !");
                Toast.makeText(getApplicationContext(), "Bluetooth non activé !", Toast.LENGTH_SHORT).show();
                Intent activeBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(activeBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
            }
            else
            {
                Log.d(TAG,"Bluetooth activé !");
                Toast.makeText(getApplicationContext(), "Bluetooth activé", Toast.LENGTH_SHORT).show();
            }
        }
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

    /**
     * @brief Récupère les péripheriques Bluetooth qui sont appairés
     *
     * @fn ActivitePartie::recupererPeripheriques()
     *
     */
    public void recupererPeripheriques()
    {
        Log.d(TAG,"recupererPeripheriques()");
        devices = bluetoothAdapter.getBondedDevices();
    }

    /**
     * @brief Se connecter sur un péripherique via son adresse
     *
     * @fn ActivitePartie::connecterPeripheriquesBluetooth
     */
    public void connecterPeripheriquesBluetooth()
    {
        Log.d(TAG,"connecterPeripheriquesBluetooth()");

        String nomPeripherique = NOM_PERIPHERIQUE_BLUETOOTH_ECRAN;
        for (BluetoothDevice device : devices)
        {
            if(device.getName().contains(nomPeripherique))
            {
                raspberry = new Peripherique(device, handler);
                Log.d(TAG,"Adresse du péripherique raspberrypi " + device.getAddress());
                break;
            }
        }

        nomPeripherique = NOM_PERIPHERIQUE_BLUETOOTH_CIBLE;
        for (BluetoothDevice device : devices)
        {
            if(device.getName().contains(nomPeripherique))
            {
                darts = new Peripherique(device, handler);
                Log.d(TAG,"Adresse du péripherique darts " + device.getAddress());
                break;
            }
        }

        if(raspberry != null)
            raspberry.connecter();
        if(darts != null)
            darts.connecter();
    }

    /**
     * @brief Envoie une trame à un péripherique
     *
     * @fn ActivitePartie::envoyerTrame(Peripherique peripherique, String trame)
     *
     */
    public void envoyerTrame(Peripherique peripherique, String trame)
    {
        Log.d(TAG,"envoyerTrame() " + peripherique.getNom());
        if(peripherique != null)
            peripherique.envoyer(trame);
    }

    // Gère les communications avec le thread réseau
    final private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            Bundle b = msg.getData();

            switch(b.getInt("etat"))
            {
                case Peripherique.CODE_ERREUR_CONNECTER:
                    Log.d(TAG,"<Bluetooth> Erreur " + b.getString("nom") + " [" + b.getString("adresse") + "] connecter");
                    break;
                case Peripherique.CODE_ERREUR_RECEVOIR:
                    Log.d(TAG,"<Bluetooth> Erreur " + b.getString("nom") + " [" + b.getString("adresse") + "] envoyer");
                    //reconnecter ?
                    break;
                case Peripherique.CODE_ERREUR_ENVOYER:
                    Log.d(TAG,"<Bluetooth> Erreur " + b.getString("nom") + " [" + b.getString("adresse") + "] envoyer");
                    //reconnecter ?
                    break;
                case Peripherique.CODE_CONNEXION:
                    Log.d(TAG,"<Bluetooth> Connexion " + b.getString("nom") + " [" + b.getString("adresse") + "] ok");
                    break;
                case Peripherique.CODE_RECEPTION:
                    String donnees = b.getString("donnees");
                    if(donnees.contains("\r\n"))
                    {
                        Log.d(TAG,"<Bluetooth> Données reçues " + b.getString("nom") + " [" + b.getString("adresse") + "] : " + donnees.replace("\r\n", ""));
                    }
                    else
                    {
                        Log.d(TAG,"<Bluetooth> Données reçues " + b.getString("nom") + " [" + b.getString("adresse") + "] : " + donnees);
                    }
                    break;
                case Peripherique.CODE_DECONNEXION:
                    Log.d(TAG,"<Bluetooth> Déconnexion " + b.getString("nom") + " [" + b.getString("adresse") + "] ok");
                    break;
                default:
                    Log.d(TAG,"<Bluetooth> code état inconnu ! ");
            }
        }
    };
}
