package click.mr_b.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import android.widget.Toast;

public class FingerprintDialog extends AppCompatActivity implements AuthenticationListener {

    // Declare a string variable for the key we’re going to use in our fingerprint authentication
    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private TextView textView;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_dialog);

        //Get an instance of KeyguardManager and FingerprintManager//
        keyguardManager =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager =
                (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
        // for starting the authentication process (via the startAuth method) and processing the authentication process events//
        FingerprintHandler helper = new FingerprintHandler(this, this);
        helper.startAuth(fingerprintManager, null);
        //Toast.makeText(this, "Started Authentication", Toast.LENGTH_LONG).show();
    }

    @Override
    public void authenticationSucceeded(FingerprintManager.CryptoObject cryptoObject) {
        //Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "0");
        //returnIntent.putExtra("serializableResult", result);
        this.setResult(Activity.RESULT_OK,returnIntent);
        this.finish();
    }

    @Override
    public void authenticationFailed() {
        this.setResult(Activity.RESULT_CANCELED);
        this.finish();
    }

    @Override
    public void authenticationCancelled() {
        this.setResult(Activity.RESULT_CANCELED);
        this.finish();
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }



}
