package rdevs.mtmt.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import rdevs.mtmt.R;
import rdevs.mtmt.views.ColorSeekBar;

/**
 * Created by Kandeparker on 25-Mar-16.
 */
public class FragmentColor extends Fragment {

    PopupWindow pw;
    View v = null;
    ColorPickerListener listener;
    ColorSeekBar colorSeekBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.popup_color_picker, null);
        colorSeekBar = (ColorSeekBar) v.findViewById(R.id.colorSlider);
        pw = new PopupWindow(v, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);


        pw.setContentView(v);
        pw.setOutsideTouchable(true);
        pw.setFocusable(true);
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        colorSeekBar.setColors(R.array.material_colors); // material_colors is defalut included in res/color,just use it.
        colorSeekBar.setColorBarValue(10); //0 - maxValue
        colorSeekBar.setAlphaBarValue(255); //0-255


        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarValue, int alphaValue, int color) {
                if (listener != null) {
                    listener.onColorChangeListener(colorBarValue, alphaValue, color);

                }
            }
        });


        return v;
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        try {
            listener =
                    (ColorPickerListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement FontItemClickListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    public interface ColorPickerListener {
        void onColorChangeListener(int colorBarValue, int alphaValue, int color);
    }


}
