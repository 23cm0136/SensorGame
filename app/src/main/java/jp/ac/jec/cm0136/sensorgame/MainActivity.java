package jp.ac.jec.cm0136.sensorgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    static final int MAP_RANDOM = 11;
    static final int MAP_NORMAL = 12;
    String color = "RED";
    public static final String PREF_NAME = "HighScorePref";
    static final String HIGH_SCORE_NORMAL = "normalScore";
    static final String HIGH_SCORE_RANDOM = "randomScore";
    private SharedPreferences sharedPreferences;
    TextView txtNormalScore;
    TextView txtRandomScore;


    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
            (sharedPreferences, key) -> {
                if (key.equals(HIGH_SCORE_NORMAL)) {
                    long normalScore = sharedPreferences.getLong(HIGH_SCORE_NORMAL, 0);
                    if (normalScore != 0) {
                        txtNormalScore.setVisibility(View.VISIBLE);
                        txtNormalScore.setText(formatTime(normalScore));
                    } else {
                        txtNormalScore.setVisibility(View.GONE);
                    }
                } else if (key.equals(HIGH_SCORE_RANDOM)) {
                    long randomScore = sharedPreferences.getLong(HIGH_SCORE_RANDOM, 0);
                    if (randomScore != 0) {
                        txtRandomScore.setVisibility(View.VISIBLE);
                        txtRandomScore.setText(formatTime(randomScore));
                    } else {
                        txtRandomScore.setVisibility(View.GONE);
                    }
                }
            };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//        long normalScore = sharedPreferences.getLong(HIGH_SCORE_NORMAL, 0);
//        long randomScore = sharedPreferences.getLong(HIGH_SCORE_RANDOM, 0);


        txtNormalScore = findViewById(R.id.txtNormalScore);
        txtRandomScore = findViewById(R.id.txtRandomScore);

//        if (normalScore != 0 ) {
//            txtNormalScore.setVisibility(View.VISIBLE);
//            txtNormalScore.setText(formatTime(normalScore));
//        }
//        if (randomScore != 0 ) {
//            txtRandomScore.setVisibility(View.VISIBLE);
//            txtRandomScore.setText(formatTime(randomScore));
//        }
        updateScoresFromPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);


        Spinner spinner = findViewById(R.id.sp);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                color = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.btnStart).setOnClickListener(v -> {
            launchLabyrinthActivity(MAP_NORMAL, color);
        });
        findViewById(R.id.btnRandom).setOnClickListener(v -> {
            launchLabyrinthActivity(MAP_RANDOM, color);
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    private void launchLabyrinthActivity(int value, String color) {
        Intent intent = new Intent(this, LabyrinthActivity.class);
        intent.putExtra("MODE", value);
        intent.putExtra("COLOR", color);
        startActivity(intent);
    }


    private String formatTime(long millis) {
        final long min = (millis / (1000 * 60));
        final long sec = (millis / 1000) % 60;
        final long milli = millis % 1000;
        return String.format(Locale.getDefault(), "　%02d:%02d.%03d", min , sec, milli);
    }


    private void updateScoresFromPreferences() {
        long normalScore = sharedPreferences.getLong(HIGH_SCORE_NORMAL, 0);
        if (normalScore != 0) {
            txtNormalScore.setVisibility(View.VISIBLE);
            txtNormalScore.setText(formatTime(normalScore));
        } else {
            txtNormalScore.setVisibility(View.GONE);
        }

        long randomScore = sharedPreferences.getLong(HIGH_SCORE_RANDOM, 0);
        if (randomScore != 0) {
            txtRandomScore.setVisibility(View.VISIBLE);
            txtRandomScore.setText(formatTime(randomScore));
        } else {
            txtRandomScore.setVisibility(View.GONE);
        }
    }

}