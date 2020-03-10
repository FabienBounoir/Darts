package projet.lasalle84.darts;
/**
 * @file ActiviteCreerPartie.java
 * @brief Déclaration de la classe ActivitéCréerPartie
 * @author Menella Erwan
 */
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ActiviteCreerPartie extends AppCompatActivity {
    /**
     * @brief Méthode appelée à la création de l'activité
     *
     * @fn ActiviteCreerPartie::onCreate(Bundle savedInstanceState)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creer_partie);
    }
}
