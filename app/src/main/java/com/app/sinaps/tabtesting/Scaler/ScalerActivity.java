package com.app.sinaps.tabtesting.Scaler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

public class ScalerActivity extends AppCompatActivity implements MiniGame, View.OnTouchListener{

    private SinapsFastRenderView renderView;
    /** Виртуальных фреймбуфер для SurfaceView */
    private Bitmap frameBuffer;
    /** Рабочее изображение */
    private Bitmap picture;
    /** Канва которая будет связана с фреймбуфером. Используется для рисования на SurfaceView */
    private Canvas canvas;
    private Paint paint;
    private Rect srcRect;
    private Rect dstRect;
    /** Прямоугольник для рисования кнопки увеличения */
    private Rect zoomInBtn;
    /** Прямоугольник для рисования кнопки уменьшения */
    private Rect zoomOutBtn;

    public final int PERMISSION_REQUEST_CODE = 555;
    /** Границы кнопок*/
    private int marginLeft = 20;
    private int marginTop = 100;
    private int marginBottom = -180;
    private int buttonWidth = 80;
    private int buttonHeight = 80;
    /** Ограничители для масштабирования*/
    private int maxSrcWidth = 5000;
    private int minSrcWidth = 30;
    /** Границы текста, содержимое и цвет для кнопок*/
    private int textMarginLeft = 50;
    private int textMarginTop = 160;
    private int textMarginBottom = -120;
    private String zoomInBtnText = "+";
    private String zoomOutBtnText = "-";
    private int buttonColor = Color.argb(200, 200, 15, 15);
    private int buttonTextSize = 48;

    private float zoomFactor = 0.1f;
    private int lenthStep = 100;

