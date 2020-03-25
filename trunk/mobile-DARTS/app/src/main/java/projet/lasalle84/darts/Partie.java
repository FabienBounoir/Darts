package projet.lasalle84.darts;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static android.os.SystemClock.sleep;

/**
 * @file Partie.java
 * @brief Déclaration de la classe Partie
 * @author Menella Erwan
 */

/**
 * @class Partie
 * @brief Déclaration de la classe Partie
 */
public class Partie
{
    /**
     * Constantes
     */
    private final static String TAG = "Partie";                                     //!< Tag pour Log
    private final static String NOM_PERIPHERIQUE_BLUETOOTH_ECRAN = "ecran-darts";   //!< le nom du périphérique Bluetooth du module écran
    private final static String NOM_PERIPHERIQUE_BLUETOOTH_CIBLE = "impact-darts";  //!< le nom du périphérique Bluetooth du module cible
    public final static int NB_FLECHETTE = 3;
    /**
     * Code HandlerUI
     */
    public final static int JOUEUR_SUIVANT = 0;
    public final static int SET_SCORE = 1;
    public final static int IMPACT = 2;
    public final static int GAGNANT = 3;



    /**
     * Attributs
     */
    private int nbJoueurs;                                          //!< Nombre de joueur
    private int nbManche;                                           //!< Nombre de manche
    private TypeJeu typeJeu;                                        //!< Mode de jeu
    private ArrayList<Joueur> lesJoueurs;                           //!< Les objets Joueur stocker dans un conteneur (Queue)
    private BluetoothAdapter bluetoothAdapter;                      //!<
    private Set<BluetoothDevice> devices;                           //!< Les peripheriques qui sont appairés
    private Peripherique raspberry = null;                          //!< Peripherique raspberry connecté en Bluetooth
    private Peripherique darts = null;                              //!< Peripherique darts connecté en Bluetooth
    private Handler handlerUI = null;                               //!< Handler pour gérer l'interface
    private int impact[] = null;
    private boolean impactEstRecuperer;
    private boolean estFini = false;
    private boolean estDoubleImpact = false;

    /**
     * @brief Constructeur de la classe Partie
     *
     * @fn Partie::Partie(Queue<Joueur> lesJoueur, TypeJeu typeJeu)
     * @param lesJoueurs
     * @param typeJeu
     */
    public Partie(ArrayList<Joueur> lesJoueurs, TypeJeu typeJeu, BluetoothAdapter bluetoothAdapter, Handler handlerUI)
    {
        this.lesJoueurs = lesJoueurs;
        this.typeJeu = typeJeu;
        this.bluetoothAdapter = bluetoothAdapter;
        this.handlerUI = handlerUI;
        this.nbManche = 0;
        this.nbJoueurs = this.lesJoueurs.size();
        recupererPeripheriques();
        connecterPeripheriquesBluetooth();

    }



