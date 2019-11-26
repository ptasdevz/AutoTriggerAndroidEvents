package com.ptasdevz.autotriggerevents;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.MotionEventCompat;

import android.app.Instrumentation;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PTASDEVZ";
    private ImageView imageView;
    private EditText editText;
    private ToggleButton tglBtnAutoMove, tglBtnAutowrite;
    final boolean[] offArr = new boolean[1];
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mPosX;
    private float mPosY;
    private ConstraintLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        layout = findViewById(R.id.img_layout);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {
                int action = ev.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN: {

                        final int pointerIndex = ev.getActionIndex();
                        final float x = ev.getX(pointerIndex);
                        final float xRaw = ev.getRawX();
                        final float yRaw = ev.getRawY();
                        final float y = ev.getY(pointerIndex);
                        Log.d(TAG, "onTouch: layout down: xRaw:"+xRaw + " yPos: yRaw:" + yRaw);
                        Log.d(TAG, "onTouch: layout down: x:"+x + " yPos: x:" + y);

                        // Remember where we started (for dragging)
                        mLastTouchX = x;
                        mLastTouchY = y;
                        // Save the ID of this pointer (for dragging)
                        mActivePointerId = ev.getPointerId(0);
                        break;
                    }
                }

                return true;
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {
                // Let the ScaleGestureDetector inspect all events.
//                mScaleDetector.onTouchEvent(ev);

                final int action = ev.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN: {

                        final int pointerIndex = ev.getActionIndex();
                        final float x = ev.getX(pointerIndex);
                        final float xRaw = ev.getRawX();
                        final float yRaw = ev.getRawY();
                        final float y = ev.getY(pointerIndex);
                        Log.d(TAG, "onTouch: img down: xRaw:"+xRaw + " yPos: yRaw:" + yRaw);
                        Log.d(TAG, "onTouch: img down: x:"+x + " yPos: x:" + y);

                        // Remember where we started (for dragging)
                        mLastTouchX = x;
                        mLastTouchY = y;
                        // Save the ID of this pointer (for dragging)
                        mActivePointerId = ev.getPointerId(0);
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        // Find the index of the active pointer and fetch its position
                        final int pointerIndex = ev.findPointerIndex(mActivePointerId);

                        final float x = ev.getX(pointerIndex);
                        final float y = ev.getY(pointerIndex);

                        // Calculate the distance moved
                        final float dx = x - mLastTouchX;
                        final float dy = y - mLastTouchY;

                        mPosX += dx;
                        mPosY += dy;

//                        invalidate();

                        // Remember this touch position for the next move event
                        mLastTouchX = x;
                        mLastTouchY = y;
                        Log.d(TAG, "onTouch img move: xPos:"+mPosX + " yPos:" + mPosY);
                        positionViewInLayout(mPosX,mPosY,false);
                        break;
                    }

                    case MotionEvent.ACTION_UP: {
                        mActivePointerId = INVALID_POINTER_ID;
                        break;
                    }

                    case MotionEvent.ACTION_CANCEL: {
                        mActivePointerId = INVALID_POINTER_ID;
                        break;
                    }

                    case MotionEvent.ACTION_POINTER_UP: {

                        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

                        if (pointerId == mActivePointerId) {
                            // This was our active pointer going up. Choose a new
                            // active pointer and adjust accordingly.
                            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                            mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                            mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
                            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                        }
                        break;
                    }
                }
                return true;
            }

        });
        editText = findViewById(R.id.editText);
        tglBtnAutoMove = findViewById(R.id.btn_auto_move);
        tglBtnAutowrite = findViewById(R.id.btn_auto_write);

        tglBtnAutoMove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //enable toggle
                            try {
                                Instrumentation instrumentation = new Instrumentation();
                                int[]locationImg = new int[2];
                                int[]locationLayout = new int[2];
                                imageView.getLocationInWindow(locationImg);
                                layout.getLocationInWindow(locationLayout);
                                instrumentation.sendPointerSync(
                                        MotionEvent.obtain(
                                                SystemClock.uptimeMillis(),
                                                SystemClock.uptimeMillis(),
                                                MotionEvent.ACTION_DOWN,
                                                locationImg[0],
                                                locationImg[1], 0));
                                instrumentation.sendPointerSync(
                                        MotionEvent.obtain(
                                                SystemClock.uptimeMillis(),
                                                SystemClock.uptimeMillis(),
                                                MotionEvent.ACTION_DOWN,
                                                locationLayout[0],
                                                locationLayout[1], 0));
//                                instrumentation.sendPointerSync(
//                                        MotionEvent.obtain(
//                                                SystemClock.uptimeMillis(),
//                                                SystemClock.uptimeMillis(),
//                                                MotionEvent.ACTION_MOVE,
//                                                imageView.getWidth(),
//                                                imageView.getHeight(), 0));
//
//                                instrumentation.sendPointerSync(
//                                        MotionEvent.obtain(
//                                                SystemClock.uptimeMillis(),
//                                                SystemClock.uptimeMillis(),
//                                                MotionEvent.ACTION_UP,
//                                                imageView.getWidth(),
//                                                imageView.getHeight(), 0));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                }else{

                }
            }
        });

        tglBtnAutowrite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    //toggle enabled
                    offArr[0] = !b;
                  new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Instrumentation instrumentation = new Instrumentation();
                            ArrayList<Integer> textToWrite = new ArrayList<>();
                            textToWrite.add(KeyEvent.KEYCODE_W);
                            textToWrite.add(KeyEvent.KEYCODE_E);
                            textToWrite.add(KeyEvent.KEYCODE_L);
                            textToWrite.add(KeyEvent.KEYCODE_C);
                            textToWrite.add(KeyEvent.KEYCODE_O);
                            textToWrite.add(KeyEvent.KEYCODE_M);
                            textToWrite.add(KeyEvent.KEYCODE_E);
                            textToWrite.add(KeyEvent.KEYCODE_SPACE);
                            textToWrite.add(KeyEvent.KEYCODE_T);
                            textToWrite.add(KeyEvent.KEYCODE_O);
                            textToWrite.add(KeyEvent.KEYCODE_SPACE);
                            textToWrite.add(KeyEvent.KEYCODE_T);
                            textToWrite.add(KeyEvent.KEYCODE_H);
                            textToWrite.add(KeyEvent.KEYCODE_E);
                            textToWrite.add(KeyEvent.KEYCODE_SPACE);
                            textToWrite.add(KeyEvent.KEYCODE_R);
                            textToWrite.add(KeyEvent.KEYCODE_E);
                            textToWrite.add(KeyEvent.KEYCODE_A);
                            textToWrite.add(KeyEvent.KEYCODE_L);
                            textToWrite.add(KeyEvent.KEYCODE_SPACE);
                            textToWrite.add(KeyEvent.KEYCODE_W);
                            textToWrite.add(KeyEvent.KEYCODE_O);
                            textToWrite.add(KeyEvent.KEYCODE_R);
                            textToWrite.add(KeyEvent.KEYCODE_L);
                            textToWrite.add(KeyEvent.KEYCODE_D);
                            textToWrite.add(KeyEvent.KEYCODE_PERIOD);

                            while (true) {
                                for (Integer keyCode : textToWrite) {

                                    try {
                                        instrumentation.sendKeyDownUpSync(keyCode);
                                        SystemClock.sleep(600);
                                        Log.d(TAG, "run: " + offArr[0]);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        //offArr[0]=true;
                                        tglBtnAutowrite.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tglBtnAutowrite.setChecked(false);
                                            }
                                        });
                                    }finally {
                                        if (offArr[0] == true) break;
                                    }


                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        editText.setText("");
                                    }
                                });
                                Log.d(TAG, "run: " + offArr[0]);
                                if (offArr[0] == true) break;
                            }
                        }
                    }).start();

                }else {
                    editText.setText("");
                    offArr[0] = true;
                    Log.d(TAG, "onCheckedChanged: "+ offArr[0]);
                }
            }
        });

    }
    private void positionViewInLayout(float dropPosX, float dropPosY,boolean isReverted) {

        ImageView dropEleImg = imageView;
        //update last dropped position of element
//            ElementPos lastPos = mathElement.getLastPos();
//            ElementPos currPos = mathElement.getCurrPos();

//            lastPos.setLeft(currPos.getLeft());
//            lastPos.setTop(currPos.getTop());
//            lastPos.setRight(currPos.getRight());
//            lastPos.setBottom(currPos.getBottom());
//
//            currPos.setLeft(dropPosX);
//            currPos.setTop(dropPosY);
//            currPos.setRight(dropPosX + dropEleImg.getWidth());
//            currPos.setBottom(dropPosY + dropEleImg.getHeight());

        if (isReverted) {

//                lastPos.setLeft(currPos.getLeft());
//                lastPos.setTop(currPos.getTop());
//                lastPos.setRight(currPos.getRight());
//                lastPos.setBottom(currPos.getBottom());

        }

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(dropEleImg.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, 0);
        constraintSet.connect(dropEleImg.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, 0);
        constraintSet.connect(dropEleImg.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, 0);
        constraintSet.connect(dropEleImg.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM, 0);
        int eleImgWidth = dropEleImg.getWidth();
        int eleImgHeight = dropEleImg.getHeight();
        int layoutConstraintWidth = layout.getWidth() - dropEleImg.getWidth() + 1;
        int layoutConstraintHeight = layout.getHeight() - dropEleImg.getHeight() + 1;
        float horizontalBias = (dropPosX - (eleImgWidth / 2)) / layoutConstraintWidth;
        if (horizontalBias < 0) horizontalBias = 0;
        else if (horizontalBias > 1) horizontalBias = 1;
        constraintSet.setHorizontalBias(dropEleImg.getId(), horizontalBias);
        float verticalBias = (dropPosY - (eleImgHeight / 2)) / layoutConstraintHeight;
        if (verticalBias < 0) verticalBias = 0;
        else if (verticalBias > 1) verticalBias = 1;
        constraintSet.setVerticalBias(dropEleImg.getId(), verticalBias);
        constraintSet.applyTo(layout);
    }

}
