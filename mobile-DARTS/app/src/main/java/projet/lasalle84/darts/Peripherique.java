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

public class Peripherique extends Thread
{
    private BluetoothDevice device;
    private String nom;
    private String adresse;
    private Handler handler;
    private BluetoothSocket socket = null;
    private InputStream receiveStream = null;
    private OutputStream sendStream = null;
    private TReception tReception;
    public final static int CODE_CONNEXION = 0;
    public final static int CODE_RECEPTION = 1;
    public final static int CODE_DECONNEXION = 2;
    private final static String TAG = "Peripherique";



    public Peripherique(BluetoothDevice device, Handler handler)
    {
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
            tReception = new TReception(handler);
        }

    public void connecter()
    {
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

                }
                catch (IOException e)
                {
                    System.out.println("<Socket> error connect");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public boolean deconnecter()
    {
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

    public void envoyer(final String data)
    {
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