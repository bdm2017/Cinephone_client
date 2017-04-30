package lapupe.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by vagabond on 23/04/17.
 */

public class ListSeance extends Activity {

    TextView tvNamefilm;
    TextView tvDS;
    TextView tvDescription;
    TextView tvRealisateur;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_seance);

        Intent intent = getIntent();
        String namefilm = intent.getStringExtra("data1");
        tvNamefilm = (TextView) findViewById(R.id.nameFilm);
        tvNamefilm.setText(namefilm);

        String sDS = intent.getStringExtra("data2");
        tvDS = (TextView) findViewById(R.id.dateDS2);
        tvDS.setText(sDS);

        String sDescription = intent.getStringExtra("data3");
        tvDescription = (TextView) findViewById(R.id.descrition2);
        tvDescription.setText(sDescription);

        String sRealisateur = intent.getStringExtra("data4");
        tvRealisateur = (TextView) findViewById(R.id.realisateur2);
        tvRealisateur.setText(sRealisateur);
    }


}
