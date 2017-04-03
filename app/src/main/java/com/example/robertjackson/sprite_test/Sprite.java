package com.example.robertjackson.sprite_test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.Random;



/**
 * Sprite class
 * This is a bean type object, it has setters and getters but very little
 * logic otherwise. The logic it does have is move, and draw.
 * So it can adjust where it is on the screen slightly, as well as appear.
 */
public class Sprite extends SurfaceView implements SurfaceHolder.Callback {

    SpriteThread thread;
    Context ctx;

    public SurfaceHolder sh;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    public Sprite(Context context){
        super(context);
        sh = getHolder();
        sh.addCallback(this);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        ctx = context;

        setFocusable(true);


    }


    // surface views are faster for animation then regular views
    // unlike regular views, surface views can be drawn on by a background thread
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {


        thread = new SpriteThread(sh, ctx, new Handler());

        thread.setRunning(true);

        thread.start();


    }

    public SpriteThread getThread(){
        return thread;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    thread.setSurfaceSize(i1, i2);


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        thread.setRunning(false);

        while (retry) {
            try {
                thread.join();

                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    class SpriteThread extends Thread {
        private int canvasWidth = 200;
        private int canvasHeight = 400;
        private static final int SPEED = 200;
        private boolean run = false;

        private float bubbleX;
        private float bubbleY;
        private float headingX;
        private float headingY;

        public SpriteThread(SurfaceHolder surfaceHolder, Context context,
                            Handler handler) {
            sh = surfaceHolder;
            handler = handler;
            ctx = context;
        }
        public void doStart() {
            synchronized (sh) {
                // Start bubble in centre and create some random motion
                bubbleX = canvasWidth / 2;
                bubbleY = canvasHeight / 2;
                headingX = (float) (-1 + (Math.random() * 2));
                headingY = (float) (-1 + (Math.random() * 2));
            }
        }
        public void run() {
            while (run) {
                Canvas c = null;
                try {
                    c = sh.lockCanvas(null);
                    synchronized (sh) {
                        doDraw(c);

                    }
                } finally {
                    if (c != null) {
                        sh.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        public void setRunning(boolean b) {
            run = b;
        }
        public void setSurfaceSize(int width, int height) {
            synchronized (sh) {
                canvasWidth = width;
                canvasHeight = height;
                doStart();
            }
        }
        private void doDraw(Canvas canvas) {
            //canvas.save();
            bubbleX = bubbleX + (headingX * SPEED);

            if(bubbleX < 0 && headingX < 0){
                bubbleX = 0;
                headingX = -headingX;
            }
            if(bubbleY< 0 && headingY < 0){
                bubbleY = 0;
                headingY = -headingY;
            }
            if(bubbleX > canvasHeight && headingX > 0){
                bubbleX = canvasHeight ;
                headingX = -headingX;
            }
            if(bubbleY > canvasHeight && headingY > 0){
                bubbleY = canvasHeight ;
                headingY = -headingY;
            }

            bubbleY = bubbleY + (headingY * SPEED);
           // canvas.restore();
            canvas.drawColor(Color.BLACK);
            canvas.drawCircle(bubbleX, bubbleY, 50, paint);
        }
    }

}




