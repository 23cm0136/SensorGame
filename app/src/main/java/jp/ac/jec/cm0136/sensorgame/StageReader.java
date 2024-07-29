package jp.ac.jec.cm0136.sensorgame;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StageReader {
    private Context context;

    public StageReader(Context context) {
        this.context = context;
    }
    public int[][] readStageFile(int level) {
        int[][] data = new int[GameMap.MAP_ROWS][GameMap.MAP_COLS];
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        String fileName = "stage" + level + ".txt";
        try {
            inputStream = assetManager.open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < GameMap.MAP_ROWS) {
                  String[] columns = line.split(",");
                  for (int col = 0; col < GameMap.MAP_COLS; col++) {
                    data[row][col] = Integer.parseInt(columns[col]);
                  }
                  row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }
}
