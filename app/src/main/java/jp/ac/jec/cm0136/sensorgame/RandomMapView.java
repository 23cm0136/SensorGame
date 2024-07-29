package jp.ac.jec.cm0136.sensorgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.Locale;


public class RandomMapView extends View implements SensorEventListener, Runnable {
    private SharedPreferences sp;
    private Handler mHandler = null;
    private static final float FILTER_FACTOR = 0.2f;
    private float mAccelX = 0.0f;
    private float mAccelY = 0.0f;
    private float mVectorX = 0.0f;
    private float mVectorY = 0.0f;
//    private GameMap map = null;
    private GameMap2 map = null;
    private Ball ball = null;
    private int mWidth;
    private int mHeight;

    private static final float REBOUND = 0.5f;
    private static final int WALL = GameMap2.WALL_TILE;

    public static final int GAME_RUN = 1;
    public static final int GAME_WIN = 2; // ゴールした時
    public static final int GAME_CLEAR = 4; // 3段階全部完了時
    private int gameLevel = 1; // 段階数
    private int gameState = 0;
    Paint fullScr = new Paint();
    Paint message = new Paint();

    private long mStartTime = 0;
    private long mTotalTime = 0;
    int[][] data;


    public RandomMapView(Context context,String color) {
        super(context);
        init(context,color);
    }
    private void init(Context context, String color) {
        mHandler = new Handler();

        map = new GameMap2(context);
//        map = new GameMap();
        ball = new Ball();
        ball.setColor(color);
        data = makeRandomData();

        // TODO: Clear this!
//        data[2][1] = GameMap2.EXIT_TILE;

        map.setData(data);
        sp = context.getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE);
    }

    private int[][] makeRandomData() {

        data = new int[32][20];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (i == 0 || i == data.length - 2 || j == 0 || j == data[i].length - 2) {
                    data[i][j] = GameMap2.PATH_TILE;
                } else {
                    data[i][j] = GameMap2.WALL_TILE;
                }
            }
        }
        dig(1,1);

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (i == 0 || i == data.length - 2 || j == 0 || j == data[i].length - 2 || i == data.length - 1 || j == data[i].length - 1) {
                    data[i][j] = GameMap2.WALL_TILE;
                }
            }
        }
        data[29][17] = GameMap2.EXIT_TILE;

        return data;
    }


    private void dig(int x , int y) {
        int[] upx = {1,-1,0,0};
        int[] upy = {0,0,1,-1};
        int dir = (int) (Math.random() * 4);
        int count = 0;
        while (count < 4) {
            int x1 = x + upx[dir];
            int y1 = y + upy[dir];
            int x2 = x1 + upx[dir];
            int y2 = y1 + upy[dir];

            if (data[x1][y1]==GameMap2.WALL_TILE && data[x2][y2]== GameMap2.WALL_TILE) {
                data[x1][y1] = GameMap2.PATH_TILE;
                data[x2][y2] = GameMap2.PATH_TILE;
                dig(x2,y2);
            } else {
                dir = (dir + 1) % 4;
                count += 1;
            }
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        map.draw(canvas);
        ball.draw(canvas);
        Log.d("MapView", "onDraw-gameState: " + gameState);
        if (gameState != GAME_RUN) {
            if (gameState == GAME_CLEAR) {
                drawGameMessage(canvas, "やった！"+" Finished in " + formatTime(mTotalTime), Color.GREEN);
            } else if (gameState == GAME_WIN) {
                drawGameMessage(canvas, "Level " + (gameLevel-1) + " Cleared!", Color.BLUE);
            }
        }
    }
    private String formatTime(long millis) {
        final long min = (millis / (1000 * 60));
        final long sec = (millis / 1000) % 60;
        final long milli = millis % 1000;
        return String.format(Locale.getDefault(), "　%02d:%02d.%03d", min , sec, milli);
    }

    private void drawGameMessage(Canvas canvas, String text, int color) {
        fullScr.setColor(0xDD000000);
        canvas.drawRect(0f, 0f, mWidth, mHeight, fullScr);

        message.setColor(color);
        message.setAntiAlias(true);
        message.setTextSize(40);
        message.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text, mWidth / 2, mHeight / 2, message);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        map.setSize(w, h);
        ball.setRadius(w / (2 * GameMap2.MAP_ROWS));
        initGame();
    }

    private void initGame() {
        mTotalTime = 0;
        ball.setPosition(ball.getRadius() * 6, ball.getRadius() * 6);
//        ball.setPosition(1,1);
        invalidate();
    }

    public void startGame() {
        mHandler.post(this);
        ball.setPosition(ball.getRadius() * 6, ball.getRadius() * 6);
        mStartTime = System.currentTimeMillis();
    }

    public void toNextStage() {
        gameState = GAME_RUN;
        data = makeRandomData();
        // TODO: Clear this!
//        data[2][1] = GameMap2.EXIT_TILE;
        map.setData(data);
        Log.d("RandomMapView", "toNextStage: "+gameLevel);
        initGame();
        startGame();
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            float[] values = event.values.clone();
            mAccelX =(mAccelX * FILTER_FACTOR) + (values[0] * (1.0f - FILTER_FACTOR));
            mAccelY =(mAccelY * FILTER_FACTOR) + (-values[1] * (1.0f - FILTER_FACTOR));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void run() {
        gameLoop();
    }

    private void gameLoop() {
        mVectorX = mVectorX - mAccelX;
        mVectorY = mVectorY - mAccelY;

        int nextX = ball.getX() + (int) mVectorX;
        int nextY = ball.getY() + (int) mVectorY;
        int radius = ball.getRadius();

        if ((nextX - radius) < 0) {
            mVectorX *= -0.5f;
        } else if ((nextX + radius) > mWidth) {
            mVectorX *= -0.5f;
        }
        if ((nextY - radius) < 0) {
            mVectorY *= -0.5f;
        } else if ((nextY + radius) > mHeight) {
            mVectorY *= -0.5f;
        }

        if (radius < nextX && nextX < mWidth - radius && radius < nextY && nextY < mHeight - radius) {
            // 壁の当たり判定
            int ul = map.getCellType(nextX - radius, nextY - radius);
            int ur = map.getCellType(nextX + radius, nextY - radius);
            int dl = map.getCellType(nextX - radius, nextY + radius);
            int dr = map.getCellType(nextX + radius, nextY + radius);
            if (ul != WALL && ur != WALL && dl != WALL && dr != WALL) {
            } else if (ul != WALL && ur == WALL && dl != WALL && dr == WALL) {
                mVectorX *= -REBOUND;
            } else if (ul == WALL && ur != WALL && dl == WALL && dr != WALL) {
                mVectorX *= -REBOUND;
            } else if (ul == WALL && ur == WALL && dl != WALL && dr != WALL) {
                mVectorY *= -REBOUND;
            } else if (ul != WALL && ur != WALL && dl == WALL && dr == WALL) {
                mVectorY *= -REBOUND;
            } else if (ul == WALL && ur != WALL && dl != WALL && dr != WALL) {
                if (mVectorX < 0.0f && mVectorY > 0.0f) {
                    mVectorX *= -REBOUND;
                } else if (mVectorX > 0.0f && mVectorY < 0.0f) {
                    mVectorY *= -REBOUND;
                } else {
                    mVectorX *= -REBOUND;
                    mVectorY *= -REBOUND;
                }
            } else if (ul != WALL && ur == WALL && dl != WALL && dr != WALL) {
                if (mVectorX > 0.0f && mVectorY > 0.0f) {
                    mVectorX *= -REBOUND;
                } else if (mVectorX < 0.0f && mVectorY < 0.0f) {
                    mVectorY *= -REBOUND;
                } else {
                    mVectorX *= -REBOUND;
                    mVectorY *= -REBOUND;
                }
            } else if (ul != WALL && ur != WALL && dl == WALL && dr != WALL) {
                if (mVectorX > 0.0f && mVectorY > 0.0f) {
                    mVectorY *= -REBOUND;
                } else if (mVectorX < 0.0f && mVectorY < 0.0f) {
                    mVectorX *= -REBOUND;
                } else {
                    mVectorX *= -REBOUND;
                    mVectorY *= -REBOUND;
                }
            } else if (ul != WALL && ur != WALL && dl != WALL && dr == WALL) {
                if (mVectorX < 0.0f && mVectorY > 0.0f) {
                    mVectorY *= -REBOUND;
                } else if (mVectorX > 0.0f && mVectorY < 0.0f) {
                    mVectorX *= -REBOUND;
                } else {
                    mVectorX *= -REBOUND;
                    mVectorY *= -REBOUND;
                }
            } else {
                mVectorX *= -REBOUND;
                mVectorY *= -REBOUND;
            }
        }

        Log.d("RandomMapView", "Loop-getCellType: " + map.getCellType(nextX, nextY));
        if (map.getCellType(nextX, nextY) == GameMap2.EXIT_TILE) {
            if (gameLevel == 3) {
                gameState = GAME_CLEAR;
                freeHandler();
                mTotalTime = System.currentTimeMillis() - mStartTime;
                long tmp = sp.getLong(MainActivity.HIGH_SCORE_RANDOM, 0);
                if (tmp == 0 || tmp > mTotalTime) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putLong(MainActivity.HIGH_SCORE_RANDOM, mTotalTime);
                    editor.apply();
                }
            } else {
                gameState = GAME_WIN;
                gameLevel++;
            }

            invalidate();
            return;
        }


        ball.move((int) mVectorX, (int) mVectorY);
        invalidate();
        if (mHandler != null) {
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, 30);
        }
    }

    void freeHandler() {
        if (mHandler != null) {
            mHandler.removeCallbacks(this);
            mHandler = null;
        }
    }

    public int getGameState() {
        return gameState;
    }
}
