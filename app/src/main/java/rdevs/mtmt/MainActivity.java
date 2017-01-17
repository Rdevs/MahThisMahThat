
package rdevs.mtmt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

import rdevs.mtmt.views.Utils;

public class MainActivity extends AppCompatActivity implements
        ImageChooserListener {
    String path = "";
    Context context;
    ProgressDialog pd;
    Button camera, gallery;
    TextView mah, meme, generator;
    private int chooserType;
    private ImageChooserManager imageChooserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_new);
        context = this;


        mah = (TextView) findViewById(R.id.mah);
        meme = (TextView) findViewById(R.id.meme);
        generator = (TextView) findViewById(R.id.generator);
        camera = (Button) findViewById(R.id.bt_camera);
        gallery = (Button) findViewById(R.id.bt_gallery);


        Utils.setTypeface(context, mah, Utils.FONT_DAYPOSTER);
        Utils.setTypeface(context, meme, Utils.FONT_DAYPOSTER);
        Utils.setTypeface(context, generator, Utils.FONT_DAYPOSTER);
        //   Utils.setTypeface(context,camera,Utils.FONT_DAYPOSTER);
        //   Utils.setTypeface(context,gallery,Utils.FONT_DAYPOSTER);


        pd = new ProgressDialog(context);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            camera.setVisibility(View.GONE);
        }

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capturePhoto();
            }
        });


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_photo_from_gallery();
            }
        });


    }

///// image selection functions

    public void takePic() {
        PackageManager pm = context.getPackageManager();

        CharSequence[] cs;
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            cs = new CharSequence[]{"Gallery", "Camera"};
        } else {
            cs = new CharSequence[]{"Gallery"};
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Image Source");
        builder.setItems(cs,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                select_photo_from_gallery();
                                break;

                            case 1:
                                capturePhoto();
                                break;

                            default:
                                break;
                        }
                    }
                });

        builder.show();
    }

    private void capturePhoto() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            pd.show();
            path = imageChooserManager.choose();
            Log.e("SELECTED IMAGE PATH", path + " ");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void select_photo_from_gallery() {

        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            pd.show();
            path = imageChooserManager.choose();
            Log.e("SELECTED IMAGE PATH", path + " ");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(this, chooserType, true);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(path);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //FOR IMAGE VIEW
        if (resultCode != RESULT_OK) {
            pd.dismiss();
            return;
        }
        Log.i("IMAGE CHOOSER", "OnActivityResult");
        Log.i("IMAGE CHOOSER", "File Path : " + path);
        Log.i("IMAGE CHOOSER", "Chooser Type: " + chooserType);
        if (resultCode == RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);
        } else {
            pd.dismiss();
        }
    }

//////////////////////////


    @Override
    public void onImageChosen(final ChosenImage chosenImage) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (chosenImage != null) {
                    Log.e("IMAGE CHOOSER", "Chosen Image: Is not null");
                    path = chosenImage.getFilePathOriginal();
                    Intent i = new Intent(context, Activity_SetText.class);
                    i.putExtra("file_path", path);
                    Log.e("PATH1", path + "");
                    startActivity(i);

                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    pd.dismiss();

                } else {
                    Log.e("IMAGE CHOOSER", "Chosen Image: Is null");
                    pd.dismiss();
                }
            }
        });
    }

    @Override
    public void onError(String s) {
        path = "";
        pd.dismiss();
        Toast.makeText(context, "Error while choosing image", Toast.LENGTH_SHORT).show();
    }

}