    private boolean inTouch;
    private boolean isSingleTouch;
    private boolean isDoubleTouch;
    /** Начальное расстояние между двумя пальцами при мултитаче*/
    private int startLenth;
    private int startX;
    private int startY;
    /** Расстояние между двумя пальцами при мултитаче*/
    private int elapsedLenth;
    /** Индексы касаний*/
    private int downPI;
    private int upPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        frameBuffer = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_4444);
        srcRect = new Rect(0, 0, frameBuffer.getWidth() - 1, frameBuffer.getHeight() - 1);
        dstRect = new Rect(0, 0, frameBuffer.getWidth() - 1, frameBuffer.getHeight() - 1);

        Uri imageUri = (Uri) getIntent().getExtras().get(ScalerFragment.IMAGE_URI_KEY);
        picture = loadPicture(imageUri);
        renderView = new SinapsFastRenderView(this, this, frameBuffer);

        setContentView(renderView);

        renderView.setOnTouchListener(this);
        canvas = new Canvas(frameBuffer);
        paint = new Paint();

        zoomInBtn = new Rect(dstRect.left + marginLeft, dstRect.top + marginTop,
                dstRect.left + marginLeft + buttonWidth,
                dstRect.top + marginTop + buttonHeight);

        zoomOutBtn = new Rect(dstRect.left + marginLeft, dstRect.bottom + marginBottom,
                dstRect.left + marginLeft + buttonWidth,
                dstRect.bottom + marginBottom + buttonHeight);
    }
    /** Загружает переданную с другого активити картинку
     *
     */
    private Bitmap loadPicture(Uri imageUri) {
        Bitmap bitmap = null;
        Log.d("scaling", "Начинаем создание битамапа: ");
        Log.d("scaling", "Uri: " + imageUri);

        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public void onResume() {
        super.onResume();
        renderView.resume();
    }

    public void onPause() {
        super.onPause();
        renderView.pause();
    }

    @Override
    public void update(float deltaTime) {

    }
    /** Рисует изображение на экране
     *
     */
    @Override
    public void present(float deltaTime) {
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(picture, srcRect, dstRect, null);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(buttonColor);
        paint.setTextSize(buttonTextSize);
        canvas.drawRect(zoomInBtn, paint);
        canvas.drawRect(zoomOutBtn, paint);
        paint.setColor(Color.WHITE);
        canvas.drawText(zoomInBtnText, dstRect.left + textMarginLeft, dstRect.top + textMarginTop, paint);
        canvas.drawText(zoomOutBtnText, dstRect.left + textMarginLeft, dstRect.bottom + textMarginBottom, paint);
    }
    /** Увеличивает изображение путем уменьшения площади прямоугольника, выступающего источником
     *
     */
    private void zoomIn(){
        if(srcRect.width() > minSrcWidth && srcRect.height() > minSrcWidth) {
            int xStep = (int) (srcRect.width() * zoomFactor);
            int yStep = (int) (srcRect.height() * zoomFactor);
            srcRect.inset(xStep, yStep);
        }
    }
    /** Уменьшает изображение путем увеличения площади прямоугольника, выступающего источником
     *
     */
    private void zoomOut(){
        if(srcRect.width() < maxSrcWidth && srcRect.height() < maxSrcWidth) {
            int xStep = (int) (srcRect.width() * zoomFactor);
            int yStep = (int) (srcRect.height() * zoomFactor);
            srcRect.inset(-xStep, -yStep);
        }
    }
    /** Перемещает изображение по экрану
     *
     */
    private void translate(int x, int y){
        srcRect.offset(- x, -y);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int actionMask = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        int pointerCount = event.getPointerCount();

        switch (actionMask){
            case MotionEvent.ACTION_DOWN:
                inTouch = true;
                isSingleTouch = true;
                startX = (int)event.getX(0);
                startY = (int)event.getY(0);
            case MotionEvent.ACTION_POINTER_DOWN:
                downPI = pointerIndex;
                if(pointerCount == 2){
                    isDoubleTouch = true;
                    startLenth = getLenth(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                }else{
                    isDoubleTouch = false;
                }
                break;
            case  MotionEvent.ACTION_UP:
                if(event.getX(0) > zoomInBtn.left && event.getX(0) < zoomInBtn.right&&
                        event.getY(0) > zoomInBtn.top && event.getY(0) < zoomInBtn.bottom)
                    zoomIn();
                if(event.getX(0) > zoomOutBtn.left && event.getX(0) < zoomOutBtn.right&&
                        event.getY(0) > zoomOutBtn.top && event.getY(0) < zoomOutBtn.bottom)
                    zoomOut();
                inTouch = false;
            case MotionEvent.ACTION_POINTER_UP:
                isDoubleTouch = false;
                isSingleTouch = false;
                upPI = pointerIndex;
                break;
            case MotionEvent.ACTION_MOVE:
                if(isDoubleTouch){
                    checkLenth(getLenth(event.getX(0), event.getY(0), event.getX(1), event.getY(1)));
                }
                if(isSingleTouch){
                    translate((int)(event.getX(0) - startX), (int)(event.getY(0) - startY));
                    startX = (int)event.getX(0);
                    startY = (int)event.getY(0);
                }
                break;
        }

        return true;
    }
    /** Вычисляет расстояние между двумя пальцами при мультитач касании
     *
     */
    public int getLenth(float x1, float y1, float x2, float y2){
        int lenth = (int) Math.sqrt(Math.pow((x2-x1), 2) + Math.pow((y2 - y1), 2));
        return lenth;
    }
    /** Проверяет достаточно ли пользвоатель "раздвинул" или "сдвинул" пальцы
     * при мультаче, что бы произвести соответственно увеличение или уменьшение
     * изображения
     */
    public void checkLenth(int lenth){
        int growing = lenth - startLenth;
        elapsedLenth += growing;
        if(elapsedLenth > lenthStep){
            zoomIn();
            elapsedLenth = 0;
        }
        if(elapsedLenth < - lenthStep){
            zoomOut();
            elapsedLenth = 0;
        }
        startLenth = lenth;
    }
}
