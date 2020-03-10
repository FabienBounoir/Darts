package projet.lasalle84.darts;
/**
 * @file ActiviteAjouterJoueur.java
 * @brief Déclaration de la classe ActiviteAjouterJoueur
 * @author Menella Erwan
 */
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
/**
 * @class ActiviteAjouterJoueur
 * @brief Déclaration de la classe ActiviteAjouterJoueur
 */
public class ActiviteAjouterJoueur extends AppCompatActivity {
    /**
     * @brief Méthode appelée à la création de l'activité
     *
     * @fn ActiviteAjouterJoueur::onCreate(Bundle savedInstanceState)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_joueur);
    }
}
