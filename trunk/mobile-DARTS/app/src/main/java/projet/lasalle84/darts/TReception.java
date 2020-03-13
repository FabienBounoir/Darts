package projet.lasalle84.darts;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import static android.content.ContentValues.TAG;

public class TReception extends Thread
{
    Handler handlerUI;
    private boolean fini;
    InputStream receiveStream;

    TReception(Handler h)
    {
        handlerUI = h;
        fini = false;
    }

    @Override
    public void run()
    {
        BufferedReader reception = new BufferedReader(new InputStreamReader(receiveStream));
        while(!fini)
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
                //System.out.println("<Socket> error read");
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

    public void arreter()
    {
        if(!fini)
        {
            fini = true;
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
