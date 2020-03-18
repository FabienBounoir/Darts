package projet.lasalle84.darts;

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
    private Handler handlerUI;                  //!< Handler pour faire passer la trame arrivant
    private boolean estFini;                    //!< Thread est fini ?
    private InputStream receiveStream;          //!< Input du Bluetooth
    private final String TAG = "TReception";    //!< TAG

    /**
     * @brief Constructeur de la classe TReception
     * @fn TReception::TReception(Handler handler)
     * @param handler
     */
    TReception(Handler handler, InputStream flux)
    {
        Log.d(TAG,"TReception()" );
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
        Log.d(TAG,"run()" );
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
                    Log.d(TAG, "run() trame : " + trame);
                    Message msg = Message.obtain();
                    msg.what = Peripherique.CODE_RECEPTION;
                    msg.obj = trame;
                    handlerUI.sendMessage(msg);
                }
            }
            catch (IOException e)
            {
                System.out.println("<Socket> error read");
                e.printStackTrace();
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

    /**
     * @brief Arrêt du Thread pour écouter les trames du Bluetooth
     * @fn TReception::arreter()
     */
    public void arreter()
    {
        Log.d(TAG,"arreter()" );
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