    /**
     * @brief Récupère les péripheriques Bluetooth qui sont appairés
     *
     * @fn Partie::recupererPeripheriques()
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
     * @fn Partie::connecterPeripheriquesBluetooth
     */
    public void connecterPeripheriquesBluetooth()
    {
        Log.d(TAG,"connecterPeripheriquesBluetooth()");

        String nomPeripherique = NOM_PERIPHERIQUE_BLUETOOTH_ECRAN;
        for (BluetoothDevice device : devices)
        {
            if(device.getName().contains(nomPeripherique))
            {
                raspberry = new Peripherique(device, handlerBluetooth);
                Log.d(TAG,"Adresse du péripherique raspberrypi " + device.getAddress());
                break;
            }
        }

        nomPeripherique = NOM_PERIPHERIQUE_BLUETOOTH_CIBLE;
        for (BluetoothDevice device : devices)
        {
            if(device.getName().contains(nomPeripherique))
            {
                darts = new Peripherique(device, handlerBluetooth);
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
     * @fn Partie::envoyerTrame(Peripherique peripherique, String trame)
     *
     */
    public void envoyerTrame(Peripherique peripherique, String trame)
    {
        Log.d(TAG,"envoyerTrame() " + peripherique.getNom());
        if(peripherique != null)
            peripherique.envoyer(trame);
    }
    /**
     * @brief Démarrer la partie
     *
     * @fn Partie::demarrer()
     *
     */
    public void demarrer() {
        Log.d(TAG, "demarrer()");
        initialiserPartie();

        do
        {
            nbManche++;
            Iterator<Joueur> it = lesJoueurs.iterator();
            Log.d(TAG, "Manche numéro " + nbManche);
            while (it.hasNext())
            {
                Joueur monJoueur = it.next();
                Log.d(TAG, "c'est le tour à " + monJoueur.getNom());
                actualiserJoueurIHM(monJoueur.getNom());
                int pointVolley = 0;

                for(int i = 0; i < NB_FLECHETTE; i++)
                {
                    impactEstRecuperer = false;
                    attendreImpact();
                    pointVolley =+ impact[0]*impact[1];
                    if(!monJoueur.retirerPoint(pointVolley))
                    {
                        i = NB_FLECHETTE;
                        pointVolley = 0;
                    }
                    else if (monJoueur.getScore() == 0 && !typeJeu.estDoubleOut())
                    {
                        envoyerGagnantIHM(monJoueur);
                    }
                    else if (monJoueur.getScore() == 0 && typeJeu.estDoubleOut() && estDoubleImpact)
                    {
                        envoyerGagnantIHM(monJoueur);
                    }


                }
                actualiserScoreIHM(monJoueur, monJoueur.getScore());
            }
        }while (!estFini);

    }

    /**
     * @brief Initialiser la partie
     *
     * @fn Partie::initialiserPartie()
     *
     */
    public void initialiserPartie() {
        Iterator<Joueur> it = lesJoueurs.iterator();
        String nomJoueurTrame = new String();
        while(it.hasNext())
        {
            Joueur monJoueur = it.next();
            monJoueur.setScore(typeJeu.getPointDepart());
            actualiserScoreIHM(monJoueur,typeJeu.getPointDepart());
            nomJoueurTrame = nomJoueurTrame.concat(monJoueur.getNom() + ";");
        }

        envoyerTrame(raspberry,"$DARTS;START;" + typeJeu.getTypeJeu() + ";" + lesJoueurs.size() + ";" + nomJoueurTrame + "\r\n");
    }

    // Gère les communications avec le thread réseau

    private Handler handlerBluetooth = new Handler()
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
                        traitementTrame(donnees);
                    }
                    else
                    {
                        Log.d(TAG,"<Bluetooth> Données reçues " + b.getString("nom") + " [" + b.getString("adresse") + "] : " + donnees);
                        traitementTrame(donnees);
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

    /**
     * @brief Attendre l'impact
     *
     * @fn Partie::attendreImpact()
     *
     */
    public void attendreImpact() {
        Log.d(TAG, "attendreImpact()");
        while (!impactEstRecuperer)
        {
            sleep(1000);

        }
        envoyerTrame(raspberry,"$DART;GAME;" + impact[0] + ";" + impact[1] + "\r\n");
        impactIHM(impact[0],impact[1]);
        Log.d(TAG, "Type cible: " + impact[0] + "Numero Cible:" + impact[1]);

    }

    /**
     * @brief traiter la trame reçu par Handler
     *
     * @fn Partie::traitementTrame(String trame)
     *
     */
    public void traitementTrame(String trame)
    {
        trame = trame.replace("\r\n", "");
        String[] trameDecoupe = trame.split(";",4);
        Bundle bundle = new Bundle();

        switch (trameDecoupe[1])
        {
            case "GAME":
                Log.d(TAG, "traitementTrame: GAME");
                impact = new int[2];
                impact[0] = Integer.parseInt(trameDecoupe[2]);
                impact[1] = Integer.parseInt(trameDecoupe[3]);
                impactEstRecuperer = true;

                break;
            case "PAUSE":
                //TODO PAUSE
                break;
            case "PLAY":
                //TODO PLAY
                break;
            case "STOP":
                //TODO STOP
                break;
            case "RESET":
                //TODO RESET
                break;
            case "HEARTBEAT ":
                //TODO HEARTBEAT
                break;
            case "ACK":
                //TODO ACK
                break;
        }
    }

    /**
     * @brief Actualiser le score du joueur à IHM
     *
     * @fn Partie::actualiserScoreIHM(Joueur monJoueur, int nbFlechette)
     *
     */
    public void actualiserScoreIHM(Joueur monJoueur, int score)
    {
        Log.d(TAG, "actualiserScoreIHM()");
        Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putInt("action",SET_SCORE);
        b.putString("joueur", monJoueur.getNom());
        b.putInt("score", score);
        msg.setData(b);
        handlerUI.sendMessage(msg);
    }

    /**
     * @brief Actualiser le joueur à IHM
     *
     * @fn Partie::actualiserJoueurIHM(String monJoueur)
     *
     */
    public void actualiserJoueurIHM(String monJoueur)
    {
        Log.d(TAG, "actualiserJoueurIHM()");
        Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putInt("action",JOUEUR_SUIVANT);
        b.putString("joueur", monJoueur);
        msg.setData(b);
        handlerUI.sendMessage(msg);
    }

    /**
     * @brief Envoyer impact à IHM
     *
     * @fn Partie::impactIHM(int typePoint, int numeroCible)
     *
     */
    public void impactIHM(int typePoint, int numeroCible)
    {
        Log.d(TAG, "impactIHM()");
        Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putInt("action",IMPACT);
        b.putInt("typePoint",typePoint);
        b.putInt("numeroCible",numeroCible);
        msg.setData(b);
        handlerUI.sendMessage(msg);
    }

    /**
     * @brief Envoyer le gagnant a IHM
     *
     * @fn Partie::envoyerGagnantIHM(Joueur monJoueur)
     *
     */
    public void envoyerGagnantIHM(Joueur monJoueur) {
        Log.d(TAG, "envoyerGagnantIHM()");
        estFini = true;
        Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putInt("action",GAGNANT);
        b.putString("gagnant",monJoueur.getNom());
        msg.setData(b);
        handlerUI.sendMessage(msg);
    }
}
