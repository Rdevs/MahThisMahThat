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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rdevs.mtmt.R;
import rdevs.mtmt.models.Fonts;

/**
 * Created by Kandeparker on 25-Mar-16.
 */
public class FragmentEffects extends Fragment {


    public int[] filterString = {
            R.string.text_filter_normal, R.string.text_filter_in1977, R.string.text_filter_amaro, R.string.text_filter_brannan, R.string.text_filter_early_bird, R.string.text_filter_hefe, R.string.text_filter_hudson, R.string.text_filter_inkwell, R.string.text_filter_lomofi, R.string.text_filter_lord_kelvin,
            R.string.text_filter_early_bird, R.string.text_filter_rise, R.string.text_filter_sierra, R.string.text_filter_sutro, R.string.text_filter_toaster, R.string.text_filter_valencia, R.string.text_filter_walden, R.string.text_filter_xproii
    };
    public int[] images = {
            R.drawable.filter_normal, R.drawable.filter_in1977, R.drawable.filter_amaro, R.drawable.filter_brannan,
            R.drawable.filter_early_bird, R.drawable.filter_hefe, R.drawable.filter_hudson, R.drawable.filter_inkwell,
            R.drawable.filter_lomofi, R.drawable.filter_lord_kelvin, R.drawable.filter_nashville, R.drawable.filter_rise,
            R.drawable.filter_sierra, R.drawable.filter_sutro, R.drawable.filter_toaster, R.drawable.filter_valencia,
            R.drawable.filter_walden, R.drawable.filter_xproii
    };
    Context context;
    PopupWindow pw;
    View v = null, anchorView;
    LinearLayout container;
    List<Fonts> font_list = new ArrayList<Fonts>();
    EffectsItemClickListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.popup_effect_picker, null);
        container = (LinearLayout) v.findViewById(R.id.effects_container);

        pw = new PopupWindow(v, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        pw.setContentView(v);
        pw.setOutsideTouchable(true);
        pw.setFocusable(true);
        // Removes default background.
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        container.removeAllViews();

        for (int i = 0; i < images.length; i++) {
            final int index = i;

            View view = inflater.inflate(R.layout.item_image,
                    null);
            ImageView imageview = (ImageView) view
                    .findViewById(R.id.image_iv);
            TextView textView = (TextView) view.findViewById(R.id.image_tv);
            imageview.setImageDrawable(getActivity().getResources().getDrawable(images[i]));
            textView.setText(getActivity().getResources().getString(filterString[i]));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onEffectClickListener(index);
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


        super.onAttach(context);
        try {
            listener =
                    (EffectsItemClickListener) getActivity();
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


    public interface EffectsItemClickListener {
        void onEffectClickListener(int effect_position);
    }


}
