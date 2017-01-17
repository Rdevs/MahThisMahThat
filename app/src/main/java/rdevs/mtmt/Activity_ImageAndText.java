package rdevs.mtmt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import rdevs.mtmt.fragments.FragmentColor;
import rdevs.mtmt.fragments.FragmentEffects;
import rdevs.mtmt.fragments.FragmentFonts;
import rdevs.mtmt.models.TestEvent;
import rdevs.mtmt.models.TextOverlay;
import rdevs.mtmt.views.Utils;


/**
 * Created by Kandeparker on 08-Feb-16.
 */
public class Activity_ImageAndText extends AppCompatActivity implements GPUImageView.OnPictureSavedListener, FragmentEffects.EffectsItemClickListener, FragmentColor.ColorPickerListener, FragmentFonts.FontItemClickListener {


    String mahText = "";
    String path = "";
    Button done, prev;
    TabLayout tabLayout;
    Toolbar toolbar;
    ViewPager viewPager;
    Context context;
    private FrameLayout contentLayout_;
    private ImageView contentView_;
    private EditText overlayTextView_;
    private TextOverlay textOverlay_;
    private GPUImageFilter mFilter;
    private GPUImageView mGPUImageView;
    private GPUImageFilterTools.FilterList filters = new GPUImageFilterTools.FilterList();
    private ResizeTouchListener resizeTouchListener_ = new ResizeTouchListener(new ResizeTouchListener.OnChangesGesturesListener() {
        public float initialScale_;
        public float initialRotation_;
        private int initialMarginLeft_;
        private int initialMarginTop_;
        private FrameLayout.LayoutParams textParams_;

        @Override
        public void onDragGestureReset() {
            if (overlayTextView_ != null) {
                textParams_ = (FrameLayout.LayoutParams) overlayTextView_.getLayoutParams();
                initialMarginTop_ = textParams_.topMargin;
                initialMarginLeft_ = textParams_.leftMargin;
            }
        }

        @Override
        public void onRotateGestureReset() {
            initialRotation_ = overlayTextView_.getRotation();
            initialScale_ = overlayTextView_.getScaleX();
        }

        @Override
        public void onDrag(int dragX, int dragY) {
            if (overlayTextView_ != null && textParams_ != null) {
                textParams_.leftMargin = initialMarginLeft_ + dragX;
                textParams_.topMargin = initialMarginTop_ + dragY;
                overlayTextView_.setLayoutParams(textParams_);
                textOverlay_.setLocation(new float[]{((float) textParams_.leftMargin) / contentView_.getWidth(), ((float) textParams_.topMargin) / contentView_.getHeight()});
            }
        }

        @Override
        public void onRotate(int degrees) {
            if (overlayTextView_ != null) {
                overlayTextView_.setRotation(initialRotation_ - degrees);
                textOverlay_.setRotation(initialRotation_ - degrees);
            }
        }

        @Override
        public void onZoom(float zoom) {
            if (overlayTextView_ != null) {
                overlayTextView_.setScaleX(initialScale_ * zoom);
                overlayTextView_.setScaleY(initialScale_ * zoom);
                textOverlay_.setScale(initialScale_ * zoom);
            }
        }
    });

    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, 0, 0, null);

        canvas.drawBitmap(bmp2, 0, 0, null);
        Log.i("bmOverlay.......", "" + bmOverlay);
        return bmOverlay;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_new);
        context = this;
        if (savedInstanceState == null) {
            textOverlay_ = new TextOverlay();
        } else {
            textOverlay_ = savedInstanceState.getParcelable(Utils.STATE_TEXT_OVERLAY);
        }

        initView();
        initData();
        initiateOverlayEditText();
        setTextOverlayEditable(true);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                contentLayout_.setDrawingCacheEnabled(true);
                contentLayout_.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                contentLayout_.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
                contentLayout_.buildDrawingCache(true);
                Bitmap b = Bitmap.createBitmap(contentLayout_.getDrawingCache());
                SaveBitmapTask task = new SaveBitmapTask();
                task.execute(overlay(mGPUImageView.getGPUImage().getBitmapWithFilterApplied(), b));
                contentLayout_.destroyDrawingCache();
                mGPUImageView.destroyDrawingCache();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextOverlay.isDefault(textOverlay_) && overlayTextView_ == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initiateOverlayEditText();
                }
            }, 500);
        }

    }

    /**
     * Creates and adds EditText for text overlapping
     */
    private void initiateOverlayEditText() {
        overlayTextView_ = TextOverlay.createOverlayEditText(this, textOverlay_, contentView_);
        overlayTextView_.setBackgroundColor(Color.TRANSPARENT);
        setTextOverlayEditable(true);
        contentLayout_.addView(overlayTextView_);
        overlayTextView_.setText(mahText);
        overlayTextView_.setOnTouchListener(resizeTouchListener_);
        overlayTextView_.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textOverlay_.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Utils.STATE_TEXT_OVERLAY, textOverlay_);
    }

    private void setTextOverlayEditable(boolean isEditable) {
        overlayTextView_.setFocusable(isEditable);
        overlayTextView_.setFocusableInTouchMode(isEditable);
        overlayTextView_.setClickable(isEditable);

    }


    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        contentLayout_ = (FrameLayout) findViewById(R.id.content_layout);
        contentView_ = (ImageView) findViewById(R.id.iv_background_image);
        done = (Button) findViewById(R.id.done);
        prev = (Button) findViewById(R.id.bt_back);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);

        }


        mGPUImageView = (GPUImageView) findViewById(R.id.gpuimage);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE | TabLayout.GRAVITY_CENTER);
        //  tabLayout.setPadding(10, 0, 10, 0);
        tabLayout.setSelectedTabIndicatorHeight(5);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.color_red));
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void initData() {

        mahText = getIntent().getStringExtra("final_string");
        path = getIntent().getStringExtra("file_path");
        mGPUImageView.setImage(new File(path + ""));
        Log.e("PATH3", path);

        filters.addFilter("default", GPUImageFilterTools.FilterType.I_1977);
        filters.addFilter("1977", GPUImageFilterTools.FilterType.I_1977);
        filters.addFilter("Amaro", GPUImageFilterTools.FilterType.I_AMARO);
        filters.addFilter("Brannan", GPUImageFilterTools.FilterType.I_BRANNAN);
        filters.addFilter("Earlybird", GPUImageFilterTools.FilterType.I_EARLYBIRD);
        filters.addFilter("Hefe", GPUImageFilterTools.FilterType.I_HEFE);
        filters.addFilter("Hudson", GPUImageFilterTools.FilterType.I_HUDSON);
        filters.addFilter("Inkwell", GPUImageFilterTools.FilterType.I_INKWELL);
        filters.addFilter("Lomo", GPUImageFilterTools.FilterType.I_LOMO);
        filters.addFilter("LordKelvin", GPUImageFilterTools.FilterType.I_LORDKELVIN);
        filters.addFilter("Nashville", GPUImageFilterTools.FilterType.I_NASHVILLE);
        filters.addFilter("Rise", GPUImageFilterTools.FilterType.I_NASHVILLE);
        filters.addFilter("Sierra", GPUImageFilterTools.FilterType.I_SIERRA);
        filters.addFilter("sutro", GPUImageFilterTools.FilterType.I_SUTRO);
        filters.addFilter("Toaster", GPUImageFilterTools.FilterType.I_TOASTER);
        filters.addFilter("Valencia", GPUImageFilterTools.FilterType.I_VALENCIA);
        filters.addFilter("Walden", GPUImageFilterTools.FilterType.I_WALDEN);
        filters.addFilter("Xproll", GPUImageFilterTools.FilterType.I_XPROII);
        filters.addFilter("Contrast", GPUImageFilterTools.FilterType.CONTRAST);
        filters.addFilter("Brightness", GPUImageFilterTools.FilterType.BRIGHTNESS);
        filters.addFilter("Sepia", GPUImageFilterTools.FilterType.SEPIA);
        filters.addFilter("Vignette", GPUImageFilterTools.FilterType.VIGNETTE);
        filters.addFilter("ToneCurve", GPUImageFilterTools.FilterType.TONE_CURVE);
        filters.addFilter("Lookup (Amatorka)", GPUImageFilterTools.FilterType.LOOKUP_AMATORKA);
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(
                filter.getClass()))) {
            mFilter = filter;
            mGPUImageView.setFilter(mFilter);

        }
    }

    @Override
    public void onPictureSaved(Uri uri) {

    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentEffects(), "Effects");
        adapter.addFragment(new FragmentFonts(), "Fonts");
        adapter.addFragment(new FragmentColor(), "Color");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    @Override
    public void onEffectClickListener(int effect_position) {
        if (effect_position == 0) {
            switchFilterTo(new GPUImageFilter());
        } else {
            GPUImageFilter filter = GPUImageFilterTools
                    .createFilterForType(Activity_ImageAndText.this,
                            filters.filters.get(effect_position));
            switchFilterTo(filter);
        }
    }

    @Override
    public void onColorChangeListener(int colorBarValue, int alphaValue, int color) {
        overlayTextView_.setTextColor(color);
        overlayTextView_.setAlpha(alphaValue);
        Log.e("COLOR AND ALPHA", "colorbar value " + colorBarValue + " aplha " + alphaValue + " " + color);
    }

    @Override
    public void onFontSelectedListener(Typeface typeface) {
        overlayTextView_.setTypeface(typeface);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    class SaveBitmapTask extends AsyncTask<Bitmap, Void, File> {

        ProgressDialog p = new ProgressDialog(context);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p.setMessage("Please wait");
            p.setTitle("Saving image");
            p.setIndeterminate(true);
            p.setCancelable(false);
            p.setCanceledOnTouchOutside(false);
            p.show();
        }

        @Override
        protected File doInBackground(Bitmap... bitmaps) {

            File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), getResources().getString(R.string.app_name));

            if (!imageRoot.exists()) {
                imageRoot.mkdirs();
            }


            Date date = new Date();
            File imageFile = new File(imageRoot.toString(), date.getTime() + ".png");
            if (imageFile.exists()) {
                imageFile.delete();
            }

            Log.e("FILE PATH", imageRoot.toString() + "/" + date.getTime() + ".png");
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(imageFile);
                bitmaps[0].compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();

                //START SHARING
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //   finish();
                //   startActivity(Intent.createChooser(shareIntent, "Share image"));

            } catch (FileNotFoundException e) {
                Log.e("GREC", e.getMessage(), e);
            } catch (IOException e) {
                Log.e("GREC", e.getMessage(), e);
            }

            return imageFile;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            p.dismiss();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            MyApplication.getEventBus().post(new TestEvent("finish"));
            finish();
            startActivity(Intent.createChooser(shareIntent, "Share image"));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }
}



