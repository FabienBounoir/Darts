package projet.lasalle84.darts;
/**
 * @file ActiviteAjouterJoueur.java
 * @brief Déclaration de la classe ActivitePartie
 * @author Menella Erwan
 */
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Set;

/**
 * @class ActivitePartie
 * @brief Déclaration de la classe ActivitePartie
 */
public class ActivitePartie extends AppCompatActivity {
    /**
     * @brief Méthode appelée à la création de l'activité
     *
     * @fn ActivitePartie::onCreate(Bundle savedInstanceState)
     * @param savedInstanceState
     */
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    private final static String TAG = "IHMPartie";
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> devices;
    private Peripherique monPeripherique;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partie);

        ActiverBlutooth();
        afficherPeripheriques();
        //jouer();

    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
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

    public void afficherPeripheriques()
    {
        devices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice blueDevice : devices)
        {
            Toast.makeText(getApplicationContext(), "Device = " + blueDevice.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    public void jouer()
    {
        handler = new Handler();

        for (BluetoothDevice device : devices)
        {
            if (device.getAddress() == "DC:A6:32:52:85:1A")
            {
                monPeripherique = new Peripherique(device,handler);
            }
        }

        //monPeripherique.connecter();
        //monPeripherique.envoyer("$DART;START;301;2;Erwan;Fabien\r\n");
        //monPeripherique.deconnecter();
    }
}



