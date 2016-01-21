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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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

    private LinearLayout mFactory, mConsumer2;
    private FrameLayout mConsumer1;
    private DragDetectSpecView mTopView, mBottomView;
    private MyDragEventListener mDragEventListener1;
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

        mConsumer1 = (FrameLayout)findViewById(R.id.container_consumer1);
        mConsumer1.setTag(R.id.description, CONSUMER1_DESC);
        mConsumer1.setTag(R.id.backgroundcolor,Color.WHITE);
        mConsumer1.setTag(R.id.transition_bg_id,R.drawable.border_transition);

        mConsumer2 = (LinearLayout)findViewById(R.id.container_consumer2);
        mConsumer2.setTag(R.id.description, CONSUMER2_DESC);
        mConsumer2.setTag(R.id.backgroundcolor, Color.WHITE);
        mConsumer2.setTag(R.id.transition_bg_id,R.drawable.border_transition);

        mTopView = (DragDetectSpecView)findViewById(R.id.triangle_top);
        mTopView.setTag(R.id.description, CONSUMER1_DESC);
        mTopView.setTag(R.id.backgroundcolor, Color.WHITE);
        mTopView.setTag(R.id.transition_bg_id,R.drawable.triangle_top_transition);

        mBottomView = (DragDetectSpecView)findViewById(R.id.triangle_bottom);
        mBottomView.setTag(R.id.description, CONSUMER1_DESC);
        mBottomView.setTag(R.id.backgroundcolor, Color.WHITE);
        mBottomView.setTag(R.id.transition_bg_id,R.drawable.triangle_bottom_transition);
    }

    private void initListener() {
        mDragEventListener1 = new MyDragEventListener();
        mConsumer1.setOnDragListener(mDragEventListener1);
        mConsumer2.setOnDragListener(mDragEventListener1);

        mTopView.addFriends(mBottomView);
//        mBottomView.addFriends(mTopView);
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
                    return true;
                }
                case DragEvent.ACTION_DRAG_ENTERED: {
                    mOriginBackground = new WeakReference<>(v.getBackground());
                    invadeWarning(v);
                    return true;
                }
                case DragEvent.ACTION_DRAG_LOCATION:{
                    return true;
                }
                case DragEvent.ACTION_DROP:{
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
                    invadeWarning(v);
                    return true;
                }
            }
            return false;
        }

        private void invadeWarning(View v){
            int drawableId = Integer.valueOf(v.getTag(R.id.transition_bg_id).toString());
            TransitionDrawable drawable = (TransitionDrawable)getDrawable(drawableId);
            v.setBackground(drawable);
            drawable.startTransition(100);
            v.invalidate();
        }
    }
}
