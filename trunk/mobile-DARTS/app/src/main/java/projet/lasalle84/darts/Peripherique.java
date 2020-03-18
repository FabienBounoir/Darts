package projet.lasalle84.darts;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @file Peripherique.java
 * @brief Déclaration de la classe Peripherique
 * @author Menella Erwan
 */

/**
 * @class Peripherique
 * @brief Déclaration de la classe Peripherique
 */

public class Peripherique extends Thread
{
    private BluetoothDevice device;                     //!< Objet BluetoothDevice
    private String nom;                                 //!< Nom du peripherique bluetooth
    private String adresse;                             //!< Adresse du peripherique bluetooth
    private Handler handler;                            //!< Handler permet de traiter les trames
    private BluetoothSocket socket = null;              //!< Socket Bluetooth
    private InputStream receiveStream = null;           //!< Input du Bluetooth
    private OutputStream sendStream = null;             //!< Output du BLuetooth
    private TReception tReception;                      //!< Thread pour traiter les trames
    public final static int CODE_CONNEXION = 0;         //!< Code de Connection
    public final static int CODE_RECEPTION = 1;         //!< Code de Reception
    public final static int CODE_DECONNEXION = 2;       //!< Code de Deconnexion
    private final static String TAG = "Peripherique";   //!< TAG pour log


    /**
     * @brief Constructeur de la classe Peripherique
     * @fn Peripherique::Peripherique(BluetoothDevice device, Handler handler)
     * @param device,handler
     */
    public Peripherique(BluetoothDevice device, Handler handler)
    {
        Log.d(TAG,"Peripherique()");
        if(device != null)
        {
            this.device = device;
            this.nom = device.getName();
            this.adresse = device.getAddress();
            this.handler = handler;
        }
        else
        {
            this.device = device;
            this.nom = "Aucun";
            this.adresse = "";
            this.handler = handler;
        }

        try
        {
            assert device != null;
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            receiveStream = socket.getInputStream();
            sendStream = socket.getOutputStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            socket = null;
        }

        if(socket != null)
            tReception = new TReception(handler,receiveStream);
    }

    /**
     * @brief Méthode pour se connecter sur le perihperique en Bluetooth
     * @fn Peripherique::connecter()
     */
    public void connecter()
    {
        Log.d(TAG,"connecter()");
        new Thread()
        {
            @Override public void run()
            {
                try
                {
                    socket.connect();

                    Message msg = Message.obtain();
                    msg.arg1 = CODE_CONNEXION;
                    handler.sendMessage(msg);

                    tReception.start();
                    Log.d(TAG,"connecter reussi");

                }
                catch (IOException e)
                {
                    System.out.println("<Socket> error connect");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * @brief Méthode pour se deconnecter sur le perihperique en Bluetooth
     * @fn Peripherique::deconnecter()
     */
    public boolean deconnecter()
    {
        Log.d(TAG,"deconnecter()");
        try
        {
            tReception.arreter();

            socket.close();
            return true;
        }
        catch (IOException e)
        {
            System.out.println("<Socket> error close");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @brief Méthode pour envoyer une trame en Bluetooth
     * @fn Peripherique::envoyer()
     */
    public void envoyer(final String data)
    {
        Log.d(TAG,"envoyer() ");
        if(socket == null) {
            Log.d(TAG,"ANNULE");
            return;
        }
        new Thread()
        {
            @Override public void run()
            {
                try
                {
                    if(socket.isConnected())
                    {
                        sendStream.write(data.getBytes());
                        sendStream.flush();
                        Log.d(TAG,"trame envoyer");
                    }
                }
                catch (IOException e)
                {
                    System.out.println("<Socket> error send");
                    e.printStackTrace();
                }
            }
        }.start();
    }

}