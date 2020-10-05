package com.lasalle.darts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "MainActivity"; //!< le TAG de la classe pour les logs
    private BottomNavigationView bottomNavigationView;  //!< layout permettant d'avoir un menu de navigation (en haut)
    private ImageView logoDarts;
    private ImageButton connexionCible;
    private ImageButton connexionEcran;
    private ImageButton boutonDemarrer;
    private ImageButton boutonArreter;
    private ImageButton boutonReset;
    private CibleView cibleView = null;
    private SoundPool soundPool;
    private AudioManager audioManager;
    private boolean soundPoolLoaded = false;
    private int sound_180;
    private int sound_bull;
    private int sound_lost;
    private int sound_out;
    private int sound_pause;
    private int sound_play;
    private int sound_gagne;
    private int streamId = -1;
    private TextView volee;
    private TextView typePartie;
    private final String[] typesPartie = {"501 DOUBLE OUT","501","301 DOUBLE OUT","301"};
    private final int[] pointsTypesPartie = {501,501,301,301};
    private int numeroTypePartie = 3;
    private final String[] choix = {"Non", "Oui"};
    private int affichageRegle = 0;
    private int activationSon = 1;
    private static List<Joueur> joueurs = null;
    private boolean startActivityJoueurs = false;
    private static List<Joueur> joueursSelectionnes = null;
    private RecyclerView recyclerView;
    private JoueurSelectionneAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ServeurBDD serveurBDD;
    private BluetoothAdapter adaptateurBluetooth = null;
    private Set<BluetoothDevice> devices;
    PeripheriqueBluetooth peripheriqueBluetoothCible = null;
    PeripheriqueBluetooth peripheriqueBluetoothEcran = null;
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    private final static String NOM_PERIPHERIQUE_BLUETOOTH_ECRAN = "ecran-darts";
    private final static String NOM_PERIPHERIQUE_BLUETOOTH_CIBLE = "cible-darts";
    private boolean ecranEstDetecte = false;
    private boolean cibleEstDetecte = false;
    private boolean ecranEstConnecte = false;
    private boolean cibleEstConnecte = false;
    private int etatPartie = Partie.FINIE;
    private int numeroJoueur = 0;
    Volee voleeEncours = new Volee();
    private int nbSets = 1;
    private int nbManches = 3;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        serveurBDD = new ServeurBDD(this);
        serveurBDD.open();

        initialiserRessourcesLayout();

        initialiserAudio();

        demarrerBluetooth();
    }

    private void initialiserAudio()
    {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        AudioAttributes audioAttrib = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        SoundPool.Builder builder= new SoundPool.Builder();
        builder.setAudioAttributes(audioAttrib).setMaxStreams(4);
        this.soundPool = builder.build();
        sound_180 = this.soundPool.load(this, R.raw.sound_180,1);
        sound_bull = this.soundPool.load(this, R.raw.sound_bull,1);
        sound_lost = this.soundPool.load(this, R.raw.sound_lost,1);
        sound_out = this.soundPool.load(this, R.raw.sound_out,1);
        sound_pause = this.soundPool.load(this, R.raw.pause,1);
        sound_play = this.soundPool.load(this, R.raw.music,1);
        sound_gagne = this.soundPool.load(this, R.raw.gagne,1);
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPoolLoaded = true;
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume()");
        joueurs = recupererJoueurs();
        recupererJoueursSelectionnes();
        adapter.notifyDataSetChanged();
        initialiserBoutonsPartie();
    }

    /**
     * @brief Récupère et initialise les widgets du layout activity_main
     * @return void
     */
    public void initialiserRessourcesLayout()
    {
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView)findViewById(R.id.listeJoueursSelectionnes);
        //recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        joueurs = recupererJoueurs();
        joueursSelectionnes = new ArrayList<Joueur>();
        recupererJoueursSelectionnes();

        adapter = new JoueurSelectionneAdapter(joueursSelectionnes);
        recyclerView.setAdapter(adapter);

        cibleView = new CibleView(this);
        //cibleView.setBackgroundColor(Color.WHITE);
        LinearLayout myLayout1 = (LinearLayout)findViewById(R.id.cibleView);
        myLayout1.addView(cibleView);

        volee = findViewById(R.id.volee);
        typePartie = findViewById(R.id.typePartie);
        typePartie.setText("JEU " + typesPartie[numeroTypePartie]);

        logoDarts = findViewById(R.id.logoDarts);
        connexionCible = findViewById(R.id.connexionCible);
        connexionEcran = findViewById(R.id.connexionEcran);
        boutonDemarrer = findViewById(R.id.boutonDemarrer);
        boutonArreter = findViewById(R.id.boutonArreter);
        boutonReset = findViewById(R.id.boutonReset);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        initialiserEvenements();
    }

    /**
     * @brief Applique les gestionnaires d'évènements
     * @return void
     */
    public void initialiserEvenements()
    {
        connexionCible.setOnClickListener((View.OnClickListener) this);
        connexionEcran.setOnClickListener((View.OnClickListener) this);
        boutonDemarrer.setOnClickListener((View.OnClickListener) this);
        boutonArreter.setOnClickListener((View.OnClickListener) this);
        boutonReset.setOnClickListener((View.OnClickListener) this);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.Jeu:
                        return true;
                    case R.id.Joueurs:
                        if(etatPartie == Partie.FINIE)
                        {
                            startActivityJoueurs = true;
                            Intent intentJoueurs = new Intent(MainActivity.this, JoueursActivity.class);
                            startActivity(intentJoueurs);
                        }
                        return false;
                    case R.id.Historique:
                        Intent intentHistorique = new Intent(MainActivity.this, HistoriqueActivity.class);
                        startActivity(intentHistorique);
                        return false;
                }
                return false;
            }
        });
    }

    private void testerEnvoiFlechette() {
        String trame = "$DART;GAME;0;0\r\n";
        envoyerTramePeripheriqueBluetoothEcran(trame);
    }

    private void selectionnerTypeJeu() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Choisir le type de jeu");
        final int[] checkedItem = {numeroTypePartie};
        alertDialog.setSingleChoiceItems(typesPartie, numeroTypePartie, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedItem[0] = which;
            }
        });
        alertDialog.setPositiveButton("Valider", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                numeroTypePartie = checkedItem[0];
                typePartie.setText("JEU " + typesPartie[numeroTypePartie]);
            }
        });
        alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void selectionnerTypePartie() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Choisir le type de partie");
        LayoutInflater factory = LayoutInflater.from(this);
        final View detailsPartieView = factory.inflate(R.layout.choix_type_partie, null);
        alertDialog.setView(detailsPartieView);
        final NumberPicker npNnbSets = (NumberPicker) detailsPartieView.findViewById(R.id.nbSets);
        npNnbSets.setMaxValue(9);
        npNnbSets.setMinValue(1);
        npNnbSets.setValue(nbSets);
        npNnbSets.setWrapSelectorWheel(false);
        final NumberPicker npNbManches = (NumberPicker) detailsPartieView.findViewById(R.id.nbManches);
        npNbManches.setMaxValue(9);
        npNbManches.setMinValue(1);
        npNbManches.setValue(nbManches);
        npNbManches.setWrapSelectorWheel(false);
        alertDialog.setPositiveButton("Valider", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                nbSets = npNnbSets.getValue();
                nbManches = npNbManches.getValue();
            }
        });
        alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void selectionnerAffichageRegles() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Voulez-vous afficher les règles sur l'écran ?");
        final int[] checkedItem = {affichageRegle};
        alertDialog.setSingleChoiceItems(choix, affichageRegle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedItem[0] = which;
            }
        });
        alertDialog.setPositiveButton("Valider", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                affichageRegle = checkedItem[0];
            }
        });
        alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void selectionnerActivationSon() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Voulez-vous activer les sons sur le mobile ?");
        final int[] checkedItem = {activationSon};
        alertDialog.setSingleChoiceItems(choix, activationSon, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedItem[0] = which;
            }
        });
        alertDialog.setPositiveButton("Valider", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                activationSon = checkedItem[0];
                if(activationSon == 0)
                    desactiverSon();
            }
        });
        alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void afficherAPropos() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Darts 2020");
        alertDialog.setMessage("BTS SN LaSalle Avignon");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    private void afficherInterdit() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Darts 2020");
        alertDialog.setMessage("Une partie est déjà en cours !");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    /**
     * @brief Méthode appelée au démarrage de l'activité MainActivity
     * @return void
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * @brief Méthode appelée quand on appuye sur boutons du menu
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.configurerJeu:
                if(etatPartie == Partie.FINIE)
                    selectionnerTypeJeu();
                else
                    afficherInterdit();
                return true;
            case R.id.configurerPartie:
                if(etatPartie == Partie.FINIE)
                    selectionnerTypePartie();
                else
                    afficherInterdit();
                return true;
            case R.id.affichageRegle:
                if(etatPartie == Partie.FINIE)
                    selectionnerAffichageRegles();
                else
                    afficherInterdit();
                return true;
            case R.id.activationSon:
                selectionnerActivationSon();
                return true;
            case R.id.testerEnvoiFlechette:
                if(etatPartie != Partie.FINIE)
                    testerEnvoiFlechette();
                return true;
            case R.id.aPropos:
                afficherAPropos();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void demarrerPartie() {
        if(etatPartie == Partie.FINIE)
        {
            if(cibleEstConnecte)
            {
                if(joueursSelectionnes.size() > 0)
                {
                    initialiserPartie();
                    //Format : $DART;START;TYPE_PARTIE;AFFICHER_REGLE;NB_JOUEURS[;NOMS]\r\n
                    //$DART;START;501;1;2;fabien;erwan
                    String choixTypePartie = typesPartie[numeroTypePartie].replace(" ","_");
                    String trame = Protocole.EN_TETE + Protocole.DELIMITEUR_CHAMP + "START" + Protocole.DELIMITEUR_CHAMP + choixTypePartie + Protocole.DELIMITEUR_CHAMP;
                    trame += Integer.toString(affichageRegle);
                    trame += Protocole.DELIMITEUR_CHAMP;
                    trame += Integer.toString(joueursSelectionnes.size());
                    for(int i = 0; i < joueursSelectionnes.size(); ++i)
                    {
                        trame += Protocole.DELIMITEUR_CHAMP;
                        trame += joueursSelectionnes.get(i).getNom();
                    }
                    trame += Protocole.DELIMITEUR_FIN;
                    envoyerTramePeripheriqueBluetoothCible(trame);
                    envoyerTramePeripheriqueBluetoothEcran(trame);
                    etatPartie = Partie.EN_COURS;
                    initialiserBoutonsPartie();
                    volee.setBackgroundResource(R.drawable.volee);
                    jouerSon(sound_play, -1);
                }
            }
        }
    }

    private void mettreEnPausePartie() {
        if(etatPartie == Partie.EN_COURS)
        {
            if(cibleEstConnecte)
            {
                String trame = Protocole.EN_TETE + Protocole.DELIMITEUR_CHAMP + "PAUSE" + Protocole.DELIMITEUR_FIN;
                envoyerTramePeripheriqueBluetoothCible(trame);
                envoyerTramePeripheriqueBluetoothEcran(trame);
                etatPartie = Partie.EN_PAUSE;
                initialiserBoutonsPartie();
                jouerSon(sound_pause, -1);
            }
        }
    }

    private void reprendrePartie() {
        if(etatPartie == Partie.EN_PAUSE)
        {
            if(cibleEstConnecte)
            {
                String trame = Protocole.EN_TETE + Protocole.DELIMITEUR_CHAMP + "PLAY" + Protocole.DELIMITEUR_FIN;
                envoyerTramePeripheriqueBluetoothCible(trame);
                envoyerTramePeripheriqueBluetoothEcran(trame);
                etatPartie = Partie.EN_COURS;
                initialiserBoutonsPartie();
            }
        }
    }

    private void arreterPartie() {
        if(etatPartie > Partie.FINIE)
        {
            if(cibleEstConnecte)
            {
                String trame = Protocole.EN_TETE + Protocole.DELIMITEUR_CHAMP + "STOP" + Protocole.DELIMITEUR_FIN;
                envoyerTramePeripheriqueBluetoothCible(trame);
                envoyerTramePeripheriqueBluetoothEcran(trame);
                etatPartie = Partie.TERMINEE;
                enregistrerPartie();
                initialiserBoutonsPartie();
                arreterSon();
            }
        }
    }

    private void reinitialiserPartie() {
        if(etatPartie > Partie.FINIE)
        {
            if(cibleEstConnecte)
            {
                String trame = Protocole.EN_TETE + Protocole.DELIMITEUR_CHAMP + "RESET" + Protocole.DELIMITEUR_FIN;
                envoyerTramePeripheriqueBluetoothCible(trame);
                envoyerTramePeripheriqueBluetoothEcran(trame);
                etatPartie = Partie.FINIE;
                initialiserBoutonsPartie();
                arreterSon();
            }
        }
    }

    private void afficherReglePartie() {
        if(etatPartie == Partie.FINIE)
        {
            if(ecranEstConnecte && affichageRegle == 1)
            {
                String choixTypePartie = typesPartie[numeroTypePartie].replace(" ","_");
                String trame = Protocole.EN_TETE + Protocole.DELIMITEUR_CHAMP + "REGLE" + Protocole.DELIMITEUR_CHAMP + choixTypePartie + Protocole.DELIMITEUR_FIN;
                envoyerTramePeripheriqueBluetoothEcran(trame);
            }
        }
    }

    private void initialiserPartie() {
        final int random = new Random().nextInt(joueursSelectionnes.size()) - 1;
        numeroJoueur = random;
        voleeEncours.initialiser();
        cibleView.setVolee(voleeEncours);
        for(int i = 0; i < joueursSelectionnes.size(); ++i)
        {
            joueursSelectionnes.get(i).initialiserPartie(typesPartie[numeroTypePartie]);
            joueursSelectionnes.get(i).setPoints(pointsTypesPartie[numeroTypePartie]);
        }
        selectionnerJoueur();
        adapter.notifyDataSetChanged();
    }

    public void jouerSon(int id, int loop)  {
        if(soundPoolLoaded && activationSon == 1) {
            arreterSon();
            streamId = this.soundPool.play(id, 1f, 1f, 1, loop, 1f);
        }
    }

    public void arreterSon()  {
        if(soundPoolLoaded && activationSon == 1) {
            if(streamId != -1)
            {
                this.soundPool.stop(streamId);
            }
        }
    }

    private void desactiverSon()
    {
        if(soundPoolLoaded) {
            if(streamId != -1)
            {
                this.soundPool.stop(streamId);
            }
        }
    }

    private List<Joueur> recupererJoueurs()
    {
        List<Joueur> listeJoueurs = null;
        if(startActivityJoueurs)
        {
            listeJoueurs = new ArrayList<Joueur>();
            List<Joueur> listeJoueursSelectionnes = JoueursActivity.getJoueurs();
            List<Joueur> listeJoueursBD = serveurBDD.getJoueurs();
            for(int i = 0; i < listeJoueursBD.size(); ++i)
            {
                if(i < listeJoueursSelectionnes.size() && listeJoueursSelectionnes.get(i).getSelectionne())
                {
                    listeJoueursBD.get(i).setSelectionne(true);
                    listeJoueurs.add(listeJoueursBD.get(i));
                }
                else
                    listeJoueurs.add(listeJoueursBD.get(i));
            }
        }
        else
        {
            listeJoueurs = serveurBDD.getJoueurs();
        }

        return listeJoueurs;
    }

    private void recupererJoueursSelectionnes()
    {
        if(startActivityJoueurs)
        {
            List<Joueur> listeJoueurs = JoueursActivity.getJoueurs();
            joueursSelectionnes.clear();
            for(int i = 0; i < listeJoueurs.size(); ++i)
            {
                if(listeJoueurs.get(i).getSelectionne())
                {
                    joueursSelectionnes.add(listeJoueurs.get(i));
                }
            }
        }
        else
        {

        }
    }

    private void selectionnerJoueur()
    {
        numeroJoueur++;
        if(numeroJoueur == joueursSelectionnes.size())
            numeroJoueur = 0;
        for(int i = 0; i < joueursSelectionnes.size(); ++i)
        {
            if(i == numeroJoueur)
            {
                joueursSelectionnes.get(i).setActive(true);
                SpannableString st = new SpannableString(joueursSelectionnes.get(i).getNom());
                //st.setSpan(new RelativeSizeSpan(2f), 1, 2, 0);
                volee.setText(st);
                layoutManager.scrollToPosition(numeroJoueur);
            }
            else
                joueursSelectionnes.get(i).setActive(false);
        }
    }

    private void initialiserBoutonsPartie()
    {
        if(joueursSelectionnes.size() > 0)
        {
            boutonDemarrer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            logoDarts.setVisibility(View.INVISIBLE);
        }
        else
        {
            logoDarts.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            volee.setVisibility(View.INVISIBLE);
            boutonDemarrer.setVisibility(View.INVISIBLE);
            boutonArreter.setVisibility(View.INVISIBLE);
        }

        if(cibleEstConnecte)
        {
            connexionCible.setImageResource(R.drawable.cible_connecte);
            boutonDemarrer.setImageResource(R.drawable.bouton_demarrer);
            boutonDemarrer.setEnabled(true);
        }
        else
        {
            connexionCible.setImageResource(R.drawable.cible_deconnecte);
            boutonDemarrer.setImageResource(R.drawable.bouton_demarrer_desactive);
            boutonDemarrer.setEnabled(false);
        }

        if(ecranEstConnecte)
        {
            connexionEcran.setImageResource(R.drawable.ecran_connecte);
        }
        else
        {
            connexionEcran.setImageResource(R.drawable.ecran_deconnecte);
        }

        if(etatPartie == Partie.EN_COURS)
        {
            boutonDemarrer.setImageResource(R.drawable.bouton_pause);
            boutonArreter.setVisibility(View.VISIBLE);
            volee.setVisibility(View.VISIBLE);
        }
        else if(etatPartie == Partie.EN_PAUSE)
        {
            boutonDemarrer.setImageResource(R.drawable.bouton_reprendre);
            boutonArreter.setVisibility(View.VISIBLE);
            volee.setVisibility(View.VISIBLE);
        }
        else if(etatPartie == Partie.TERMINEE)
        {
            boutonDemarrer.setImageResource(R.drawable.bouton_demarrer);
            boutonArreter.setVisibility(View.INVISIBLE);
        }
        else if(etatPartie == Partie.FINIE)
        {
            boutonDemarrer.setImageResource(R.drawable.bouton_demarrer);
            boutonArreter.setVisibility(View.INVISIBLE);
        }
    }

    private void demarrerBluetooth()
    {
        adaptateurBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (adaptateurBluetooth == null)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth non activé !", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (!adaptateurBluetooth.isEnabled())
            {
                Toast.makeText(getApplicationContext(), "Bluetooth non activé !", Toast.LENGTH_SHORT).show();
                Intent activeBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(activeBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
                //bluetoothAdapter.enable();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Recherche des appareils Bluetooth en cours ...", Toast.LENGTH_LONG).show();
                // Recherche des périphériques connus
                devices = adaptateurBluetooth.getBondedDevices();
                // Connexion aux appareils Bluetooth
                connecterPeripheriquesBluetooth();
            }
        }
    }

    /**
     * Méthode connecterPeripheriquesBluetooth() permetant la connexion aux appareils Bluetooth
     */
    private void connecterPeripheriquesBluetooth()
    {
        // Déconnexion de tous les appareils avant une possible connexion
        deconnecterPeripheriquesBluetooth();

        for (BluetoothDevice appareilBluetooth : devices)
        {
            if (appareilBluetooth.getName().equals(NOM_PERIPHERIQUE_BLUETOOTH_ECRAN))
            {
                connecterPeripheriqueBluetoothEcran(appareilBluetooth);
            }
            if (appareilBluetooth.getName().equals(NOM_PERIPHERIQUE_BLUETOOTH_CIBLE))
            {
                connecterPeripheriqueBluetoothCible(appareilBluetooth);
            }
        }

        verifierConnexionAppareilsBluetooth();
    }

    /**
     * Méthode deconnecterPeripheriquesBluetooth() permetant la déconnexion des appareils Bluetooth du projet connectés.
     */
    private void deconnecterPeripheriquesBluetooth()
    {
        if(peripheriqueBluetoothEcran != null)
        {
            peripheriqueBluetoothEcran.deconnecter(true);
        }

        if(peripheriqueBluetoothCible != null)
        {
            peripheriqueBluetoothCible.deconnecter(true);
        }
    }

    /**
     * Méthode connecterPeripheriqueBluetoothEcran() permettant la connexion Bluetooth à l'écran.
     *
     * @param appareilBluetooth étant le Bluetooth de l'écran
     */
    private void connecterPeripheriqueBluetoothEcran(BluetoothDevice appareilBluetooth)
    {
        peripheriqueBluetoothEcran = new PeripheriqueBluetooth(appareilBluetooth, handler);
        peripheriqueBluetoothEcran.connecter();
    }

    private void connecterPeripheriqueBluetoothEcran()
    {
        if(peripheriqueBluetoothEcran != null)
        {
            reinitialiserPartie();
            peripheriqueBluetoothEcran.deconnecter(true);
        }
        for (BluetoothDevice appareilBluetooth : devices)
        {
            if (appareilBluetooth.getName().equals(NOM_PERIPHERIQUE_BLUETOOTH_ECRAN))
            {
                connecterPeripheriqueBluetoothEcran(appareilBluetooth);
                break;
            }
        }
    }

    /**
     * Méthode connecterPeripheriqueBluetoothCible() permettant la connexion Bluetooth à la cible.
     *
     * @param appareilBluetooth étant le Bluetooth de la cible
     */
    private void connecterPeripheriqueBluetoothCible(BluetoothDevice appareilBluetooth)
    {
        peripheriqueBluetoothCible = new PeripheriqueBluetooth(appareilBluetooth, handler);
        peripheriqueBluetoothCible.connecter();
    }

    private void connecterPeripheriqueBluetoothCible()
    {
        if(peripheriqueBluetoothCible != null)
        {
            reinitialiserPartie();
            peripheriqueBluetoothCible.deconnecter(true);
        }
        for (BluetoothDevice appareilBluetooth : devices)
        {
            if (appareilBluetooth.getName().equals(NOM_PERIPHERIQUE_BLUETOOTH_CIBLE))
            {
                connecterPeripheriqueBluetoothCible(appareilBluetooth);
                break;
            }
        }
    }

    private void envoyerTramePeripheriqueBluetoothCible(String trame)
    {
        if(peripheriqueBluetoothCible != null)
        {
            if (peripheriqueBluetoothCible.estConnecte())
                peripheriqueBluetoothCible.envoyer(trame);
        }
    }

    private void envoyerTramePeripheriqueBluetoothEcran(String trame)
    {
        if(peripheriqueBluetoothEcran != null)
        {
            if (peripheriqueBluetoothEcran.estConnecte())
                peripheriqueBluetoothEcran.envoyer(trame);
        }
    }

    private void decoderTramePeripheriqueBluetoothCible(String trame)
    {
        Log.d(TAG, "decoderTramePeripheriqueBluetoothCible() trame : " + trame);
        int typePoint = -1;
        int numeroCible = -1;
        int numeroFlechette = -1;
        String donnees = trame.replace("\r\n", "");
        String champs[] = donnees.split(";");
        //Format : $DART;GAME;TYPE_POINT;NUMERO_CIBLE\r\n
        //         0     1    2          3
        /*if (champs[0].equals(Protocole.EN_TETE) && champs.length == Protocole.TRAME_GAME_NB_CHAMPS)
        {
            if (champs[1].equals(Protocole.TRAME_GAME))
            {
                typePoint = Integer.parseInt(champs[2]);
                numeroCible = Integer.parseInt(champs[3]);
                receptionnerImpact(typePoint, numeroCible);
            }
        }*/
        //Format : $DART;GAME;TYPE_POINT;NUMERO_CIBLE;NUMERO FLECHETTE\r\n
        //         0     1    2          3            4
        if (champs[0].equals(Protocole.EN_TETE) && champs.length == Protocole.TRAME_GAME_NB_CHAMPS)
        {
            if (champs[1].equals(Protocole.TRAME_GAME))
            {
                typePoint = Integer.parseInt(champs[2]);
                numeroCible = Integer.parseInt(champs[3]);
                numeroFlechette = Integer.parseInt(champs[3]);
                receptionnerImpact(typePoint, numeroCible, numeroFlechette);
            }
        }
        else if (champs[0].equals(Protocole.EN_TETE) && champs[1].equals(Protocole.TRAME_NEXT))
        {
            //receptionnerImpact(typePoint, numeroCible, numeroFlechette);
            changerJoueur();
        }
    }

    /*private void receptionnerImpact(int typePoint, int numeroCible)
    {
        volee.setBackgroundResource(R.drawable.volee);
        jouerSon(sound_play, -1);

        if(voleeEncours.getTerminee())
            changerJoueur();

        int points = typePoint * numeroCible;
        int score = joueursSelectionnes.get(numeroJoueur).getPoints();

        voleeEncours.inserer(typePoint, numeroCible);
        cibleView.setVolee(voleeEncours);

        if(typePoint == 2 && numeroCible == 25)
        {
            jouerSon(sound_bull, 0);
        }
        else if(typePoint == 0)
        {
            jouerSon(sound_out, 0);
        }

        String affichageVolee = joueursSelectionnes.get(numeroJoueur).getPartie().getVoleeJouee();
        if(affichageVolee.length() > 0)
            affichageVolee += " ";
        switch (typePoint)
        {
            case 3:
                affichageVolee += ("T" + Integer.toString(numeroCible));
                break;
            case 2:
                affichageVolee += ("D" + Integer.toString(numeroCible));
                break;
            case 1:
                affichageVolee += Integer.toString(numeroCible);
                break;
            case 0:
                affichageVolee += "X";
                break;
            default:
                break;
        }
        joueursSelectionnes.get(numeroJoueur).getPartie().setVoleeJouee(affichageVolee);
        SpannableString stVolee = new SpannableString(joueursSelectionnes.get(numeroJoueur).getNom() + " \u2192 " + affichageVolee + " \u2192 " + Integer.toString(voleeEncours.getPoints()) + " points");
        //stVolee.setSpan(new RelativeSizeSpan(2f), joueursSelectionnes.get(numeroJoueur).getNom(), joueursSelectionnes.get(numeroJoueur).getNom()+1, 0);
        volee.setText(stVolee);

        // Partie terminée ?
        if((score - voleeEncours.getPoints()) == 0)
        {
            if(typesPartie[numeroTypePartie].contains("DOUBLE OUT"))
            {
                // un double ?
                if(typePoint == 2)
                {
                    joueursSelectionnes.get(numeroJoueur).setPoints(score - voleeEncours.getPoints());
                    joueursSelectionnes.get(numeroJoueur).getPartie().setVolee(voleeEncours.getPoints());
                    // Gagné
                    joueursSelectionnes.get(numeroJoueur).getPartie().setResultat(true);
                }
            }
            else
            {
                joueursSelectionnes.get(numeroJoueur).setPoints(score - voleeEncours.getPoints());
                joueursSelectionnes.get(numeroJoueur).getPartie().setVolee(voleeEncours.getPoints());
                // Gagné
                joueursSelectionnes.get(numeroJoueur).getPartie().setResultat(true);
            }
        }
        else if((score - voleeEncours.getPoints()) > 0)
        {
            if(voleeEncours.getTerminee())
            {
                joueursSelectionnes.get(numeroJoueur).setPoints(score - voleeEncours.getPoints());
                joueursSelectionnes.get(numeroJoueur).getPartie().setVolee(voleeEncours.getPoints());
            }
        }
        else
        {
            //Log.d(TAG, "receptionnerImpact : joueur = " + joueursSelectionnes.get(numeroJoueur).getNom() + " points = " + points + " score = " + score + " nbFlechettes = " + voleeEncours.getNbFlechettes() + " volee points = " + voleeEncours.getPoints());
            volee.setBackgroundResource(R.drawable.volee_overshoot);
        }

        adapter.notifyDataSetChanged();

        // Gagné ?
        if(joueursSelectionnes.get(numeroJoueur).getPartie().getResultat())
        {
            SpannableString stResultat = new SpannableString(joueursSelectionnes.get(numeroJoueur).getNom() + " \u2192 " + "Vainqueur");
            //stResultat.setSpan(new RelativeSizeSpan(2f), joueursSelectionnes.get(numeroJoueur).getNom(), joueursSelectionnes.get(numeroJoueur).getNom()+1, 0);
            volee.setText(stResultat);
            volee.setBackgroundResource(R.drawable.volee_win);
            arreterPartie();
            jouerSon(sound_gagne, 0);
        }
    }*/

    private void receptionnerImpact(int typePoint, int numeroCible, int numeroFlechette)
    {
        volee.setBackgroundResource(R.drawable.volee);
        jouerSon(sound_play, -1);

        Log.d(TAG, "receptionnerImpact : joueur = " + joueursSelectionnes.get(numeroJoueur).getNom() + " numeroFlechette = " + numeroFlechette + " typePoint = " + typePoint + " numeroCible = " + numeroCible);

        if(voleeEncours.getTerminee() || numeroFlechette == -1)
            changerJoueur();

        int points = typePoint * numeroCible;
        int score = joueursSelectionnes.get(numeroJoueur).getPoints();

        voleeEncours.inserer(typePoint, numeroCible);
        cibleView.setVolee(voleeEncours);

        if(typePoint == 2 && numeroCible == 25)
        {
            jouerSon(sound_bull, 0);
        }
        else if(typePoint == 0)
        {
            jouerSon(sound_out, 0);
        }

        String affichageVolee = joueursSelectionnes.get(numeroJoueur).getPartie().getVoleeJouee();
        if(affichageVolee.length() > 0)
            affichageVolee += " ";
        switch (typePoint)
        {
            case 3:
                affichageVolee += ("T" + Integer.toString(numeroCible));
                break;
            case 2:
                affichageVolee += ("D" + Integer.toString(numeroCible));
                break;
            case 1:
                affichageVolee += Integer.toString(numeroCible);
                break;
            case 0:
                affichageVolee += "X";
                break;
            default:
                break;
        }
        joueursSelectionnes.get(numeroJoueur).getPartie().setVoleeJouee(affichageVolee);
        SpannableString stVolee = new SpannableString(joueursSelectionnes.get(numeroJoueur).getNom() + " \u2192 " + affichageVolee + " \u2192 " + Integer.toString(voleeEncours.getPoints()) + " points");
        //stVolee.setSpan(new RelativeSizeSpan(2f), joueursSelectionnes.get(numeroJoueur).getNom().length(), joueursSelectionnes.get(numeroJoueur).getNom().length()+1, 0);
        volee.setText(stVolee);

        // Partie terminée ?
        if((score - voleeEncours.getPoints()) == 0)
        {
            if(typesPartie[numeroTypePartie].contains("DOUBLE OUT"))
            {
                // un double ?
                if(typePoint == 2)
                {
                    joueursSelectionnes.get(numeroJoueur).setPoints(score - voleeEncours.getPoints());
                    joueursSelectionnes.get(numeroJoueur).getPartie().setVolee(voleeEncours.getPoints());
                    // Gagné
                    joueursSelectionnes.get(numeroJoueur).getPartie().setResultat(true);
                }
            }
            else
            {
                joueursSelectionnes.get(numeroJoueur).setPoints(score - voleeEncours.getPoints());
                joueursSelectionnes.get(numeroJoueur).getPartie().setVolee(voleeEncours.getPoints());
                // Gagné
                joueursSelectionnes.get(numeroJoueur).getPartie().setResultat(true);
            }
        }
        else if((score - voleeEncours.getPoints()) > 0)
        {
            if(voleeEncours.getTerminee())
            {
                joueursSelectionnes.get(numeroJoueur).setPoints(score - voleeEncours.getPoints());
                joueursSelectionnes.get(numeroJoueur).getPartie().setVolee(voleeEncours.getPoints());
            }
        }
        else
        {
            //Log.d(TAG, "receptionnerImpact : joueur = " + joueursSelectionnes.get(numeroJoueur).getNom() + " points = " + points + " score = " + score + " nbFlechettes = " + voleeEncours.getNbFlechettes() + " volee points = " + voleeEncours.getPoints());
            volee.setBackgroundResource(R.drawable.volee_overshoot);
        }

        adapter.notifyDataSetChanged();

        // Gagné ?
        if(joueursSelectionnes.get(numeroJoueur).getPartie().getResultat())
        {
            SpannableString stResultat = new SpannableString(joueursSelectionnes.get(numeroJoueur).getNom() + " \u2192 " + "Vainqueur");
            //stResultat.setSpan(new RelativeSizeSpan(2f), joueursSelectionnes.get(numeroJoueur).getNom().length(), joueursSelectionnes.get(numeroJoueur).getNom().length()+1, 0);
            volee.setText(stResultat);
            volee.setBackgroundResource(R.drawable.volee_win);
            arreterPartie();
            jouerSon(sound_gagne, 0);
        }
    }

    private void changerJoueur()
    {
        voleeEncours.initialiser();
        joueursSelectionnes.get(numeroJoueur).getPartie().sauvegarderVoleeJouee();
        Log.d(TAG, "changerJoueur : joueur courant = " + joueursSelectionnes.get(numeroJoueur).getNom());
        selectionnerJoueur();
        Log.d(TAG, "changerJoueur : joueur suivant = " + joueursSelectionnes.get(numeroJoueur).getNom());
        joueursSelectionnes.get(numeroJoueur).getPartie().resetVoleeJouee();
        adapter.notifyDataSetChanged();
    }

    public static List<Joueur> getJoueurs()
    {
        return joueurs;
    }

    @Override
    public void onClick(View element)
    {
        if(element == boutonDemarrer)
        {
            Log.d(TAG,"onClick() -> boutonDemarrer");
            switch (etatPartie)
            {
                case Partie.FINIE:
                    demarrerPartie();
                    break;
                case Partie.EN_COURS:
                    mettreEnPausePartie();
                    break;
                case Partie.EN_PAUSE:
                    reprendrePartie();
                    break;
            }
        }
        else if(element == boutonArreter)
        {
            Log.d(TAG,"onClick() -> boutonArreter");
            arreterPartie();
        }
        else if(element == boutonReset)
        {
            Log.d(TAG,"onClick() -> boutonReset");
            reinitialiserPartie();
            demarrerBluetooth();
        }
        else if(element == connexionCible)
        {
            Log.d(TAG,"onClick() -> connexionCible");
            if(peripheriqueBluetoothCible != null)
            {
                if(cibleEstConnecte)
                {
                    reinitialiserPartie();
                    peripheriqueBluetoothCible.deconnecter(true);
                }
                else
                    connecterPeripheriqueBluetoothCible();
            }
        }
        else if(element == connexionEcran)
        {
            Log.d(TAG,"onClick() -> connexionEcran");
            if(peripheriqueBluetoothEcran != null)
            {
                if(ecranEstConnecte)
                {
                    reinitialiserPartie();
                    peripheriqueBluetoothEcran.deconnecter(true);
                }
                else
                    connecterPeripheriqueBluetoothEcran();
            }
        }
    }

    private void verifierConnexionAppareilsBluetooth()
    {
        ecranEstDetecte = (peripheriqueBluetoothEcran != null);
        cibleEstDetecte = (peripheriqueBluetoothCible != null);

        if(!cibleEstDetecte && !ecranEstDetecte)
        {
            Toast.makeText(getApplicationContext(), "Aucun appareil Bluetooth détecté ! ", Toast.LENGTH_SHORT).show();
            return;
        }

        if(ecranEstDetecte)
            ecranEstConnecte = peripheriqueBluetoothEcran.estConnecte();
        else
            ecranEstConnecte = false;

        if(cibleEstDetecte)
            cibleEstConnecte = peripheriqueBluetoothCible.estConnecte();
        else
            cibleEstConnecte = false;

        Log.d(TAG, "verifierConnexionAppareilsBluetooth() cibleEstConnecte = " + cibleEstConnecte + " ecranEstConnecte = " + ecranEstConnecte);

        initialiserBoutonsPartie();
    }

    private void enregistrerPartie()
    {
        Log.d(TAG, "enregistrerPartie()");

        boolean gagnant = false;
        for(int i = 0; i < joueursSelectionnes.size(); ++i)
        {
            if(joueursSelectionnes.get(i).getPartie().getResultat())
            {
                gagnant = true;
                break;
            }
        }

        if(!gagnant)
        {
            etatPartie = Partie.FINIE;
            return;
        }

        final Timer timerAsync = new Timer();
        final TimerTask timerTaskAsync = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        for(int i = 0; i < joueursSelectionnes.size(); ++i)
                        {
                            joueursSelectionnes.get(i).getPartie().setDuree();
                            serveurBDD.insererPartie(joueursSelectionnes.get(i).getPartie());
                        }
                        etatPartie = Partie.FINIE;
                        Toast.makeText(getApplicationContext(), "Partie enregistrée", Toast.LENGTH_SHORT).show();
                        timerAsync.cancel();
                    }
                });
            }
        };
        timerAsync.schedule(timerTaskAsync, 1000, 2000);
    }

    // Gère les communications avec le thread Bluetooth
    final private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            Bundle b = msg.getData();

            switch(b.getInt("etat"))
            {
                case PeripheriqueBluetooth.CODE_ERREUR_CONNECTER:
                    Log.d(TAG,"<Bluetooth> Erreur connexion : " + b.getString("nom") + " [" + b.getString("adresse") + "]");
                    break;
                case PeripheriqueBluetooth.CODE_ERREUR_RECEVOIR:
                    Log.d(TAG,"<Bluetooth> Erreur réception : " + b.getString("nom") + " [" + b.getString("adresse") + "]");
                    verifierConnexionAppareilsBluetooth();
                    //reconnecter ?
                    break;
                case PeripheriqueBluetooth.CODE_ERREUR_ENVOYER:
                    Log.d(TAG,"<Bluetooth> Erreur émission : " + b.getString("nom") + " [" + b.getString("adresse") + "]");
                    verifierConnexionAppareilsBluetooth();
                    //reconnecter ?
                    break;
                case PeripheriqueBluetooth.CODE_CONNEXION:
                    Log.d(TAG,"<Bluetooth> Connexion ok : " + b.getString("nom") + " [" + b.getString("adresse") + "]");
                    verifierConnexionAppareilsBluetooth();
                    break;
                case PeripheriqueBluetooth.CODE_RECEPTION:
                    String donnees = b.getString("donnees");
                    if(donnees.contains("\r\n"))
                    {
                        Log.d(TAG,"<Bluetooth> Données reçues " + b.getString("nom") + " [" + b.getString("adresse") + "] : " + donnees.replace("\r\n", ""));
                        if(donnees.endsWith("\r\n"))
                            Log.d(TAG,"<Bluetooth> délimiteur ok !");
                    }
                    else
                    {
                        Log.d(TAG,"<Bluetooth> Données reçues " + b.getString("nom") + " [" + b.getString("adresse") + "] : " + donnees);
                        Log.d(TAG,"<Bluetooth> Erreur délimiteur !");
                        donnees += "\r\n";
                    }
                    envoyerTramePeripheriqueBluetoothEcran(donnees);
                    decoderTramePeripheriqueBluetoothCible(donnees);
                    break;
                case PeripheriqueBluetooth.CODE_DECONNEXION:
                    Log.d(TAG,"<Bluetooth> Déconnexion ok : " + b.getString("nom") + " [" + b.getString("adresse") + "]");
                    verifierConnexionAppareilsBluetooth();
                    break;
                default:
                    Log.d(TAG,"<Bluetooth> code état inconnu ! ");
            }
        }
    };
}
