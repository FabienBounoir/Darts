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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

/**
 * @class ActivitePartie
 * @brief Déclaration de la classe ActivitePartie
 */
public class ActivitePartie extends AppCompatActivity implements View.OnClickListener{
    /**
     * @brief Méthode appelée à la création de l'activité
     *
     * @fn ActivitePartie::onCreate(Bundle savedInstanceState)
     * @param savedInstanceState
     */
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;     //!< Code que le bluetooth est ativer
    private final static String TAG = "IHMPartie";                  //!< Tag pour Log
    private BluetoothAdapter bluetoothAdapter;                      //!<
    private Set<BluetoothDevice> devices;                           //!< Les peripheriques qui sont appairés
    private Peripherique raspberry;                                 //!< Peripherique raspberry connecté en Bluetooth
    private Peripherique darts;                                     //!< Peripherique darts connecté en Bluetooth
    private Handler handler;                                        //!<
    private Button Seconnecter;
    private Button tirManque;

    /**
     * @brief Méthode appelée à la création de l'activité
     *
     * @fn ActivitePartie::onCreate(Bundle savedInstanceState)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partie);
        recupererWidget();
        initialiserWidget();

        ActiverBlutooth();
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
        Log.d(TAG, "onClick()");

        if (element == Seconnecter)
        {
            Log.d(TAG, "click Seconnecter");
            connecterPeripheriqueBluetooth(raspberry,"48:2C:A0:27:A3:90");
            Seconnecter.setVisibility(View.GONE);
            tirManque.setVisibility(View.VISIBLE);
        }
    }


    /**
     * @brief Récupère les widgets du layout de l'activité
     *
     * @fn ActivitePartie::recupererWidgets()
     *
     */
    public void recupererWidget()
    {
        Log.d(TAG,"recupererWidget()");
        Seconnecter = (Button) findViewById(R.id.SeConnecter);
        tirManque = (Button) findViewById(R.id.TirManque);
    }

    /**
     * @brief Récupère les widgets du layout de l'activité
     *
     * @fn ActivitePartie::initialiserWidgets()
     *
     */
    public void initialiserWidget()
    {
        Log.d(TAG,"initialiserWidget()");
        tirManque.setVisibility(View.GONE);
        tirManque.setOnClickListener(this);
        Seconnecter.setOnClickListener(this);

    }

    /**
     * @brief Méthode permet d"active le bluetooth
     *
     * @fn ActivitePartie::ActiverBlutooth()
     *
     */
    public void ActiverBlutooth()
    {
        Log.d(TAG,"ActiverBlutooth()");
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
     * @brief Récupère les Péripheriques qui sont appairés
     *
     * @fn ActivitePartie::recupererPeripheriques()
     *
     */
    public void recupererPeripheriques()
    {
        Log.d(TAG,"afficherPeripheriques()");
        devices = bluetoothAdapter.getBondedDevices();

    }

    /**
     * @brief Se connecter sur un péripherique via son adresse
     *
     * @fn ActivitePartie::connecterLesPeripheriqueBluetooth(String adresse)
     *
     */
    public void connecterPeripheriqueBluetooth(Peripherique monPeripherique,String adresse)
    {
        Log.d(TAG,"connecterPeripheriqueBluetooth()");
        handler = new Handler();

        for (BluetoothDevice device : devices)
        {
            if (device.getAddress().equals(adresse))
            {
                monPeripherique = new Peripherique(device,handler);
                Log.d(TAG,"Adresse du nouveau peripherique "+ device.getAddress());
            }
        }

        monPeripherique.connecter();
    }

    /**
     * @brief Envoie une trame à un péripherique
     *
     * @fn ActivitePartie::envoyerTrame(Peripherique peripherique,String trame)
     *
     */
    public void envoyerTrame(Peripherique peripherique,String trame)
    {
        Log.d(TAG,"connecterLesPeripheriqueBluetooth()");
        peripherique.envoyer(trame);
    }
}



