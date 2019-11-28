package com.ptasdevz.autotriggerevents;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.MotionEventCompat;

import android.app.Instrumentation;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PTASDEVZ";
    private ImageView imageView;
    private EditText editText;
    private ToggleButton tglBtnAutoMove, tglBtnAutowrite;
    final boolean[] offArr = new boolean[1];
    final boolean[] offArr1 = new boolean[1];
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mPosX;
    private float mPosY;
    private float remoteVal = 0.01f;
    private int[] locationImg = new int[2];
    private int[] locationLayout = new int[2];
    private Thread layoutThread;


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


                switch (action) {
                    case MotionEvent.ACTION_DOWN: {

                        final int pointerIndex = ev.getActionIndex();
                        final float x = ev.getX(pointerIndex);
                        final float xRaw = ev.getRawX();
                        final float yRaw = ev.getRawY();
                        final float y = ev.getY(pointerIndex);
                        Log.d(TAG, "onTouch: layout down: xRaw:" + xRaw + " yPos: yRaw:" + yRaw);
//                        Log.d(TAG, "onTouch: layout down: x:"+x + " yPos: x:" + y);

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
//                        Log.d(TAG, "onTouch: img down: xRaw:" + xRaw + " yPos: yRaw:" + yRaw);
                        Log.d(TAG, "onTouch: img down: x:" + x + " yPos: x:" + y);

                        // Remember where we started (for dragging)
                        mLastTouchX = x;
                        mLastTouchY = y;
                        // Save the ID of this pointer (for dragging)
                        mActivePointerId = ev.getPointerId(0);
//                        Log.d(TAG, "onTouch: pointer id" + mActivePointerId);
//                        Log.d(TAG, "onTouch: deviceID" + ev.getDeviceId());
//                        Log.d(TAG, "onTouch: xcoor" + ev.getX());
//                        Log.d(TAG, "onTouch: ycoor" + ev.getX());
//                        Log.d(TAG, "onTouch: btn_state" + ev.getButtonState());
//                        Log.d(TAG, "onTouch: edge_flags" + ev.getEdgeFlags());
//                        Log.d(TAG, "onTouch: orientation" + ev.getOrientation());
//                        Log.d(TAG, "onTouch: pressure" + ev.getPressure());
//                        Log.d(TAG, "onTouch: source" + ev.getSource());
//                        Log.d(TAG, "onTouch: size" + ev.getSize());
//                        Log.d(TAG, "onTouch: tool major" + ev.getToolMajor());
//                        Log.d(TAG, "onTouch: tool minor" + ev.getToolMinor());
//                        Log.d(TAG, "onTouch: touch major" + ev.getTouchMajor());
//                        Log.d(TAG, "onTouch: touch minor" + ev.getTouchMinor());
//                        Log.d(TAG, "onTouch: flags" + ev.getFlags());
//                        Log.d(TAG, "onTouch: tool type" + ev.getToolType(pointerIndex));
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        float viewPosY = view.getY() + view.getHeight();
                        float viewPosX = view.getX() + view.getWidth();
                        // Find the index of the active pointer and fetch its position
                        final int pointerIndex = ev.findPointerIndex(mActivePointerId);

                        final float x = ev.getX(pointerIndex);
                        final float y = ev.getY(pointerIndex);

                        //calculate distance moved
                        final float dx = x - mLastTouchX;
                        final float dy = y - mLastTouchY;

                        //calculate  the future x and y of view
                        float futurePosYdown = dy + view.getY() + view.getHeight();
                        float futurePosYup = dy + view.getY();
                        float futurePosXright = dx + view.getX() + view.getWidth();
                        float futurePosXleft = dx + view.getX();

                            /*
                            only update if view position remains within the limits of the layout.
                             */
                        if (futurePosYdown < layout.getHeight() && futurePosYup > 0) {

                            mPosY += dy;
                            mLastTouchY = y;// Remember this touch position for the next move event
                        }
                        if (futurePosXright < layout.getWidth() && futurePosXleft > 0) {

                            mPosX += dx;
                            mLastTouchX = x;// Remember this touch position for the next move event
                        }

//                        Log.d(TAG, "onTouch img move: x:" + x + " y:" + y);
//                        Log.d(TAG, "onTouch img move: xPos:" + mPosX + " yPos:" + mPosY);
//                        Log.d(TAG, "onTouch img move lastTouchX:" + mLastTouchX + " lastTouchY:" + mLastTouchY);
                        positionViewInCLayout(mPosX, mPosY, MainActivity.this.imageView, MainActivity.this.layout);

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
                            mLastTouchX = ev.getX(newPointerIndex);
                            mLastTouchY = ev.getY(newPointerIndex);
                            mActivePointerId = ev.getPointerId(newPointerIndex);
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
                if (b) {
                    offArr1[0] = !b;
                    layoutThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //enable toggle
                            try {
                                Instrumentation instrumentation = new Instrumentation();
                                Rect imageRect = new Rect();
                                Rect layoutRect = new Rect();
                                imageView.getLocalVisibleRect(imageRect);
                                layout.getLocalVisibleRect(layoutRect);
                                layout.getLocationInWindow(locationLayout);
                                imageView.getLocationInWindow(locationImg);
                                int width = imageView.getWidth();
                                int height = imageView.getHeight();
                                int halfWidthImageView = width / 2;
                                int halfHeightImageView = height / 2;
                                instrumentation.sendPointerSync(
                                        MotionEvent.obtain(
                                                SystemClock.uptimeMillis(),
                                                SystemClock.uptimeMillis(),
                                                MotionEvent.ACTION_DOWN,
                                                locationImg[0] + halfWidthImageView,  //press would simulate center
                                                locationImg[1] + halfHeightImageView, // press would simulate center
                                                0));


                                float coorCounter = 1f;
                                float eventX = locationImg[0] + halfWidthImageView;
                                float eventY = locationImg[1] + halfHeightImageView;
                                float layoutMax = layout.getWidth() + locationLayout[0];
                                float layoutMin = locationLayout[0];
                                while (true) {
                                    if (eventX + halfWidthImageView == layoutMax) coorCounter *= -1;
                                    if (eventX == layoutMin + halfHeightImageView) coorCounter *= -1;
                                    Log.d(TAG, "run: eventX " + eventX + " layoutMax " + layoutMax + " counter " + coorCounter + " locationx "+locationImg[0]);
                                    MotionEvent.PointerProperties pointerProperties = new MotionEvent.PointerProperties();
                                    MotionEvent.PointerProperties[] pproperties = new MotionEvent.PointerProperties[1];
                                    pointerProperties.id = 0;
                                    pproperties[0] = pointerProperties;

                                    MotionEvent.PointerCoords pointerCoords = new MotionEvent.PointerCoords();
                                    MotionEvent.PointerCoords[] pcoords = new MotionEvent.PointerCoords[1];
                                    eventX += coorCounter;
                                    pointerCoords.x = eventX;
                                    pointerCoords.y = eventY;


//                                    pointerCoords.x = locationImg[0] + imageView.getWidth()/2 + coorCounter;
//                                    pointerCoords.y = locationImg[1] + imageView.getHeight()/2;
                                    pointerCoords.pressure = 1;
                                    pointerCoords.size = 1;
                                    pointerCoords.orientation = 0;
                                    pointerCoords.toolMajor = 0;
                                    pointerCoords.touchMajor = 0;
                                    pointerCoords.toolMinor = 0;
                                    pointerCoords.touchMinor = 0;
                                    pcoords[0] = pointerCoords;

                                    MotionEvent motionEventMove = MotionEvent.obtain(
                                            SystemClock.uptimeMillis(),
                                            SystemClock.uptimeMillis() + 1L,
                                            MotionEvent.ACTION_MOVE,
                                            1,
                                            pproperties,
                                            pcoords,
                                            0,
                                            0,
                                            1,
                                            1,
                                            0,
                                            0,
                                            0,
                                            0);
                                    instrumentation.sendPointerSync(motionEventMove);
//                                    imageView.getLocationInWindow(locationImg);
//                                    coorCounter ;
//                                    Log.d(TAG, "run: coorCounter" + coorCounter + " " + offArr1[0] + "moveCounter "+moveCounter);
//                                    SystemClock.sleep(100);
                                    if (offArr1[0]) {
                                        Log.d(TAG, "run: auto move stopped");
                                        break;
                                    }
                                }

//                                MotionEvent.obtain()
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
                                tglBtnAutoMove.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tglBtnAutoMove.setChecked(false);
                                    }
                                });
                            } finally {

                            }
                        }
                    });
                    layoutThread.start();


                } else {
                    layoutThread.interrupt();
                    offArr1[0] = true;
                    Log.d(TAG, "onCheckedChanged: auto move" + offArr1[0]);
                }
            }
        });

        tglBtnAutowrite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
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
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        //offArr[0]=true;
                                        tglBtnAutowrite.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tglBtnAutowrite.setChecked(false);
                                            }
                                        });
                                    } finally {
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

                } else {
                    editText.setText("");
                    offArr[0] = true;
                    Log.d(TAG, "onCheckedChanged: auto wirte " + offArr[0]);
                }
            }
        });

    }

    private void positionViewInCLayout(float dx, float dy, View view, ConstraintLayout layout) {

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(view.getId(), ConstraintSet.RIGHT, this.layout.getId(), ConstraintSet.RIGHT, 0);
        constraintSet.connect(view.getId(), ConstraintSet.TOP, this.layout.getId(), ConstraintSet.TOP, 0);
        constraintSet.connect(view.getId(), ConstraintSet.LEFT, this.layout.getId(), ConstraintSet.LEFT, 0);
        constraintSet.connect(view.getId(), ConstraintSet.BOTTOM, this.layout.getId(), ConstraintSet.BOTTOM, 0);

        /*
        calculate vertical and horizontal bias to position element
         */
        int layoutConstraintWidth = this.layout.getWidth() - view.getWidth() + 1;
        int layoutConstraintHeight = this.layout.getHeight() - view.getHeight() + 1;

        float horizontalBias = (dx + view.getX()) / layoutConstraintWidth;
        //place view horizontal bias at the minimum or maximum of the constraint
        if (horizontalBias < 0) {
            horizontalBias = 0;
        } else if (horizontalBias > 1) {
            horizontalBias = 1;
        }
        constraintSet.setHorizontalBias(view.getId(), horizontalBias);

        float verticalBias = (dy + view.getY()) / layoutConstraintHeight;
        //place view vertical bias at the minimum or maximum of the constraint
        if (verticalBias < 0) {
            verticalBias = 0;
        } else if (verticalBias > 1) {
            verticalBias = 1;
        }
        constraintSet.setVerticalBias(view.getId(), verticalBias);

        constraintSet.applyTo(this.layout);
    }

}
