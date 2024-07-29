package jp.ac.jec.cm0136.sensorgame;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

public class LabyrinthActivity extends AppCompatActivity {
    static final int MAP_RANDOM = 11;
    static final int MAP_NORMAL = 12;
    private SensorManager manager;
    MapView mapView = null;
    RandomMapView randomMapView = null;
    int mode = MAP_NORMAL;
    String getColor, color;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mode = getIntent().getIntExtra("MODE", MAP_NORMAL);
        getColor = getIntent().getStringExtra("COLOR");
        switch (getColor) {
            case "Bright Orange":
                color = "#FFA500";
                break;
            case "Lime Green":
                this.color = "#00FF00";
                break;
            case "Hot Pink":
                this.color = "#FF69B4";
                break;
            case "Vivid Purple":
                this.color = "#FF00FF";
                break;
            default:
                color = "#FF0000";
        }

        mapView = new MapView(this, color);
        randomMapView = new RandomMapView(this, color);


        if (mode == MAP_RANDOM) {
            setContentView(randomMapView);
        } else {
            setContentView(mapView);
        }

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayer.setLooping(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        playBackgroundMusic();
        if (mode == MAP_RANDOM) {
            manager.registerListener(randomMapView, sensor, SensorManager.SENSOR_DELAY_GAME);
            randomMapView.startGame();
        } else {
            manager.registerListener(mapView, sensor, SensorManager.SENSOR_DELAY_GAME);
            mapView.startGame();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mode == MAP_RANDOM) {
            manager.unregisterListener(randomMapView);
        } else {
            manager.unregisterListener(mapView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mapView.getGameState() == MapView.GAME_OVER || mapView.getGameState() == MapView.GAME_CLEAR) {
                mapView.freeHandler();
                mapView.gameLevel = 1;
                stopBackgroundMusic();
                finish();
            } else if (mapView.getGameState() == MapView.GAME_WIN) {
                mapView.toNextStage();
            }

            Log.d("LabView", "onTouchEvent: " + mapView.gameLevel);

            if (randomMapView.getGameState() == RandomMapView.GAME_CLEAR) {
                randomMapView.freeHandler();
                stopBackgroundMusic();
                finish();
            } else if (randomMapView.getGameState() == RandomMapView.GAME_WIN) {
                randomMapView.toNextStage();
            }
        }

        return true;
    }

    private void playBackgroundMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopBackgroundMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}