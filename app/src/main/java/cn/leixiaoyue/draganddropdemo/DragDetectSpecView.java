package cn.leixiaoyue.draganddropdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 80119424 on 2016/1/20.
 */
public class DragDetectSpecView extends View {
    public static final String TAG = "DragDetectSpecView";
    private List<View> mFriends;

    public DragDetectSpecView(Context context) {
        this(context, null);
    }

    public DragDetectSpecView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragDetectSpecView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DragDetectSpecView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mFriends = new ArrayList<>();
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        final int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED: {
                return true;
            }
            case DragEvent.ACTION_DRAG_ENTERED: {
                Log.v(TAG,"[onDragEvent]ACTION_DRAG_ENTERED in " + getId());
                if (null != mFriends && !mFriends.isEmpty()) {
                    int size = mFriends.size();
                    for(int i = 0; i < size;i ++){
                        mFriends.get(i).dispatchDragEvent(event);
                    }
                }
                int x = (int) (event.getX()) - getLeft();
                int y = (int) (event.getY()) - getTop();
                if (inArea(x, y)) {
                    invadeWarning();
                }
                return true;
            }
            case DragEvent.ACTION_DRAG_LOCATION:{
                Log.v(TAG,"[onDragEvent]ACTION_DRAG_LOCATION in " + getId());
                if (null != mFriends && !mFriends.isEmpty()) {
                    int size = mFriends.size();
                    for(int i = 0; i < size;i ++){
                        mFriends.get(i).dispatchDragEvent(event);
                    }
                }
                int x = (int) (event.getX());
                int y = (int) (event.getY());
                if(inArea(x, y)){
                    invadeWarning();
                }
                return true;
            }
            case DragEvent.ACTION_DROP:{
                if(getTag(R.id.description).toString()
                        .equals(event.getClipDescription().getLabel())){
                    int color1 = (int)getTag(R.id.backgroundcolor);
//                        int color2 = Color.parseColor(event.getClipData().getItemAt(0).getText().toString());
                    int color2 = Integer.valueOf(event.getClipData().getItemAt(0).getText().toString());
                    int mixColor = ColorUtil.mix(color1, color2);
                    setTag(R.id.backgroundcolor, mixColor);
                    setBackground(new ColorDrawable(mixColor));
                    invalidate();
                }else{
                    Toast.makeText(getContext(), "不接受！", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            case DragEvent.ACTION_DRAG_EXITED: {
                return true;
            }
        }
        return false;
    }

    private boolean inArea(int x, int y){
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        getBackground().draw(canvas);
        int color = bitmap.getPixel(x, y);
        Log.v(TAG,"[inArea]color:" + color);
        return (color != 0);
    }

    public void addFriends(View v){
        mFriends.add(v);
    }

    private void invadeWarning() {
        int drawableId = Integer.valueOf(getTag(R.id.transition_bg_id).toString());
        TransitionDrawable drawable = (TransitionDrawable) getResources().getDrawable(drawableId);
        setBackground(drawable);
        drawable.startTransition(100);
        invalidate();
    }

}
