package com.app.sinaps.tabtesting.Scaler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Sinaps on 13.08.2017.
 */


/** Расширение для SurfaceView
 *
 */
public class SinapsFastRenderView extends SurfaceView implements Runnable{

    MiniGame miniGame;
    /** Виртуальный фреймбуффер для рисования на SurfaceView */
    Bitmap frameBuffer;

    Thread renderThread = null;
    SurfaceHolder holder;
    volatile boolean running = false;

    Rect dstRect;

    public SinapsFastRenderView(Context context, MiniGame miniGame, Bitmap frameBuffer) {
        super(context);

        this.miniGame = miniGame;
        this.frameBuffer = frameBuffer;

        holder = getHolder();
        dstRect = new Rect(0, 0 , frameBuffer.getWidth() - 1, frameBuffer.getHeight() - 1);


    }

    public void resume(){
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();
        while(running){
            if(!holder.getSurface().isValid())
                continue;
            float deltaTime = (System.nanoTime() - startTime) / 1.0E9F;
            startTime = System.nanoTime();

            miniGame.update(deltaTime);
            miniGame.present(deltaTime);

            Canvas canvas = holder.lockCanvas();
            canvas.drawBitmap(frameBuffer, null, dstRect, null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause(){
        running = false;
        while (true){
            try{
                renderThread.join();
                break;
            }catch (InterruptedException e){

            }
        }
    }
}



































