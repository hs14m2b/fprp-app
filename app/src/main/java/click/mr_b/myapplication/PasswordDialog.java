package click.mr_b.myapplication;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordDialog extends AppCompatActivity {

    private PasswordViewModel mPasswordViewModel;

    private ConstraintLayout mNewPassword;
    private ConstraintLayout mPassword;
    private EditText mPassword1;
    private EditText mPassword2;
    private EditText mCurrentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_dialog);
        mPasswordViewModel = ViewModelProviders.of(this).get(PasswordViewModel.class);
        mNewPassword = findViewById(R.id.newPassword);
        mPassword = findViewById(R.id.checkPassword);
        mPassword1 = findViewById(R.id.newPassword1);
        mPassword2 = findViewById(R.id.newPassword2);
        mCurrentPassword = findViewById(R.id.currentPassword);
        if (mPasswordViewModel.countEntries() == 0)
        {
            mPassword.setVisibility(View.INVISIBLE);
            mNewPassword.setVisibility(View.VISIBLE);
        }
        else
        {
            mPassword.setVisibility(View.VISIBLE);
            mNewPassword.setVisibility(View.INVISIBLE);
        }
        final Button button = findViewById(R.id.buttonCreate);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean blnSave = false;
                //check that the passwords match
                //and that they are of minimum length
                String pwd1 = mPassword1.getText().toString();
                String pwd2 = mPassword2.getText().toString();
                if (pwd1 == null || pwd1.isEmpty() || pwd1.length() < 8)
                {
                    Toast.makeText(PasswordDialog.this, "Please enter a password of minimum length 8 characters to continue", Toast.LENGTH_LONG).show();
                }
                else if (!pwd1.equals(pwd2))
                {
                    Toast.makeText(PasswordDialog.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }
                else
                {
                    blnSave = true;
                }
                if (blnSave)
                {
                    Log.d(this.getClass().getSimpleName(), "Passwords match - saving!");
                    Password newPwd = new Password();
                    newPwd.setPwdHash(pwd1);
                    mPasswordViewModel.insert(newPwd);
                    authSucceeded();
                }
            }});
        final Button buttonAuth = findViewById(R.id.buttonUnlockPlans);
        buttonAuth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean blnSave = false;
                //check that the passwords match
                String pwd = mCurrentPassword.getText().toString();
                Password currentPassFromDB = mPasswordViewModel.getPassword();
                if (currentPassFromDB.getPwdHash().equals(Password.getHash(pwd)))
                {
                    //passwords match!
                    authSucceeded();
                }
                else
                {
                    Toast.makeText(PasswordDialog.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }
            }});

        final Button buttonForgot = findViewById(R.id.buttonForgotPassword);
        buttonForgot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PasswordDialog.this);
                builder.setMessage("Are you sure you want to reset password? This will delete all your current plans!");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("DialogListener", "Clicked Yes:" + which);
                        //delete plans
                        mPasswordViewModel.deleteAll();
                        //go back to main activity
                        passwordReset();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("DialogListener", "Clicked No:" + which);
                    }
                });
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }});

    }

    private void authSucceeded()
    {
        Log.d(this.getClass().getSimpleName(), "Authentication Success!");
        //Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "0");
        this.setResult(Activity.RESULT_OK,returnIntent);
        this.finish();
    }

    private void passwordReset()
    {
        Log.d(this.getClass().getSimpleName(), "Password Reset - let Main Activity know");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "2");
        this.setResult(Activity.RESULT_OK,returnIntent);
        this.finish();
    }
}
