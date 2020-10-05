package com.lasalle.darts;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by smaniotto on 04/04/18.
 */

public class PeripheriqueBluetooth extends Thread
{
    private static final String TAG = "PeripheriqueBluetooth"; //!< le TAG de la classe pour les logs
    Handler handlerUIPrincipal;
    private String nom;
    private String adresse;
    private Handler handler = null;
    private BluetoothDevice device = null;
    private BluetoothSocket socket = null;
    private InputStream receiveStream = null;
    private OutputStream sendStream = null;
    private TReception tReception = null;
    private final ReentrantLock mutex = new ReentrantLock();
    public final static int CODE_CONNEXION = 0;
    public final static int CODE_RECEPTION = 1;
    public final static int CODE_DECONNEXION = 2;
    public final static int CODE_ERREUR_ENVOYER = -1;
    public final static int CODE_ERREUR_RECEVOIR = -2;
    public final static int CODE_ERREUR_CONNECTER = -3;

    public PeripheriqueBluetooth(BluetoothDevice device, Handler handler)
    {
        handlerUIPrincipal = handler;
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
            Log.d(TAG, "PeripheriqueBluetooth() nom : " + device.getName());
            ParcelUuid[] uuids = device.getUuids();
            /*if(uuids != null)
                for(int i = 0; i < uuids.length; i++)
                    System.out.println("<Bluetooth> uuid " + uuids[i].getUuid().toString());
            else
                System.out.println("<Bluetooth> uuid null !");*/

            socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            //socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
            Log.d(TAG, "PeripheriqueBluetooth() socket : " + getNom());

            // API 23
            //System.out.println("<Bluetooth> max receive packet size : " + socket.getMaxReceivePacketSize());
            //System.out.println("<Bluetooth> max transmit packet size : " + socket.getMaxTransmitPacketSize());
            //System.out.println("<Bluetooth> connection type : " + socket.getConnectionType());

            receiveStream = socket.getInputStream();
            sendStream = socket.getOutputStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("<Bluetooth> Erreur socket !");
            socket = null;
        }

