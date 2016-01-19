package cn.leixiaoyue.draganddropdemo;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * Created by 80119424 on 2016/1/19.
 */
public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    public static final String FACTORY_DESC = "factory";
    public static final String CONSUMER1_DESC = "consumer1";
    public static final String CONSUMER2_DESC = "consumer2";
    public static final String[] MIMETYPES_TEXT_PLAIN = new String[] {
            ClipDescription.MIMETYPE_TEXT_PLAIN };

    private LinearLayout mFactory;
    private ScrollView mConsumer1,mConsumer2;
    private MyDragEventListener mDragEventListener1,mDragEventListener2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
//        setContentView(R.layout.frame_layout);//tests how events are dispatched in cross area
        initView();
        initListener();
        produce();
    }

    private void initView() {
        mFactory = (LinearLayout)findViewById(R.id.container_factory);
        mFactory.setTag(R.id.description,FACTORY_DESC);

        mConsumer1 = (ScrollView)findViewById(R.id.container_consumer1);
        mConsumer1.setTag(R.id.description, CONSUMER1_DESC);
        mConsumer1.setTag(R.id.backgroundcolor,Color.WHITE);

        mConsumer2 = (ScrollView)findViewById(R.id.container_consumer2);
        mConsumer2.setTag(R.id.description, CONSUMER2_DESC);
        mConsumer2.setTag(R.id.backgroundcolor, Color.WHITE);
    }

    private void initListener() {
        mDragEventListener1 = new MyDragEventListener();
        mDragEventListener2 = new MyDragEventListener();
        mConsumer1.setOnDragListener(mDragEventListener1);
        mConsumer2.setOnDragListener(mDragEventListener2);
    }

    private void produce() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            View view = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100,100);
            params.rightMargin = 10;
            view.setLayoutParams(params);
            int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            view.setTag(R.id.description, CONSUMER2_DESC);
            view.setBackground(new ColorDrawable(color));
            Log.v(TAG, "[produce]" + color);
//            String hexColor = String.format("#%06X", (0xFFFFFF & color));
//            ClipData.Item item = new ClipData.Item(hexColor);
            ClipData.Item item = new ClipData.Item(color + "");
            final ClipData data = new ClipData(view.getTag(R.id.description).toString(),MIMETYPES_TEXT_PLAIN,item);
            final View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.startDrag(data,shadowBuilder,null,0);
                    return true;
                }
            });
            mFactory.addView(view);
        }
    }

    public class MyDragEventListener implements View.OnDragListener {
        private WeakReference<Drawable> mOriginBackground;

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    Log.v(TAG,"[MyDragEventListener][onDrag]started");
                    return true;
                }
                case DragEvent.ACTION_DRAG_ENTERED: {
                    Log.v(TAG,"[MyDragEventListener][onDrag]entered");
                    mOriginBackground = new WeakReference<>(v.getBackground());
                    TransitionDrawable drawable = (TransitionDrawable)getDrawable(R.drawable.border_transition);
                    v.setBackground(drawable);
                    drawable.startTransition(1000);
                    v.invalidate();
                    return true;
                }
                case DragEvent.ACTION_DRAG_LOCATION:{
                    Log.v(TAG,"[MyDragEventListener][onDrag]location");
                    return true;
                }
                case DragEvent.ACTION_DROP:{
                    Log.v(TAG,"[MyDragEventListener][onDrag]drop");
                    if(v.getTag(R.id.description).toString()
                            .equals(event.getClipDescription().getLabel())){
                        int color1 = (int)v.getTag(R.id.backgroundcolor);
//                        int color2 = Color.parseColor(event.getClipData().getItemAt(0).getText().toString());
                        int color2 = Integer.valueOf(event.getClipData().getItemAt(0).getText().toString());
                        int mixColor = ColorUtil.mix(color1, color2);
                        v.setTag(R.id.backgroundcolor,mixColor);
                        v.setBackground(new ColorDrawable(mixColor));
                        v.invalidate();
                    }else{
                        Toast.makeText(v.getContext(), "不接受！", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                case DragEvent.ACTION_DRAG_EXITED: {
                    Log.v(TAG,"[MyDragEventListener][onDrag]exited");
                    return true;
                }
            }
            return false;
        }
    }
}
