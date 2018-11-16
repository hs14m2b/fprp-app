package click.mr_b.myapplication;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Intent;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class NewPlanActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY_ID = "ID";
    public static final String EXTRA_REPLY_NAME = "NAME";
    public static final String EXTRA_REPLY_Q1 = "Q1";
    public static final String EXTRA_REPLY_Q2 = "Q2";
    public static final String EXTRA_REPLY_Q3 = "Q3";
    public static final String EXTRA_REPLY_Q4 = "Q4";
    public static final String EXTRA_REPLY_Q5 = "Q5";
    public static final String EXTRA_REPLY_Q6 = "Q6";
    public static final String EXTRA_REPLY_RP1 = "RP1";
    public static final String EXTRA_REPLY_RP2 = "RP2";
    public static final String EXTRA_REPLY_RP3 = "RP3";
    public static final String EXTRA_REPLY_RP4 = "RP4";
    public static final String EXTRA_REPLY_RP5 = "RP5";

    private TextView mPlanIdView;
    private EditText mEditPlanView;
    private EditText mEditQ1View;
    private EditText mEditQ2View;
    private EditText mEditQ3View;
    private EditText mEditQ4View;
    private EditText mEditQ5View;
    private EditText mEditQ6View;
    private EditText mEditRP1View;
    private EditText mEditRP2View;
    private EditText mEditRP3View;
    private EditText mEditRP4View;
    private EditText mEditRP5View;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Intent intent = this.getIntent();
        setContentView(R.layout.activity_new_plan);
        mPlanIdView = findViewById(R.id.textPlanId);
        mEditPlanView = findViewById(R.id.edit_plan_name);
        mEditQ1View = findViewById(R.id.edit_question1);
        mEditQ2View = findViewById(R.id.edit_question2);
        mEditQ3View = findViewById(R.id.edit_question3);
        mEditQ4View = findViewById(R.id.edit_question4);
        mEditQ5View = findViewById(R.id.edit_question5);
        mEditQ6View = findViewById(R.id.edit_question6);
        mEditRP1View = findViewById(R.id.edit_rp1);
        mEditRP2View = findViewById(R.id.edit_rp2);
        mEditRP3View = findViewById(R.id.edit_rp3);
        mEditRP4View = findViewById(R.id.edit_rp4);
        mEditRP5View = findViewById(R.id.edit_rp5);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditPlanView.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String name = mEditPlanView.getText().toString();
                    replyIntent.putExtra(EXTRA_REPLY_NAME, name);
                    replyIntent.putExtra(EXTRA_REPLY_ID, mPlanIdView.getText().toString());
                    replyIntent.putExtra(EXTRA_REPLY_Q1, mEditQ1View.getText().toString());
                    replyIntent.putExtra(EXTRA_REPLY_Q2, mEditQ2View.getText().toString());
                    replyIntent.putExtra(EXTRA_REPLY_Q3, mEditQ3View.getText().toString());
                    replyIntent.putExtra(EXTRA_REPLY_Q4, mEditQ4View.getText().toString());
                    replyIntent.putExtra(EXTRA_REPLY_Q5, mEditQ5View.getText().toString());
                    replyIntent.putExtra(EXTRA_REPLY_Q6, mEditQ6View.getText().toString());
                    replyIntent.putExtra(EXTRA_REPLY_RP1, mEditRP1View.getText().toString());
                    replyIntent.putExtra(EXTRA_REPLY_RP2, mEditRP2View.getText().toString());
                    replyIntent.putExtra(EXTRA_REPLY_RP3, mEditRP3View.getText().toString());
                    replyIntent.putExtra(EXTRA_REPLY_RP4, mEditRP4View.getText().toString());
                    replyIntent.putExtra(EXTRA_REPLY_RP5, mEditRP5View.getText().toString());
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
        Bundle bundle = intent.getExtras();
        if (bundle != null && !bundle.isEmpty()) {
            //The text of a plan should have been sent.
            // question1-6, rp1-5, name
            mEditPlanView.setText(bundle.getString(EXTRA_REPLY_NAME));
            mPlanIdView.setText(bundle.getString(EXTRA_REPLY_ID));
            mEditQ1View.setText(bundle.getString(EXTRA_REPLY_Q1));
            mEditQ2View.setText(bundle.getString(EXTRA_REPLY_Q2));
            mEditQ3View.setText(bundle.getString(EXTRA_REPLY_Q3));
            mEditQ4View.setText(bundle.getString(EXTRA_REPLY_Q4));
            mEditQ5View.setText(bundle.getString(EXTRA_REPLY_Q5));
            mEditQ6View.setText(bundle.getString(EXTRA_REPLY_Q6));
            mEditRP1View.setText(bundle.getString(EXTRA_REPLY_RP1));
            mEditRP2View.setText(bundle.getString(EXTRA_REPLY_RP2));
            mEditRP3View.setText(bundle.getString(EXTRA_REPLY_RP3));
            mEditRP4View.setText(bundle.getString(EXTRA_REPLY_RP4));
            mEditRP5View.setText(bundle.getString(EXTRA_REPLY_RP5));
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent replyIntent = new Intent();
        setResult(RESULT_CANCELED, replyIntent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(this.getClass().getSimpleName(), "++ ON START ++");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(this.getClass().getSimpleName(), "+ ON RESUME +");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "- ON PAUSE -");
        Intent replyIntent = new Intent();
        setResult(RESULT_CANCELED, replyIntent);
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(this.getClass().getSimpleName(), "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(this.getClass().getSimpleName(), "- ON DESTROY -");
    }

}