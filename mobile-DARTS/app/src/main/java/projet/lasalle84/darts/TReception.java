package projet.lasalle84.darts;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
/**
 * @file TReception.java
 * @brief Déclaration de la classe Partie
 * @author Menella Erwan
 */

/**
 * @class TReception
 * @brief Déclaration de la classe TReception
 */
class TReception extends Thread
{
    /**
     * Constantes
     */
    private final String TAG = "TReception";    //!< TAG
    /**
     * Attributs
     */
    private Peripherique peripherique;
    private Handler handlerUI;                  //!< Handler pour faire passer la trame arrivant
    private boolean estFini;                    //!< Thread est fini ?
    private InputStream receiveStream;          //!< Input du Bluetooth

    /**
     * @brief Constructeur de la classe TReception
     * @fn TReception::TReception(Handler handler)
     * @param peripherique
     * @param handler
     * @param flux
     */
    TReception(Peripherique peripherique, Handler handler, InputStream flux)
    {
        Log.d(TAG,"TReception() " + peripherique.getNom() + "[" + peripherique.getAdresse() + "]");
        this.peripherique = peripherique;
        handlerUI = handler;
        estFini = false;
        receiveStream = flux;
    }

    /**
     * @brief Execution du Thread pour écouter les trames du Bluetooth
     * @fn TReception::run()
     */
    @Override
    public void run()
    {
        Log.d(TAG,"début run() " + peripherique.getNom() + "[" + peripherique.getAdresse() + "]");
        BufferedReader reception = new BufferedReader(new InputStreamReader(receiveStream));
        while(!estFini)
        {
            try
            {
                String trame = "";
                if(reception.ready())
                {
                    trame = reception.readLine();
                }
                if(trame.length() > 0)
                {
                    Log.d(TAG, "run() trame reçue : " + trame);
                    Message msg = Message.obtain();
                    Bundle b = new Bundle();
                    b.putString("nom", peripherique.getNom());
                    b.putString("adresse", peripherique.getAdresse());
                    b.putInt("etat", Peripherique.CODE_RECEPTION);
                    b.putString("donnees", trame);
                    msg.setData(b);
                    handlerUI.sendMessage(msg);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.d(TAG, "run() Erreur socket read : " + peripherique.getNom());
                Message msg = Message.obtain();
                Bundle b = new Bundle();
                b.putString("nom", peripherique.getNom());
                b.putString("adresse", peripherique.getAdresse());
                b.putInt("etat", Peripherique.CODE_ERREUR_RECEVOIR);
                msg.setData(b);
                handlerUI.sendMessage(msg);
            }
            try
            {
                Thread.sleep(250);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putString("nom", peripherique.getNom());
        b.putString("adresse", peripherique.getAdresse());
        b.putInt("etat", Peripherique.CODE_DECONNEXION);
        b.putString("donnees", "");
        msg.setData(b);
        handlerUI.sendMessage(msg);
        Log.d(TAG,"fin run() " + peripherique.getNom() + "[" + peripherique.getAdresse() + "]");
    }

    /**
     * @brief Arrêt du Thread pour écouter les trames du Bluetooth
     * @fn TReception::arreter()
     */
    public void arreter()
    {
        Log.d(TAG,"arreter() " + peripherique.getNom() + "[" + peripherique.getAdresse() + "]");
        if(!estFini)
        {
            estFini = true;
        }
        try
        {
            Thread.sleep(250);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