        if(socket != null)
            tReception = new TReception(handler);
    }

    public BluetoothDevice getDevice()
    {
        return device;
    }

    public String getNom()
    {
        return nom;
    }

    public String getAdresse()
    {
        return adresse;
    }

    public boolean estConnecte()
    {
        if(socket != null)
            return socket.isConnected();
        return false;
    }

    public void setNom(String nom)
    {
        this.nom = nom;
    }

    public String toString()
    {
        return "\nNom : " + nom + "\nAdresse : " + adresse;
    }

    public void envoyer(final String data)
    {
        if(socket == null)
            return;
        //Log.d(TAG, "envoyer() data : " + data);
        new Thread()
        {
            @Override public void run()
            {
                try
                {
                    if(socket.isConnected())
                    {
                        //mutex.lock();
                        sendStream.write(data.getBytes());
                        sendStream.flush();
                        //mutex.unlock();
                    }
                    else
                    {
                        Log.d(TAG, "envoyer() socket non connecté : " + getNom());
                    }
                }
                catch (IOException e)
                {
                    Log.d(TAG, "envoyer() Erreur socket write : " + getNom());
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    Bundle b = new Bundle();
                    b.putString("nom", getNom());
                    b.putString("adresse", getAdresse());
                    b.putInt("etat", CODE_ERREUR_ENVOYER);
                    msg.setData(b);
                    handlerUIPrincipal.sendMessage(msg);
                }
                finally
                {
                    //mutex.unlock();
                }
            }
        }.start();
    }

    private synchronized void recevoir()
    {
        Log.d(TAG, "recevoir() nom : " + getNom());
        tReception.start();
    }

    public void connecter()
    {
        Log.d(TAG, "connecter() nom : " + getNom());
        new Thread()
        {
            @Override public void run()
            {
                try
                {
                    socket.connect();
                    Message msg = Message.obtain();
                    //msg.arg1 = CODE_CONNEXION;
                    Bundle b = new Bundle();
                    b.putString("nom", getNom());
                    b.putString("adresse", getAdresse());
                    b.putInt("etat", CODE_CONNEXION);
                    b.putString("donnees", "");
                    msg.setData(b);
                    handler.sendMessage(msg);
                    recevoir();
                }
                catch (IOException e)
                {
                    Log.d(TAG, "connecter() Erreur connect : " + getNom());
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    Bundle b = new Bundle();
                    b.putString("nom", getNom());
                    b.putString("adresse", getAdresse());
                    b.putInt("etat", CODE_ERREUR_CONNECTER);
                    msg.setData(b);
                    handlerUIPrincipal.sendMessage(msg);
                }
            }
        }.start();
    }

    public boolean deconnecter(boolean fermeture)
    {
        Log.d(TAG, "deconnecter() nom : " + getNom());
        try
        {
            //if(estConnecte())
            {
                tReception.arreter();

                if(fermeture)
                {
                    socket.close();
                }
                return true;
            }
        }
        catch (IOException e)
        {
            Log.d(TAG, "deconnecter() Erreur close : " + getNom());
            e.printStackTrace();
            return false;
        }
    }

    private class TReception extends Thread
    {
        private static final String TAG = "TReception"; //!< le TAG de la classe pour les logs
        Handler handlerUI;
        private boolean fini;

        TReception(Handler h)
        {
            handlerUI = h;
            fini = false;
        }

        @Override public void run()
        {
            Log.d(TAG, "run() nom : " + getNom());
            BufferedReader reception = new BufferedReader(new InputStreamReader(receiveStream));
            while(!fini)
            {
                try
                {
                    String trame = "";
                    if(reception.ready())
                    {
                        //mutex.lock();
                        trame = reception.readLine();
                        //mutex.unlock();
                    }
                    if(trame.length() > 0)
                    {
                        Log.d(TAG, "run() trame : " + trame);
                        Message msg = Message.obtain();
                        Bundle b = new Bundle();
                        b.putString("nom", getNom());
                        b.putString("adresse", getAdresse());
                        b.putInt("etat", CODE_RECEPTION);
                        b.putString("donnees", trame);
                        msg.setData(b);
                        handlerUI.sendMessage(msg);
                    }
                }
                catch (IOException e)
                {
                    Log.d(TAG, "run() Erreur socket read : " + getNom());
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    Bundle b = new Bundle();
                    b.putString("nom", getNom());
                    b.putString("adresse", getAdresse());
                    b.putInt("etat", CODE_ERREUR_RECEVOIR);
                    msg.setData(b);
                    handlerUI.sendMessage(msg);
                }
                finally
                {
                    //mutex.unlock();
                }
                attendre(50);
            }
            Message msg = Message.obtain();
            //msg.arg1 = CODE_DECONNEXION;
            Bundle b = new Bundle();
            b.putString("nom", getNom());
            b.putString("adresse", getAdresse());
            b.putInt("etat", CODE_DECONNEXION);
            b.putString("donnees", "");
            msg.setData(b);
            handler.sendMessage(msg);
            Log.d(TAG, "run() fin " + getNom());
        }

        /**
         * Méthode attendre() permettant d'attendre un temps donné en ms
         *
         * @param tempsMillisecondes
         */
        private void attendre(int tempsMillisecondes)
        {
            //Log.d("Thread.sleep", "attendre() tempsMillisecondes : " + tempsMillisecondes);
            try
            {
                Thread.sleep(tempsMillisecondes);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        /**
         * Méthode arreter() permettant d'arrêter le thread réception
         *
         */
        public void arreter()
        {
            Log.d(TAG, "arreter()");
            if(fini == false)
            {
                fini = true;
            }
            try
            {
                Thread.sleep(150);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
