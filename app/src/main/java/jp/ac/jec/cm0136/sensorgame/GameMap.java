package jp.ac.jec.cm0136.sensorgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class GameMap {
    public final static int PATH_TILE = 0; // 通路
    public final static int WALL_TILE = 1; // 壁
    public final static int EXIT_TILE = 2; // 出口
    public final static int VOID_TILE = 3; // 穴
    public final static int MAP_ROWS = 32;
    public final static int MAP_COLS = 20;

    private  int[][] mData;

    private int mTileWidth;
    private int mTileHeight;

    private Paint mPathPaint = new Paint();
    private Paint mWallPaint = new Paint();
    private Paint mExitPaint = new Paint();
    private Paint mVoidPaint = new Paint();

    public GameMap() {
        mData = new int[MAP_ROWS][MAP_COLS];

        mPathPaint.setColor(Color.BLACK);
        mWallPaint.setColor(Color.WHITE);
        mExitPaint.setColor(Color.CYAN);
        mVoidPaint.setColor(Color.YELLOW);
    }


    public void setData(int[][] Data) {
        mData = Data;
    }

    public void setSize(int w, int h) {
        mTileWidth = w / MAP_COLS;
        mTileHeight = h / MAP_ROWS;
    }

    public void draw(Canvas canvas) {
        for (int i = 0; i < MAP_ROWS; i++) {
            for (int j = 0; j < MAP_COLS; j++) {
                int x = j * mTileWidth;
                int y = i * mTileHeight;
                int width = (j + 1) * mTileWidth;
                int height = (i + 1) * mTileHeight;

                switch (mData[i][j]) {
                    case PATH_TILE:
                        canvas.drawRect(x, y, width, height, mPathPaint);
                        break;
                    case WALL_TILE:
                        canvas.drawRect(x, y, width, height, mWallPaint);
                        break;
                    case EXIT_TILE:
                        canvas.drawRect(x, y, width, height, mExitPaint);
                        break;
                    case VOID_TILE:
                        canvas.drawRect(x, y, width, height, mVoidPaint);
                        break;
                }
            }
        }
    }

    public int getCellType(int x ,int y) {
        // swift -> guard let
        if (mTileWidth == 0 || mTileHeight == 0) {
            return PATH_TILE; // 早期return
        }
        int j = x / mTileWidth;
        int i = y / mTileHeight;
        if (i < MAP_ROWS && j < MAP_COLS) {
            return mData[i][j];
        }
        return PATH_TILE;
    }



}
