package jp.ac.jec.cm0136.sensorgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

public class GameMap2 {
//    private Context mContext;
    public final static int PATH_TILE = 0; // 通路
    public final static int WALL_TILE = 1; // 壁
    public final static int EXIT_TILE = 2; // 出口
    public final static int VOID_TILE = 3; // 穴
    public final static int MAP_ROWS = 32;
    public final static int MAP_COLS = 20;

    private  int[][] mData;
    private Bitmap[] mTextures;

    private int mTileWidth;
    private int mTileHeight;

    public GameMap2(Context context) {
//        mContext = context;
//        initScreenSize();
        mData = new int[MAP_ROWS][MAP_COLS];

        mTextures = new Bitmap[4];
        mTextures[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_path);
        mTextures[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_wall);
        mTextures[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_exit);
        mTextures[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_void);
    }

//    private void initScreenSize() {
//        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        DisplayMetrics metrics = new DisplayMetrics();
//        display.getMetrics(metrics);
//        mTileWidth = metrics.widthPixels;
//        mTileHeight = metrics.heightPixels;
//    }
//
//    private Bitmap scaleBitmap(Bitmap bitmap) {
//        return Bitmap.createScaledBitmap(bitmap, mTileWidth, mTileHeight, true);
//    }

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
                int index = mData[i][j];
                Bitmap bitmap = mTextures[index];

                int bitmapWidth = j * mTileWidth;
                int bitmapHeight = i * mTileHeight;


//                Log.d("SIZE", "bitmapWidth: " + bitmapWidth + " bitmapHeight: " + bitmapHeight );
                canvas.drawBitmap(bitmap, bitmapWidth, bitmapHeight, null);
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
