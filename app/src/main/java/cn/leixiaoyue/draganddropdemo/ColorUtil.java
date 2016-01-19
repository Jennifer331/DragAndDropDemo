package cn.leixiaoyue.draganddropdemo;

import android.graphics.Color;

/**
 * Created by 80119424 on 2016/1/19.
 */
public class ColorUtil {
    public static int mix(int color1,int color2){
        int r1 = Color.red(color1);
        int g1 = Color.green(color1);
        int b1 = Color.blue(color1);
        int r2 = Color.red(color2);
        int g2 = Color.green(color2);
        int b2 = Color.blue(color2);
        int r3 = (int)((r1 + r2) * 0.5);
        int g3 = (int)((g1 + g2) * 0.5);
        int b3 = (int)((b1 + b2) * 0.5);
        return Color.rgb(r3, g3,b3);
    }
}
