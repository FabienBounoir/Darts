package projet.lasalle84.darts;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
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
    /**
     * Constantes
     */
    private final static String TAG = "Peripherique";   //!< TAG pour log
    public final static int CODE_CONNEXION = 0;         //!< Code de Connection
    public final static int CODE_RECEPTION = 1;         //!< Code de Reception
    public final static int CODE_DECONNEXION = 2;       //!< Code de Deconnexion
    public final static int CODE_ERREUR_ENVOYER = -1;   //!< Code erreur lors de l'envoi
    public final static int CODE_ERREUR_RECEVOIR = -2;  //!< Code erreur lors de la réception
    public final static int CODE_ERREUR_CONNECTER = -3; //!< Code erreur lors de la connexion
    /**
     * Attributs
     */
    private BluetoothDevice device;                     //!< Objet BluetoothDevice
    private String nom;                                 //!< Nom du peripherique bluetooth
    private String adresse;                             //!< Adresse du peripherique bluetooth
    private Handler handler;                            //!< Handler permet de traiter les trames
    private BluetoothSocket socket = null;              //!< Socket Bluetooth
    private InputStream receiveStream = null;           //!< Input du Bluetooth
    private OutputStream sendStream = null;             //!< Output du BLuetooth
    private TReception tReception;                      //!< Thread pour traiter les trames

    /**
     * @brief Constructeur de la classe Peripherique
     * @fn Peripherique::Peripherique(BluetoothDevice device, Handler handler)
     * @param device,handler
     */
    public Peripherique(BluetoothDevice device, Handler handler)
    {
        Log.d(TAG,"Peripherique() " + device.getName() + "[" + device.getAddress() + "]");
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
            tReception = new TReception(this, handler, receiveStream);
    }

    /**
     * @brief Méthode qui retourne le nom du périphérique
     * @fn Peripherique::getNom()
     * @return nom le nom du périphérique
     */
    public String getNom()
    {
        return nom;
    }

    /**
     * @brief Méthode qui retourne l'adresse du périphérique
     * @fn Peripherique::getAdresse()
     * @return adresse l'adresse du périphérique
     */
    public String getAdresse()
    {
        return adresse;
    }

    /**
     * @brief Méthode pour se connecter sur le perihperique en Bluetooth
     * @fn Peripherique::connecter()
     */
    public void connecter()
    {
        Log.d(TAG,"connecter() " + device.getName() + "[" + device.getAddress() + "]");
        new Thread()
        {
            @Override public void run()
            {
                try
                {
                    socket.connect();

                    Message msg = Message.obtain();
                    Bundle b = new Bundle();
                    b.putString("nom", getNom());
                    b.putString("adresse", getAdresse());
                    b.putInt("etat", CODE_CONNEXION);
                    msg.setData(b);
                    handler.sendMessage(msg);

                    // on démarre le thread de réception
                    tReception.start();
                    Log.d(TAG,"connexion reussie " + getNom());

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    Log.d(TAG,"erreur connexion " + getNom());
                    Message msg = Message.obtain();
                    Bundle b = new Bundle();
                    b.putString("nom", getNom());
                    b.putString("adresse", getAdresse());
                    b.putInt("etat", CODE_ERREUR_CONNECTER);
                    msg.setData(b);
                    handler.sendMessage(msg);
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
        Log.d(TAG,"deconnecter() " + device.getName() + "[" + device.getAddress() + "]");
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
        Log.d(TAG,"envoyer() " + device.getName() + "[" + device.getAddress() + "]");
        if(socket == null)
        {
            Log.d(TAG,"pas d'envoi");
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
                        Log.d(TAG, "envoyer() trame envoyée : " + data);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    Log.d(TAG, "envoyer() Erreur socket write : " + getNom());
                    Message msg = Message.obtain();
                    Bundle b = new Bundle();
                    b.putString("nom", getNom());
                    b.putString("adresse", getAdresse());
                    b.putInt("etat", CODE_ERREUR_ENVOYER);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }
}
