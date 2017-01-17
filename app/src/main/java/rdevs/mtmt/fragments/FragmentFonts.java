package rdevs.mtmt.fragments;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rdevs.mtmt.R;
import rdevs.mtmt.models.Fonts;
import rdevs.mtmt.views.Utils;

/**
 * Created by Kandeparker on 25-Mar-16.
 */
public class FragmentFonts extends Fragment {

    public FontItemClickListener listener;
    Context context;
    PopupWindow pw;
    LayoutInflater inf;
    View v = null, anchorView;
    LinearLayout container;
    List<Fonts> font_list = new ArrayList<Fonts>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.popup_font_picker, null);
        container = (LinearLayout) v.findViewById(R.id.fonts_container);

        pw = new PopupWindow(v, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        pw.setContentView(v);
        pw.setOutsideTouchable(true);
        pw.setFocusable(true);
        // Removes default background.
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Get fonts from assets
        try {
            AssetManager assets = getActivity().getAssets();
            String[] fontsList = assets.list(Utils.FONTS_PATH);
            for (String font : fontsList) {
                if (font.toLowerCase().endsWith(".ttf")) {
                    font_list.add(new Fonts(font.replaceAll(".ttf", ""), Typeface.createFromAsset(assets, Utils.FONTS_PATH + "/" + font)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (font_list.size() == 0) {
            font_list.add(new Fonts(Utils.DEFAULT_FONT.replaceAll(".ttf", ""), Typeface.DEFAULT));
        }

        container.removeAllViews();

        for (int i = 0; i < font_list.size(); i++) {
            final int index = i;


            View view = inflater.inflate(R.layout.fonts_row,
                    null);

            TextView textView = (TextView) view.findViewById(R.id.tv_font_name);
            textView.setText(" Abc ");
            textView.setTypeface(font_list.get(i).getTypeface());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onFontSelectedListener(font_list.get(index).getTypeface());
                        pw.dismiss();
                    }
                }
            });
            container.addView(view);
        }


        return v;

    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        try {
            listener =
                    (FontItemClickListener) getActivity();
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


    public interface FontItemClickListener {
        void onFontSelectedListener(Typeface typeface);
    }
}
