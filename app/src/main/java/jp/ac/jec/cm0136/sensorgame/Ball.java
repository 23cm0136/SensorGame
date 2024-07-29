package jp.ac.jec.cm0136.sensorgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Ball {
    private int mX = 0;
    private int mY = 0;
    private int mRadius = 0;
    private Paint mBallPaint;

    public Ball() {
        mBallPaint = new Paint();
        mBallPaint.setColor(Color.RED);
        mBallPaint.setAntiAlias(true);
    }
    public void setPosition(int x, int y) {
        mX = x;
        mY = y;
    }
    public void setColor(String color) {
        int colorHex = Color.parseColor(color);
        mBallPaint.setColor(colorHex);
    }
    public int getX() {
        return mX;
    }
    public int getY() {
        return mY;
    }
    public void setRadius(int radius) {
        mRadius = radius;
    }
    public int getRadius() {
        return mRadius;
    }
    public void draw(Canvas canvas) {
        canvas.drawCircle(mX, mY, mRadius, mBallPaint);
    }
    public void move(int dx, int dy) {
        mX += dx;
        mY += dy;
    }
}
