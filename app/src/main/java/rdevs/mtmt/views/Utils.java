package rdevs.mtmt.views;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Kandeparker on 16-Feb-16.
 */
public class Utils {

    public static final String FONTS_PATH = "fonts";
    public static final String DEFAULT_FONT = "Default";
    public static final String STATE_TEXT_OVERLAY = "STATE_TEXT_OVERLAY";
    public static String FONT_DAYPOSTER = "DayPosterShadowNF.ttf";

    public static void setTypeface(Context context, TextView tv, String font_name) {

        AssetManager assets = context.getAssets();
        Typeface tf = Typeface.createFromAsset(assets, FONTS_PATH + "/" + font_name);
        tv.setTypeface(tf);
    }

    public static void setTypeface(Context context, EditText et, String font_name) {

        AssetManager assets = context.getAssets();
        Typeface tf = Typeface.createFromAsset(assets, FONTS_PATH + "/" + font_name);
        et.setTypeface(tf);
    }

    public static void setTypeface(Context context, Button button, String font_name) {

        AssetManager assets = context.getAssets();
        Typeface tf = Typeface.createFromAsset(assets, FONTS_PATH + "/" + font_name);
        button.setTypeface(tf);
    }

}
