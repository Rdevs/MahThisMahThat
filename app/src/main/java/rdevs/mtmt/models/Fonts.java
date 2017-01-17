package rdevs.mtmt.models;

import android.graphics.Typeface;

/**
 * Created by Kandeparker on 17-Feb-16.
 */
public class Fonts {

    String name;
    Typeface typeface;


    public Fonts(String name, Typeface typeface) {
        this.name = name;
        this.typeface = typeface;
    }


    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
