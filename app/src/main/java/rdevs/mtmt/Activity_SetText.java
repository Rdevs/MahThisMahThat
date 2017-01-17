package rdevs.mtmt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import rdevs.mtmt.models.TestEvent;
import rdevs.mtmt.views.Utils;

/**
 * Created by Kandeparker on 08-Feb-16.
 */
public class Activity_SetText extends AppCompatActivity {

    EditText mThis, mThat;
    TextView mah1, mah2;
    Button done, prev;
    String file_path = "";
    Context context;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_text_new);
        context = this;
        mThis = (EditText) findViewById(R.id.et_this);
        mThat = (EditText) findViewById(R.id.et_that);
        done = (Button) findViewById(R.id.bt_done_settext);
        mah1 = (TextView) findViewById(R.id.tv_mah1);
        mah2 = (TextView) findViewById(R.id.tv_mah2);
        MyApplication.getEventBus().register(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        prev = (Button) findViewById(R.id.bt_prev);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        Utils.setTypeface(context, mah1, Utils.FONT_DAYPOSTER);
        Utils.setTypeface(context, mah2, Utils.FONT_DAYPOSTER);
        Utils.setTypeface(context, mThat, Utils.FONT_DAYPOSTER);
        Utils.setTypeface(context, mThis, Utils.FONT_DAYPOSTER);
        //   Utils.setTypeface(context, done, Utils.FONT_DAYPOSTER);


        file_path = getIntent().getStringExtra("file_path");

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity_SetText.this, Activity_ImageAndText.class);
                i.putExtra("final_string", "Mah " + mThis.getText().toString() + " Mah " + mThat.getText().toString());
                i.putExtra("file_path", file_path);
                Log.e("PATH2", file_path + "");
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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

    @Subscribe
    public void onTestEvent(TestEvent event) {
        if (event.message.equals("finish")) {
            finish();
        }

        Log.e("EVENT RECEIVED", event.message + "");

    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
        //  MyApplication.getEventBus().unregister(this);
    }
}
